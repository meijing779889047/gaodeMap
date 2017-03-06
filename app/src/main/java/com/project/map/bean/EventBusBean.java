package com.project.map.bean;

/**
 * Copyright (C) 2017,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：GaoDeMap
 * 类描述：存储eventBus中的数据
 * 创建人：Administrator
 * 创建时间：2017/3/6 11:15
 * 修改人：Administrator
 * 修改时间：2017/3/6 11:15
 * 修改备注：
 * Version:  1.0.0
 */
public class EventBusBean {

    private int        position;//位置
    private String     type;//操作类型
    private Object     data;//数据

    public EventBusBean(int position, String type, Object data) {
        this.position = position;
        this.type = type;
        this.data = data;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
