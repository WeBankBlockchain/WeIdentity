package com.webank.weid.protocol.amop;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tonychen 2019年5月7日
 *
 */
@Getter
@Setter
public class GetEncryptKeyArgs extends DirectRouteBaseMsgArgs{

	/**
	 * 
	 */
	private String keyId;
}
