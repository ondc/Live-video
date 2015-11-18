package com.dm.service.video.web.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dm.service.video.web.mq.MessageReceiver;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * ffmpeg本地应用程序调用服务
 * <p>
 *     负责调用ffmpeg程序，为摄像头与nginx流媒体服务器建立流媒体通道，提供直播资源。
 * </p>
 * Created by chenlichao on 15/6/18.
 */
@Service
public class LiveCameraService {

    private static final Logger logger = LoggerFactory.getLogger(LiveCameraService.class);

    private Map<String, ProcessHolder> runningProcesses = new ConcurrentHashMap<>();

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Value("${system.nginx.server}")
    private String nginxServer;

    @Value("${system.video.params}")
    private String videoParams;

    @Value("${system.video.transport}")
    private String videoTransport;

    @Value("${interface.url.camera}")
    public  String  url;

    @Autowired
    MessageReceiver messageReceiver;

    public boolean isLiving(String orderId) {
        return runningProcesses.containsKey(orderId);
    }

    public void startLiveCamera(String camera, String orderId) throws IOException {
        ProcessHolder ph = new ProcessHolder(camera, orderId);
        executorService.submit(ph);
        this.runningProcesses.put(orderId, ph);
    }

    public void closeLiveCamera(String orderId) throws IOException {
        ProcessHolder ph = runningProcesses.get(orderId);
        if(ph != null) {
            if(ph.closeProcess()) {
                runningProcesses.remove(orderId);
            }
        }
    }

    public String getCamera(String orderId) {
        ProcessHolder ph = runningProcesses.get(orderId);
        if(ph != null) {
            return ph.camera;
        }
        return null;
    }
    public void openCmaeraAll(){
        logger.info("访问所有摄像头调用接口{}", url);
        HttpClient client = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        try {
            HttpResponse httpResponse = client.execute(httpget);

            String result= EntityUtils.toString(httpResponse
                    .getEntity());
            JSONObject resultObject = JSONObject.parseObject(result);
            int code = resultObject.getInteger("code");
            logger.info("返回值:{}",resultObject);

            if (code == 0) {
                JSONArray data = resultObject.getJSONArray("data");
                for (int i = 0; i < data.size(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    logger.info("摄像头id{}ip{}",object.getString("cameraId"),object.getString("privateIp"));
                    String url =messageReceiver.cameraPrev.trim()+object.getString("privateIp")+messageReceiver.cameraAfter.trim();
                    this.startLiveCamera(url,"c"+object.getString("cameraId"));
                }
            }else{
                logger.error("接口返回值有误");
            }

        } catch (IOException e) {
            logger.error("访问接口异常");
            e.printStackTrace();
        }
    }

    private String[] getCmd(String camera, String orderId) {
        String str = "ffmpeg " + videoTransport + " -i " + camera
                + " " + videoParams + " "
                + nginxServer + orderId;
        logger.info("execute cmd: {}", str);
        return str.split(" ");
//        return ("ffmpeg -re -i /Users/chenlichao/Desktop/test.mp4 -vcodec libx264 -vprofile baseline -acodec aac -ar 44100 -strict -2 -ac -1 -f flv -s 640*480 -q 10 rtmp://centos7:1935/hls/" + orderId).split(" ");
    }

    public void  testCamera()  {
        List list = new ArrayList();
        list.add("192.168.10.41");
        list.add("192.168.10.42");
        list.add("192.168.10.43");
        list.add("192.168.10.44");
        for(int i=0;i<list.size();i++){
             try {
                String url =messageReceiver.cameraPrev.trim()+list.get(i).toString()+messageReceiver.cameraAfter.trim();
                this.startLiveCamera(url,"c"+i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 本地进程信息
     */
    private class ProcessHolder implements Runnable{
        Process process;
        String camera;
        String orderId;
        Boolean started = false;
        ProcessHolder(String camera, String orderId) {
            this.camera = camera;
            this.orderId = orderId;
        }

        @Override
        public void run() {
            ProcessBuilder processBuilder = new ProcessBuilder(getCmd(camera,orderId));
            File outFile = new File("/tmp/video/" + orderId + ".log");
            processBuilder.redirectError(outFile);
            logger.info("启动视频直播:[order:{}][{}] ...",orderId, camera);
            try {
                process = processBuilder.start();
                if(process.waitFor(120, TimeUnit.SECONDS)) {
                    int resultCode = process.exitValue();
                    if (resultCode != 0) {
                        logger.error("视频直播进程[order:{}][{}]，异常退出[{}]！", orderId, camera, resultCode);
                    } else {
                        logger.info("视频直播进程[order:{}][{}]，正常执行结束！", orderId, camera);
                    }
                    runningProcesses.remove(orderId);
                    return;
                }
                started = true;
                logger.info("视频直播进程 [order:{}][{}] 启动成功！", orderId, camera);
                int resultCode = process.waitFor();
                if (resultCode != 0) {
                    logger.error("视频直播进程[order:{}][{}]，异常退出, 退出码：{}!", orderId, camera, resultCode);
                } else {
                    logger.info("视频直播进程[order:{}][{}]，正常执行结束！", orderId, camera);
                }
                runningProcesses.remove(orderId);
            } catch (IOException e) {
                logger.error("视频直播进程 [order:{}][{}] 启动失败！", orderId, camera, e);
            } catch (InterruptedException e) {
                logger.error("线程意外中断！", e);
            }
        }

        public boolean closeProcess() {
            logger.info("停止视频直播[order:{}][{}]...", orderId, camera);
            OutputStream os = process.getOutputStream();
            if(os != null) {
                try {
                    os.write("q".getBytes());
                    os.flush();
                    return process.waitFor(30, TimeUnit.SECONDS) || forceDestroy();
                } catch (IOException e) {
                    logger.error("正常结束视频直播进程[order:{}][{}]失败，强制结束!", orderId, camera);
                    return forceDestroy();
                } catch (InterruptedException e) {
                    logger.error("等待视频直播进程结束时出错！", e);
                    return forceDestroy();
                }
            } else {
                return forceDestroy();
            }
        }

        private boolean forceDestroy() {
            process.destroyForcibly();
            try {
                if(!process.waitFor(30, TimeUnit.SECONDS)) {
                    logger.error("强制停止失败，请联系管理员！！！");
                    return false;
                }
            } catch (InterruptedException e1) {
                logger.error("强制停止视频直播时出错！", e1);
                return false;
            }
            return true;
        }
    }

}
