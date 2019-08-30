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
@Table ( name ="user" , schema = "")
public class User  implements Serializable {

	private static final long serialVersionUID =  5145152777759401255L;

	/**
	 * ID
	 */
	@Id
   	@Column(name = "id" )
	private Long id;

	/**
	 * 用户名
	 */
   	@Column(name = "username" )
	private String username;

	/**
	 * 密码
	 */
   	@Column(name = "password" )
	private String password;

	/**
	 * 年龄
	 */
   	@Column(name = "age" )
	private Long age;

	/**
	 * 性别
	 */
   	@Column(name = "male" )
	private String male;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getAge() {
		return this.age;
	}

	public void setAge(Long age) {
		this.age = age;
	}

	public String getMale() {
		return this.male;
	}

	public void setMale(String male) {
		this.male = male;
	}

	@Override
	public String toString() {
		return "TpApiConfig{" +
				"id='" + id + '\'' +
				"username='" + username + '\'' +
				"password='" + password + '\'' +
				"age='" + age + '\'' +
				"male='" + male + '\'' +
				'}';
	}

}
