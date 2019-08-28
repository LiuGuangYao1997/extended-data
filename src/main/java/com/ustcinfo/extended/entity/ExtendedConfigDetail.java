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
 * @Date 2019-08-28 
 */

@Entity
@Table ( name ="extended_config_detail" , schema = "")
public class ExtendedConfigDetail  implements Serializable {

	private static final long serialVersionUID =  6224753818907113049L;

	/**
	 * ID
	 */
	@Id
   	@Column(name = "id" )
	private Long id;

	/**
	 * 实体属性名
	 */
   	@Column(name = "entity_filed_name" )
	private String entityFiledName;

	/**
	 * 实体属性别名(返回的map key)
	 */
   	@Column(name = "entity_filed_alias" )
	private String entityFiledAlias;

	/**
	 * 配置主表ID，外键
	 */
   	@Column(name = "extended_main_id" )
	private Long extendedMainId;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEntityFiledName() {
		return this.entityFiledName;
	}

	public void setEntityFiledName(String entityFiledName) {
		this.entityFiledName = entityFiledName;
	}

	public String getEntityFiledAlias() {
		return this.entityFiledAlias;
	}

	public void setEntityFiledAlias(String entityFiledAlias) {
		this.entityFiledAlias = entityFiledAlias;
	}

	public Long getExtendedMainId() {
		return this.extendedMainId;
	}

	public void setExtendedMainId(Long extendedMainId) {
		this.extendedMainId = extendedMainId;
	}

	@Override
	public String toString() {
		return "TpApiConfig{" +
				"id='" + id + '\'' +
				"entityFiledName='" + entityFiledName + '\'' +
				"entityFiledAlias='" + entityFiledAlias + '\'' +
				"extendedMainId='" + extendedMainId + '\'' +
				'}';
	}

}
