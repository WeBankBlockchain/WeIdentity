package com.webank.weid.constant;

/**
 * @author tonychen 2019年4月26日
 *
 */
public enum AmopServiceType {

	GET_ENCRYPT_KEY(1),
	
	GET_POLICY(2);
	
	private Integer typeId;
	
	public Integer getTypeId() {
		return typeId;
	}
	
	AmopServiceType(Integer typeId){
		this.typeId = typeId;
	}
}
