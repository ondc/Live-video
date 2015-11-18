package com.dm.service.video.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * 工位摄像头映射服务
 * Created by chenlichao on 15/6/18.
 */
@Service
public class CameraMapService {

    @Autowired
    Environment environment;

    public String getCameraUrl(Integer id) {
        return environment.getProperty("camera." + id);
    }
}
