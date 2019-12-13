package com.webank.weid.protocol.amop;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;
import com.webank.weid.protocol.base.PresentationE;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tonychen 2019年12月4日
 *
 */
@Getter
@Setter
public class IssueCredentialArgs extends AmopBaseMsgArgs{

	private PresentationE presentation;
	
	private Integer cptId;
	
	private String claim;
	
	private String policyId;
	
	private String userWeId;
		
}
