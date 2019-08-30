package com.ustcinfo.extended.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import java.io.Serializable;

/**
 * @Description  
 * @Author  idea
 * @Date 2019-08-30 
 */

@Entity
@Table ( name ="ext_user" , schema = "")
public class ExtUser  implements Serializable {

	private static final long serialVersionUID =  801566745466732046L;

	/**
	 * 关联主表ID
	 */
	@Id
   	@Column(name = "main_table_id" )
	private Long mainTableId;

	/**
	 * 地址
	 */
   	@Column(name = "address" )
	private String address;

	/**
	 * 电话
	 */
   	@Column(name = "tel" )
	private String tel;

	/**
	 * 邮箱
	 */
   	@Column(name = "email" )
	private String email;

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
				"mainTableId='" + mainTableId + '\'' +
				"address='" + address + '\'' +
				"tel='" + tel + '\'' +
				"email='" + email + '\'' +
				'}';
	}

}
