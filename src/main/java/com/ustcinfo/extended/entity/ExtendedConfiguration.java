package com.ustcinfo.extended.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @Description  
 * @Author  idea
 * @Date 2019-08-27 
 */

@Entity
@Table ( name ="extended_configuration" , schema = "")
public class ExtendedConfiguration  implements Serializable {

	private static final long serialVersionUID =  4183185181352527030L;

	/**
	 * ID
	 */
	@Id
   	@Column(name = "id" )
	private Long id;

	/**
	 * 数据类型(业务描述，枚举类)
	 */
   	@Column(name = "data_type" )
	private String dataType;

	/**
	 * 字段所在表名
	 */
   	@Column(name = "table_name" )
	private String tableName;

	/**
	 * 字段名
	 */
   	@Column(name = "filed_name" )
	private String filedName;

	/**
	 * 实体名
	 */
   	@Column(name = "entity_name" )
	private String entityName;

	/**
	 * 实体字段名
	 */
   	@Column(name = "entity_filed_name" )
	private String entityFiledName;

	/**
	 * 实体字段中文名
	 */
   	@Column(name = "entity_filed_name_zh" )
	private String entityFiledNameZh;

	/**
	 * 关联主表名
	 */
   	@Column(name = "relevant_main_table" )
	private String relevantMainTable;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDataType() {
		return this.dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFiledName() {
		return this.filedName;
	}

	public void setFiledName(String filedName) {
		this.filedName = filedName;
	}

	public String getEntityName() {
		return this.entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getEntityFiledName() {
		return this.entityFiledName;
	}

	public void setEntityFiledName(String entityFiledName) {
		this.entityFiledName = entityFiledName;
	}

	public String getEntityFiledNameZh() {
		return this.entityFiledNameZh;
	}

	public void setEntityFiledNameZh(String entityFiledNameZh) {
		this.entityFiledNameZh = entityFiledNameZh;
	}

	public String getRelevantMainTable() {
		return this.relevantMainTable;
	}

	public void setRelevantMainTable(String relevantMainTable) {
		this.relevantMainTable = relevantMainTable;
	}

	@Override
	public String toString() {
		return "TpApiConfig{" +
				"id='" + id + '\'' +
				"dataType='" + dataType + '\'' +
				"tableName='" + tableName + '\'' +
				"filedName='" + filedName + '\'' +
				"entityName='" + entityName + '\'' +
				"entityFiledName='" + entityFiledName + '\'' +
				"entityFiledNameZh='" + entityFiledNameZh + '\'' +
				"relevantMainTable='" + relevantMainTable + '\'' +
				'}';
	}

}
