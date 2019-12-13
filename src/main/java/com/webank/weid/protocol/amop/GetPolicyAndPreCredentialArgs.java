package com.webank.weid.protocol.amop;

import java.util.Map;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tonychen 2019年12月3日
 *
 */
@Getter
@Setter
public class GetPolicyAndPreCredentialArgs extends AmopBaseMsgArgs{

	private String policyId;
	
	private String targetUserWeId;
	
	private Integer cptId;
	
	/**
	 * 用户填入的claim数据
	 */
	private String userInputForm;
	
	private Map<String, String>extra;
}
