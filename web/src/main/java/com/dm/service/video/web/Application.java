package com.dm.service.video.web;

import com.dm.framework.message.Reciver;
import com.dm.framework.message.impl.TopicReciverImpl;
import com.dm.service.video.dto.VideoMessage;
import com.dm.service.video.web.mq.MessageReceiver;
import com.dm.service.video.web.service.LiveCameraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务启动入口
 * Created by chenlichao on 15/6/18.
 */
@SpringBootApplication
@PropertySource("classpath:camera-mapper.properties")
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    LiveCameraService liveCameraService;
    @Bean(initMethod = "start")
    public Reciver reciver(@Value("${system.mq.host}") String host,
                           @Value("${system.mq.vhost}") String vhost,
                           @Value("${system.mq.port}") int port,
                           @Value("${system.mq.user}") String user,
                           @Value("${system.mq.pwd}") String pwd,
                           @Value("${system.mq.queue}") String queueName,
                           @Value("${system.mq.topic}") String topicName,
                           @Value("${system.mq.interesting}") String interesting,
                           MessageReceiver messageReceiver) {
       // this.openCameraAll();
        TopicReciverImpl reciver = new TopicReciverImpl();
        reciver.setHost(host);
        reciver.setVhost(vhost);
        reciver.setPort(port);
        reciver.setUser(user);
        reciver.setPwd(pwd);
        reciver.setDefaultQueueName(queueName);
        reciver.setTopicName(topicName);
        reciver.setInterestingKeys(interesting);
        Map<String, Reciver> map = new HashMap<>();
        map.put(VideoMessage.KEY, messageReceiver);
        reciver.setReciverMap(map);
        return reciver;
    }
    public void openCameraAll(){
        logger.info("服务启动准备开启本店所有摄像头进程");
         liveCameraService.openCmaeraAll();
        //liveCameraService.testCamera();
    }
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
