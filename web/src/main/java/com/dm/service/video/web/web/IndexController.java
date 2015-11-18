package com.dm.service.video.web.web;

import com.dm.service.video.dto.VideoAction;
import com.dm.service.video.dto.VideoMessage;
import com.dm.service.video.web.mq.MessageReceiver;
import com.dm.service.video.web.service.LiveCameraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletRequest;

/**
 *
 * Created by chenlichao on 15/6/19.
 */
@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    LiveCameraService liveCameraService;

    @Autowired
    MessageReceiver messageReceiver;

    @Value("${shopIp}")
    public  String  shopIp;

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @RequestMapping("/play/{orderId}")
    public String play(@PathVariable("orderId") String orderId, HttpServletRequest request) {
        if(liveCameraService.isLiving(orderId)) {
            request.setAttribute("orderId", orderId);
            request.setAttribute("shopIp", shopIp);
            return "player";
        } else {
            return "no-camera";
        }
    }

    @RequestMapping("/camera/{cameraId}")
    public String camera(@PathVariable("cameraId") String cameraId, HttpServletRequest request) {
        if(liveCameraService.isLiving("c"+cameraId)) {
            request.setAttribute("cameraId", cameraId);
            request.setAttribute("shopIp", shopIp);
            return "camera";
        } else {
            return "no-camera";
        }
    }

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        System.out.print("开始");
        System.out.print("url 之前 "+messageReceiver.cameraPrev);
        System.out.print("url 之后 " + messageReceiver.cameraAfter);
        System.out.print("------" + messageReceiver.shopId);
        VideoMessage msg = new VideoMessage();
        msg.setShopId(123);
        msg.setOrderId("321");
        msg.setTaskId("232");
        msg.setCameraIp("192.168.10.45");
        msg.setAction(VideoAction.START);
        msg.getCameraIp();
        return shopIp;
    }
}
