package com.ustcinfo.extended.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Description
 * @Author idea
 * @Date 2019-08-28
 */

@Entity
@Table(name = "ext_user", schema = "")
public class ExtUser implements Serializable {

    private static final long serialVersionUID = 1756187628659605375L;

    /**
     * ID
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * 关联主表ID
     */
    @Column(name = "main_table_id")
    private Long mainTableId;

    /**
     * 地址
     */
    @Column(name = "address")
    private String address;

    /**
     * 电话
     */
    @Column(name = "tel")
    private String tel;

    /**
     * 邮箱
     */
    @Column(name = "email")
    private String email;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMainTableId() {
        return this.mainTableId;
    }

    public void setMainTableId(Long mainTableId) {
        this.mainTableId = mainTableId;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return this.tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "TpApiConfig{" +
                "id='" + id + '\'' +
                "mainTableId='" + mainTableId + '\'' +
                "address='" + address + '\'' +
                "tel='" + tel + '\'' +
                "email='" + email + '\'' +
                '}';
    }

}
