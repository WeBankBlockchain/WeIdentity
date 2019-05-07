package com.webank.weid.rpc;

import com.webank.weid.protocol.amop.AmopCommonArgs;
import com.webank.weid.protocol.amop.GetEncryptKeyArgs;
import com.webank.weid.protocol.amop.GetPolicyAndChallengeArgs;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.GetEncryptKeyResponse;
import com.webank.weid.protocol.response.GetPolicyAndChallengeResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.callback.DirectRouteCallback;

/**
 * @author tonychen 2019年5月7日
 *
 */
public interface BaseClient {

	public void registerCallback(Integer directRouteMsgType, DirectRouteCallback directRouteCallback);
	
	public ResponseData<GetPolicyAndChallengeResponse> getPolicyAndChallenge(String toOrgId, GetPolicyAndChallengeArgs args);
	
	public ResponseData<GetEncryptKeyResponse> getEncryptKey(String toOrgId, GetEncryptKeyArgs args);
	
	ResponseData<AmopResponse> request(String toOrgId, AmopCommonArgs args);
}
