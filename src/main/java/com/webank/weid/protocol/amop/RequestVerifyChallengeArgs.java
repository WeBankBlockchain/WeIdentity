package com.webank.weid.protocol.amop;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;
import com.webank.weid.protocol.base.Challenge;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tonychen 2020年3月12日
 *
 */
@Getter
@Setter
public class RequestVerifyChallengeArgs extends AmopBaseMsgArgs{

	private String channelId;
	private String selfWeId;
	private Challenge challenge;
	private String signData;
}
