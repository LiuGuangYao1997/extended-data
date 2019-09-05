package com.ustcinfo.extended.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description  
 * @Author  liu.guangyao@ustcinfo.com
 * @Date 2019-09-05 
 */

@Entity
@Table ( name ="ext_user" , schema = "")
public class ExtUser  implements Serializable {

	private static final long serialVersionUID =  3832787880281006967L;

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

	/**
	 * 生日
	 */
   	@Column(name = "birthday" )
	private Date birthday;

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

	public Date getBirthday() {
		return this.birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	@Override
	public String toString() {
		return "TpApiConfig{" +
				"mainTableId='" + mainTableId + '\'' +
				"address='" + address + '\'' +
				"tel='" + tel + '\'' +
				"email='" + email + '\'' +
				"birthday='" + birthday + '\'' +
				'}';
	}

}
