package com.dm.service.video.web.mq;

import com.dm.framework.message.Message;
import com.dm.framework.message.Reciver;
import com.dm.service.video.dto.VideoMessage;
import com.dm.service.video.web.service.LiveCameraService;
import com.dm.service.video.web.service.CameraMapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * MQ消息接收
 * Created by chenlichao on 15/6/18.
 */
@Service
public class MessageReceiver implements Reciver {

    private static final Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

    @Autowired
    private CameraMapService cameraMapService;

    @Autowired
    private LiveCameraService liveCameraService;

    @Value("${camera.prev}")
    public  String cameraPrev;

    @Value("${camera.after}")
    public  String cameraAfter;

    @Value("${shopId}")
    public  Integer shopId;

    public void onMessage(Message msg) {
        logger.debug("处理直播启停消息");
        if(msg instanceof VideoMessage) {
            VideoMessage message = (VideoMessage)msg;
            logger.info("shopId : "+shopId+" message [shopId]"+message.getShopId());
            if(shopId.equals(message.getShopId())){
                logger.info("开始查看本店摄像直播");
                intoShop(message);
            }else{
                logger.info("本店没有此摄像头 意外退出");
                return;
            }

        } else {
            logger.warn("Unknown message type!");
        }
    }

    private void intoShop( VideoMessage message){

        switch (message.getAction()) {

            case START: {
                if(!liveCameraService.isLiving(message.getOrderId())) {
                    // String url = cameraMapService.getCameraUrl(message.getStation());
                    String url = cameraPrev.trim()+message.getCameraIp()+cameraAfter.trim();
                    try {
                        liveCameraService.startLiveCamera(url,message.getOrderId());
                    } catch (IOException e) {
                        logger.error("启动【{}】直播失败！", url);
                        logger.error("", e);
                    }
                }
                break;
            }
            case FINISH:
                String url = liveCameraService.getCamera(message.getOrderId());
                try {
                    liveCameraService.closeLiveCamera(message.getOrderId());
                } catch (IOException e) {
                    logger.error("关闭【{}】直播失败！", url);
                    logger.error("", e);
                }
                break;
            case PAUSE:
                try {
                    liveCameraService.closeLiveCamera(message.getOrderId());
                } catch (IOException e) {
                    logger.error("停止【{}】直播失败！", message.getOrderId());
                    logger.error("", e);
                }
                break;
            default:
                logger.warn("Unknown status message!");
        }
    }

}
