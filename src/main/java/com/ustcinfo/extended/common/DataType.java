package com.ustcinfo.extended.common;

/**
 * @Author: liu.guangyao@ustcinfo.com
 * @Date: 2019/8/27 16:35
 */
public enum DataType {

    USER_INFO_SELECT_1("USER_INFO_SELECT_1", "用户个人信息查询1","1"),
    PRODUCT_INFO_SELECT_1("PRODUCT_INFO_SELECT_1", "产品信息查询1", "2");

    private String key;
    private String describe;
    private String code;

    private DataType(String key, String describe, String code){
        this.key = key;
        this.describe = describe;
        this.code = code;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }}
