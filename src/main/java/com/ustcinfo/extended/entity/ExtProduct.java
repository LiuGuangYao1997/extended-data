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
 * @Author  idea
 * @Date 2019-08-30 
 */

@Entity
@Table ( name ="ext_product" , schema = "")
public class ExtProduct  implements Serializable {

	private static final long serialVersionUID =  2608037592690174668L;

	/**
	 * 关联主表ID
	 */
   	@Column(name = "main_table_id" )
	private Long mainTableId;

	/**
	 * 生产日期
	 */
   	@Column(name = "production_date" )
	private Date productionDate;

	/**
	 * 生产公司
	 */
   	@Column(name = "producer" )
	private String producer;

	/**
	 * 生产地址
	 */
   	@Column(name = "production_address" )
	private String productionAddress;

	public Long getMainTableId() {
		return this.mainTableId;
	}

	public void setMainTableId(Long mainTableId) {
		this.mainTableId = mainTableId;
	}

	public Date getProductionDate() {
		return this.productionDate;
	}

	public void setProductionDate(Date productionDate) {
		this.productionDate = productionDate;
	}

	public String getProducer() {
		return this.producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public String getProductionAddress() {
		return this.productionAddress;
	}

	public void setProductionAddress(String productionAddress) {
		this.productionAddress = productionAddress;
	}

	@Override
	public String toString() {
		return "TpApiConfig{" +
				"mainTableId='" + mainTableId + '\'' +
				"productionDate='" + productionDate + '\'' +
				"producer='" + producer + '\'' +
				"productionAddress='" + productionAddress + '\'' +
				'}';
	}

}
