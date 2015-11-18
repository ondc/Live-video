package com.dm.service.video.dto;

import com.dm.framework.message.Message;

/**
 * MQ消息体
 * Created by wxd on 2015/9/6.
 */
public class VideoMessage extends Message {

    public static final String KEY = "dm.video.live";

    private Integer shopId;

    private String shopCode;

    private String cameraIp;

    private String orderId;

    private String taskId;

    private VideoAction action;

    public VideoMessage() {
        setKey(KEY);
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getCameraIp() {
        return cameraIp;
    }

    public void setCameraIp(String cameraIp) {
        this.cameraIp = cameraIp;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public VideoAction getAction() {
        return action;
    }

    public void setAction(VideoAction action) {
        this.action = action;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }
}
