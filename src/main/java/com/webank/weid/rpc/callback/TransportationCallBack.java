package com.webank.weid.rpc.callback;

import org.apache.commons.lang3.StringUtils;

import com.webank.weid.constant.AmopServiceType;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.amop.AmopCommonArgs;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.service.impl.callback.KeyManagerHandle;
import com.webank.weid.service.impl.callback.PresentationHandle;

/**
 * @author tonychen 2019年4月28日
 *
 */
public class TransportationCallBack extends DirectRouteCallback {

	private static KeyManagerHandle keyManagerHandle = new KeyManagerHandle();

	private static PresentationHandle presentationHandle = new PresentationHandle();

	public AmopResponse onPush(AmopCommonArgs args) {

		String result = StringUtils.EMPTY;
		AmopResponse response = new AmopResponse();

		if (AmopServiceType.GET_ENCRYPT_KEY.getTypeId().toString().equals(args.getServiceType())) {
			result = keyManagerHandle.queryKey(args.getMessage());
		} else if (AmopServiceType.GET_POLICY.getTypeId().toString().equals(args.getServiceType())) {
			result = presentationHandle.getPolicyByPolicyId(args.getMessage());
		}
		AmopResponse amopResponse = new AmopResponse();
		amopResponse.setResult(result);
		amopResponse.setErrorCode(ErrorCode.SUCCESS.getCode());
		amopResponse.setErrorMessage(ErrorCode.SUCCESS.getCodeDesc());

		return response;
	}
}
