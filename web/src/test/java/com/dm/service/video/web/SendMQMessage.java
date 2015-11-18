package com.dm.service.video.web;


import com.dm.framework.message.Sender;
import com.dm.framework.message.impl.DefaultSenderImpl;
import com.dm.framework.message.impl.TopicSenderImpl;
import com.dm.service.video.dto.VideoAction;
import com.dm.service.video.dto.VideoMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * 发送mq消息
 * Created by chenlichao on 15/6/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SendMQMessage.class)
@Configuration
@PropertySource("classpath:application.properties")
public class SendMQMessage {

    private Object lock = new Object();

    @Autowired
    Environment env;

    Sender sender;

    @Before
    public void setUp() throws IOException {
        if(sender == null) {
            synchronized (lock) {
                if (sender == null) {
                    TopicSenderImpl s = new TopicSenderImpl();
                    s.setHost(env.getProperty("system.mq.host"));
                    s.setVhost(env.getProperty("system.mq.vhost"));
                    s.setUser(env.getProperty("system.mq.user"));
                    s.setPwd(env.getProperty("system.mq.pwd"));
                    s.setPort(Integer.valueOf(env.getProperty("system.mq.port")));
                    s.setTopicName(env.getProperty("system.mq.topic"));
                    s.init();
                    sender = s;
                }
            }
        }
    }

    @Test
    public void sendMessage(){
        VideoMessage msg = new VideoMessage();
        msg.setShopId(4);
        msg.setOrderId("105");
        msg.setTaskId("202");
        msg.setAction(VideoAction.START);
        msg.setCameraIp("192.168.10.5");
        sender.send(msg);
//        int index = 1;
//        for(int i=41; i<57; i++) {
//            msg.setCameraIp("192.168.10." + i);
//            msg.setOrderId("c" + index++);
//            sender.send(msg);
//        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
