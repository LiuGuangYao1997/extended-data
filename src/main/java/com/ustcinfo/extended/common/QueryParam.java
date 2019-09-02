package com.ustcinfo.extended.common;

/**
 * @Author: liu.guangyao@ustcinfo.com
 * @Date: 2019/9/2 10:18
 */
public class QueryParam {

    private String filed;
    private String log;
    private String val;

    public QueryParam(String filed, String log, String val) {
        this.filed = filed;
        this.log = log;
        this.val = val;
    }

    public String getFiled() {
        return filed;
    }

    public void setFiled(String filed) {
        this.filed = filed;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "QueryParam{" +
                "filed='" + filed + '\'' +
                ", log='" + log + '\'' +
                ", val='" + val + '\'' +
                '}';
    }
}
