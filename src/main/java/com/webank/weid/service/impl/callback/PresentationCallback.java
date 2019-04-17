package com.webank.weid.service.impl.callback;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.amop.CheckDirectRouteMsgHealthArgs;
import com.webank.weid.protocol.base.PolicyAndChellenge;
import com.webank.weid.protocol.response.DirectRouteNotifyMsgResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.callback.DirectRouteCallback;
import com.webank.weid.suite.entity.EncodeType;
import com.webank.weid.suite.transportation.json.JsonTransportation;
import com.webank.weid.suite.transportation.json.JsonTransportationService;
import com.webank.weid.suite.transportation.json.protocol.JsonProtocolProperty;

/**
 * 用于处理机构根据policyId获取policy的回调.
 * 
 * @author v_wbgyang
 *
 */
public class PresentationCallback extends DirectRouteCallback {
    
    private static final Logger logger = 
            LoggerFactory.getLogger(PresentationCallback.class);

    private JsonTransportation jsonTransportationService = new JsonTransportationService();
    
    private PresentationPolicyService policyService;
    
    private static final int SUCCESS = 100;
    
    private static final int NO_POLICY_SERVICE_CODE = 101;
    
    private static final int TRANSPORTATION_FAIL_CODE = 102;
    
    @Override
    public DirectRouteNotifyMsgResult onPush(CheckDirectRouteMsgHealthArgs arg) {
        logger.info("PresentationCallback param:{}", arg);
        DirectRouteNotifyMsgResult result = new  DirectRouteNotifyMsgResult();
        result.setMessage(StringUtils.EMPTY);
        if (policyService == null) {
            logger.error("PresentationCallback policyService is null");
            result.setErrorCode(NO_POLICY_SERVICE_CODE);
            return result;
        }
        PolicyAndChellenge policyAndChellenge = policyService.obtainPolicy(arg.getMessage());
        ResponseData<String> policyResponse =
            jsonTransportationService.serialize(
                policyAndChellenge, 
                new JsonProtocolProperty(EncodeType.ORIGINAL)
            );
        if (policyResponse.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()) {
            result.setErrorCode(SUCCESS);
            result.setMessage(policyResponse.getResult());
        } else {
            logger.error(
                "PresentationCallback Json serialize fail:{}-{}", 
                policyResponse.getErrorCode(), 
                policyResponse.getErrorMessage()
            );
            result.setErrorCode(TRANSPORTATION_FAIL_CODE);
        }
        return result;
    }
    
    public void registPolicyService(PresentationPolicyService policyService) {
        this.policyService = policyService;
    }
}
