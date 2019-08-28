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
@Table ( name ="extended_config_main" , schema = "")
public class ExtendedConfigMain  implements Serializable {

	private static final long serialVersionUID =  5868997933678426817L;

	/**
	 * ID
	 */
	@Id
   	@Column(name = "id" )
	private Long id;

	/**
	 * 实体名
	 */
   	@Column(name = "entity_name" )
	private String entityName;

	/**
	 * 实体别名
	 */
   	@Column(name = "entity_alias" )
	private String entityAlias;

	/**
	 * 是否是主表：0为否，1为是
	 */
   	@Column(name = "is_main_table" )
	private Long isMainTable;

	/**
	 * 主键属性名
	 */
   	@Column(name = "primary_key_filed_name" )
	private String primaryKeyFiledName;

	/**
	 * 当是扩展表时，与主表关联的外键的属性名
	 */
   	@Column(name = "foreign_key_filed_name" )
	private String foreignKeyFiledName;

	/**
	 * 业务代码
	 */
   	@Column(name = "business_code" )
	private String businessCode;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEntityName() {
		return this.entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getEntityAlias() {
		return this.entityAlias;
	}

	public void setEntityAlias(String entityAlias) {
		this.entityAlias = entityAlias;
	}

	public Long getIsMainTable() {
		return this.isMainTable;
	}

	public void setIsMainTable(Long isMainTable) {
		this.isMainTable = isMainTable;
	}

	public String getPrimaryKeyFiledName() {
		return this.primaryKeyFiledName;
	}

	public void setPrimaryKeyFiledName(String primaryKeyFiledName) {
		this.primaryKeyFiledName = primaryKeyFiledName;
	}

	public String getForeignKeyFiledName() {
		return this.foreignKeyFiledName;
	}

	public void setForeignKeyFiledName(String foreignKeyFiledName) {
		this.foreignKeyFiledName = foreignKeyFiledName;
	}

	public String getBusinessCode() {
		return this.businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}

	@Override
	public String toString() {
		return "TpApiConfig{" +
				"id='" + id + '\'' +
				"entityName='" + entityName + '\'' +
				"entityAlias='" + entityAlias + '\'' +
				"isMainTable='" + isMainTable + '\'' +
				"primaryKeyFiledName='" + primaryKeyFiledName + '\'' +
				"foreignKeyFiledName='" + foreignKeyFiledName + '\'' +
				"businessCode='" + businessCode + '\'' +
				'}';
	}

}
