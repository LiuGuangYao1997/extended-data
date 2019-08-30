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
@Table ( name ="extended_data_entity" , schema = "")
public class ExtendedDataEntity  implements Serializable {

	private static final long serialVersionUID =  980649498181756634L;

	/**
	 * ID
	 */
	@Id
   	@Column(name = "id" )
	private Long id;

	/**
	 * 主实体名
	 */
   	@Column(name = "main_entity_name" )
	private String mainEntityName;

	/**
	 * 主实体别名
	 */
   	@Column(name = "main_entity_alias" )
	private String mainEntityAlias;

	/**
	 * 主实体主键所在字段名
	 */
   	@Column(name = "main_entity_primarykey" )
	private String mainEntityPrimarykey;

	/**
	 * 扩展实体名
	 */
   	@Column(name = "ext_entity_name" )
	private String extEntityName;

	/**
	 * 扩展实体别名
	 */
   	@Column(name = "ext_entity_alias" )
	private String extEntityAlias;

	/**
	 * 扩展实体外键所在字段名
	 */
   	@Column(name = "ext_entity_foreignkey" )
	private String extEntityForeignkey;

	/**
	 * 数据类型代码
	 */
   	@Column(name = "data_type_code" )
	private String dataTypeCode;

	/**
	 * 数据中文名
	 */
   	@Column(name = "data_name_zh" )
	private String dataNameZh;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMainEntityName() {
		return this.mainEntityName;
	}

	public void setMainEntityName(String mainEntityName) {
		this.mainEntityName = mainEntityName;
	}

	public String getMainEntityAlias() {
		return this.mainEntityAlias;
	}

	public void setMainEntityAlias(String mainEntityAlias) {
		this.mainEntityAlias = mainEntityAlias;
	}

	public String getMainEntityPrimarykey() {
		return this.mainEntityPrimarykey;
	}

	public void setMainEntityPrimarykey(String mainEntityPrimarykey) {
		this.mainEntityPrimarykey = mainEntityPrimarykey;
	}

	public String getExtEntityName() {
		return this.extEntityName;
	}

	public void setExtEntityName(String extEntityName) {
		this.extEntityName = extEntityName;
	}

	public String getExtEntityAlias() {
		return this.extEntityAlias;
	}

	public void setExtEntityAlias(String extEntityAlias) {
		this.extEntityAlias = extEntityAlias;
	}

	public String getExtEntityForeignkey() {
		return this.extEntityForeignkey;
	}

	public void setExtEntityForeignkey(String extEntityForeignkey) {
		this.extEntityForeignkey = extEntityForeignkey;
	}

	public String getDataTypeCode() {
		return this.dataTypeCode;
	}

	public void setDataTypeCode(String dataTypeCode) {
		this.dataTypeCode = dataTypeCode;
	}

	public String getDataNameZh() {
		return this.dataNameZh;
	}

	public void setDataNameZh(String dataNameZh) {
		this.dataNameZh = dataNameZh;
	}

	@Override
	public String toString() {
		return "TpApiConfig{" +
				"id='" + id + '\'' +
				"mainEntityName='" + mainEntityName + '\'' +
				"mainEntityAlias='" + mainEntityAlias + '\'' +
				"mainEntityPrimarykey='" + mainEntityPrimarykey + '\'' +
				"extEntityName='" + extEntityName + '\'' +
				"extEntityAlias='" + extEntityAlias + '\'' +
				"extEntityForeignkey='" + extEntityForeignkey + '\'' +
				"dataTypeCode='" + dataTypeCode + '\'' +
				"dataNameZh='" + dataNameZh + '\'' +
				'}';
	}

}
