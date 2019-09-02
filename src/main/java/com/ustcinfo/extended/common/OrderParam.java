package com.ustcinfo.extended.common;

/**
 * @Author: liu.guangyao@ustcinfo.com
 * @Date: 2019/9/2 11:04
 */
public class OrderParam {
    private String filed;
    private String order;

    public OrderParam(String filed, String order) {
        this.filed = filed;
        this.order = order;
    }

    public String getFiled() {
        return filed;
    }

    public void setFiled(String filed) {
        this.filed = filed;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "OrderParam{" +
                "filed='" + filed + '\'' +
                ", order='" + order + '\'' +
                '}';
    }
}
