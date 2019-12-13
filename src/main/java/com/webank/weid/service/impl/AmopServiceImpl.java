/*
 *       Copyright© (2018) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.wedpr.selectivedisclosure.CredentialTemplateEntity;
import com.webank.wedpr.selectivedisclosure.IssuerClient;
import com.webank.wedpr.selectivedisclosure.IssuerResult;
import com.webank.wedpr.selectivedisclosure.UserClient;
import com.webank.wedpr.selectivedisclosure.UserResult;
import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.protocol.amop.GetEncryptKeyArgs;
import com.webank.weid.protocol.amop.GetPolicyAndChallengeArgs;
import com.webank.weid.protocol.amop.GetPolicyAndPreCredentialArgs;
import com.webank.weid.protocol.amop.IssueCredentialArgs;
import com.webank.weid.protocol.amop.RequestIssueCredentialArgs;
import com.webank.weid.protocol.amop.RequestSignCredentialArgs;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PolicyAndChallenge;
import com.webank.weid.protocol.base.PolicyAndPreCredential;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.request.CreateCredentialPojoArgs;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.GetEncryptKeyResponse;
import com.webank.weid.protocol.response.GetPolicyAndChallengeResponse;
import com.webank.weid.protocol.response.PolicyAndPreCredentialResponse;
import com.webank.weid.protocol.response.RequestIssueCredentialResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.SignCredentialResponse;
import com.webank.weid.rpc.AmopService;
import com.webank.weid.rpc.CptService;
import com.webank.weid.rpc.CredentialPojoService;
import com.webank.weid.rpc.callback.AmopCallback;
import com.webank.weid.service.BaseService;
import com.webank.weid.service.fisco.WeServer;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.service.impl.callback.RequesSignCredentialCallback;
import com.webank.weid.suite.api.persistence.Persistence;
import com.webank.weid.suite.persistence.sql.driver.MysqlDriver;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.JsonUtil;


/**
 * Created by Junqi Zhang on 2019/4/10.
 */
public class AmopServiceImpl extends BaseService implements AmopService {

    private static final Logger logger = LoggerFactory.getLogger(AmopServiceImpl.class);

    private static CptService cptService = new CptServiceImpl();
    
    /**
     * persistence service.
     */
    private static Persistence dataDriver = new MysqlDriver();
    
    /**
     * credentialpojo service
     */
    private static CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();

    @Override
    public ResponseData<PolicyAndChallenge> getPolicyAndChallenge(
        String targetOrgId,
        Integer policyId,
        String targetUserWeId
    ) {
        try {
            if (StringUtils.isBlank(fiscoConfig.getCurrentOrgId())) {
                logger.error("the orgId is null, policyId = {}", policyId);
                return new ResponseData<PolicyAndChallenge>(null, ErrorCode.ILLEGAL_INPUT);
            }
            GetPolicyAndChallengeArgs args = new GetPolicyAndChallengeArgs();
            args.setFromOrgId(fiscoConfig.getCurrentOrgId());
            args.setToOrgId(targetOrgId);
            args.setPolicyId(String.valueOf(policyId));
            args.setMessageId(super.getSeq());
            args.setTargetUserWeId(targetUserWeId);
            ResponseData<GetPolicyAndChallengeResponse> retResponse =
                this.getPolicyAndChallenge(targetOrgId, args);
            if (retResponse.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                logger.error("AMOP response fail, policyId={}, errorCode={}, errorMessage={}",
                    policyId,
                    retResponse.getErrorCode(),
                    retResponse.getErrorMessage()
                );
                return new ResponseData<PolicyAndChallenge>(
                    null,
                    ErrorCode.getTypeByErrorCode(retResponse.getErrorCode().intValue())
                );
            }
            GetPolicyAndChallengeResponse result = retResponse.getResult();
            ErrorCode errorCode =
                ErrorCode.getTypeByErrorCode(result.getErrorCode().intValue());
            return new ResponseData<PolicyAndChallenge>(result.getPolicyAndChallenge(), errorCode);
        } catch (Exception e) {
            logger.error("getPresentationPolicy failed due to system error. ", e);
            return new ResponseData<PolicyAndChallenge>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    private ResponseData<GetPolicyAndChallengeResponse> getPolicyAndChallenge(
        String toOrgId,
        GetPolicyAndChallengeArgs args) {
        return this.getImpl(
            fiscoConfig.getCurrentOrgId(),
            toOrgId,
            args,
            GetPolicyAndChallengeArgs.class,
            GetPolicyAndChallengeResponse.class,
            AmopMsgType.GET_POLICY_AND_CHALLENGE,
            WeServer.AMOP_REQUEST_TIMEOUT
        );
    }

    /**
     * 发送普通消息的AMOP请求接口.
     */
    public ResponseData<AmopResponse> request(String toOrgId, AmopCommonArgs args) {
        return this.getImpl(
            fiscoConfig.getCurrentOrgId(),
            toOrgId,
            args,
            AmopCommonArgs.class,
            AmopResponse.class,
            AmopMsgType.TYPE_TRANSPORTATION,
            WeServer.AMOP_REQUEST_TIMEOUT
        );
    }

    /**
     *  通过AMOP获取秘钥请求接口.
     */
    public ResponseData<GetEncryptKeyResponse> getEncryptKey(String toOrgId,
        GetEncryptKeyArgs args) {
        return this.getImpl(
            fiscoConfig.getCurrentOrgId(),
            toOrgId,
            args,
            GetEncryptKeyArgs.class,
            GetEncryptKeyResponse.class,
            AmopMsgType.GET_ENCRYPT_KEY,
            WeServer.AMOP_REQUEST_TIMEOUT
        );
    }

    /**
     * 注册回调函数接口.
     */
    public void registerCallback(Integer directRouteMsgType, AmopCallback directRouteCallback) {
        super.getPushCallback().registAmopCallback(directRouteMsgType, directRouteCallback);
    }

	/* (non-Javadoc)
	 * @see com.webank.weid.rpc.AmopService#requestPolicyAndPreCredential(java.lang.String, java.lang.Integer, java.lang.String)
	 */
	public ResponseData<PolicyAndPreCredentialResponse> requestPolicyAndPreCredential(
		String toOrgId,
		GetPolicyAndPreCredentialArgs args) {

		return this.getImpl(
	        fiscoConfig.getCurrentOrgId(),
	        toOrgId,
	        args,
	        GetPolicyAndPreCredentialArgs.class,
	        PolicyAndPreCredentialResponse.class,
	        AmopMsgType.GET_POLICY_AND_PRE_CREDENTIAL,
	        WeServer.AMOP_REQUEST_TIMEOUT
	        );
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.rpc.AmopService#requestIssueCredential(java.lang.String, com.webank.weid.protocol.amop.RequestIssueCredentialArgs)
	 */
	@Override
	public ResponseData<RequestIssueCredentialResponse> requestIssueCredential(
		String toOrgId,
		RequestIssueCredentialArgs args) {

		PolicyAndPreCredential policyAndPreCredential  = args.getPolicyAndPreCredential();
		String claimJson = policyAndPreCredential.getClaim();
		CredentialPojo preCredential = policyAndPreCredential.getPreCredential();
		ResponseData<CredentialPojo> userCredentialResp =
			credentialPojoService.prepareZKPCredential(
				preCredential,
				claimJson,
				args.getAuth());

		CredentialPojo userCredential = userCredentialResp.getResult();
		PolicyAndChallenge policyAndChallenge = policyAndPreCredential.getPolicyAndChallenge();

		List<CredentialPojo>credentialList = new ArrayList<>();
		credentialList.add(preCredential);
		credentialList.add(userCredential);

		ResponseData<PresentationE> presentationResp = 
			credentialPojoService.createPresentation(
				credentialList, 
				policyAndChallenge.getPresentationPolicyE(), 
				policyAndChallenge.getChallenge(), 
				args.getAuth());

		PresentationE presentation = presentationResp.getResult();

		Integer cptId = (Integer) userCredential.getClaim().get("cptId");
		IssueCredentialArgs issueCredentialArgs = new IssueCredentialArgs();
		issueCredentialArgs.setClaim(claimJson);
		issueCredentialArgs.setCptId(cptId);
		issueCredentialArgs.setUserWeId(args.getAuth().getWeId());
		issueCredentialArgs.setPolicyId(args.getPolicyId());
		issueCredentialArgs.setPresentation(presentation);

		//1. request issuer to issue credential
		ResponseData<RequestIssueCredentialResponse> resp =  this.getImpl(
		    fiscoConfig.getCurrentOrgId(),
		    toOrgId,
		    issueCredentialArgs,
		    IssueCredentialArgs.class,
		    RequestIssueCredentialResponse.class,
		    AmopMsgType.REQUEST_SIGN_CREDENTIAL,
		    WeServer.AMOP_REQUEST_TIMEOUT
		    );

		RequestIssueCredentialResponse response = resp.getResult();
		CredentialPojo credentialPojo = response.getCredentialPojo();
		String credentialJson = null;
		try {
			credentialJson = JsonUtil.credentialToMonolayer(credentialPojo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String,String>credentialInfoMap = DataToolUtils.deserialize(credentialJson, HashMap.class);

		ResponseData<CredentialTemplateEntity> resp1 = cptService.queryCredentialTemplate(cptId);
		CredentialTemplateEntity template = resp1.getResult();
		String id = new StringBuffer().append(args.getAuth().getWeId()).append("_").append(cptId).toString();
		ResponseData<String> dbResp = dataDriver.get(DataDriverConstant.DOMAIN_USER_MASTER_SECRET, id);
		if (dbResp.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            throw new DatabaseException("database error!");
        }
		String userInfo = dbResp.getResult();
		Map<String, String>userInfoMap = DataToolUtils.deserialize(userInfo, HashMap.class);
		String masterSecret = userInfoMap.get("masterSecret");
		String credentialSecretsBlindingFactors = userInfoMap.get("credentialSecretsBlindingFactors");
		UserResult userResult = UserClient.blindCredentialSignature(
			response.getCredentialSignature(),  //response.getCredentialSignature()
			credentialInfoMap,   //from credentialPojo
			template, //查链
			masterSecret,   //查数据库
			credentialSecretsBlindingFactors, //查数据库
			response.getIssuerNonce()); //response.getUserNonce();
		String newCredentialSignature = userResult.credentialSignature;

		ResponseData<Integer> dbResponse = 
			dataDriver.saveOrUpdate(
				DataDriverConstant.DOMAIN_USER_CREDENTIAL_SIGNATURE, 
				id, 
				newCredentialSignature);
		if (dbResponse.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            throw new DatabaseException("database error!");
        }
		//return new ResponseData<SignCredentialResponse>(null, ErrorCode.UNKNOW_ERROR);
		//String newCredentialSignature = userResult.credentialSignature;
		return null;
	}

	/* (non-Javadoc)
	 * @see com.webank.weid.rpc.AmopService#requestSignCredential(java.lang.String, com.webank.weid.protocol.amop.RequestIssueCredentialArgs)
	 */
	@Override
	public ResponseData<SignCredentialResponse> requestSignCredential(
		String toOrgId,
		RequestSignCredentialArgs args,
		WeIdAuthentication auth) {

		//1.用户拿到issuer发的credential，先调用makeCredential，生成credentialSignatureRequest、userNonce、masterSecret
		CredentialPojo credential = args.getCredentialPojo();
		Integer cptId = credential.getCptId();
		ResponseData<CredentialTemplateEntity> resp = cptService.queryCredentialTemplate(cptId);
		CredentialTemplateEntity template = resp.getResult();
		UserResult userResult;
		try {
			String credentialInfo = JsonUtil.credentialToMonolayer(credential);
			HashMap<String, String>credentialInfoMap = DataToolUtils.deserialize(credentialInfo, HashMap.class);

			userResult = UserClient.makeCredential(credentialInfoMap, template);
			String credentialSignatureRequest = userResult.credentialSignatureRequest;
			String userNonce = userResult.userNonce;
			String masterSecret = userResult.masterSecret;
			String credentialSecretsBlindingFactors = userResult.credentialSecretsBlindingFactors;

			//2.保存masterSecret，同时将cptId、credentialSignatureRequest、userNonce生成一个credential传给issuer
			//issuer拿到这个credential可以先验证签名，然后将credentialSignatureRequest和userNonce取出，调用signCredential，生成credentialSignature和issuerNonce回传给用户
			ResponseData<Integer> response = dataDriver.update(DataDriverConstant.DOMAIN_USER_MASTER_SECRET, String.valueOf(cptId), masterSecret);
			if (response.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                throw new DatabaseException("database error!");
            }
			CreateCredentialPojoArgs createCredentialPojoArgs = new CreateCredentialPojoArgs();
			Map<String, String>claim = new HashMap<>();
			claim.put("cptId", String.valueOf(cptId));
			claim.put("credentialSignatureRequest", credentialSignatureRequest);
			claim.put("userNonce", userNonce);
			createCredentialPojoArgs.setIssuer(auth.getWeId());
			createCredentialPojoArgs.setWeIdAuthentication(auth);
			createCredentialPojoArgs.setCptId(111);
			createCredentialPojoArgs.setClaim(claim);
			createCredentialPojoArgs.setIssuanceDate(System.currentTimeMillis());
			createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis()+1234445);

			ResponseData<CredentialPojo> credentialResp = credentialPojoService.createCredential(createCredentialPojoArgs);
			if(credentialResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
				return new ResponseData<SignCredentialResponse>(null, ErrorCode.UNKNOW_ERROR);
			}

			args.setCredentialPojo(credentialResp.getResult());
			args.setUserWeId(auth.getWeId());
		
		/**
		 * --------------------------------------------------
		 * 测试代码
		 * -------------------------------------------------
		 */
		RequesSignCredentialCallback callback = new RequesSignCredentialCallback();
		SignCredentialResponse signCredentialResponse  = callback.onPush(args);

		String credentialSignature =  signCredentialResponse.getCredentialSignature();
		String issuerNonce = signCredentialResponse.getIssuerNonce();

		UserResult userResult1 =
            UserClient.blindCredentialSignature(
                credentialSignature,
                    credentialInfoMap,
                    template,
                    masterSecret,
                    credentialSecretsBlindingFactors,
                    issuerNonce);
        String newCredentialSignature = userResult1.credentialSignature;
        ResponseData<Integer> dbResp =
            dataDriver.save(
                DataDriverConstant.DOMAIN_USER_CREDENTIAL_SIGNATURE,
                String.valueOf(cptId),
                newCredentialSignature);
        if (dbResp.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            throw new DatabaseException("database error!");
        }
        return new ResponseData<SignCredentialResponse>(null, ErrorCode.UNKNOW_ERROR);
		/**
		 * --------------------------------------------------------------
		 * 测试代码
		 * --------------------------------------------------------
		 */
		
		//3. 用户拿到issuer的credentialSignature和issuerNonce之后，进行盲化，并生成新的credentialSignature，用于后续生成证据用
//		ResponseData<SignCredentialResponse> signedResponse = 
//			this.getImpl(
//			    fiscoConfig.getCurrentOrgId(),
//			    toOrgId,
//			    args,
//			    RequestSignCredentialArgs.class,
//			    SignCredentialResponse.class,
//			    AmopMsgType.REQUEST_SIGN_CREDENTIAL,
//			    WeServer.AMOP_REQUEST_TIMEOUT
//			    );
//		SignCredentialResponse res = signedResponse.getResult();
//		String credentialSignature =  res.getCredentialSignature();
//		String issuerNonce = res.getIssuerNonce();
//		
//		UserResult userResult1 =
//                UserClient.blindCredentialSignature(
//                        credentialSignature,
//                        credentialInfoMap,
//                        credentialTemplateStorage,
//                        masterSecret,
//                        credentialSecretsBlindingFactors,
//                        issuerNonce);
//		String newCredentialSignature = userResult1.credentialSignature;
//		ResponseData<Integer> dbResp = 
//			dataDriver.save(
//				DataDriverConstant.DOMAIN_USER_CREDENTIAL_SIGNATURE, 
//				String.valueOf(cptId), 
//				newCredentialSignature);
//		if (dbResp.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
//            throw new DatabaseException("database error!");
//        }
//		return signedResponse;
		} catch (IOException e) {
			logger.error("[requestSignCredential] request sign credential with exception.", e);
			return new ResponseData<SignCredentialResponse>(null, ErrorCode.UNKNOW_ERROR);
		}
//		return this.getImpl(
//		    fiscoConfig.getCurrentOrgId(),
//		    toOrgId,
//		    args,
//		    RequestSignCredentialArgs.class,
//		    SignCredentialResponse.class,
//		    AmopMsgType.REQUEST_SIGN_CREDENTIAL,
//		    WeServer.AMOP_REQUEST_TIMEOUT
//		    );

	}
	
	public SignCredentialResponse onPush(RequestSignCredentialArgs args) {

    	SignCredentialResponse result = new SignCredentialResponse();
    	//check input parameters
//    	if(!checkArgs(args)) {
//    		logger.error("[RequesSignCredentialCallback] input args is illegal!");
//    		return result;
//    		
//    	}
    	//verify credential(based on cpt 111)
    	CredentialPojo credential = args.getCredentialPojo();
    	if(credential.getCptId() == 111) {

    		ResponseData<Boolean> verifyResult = credentialPojoService.verify(credential.getIssuer(), credential);
    		if(verifyResult.getResult()) {

                Map<String, Object>claim = credential.getClaim();
                String credentialSignatureRequest = (String) claim.get("credentialSignatureRequest");
                String nonce = (String) claim.get("nonce");
                String userId = args.getUserWeId();
                String cptId = (String) claim.get("cptId");
                ResponseData<CredentialTemplateEntity> cptTemplate = cptService.queryCredentialTemplate(Integer.valueOf(cptId));
                CredentialTemplateEntity credentialTemplate = cptTemplate.getResult();
                ResponseData<String> dataResp = dataDriver.get(DataDriverConstant.DOMAIN_ISSUER_TEMPLATE_SECRET, cptId);
                String templateSecretKey = dataResp.getResult();
                IssuerResult issuerResult  =
    				IssuerClient.signCredential(
    					credentialTemplate,
    				    templateSecretKey,
    				    credentialSignatureRequest,
    				    userId,
    				    nonce);
                String issuerNonce = issuerResult.issuerNonce;
                String credentialSignature = issuerResult.credentialSignature;

                result.setCredentialSignature(credentialSignature);
                result.setIssuerNonce(issuerNonce);
    		}
    	}
    	return result;
    }
}
