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
@Table(name = "product", schema = "")
public class Product implements Serializable {

    private static final long serialVersionUID = 2583239397180166180L;

    /**
     * ID
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * 产品名
     */
    @Column(name = "name")
    private String name;

    /**
     * 数量
     */
    @Column(name = "amount")
    private Long amount;

    /**
     * 价格
     */
    @Column(name = "price")
    private Long price;

    /**
     * 类型
     */
    @Column(name = "type")
    private String type;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAmount() {
        return this.amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getPrice() {
        return this.price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TpApiConfig{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "amount='" + amount + '\'' +
                "price='" + price + '\'' +
                "type='" + type + '\'' +
                '}';
    }

}
