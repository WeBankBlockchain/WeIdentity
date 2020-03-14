package com.webank.weid.protocol.amop;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;
import com.webank.weid.protocol.base.Challenge;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tonychen 2020年3月10日
 *
 */
@Getter
@Setter
public class GetWeIdAuthArgs extends AmopBaseMsgArgs{

	/**
	 * self weId
	 */
	private String weId;
	
	/**
	 * the challenge
	 */
	private Challenge challenge;
	
	/**
	 * 0:single, 1:mutual
	 */
	private Integer type;
}
