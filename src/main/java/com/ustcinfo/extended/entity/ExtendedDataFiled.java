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
@Table ( name ="extended_data_filed" , schema = "")
public class ExtendedDataFiled  implements Serializable {

	private static final long serialVersionUID =  2805130582921813583L;

	/**
	 * ID
	 */
	@Id
   	@Column(name = "id" )
	private Long id;

	/**
	 * 与entended_data_entity关联的外键
	 */
   	@Column(name = "ext_entity_id" )
	private Long extEntityId;

	/**
	 * 属性名
	 */
   	@Column(name = "filed_name" )
	private String filedName;

	/**
	 * 属性别名
	 */
   	@Column(name = "filed_alias" )
	private String filedAlias;

	/**
	 * 是否是主表的字段：0为扩展表字段，1为主表字段
	 */
   	@Column(name = "is_main_entity_filed" )
	private Long isMainEntityFiled;

	/**
	 * 字段中文名
	 */
   	@Column(name = "filed_name_zh" )
	private String filedNameZh;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getExtEntityId() {
		return this.extEntityId;
	}

	public void setExtEntityId(Long extEntityId) {
		this.extEntityId = extEntityId;
	}

	public String getFiledName() {
		return this.filedName;
	}

	public void setFiledName(String filedName) {
		this.filedName = filedName;
	}

	public String getFiledAlias() {
		return this.filedAlias;
	}

	public void setFiledAlias(String filedAlias) {
		this.filedAlias = filedAlias;
	}

	public Long getIsMainEntityFiled() {
		return this.isMainEntityFiled;
	}

	public void setIsMainEntityFiled(Long isMainEntityFiled) {
		this.isMainEntityFiled = isMainEntityFiled;
	}

	public String getFiledNameZh() {
		return this.filedNameZh;
	}

	public void setFiledNameZh(String filedNameZh) {
		this.filedNameZh = filedNameZh;
	}

	@Override
	public String toString() {
		return "TpApiConfig{" +
				"id='" + id + '\'' +
				"extEntityId='" + extEntityId + '\'' +
				"filedName='" + filedName + '\'' +
				"filedAlias='" + filedAlias + '\'' +
				"isMainEntityFiled='" + isMainEntityFiled + '\'' +
				"filedNameZh='" + filedNameZh + '\'' +
				'}';
	}

}
