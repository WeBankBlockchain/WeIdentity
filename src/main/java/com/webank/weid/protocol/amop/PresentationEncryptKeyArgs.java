package com.webank.weid.protocol.amop;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tonychen 2019年4月16日
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PresentationEncryptKeyArgs extends DirectRouteBaseMsgArgs{

	/*
	 * request message
	 */
	private String message;
}
