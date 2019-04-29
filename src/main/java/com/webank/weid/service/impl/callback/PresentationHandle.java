package com.webank.weid.service.impl.callback;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.base.PolicyAndChellenge;
import com.webank.weid.protocol.response.HandleEntity;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.entity.EncodeType;
import com.webank.weid.suite.transportation.json.JsonTransportation;
import com.webank.weid.suite.transportation.json.JsonTransportationService;
import com.webank.weid.suite.transportation.json.protocol.JsonProtocolProperty;
import com.webank.weid.util.DataToolUtils;

/**
 * 用于处理机构根据policyId获取policy的回调.
 * 
 * @author v_wbgyang
 *
 */
public class PresentationHandle {
    
    private static final Logger logger = 
            LoggerFactory.getLogger(PresentationHandle.class);

    private static JsonTransportation jsonTransportationService = new JsonTransportationService();
    
    private static PresentationPolicyService policyService;

    public String getPolicyByPolicyId(String policyId) {
        logger.info("PresentationCallback param:{}", policyId);
        HandleEntity entity = new HandleEntity();
        entity.setResult(StringUtils.EMPTY);
        if (policyService == null) {
            logger.error("PresentationCallback policyService is null");
            entity.setErrorCode(ErrorCode.POLICY_SERVICE_NOT_EXISTS.getCode());
            entity.setErrorMessage(ErrorCode.POLICY_SERVICE_NOT_EXISTS.getCodeDesc());
            return DataToolUtils.serialize(entity);
        }
        PolicyAndChellenge policyAndChellenge;
        try {
            policyAndChellenge = policyService.obtainPolicy(policyId);
        } catch (Exception e) {
            logger.error("the policy service call fail, please check the error log.", e);
            entity.setErrorCode(ErrorCode.POLICY_SERVICE_CALL_FAIL.getCode());
            entity.setErrorMessage(ErrorCode.POLICY_SERVICE_CALL_FAIL.getCodeDesc());
            return DataToolUtils.serialize(entity);
        }
        ResponseData<String> policyResponse =
            jsonTransportationService.serialize(
                policyAndChellenge, 
                new JsonProtocolProperty(EncodeType.ORIGINAL)
            );
        if (policyResponse.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            logger.error(
                    "PresentationCallback Json serialize fail:{}-{}", 
                    policyResponse.getErrorCode(), 
                    policyResponse.getErrorMessage()
                );
        }
        entity.setErrorCode(policyResponse.getErrorCode());
        entity.setErrorMessage(policyResponse.getErrorMessage());
        entity.setResult(policyResponse.getResult());
        return DataToolUtils.serialize(entity);
    }
    
    public static void registPolicyService(PresentationPolicyService service) {
        policyService = service;
    }
}
