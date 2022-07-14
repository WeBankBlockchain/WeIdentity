

package com.webank.weid.rpc.callback;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.amop.CheckAmopMsgHealthArgs;
import com.webank.weid.protocol.amop.GetEncryptKeyArgs;
import com.webank.weid.protocol.amop.GetPolicyAndChallengeArgs;
import com.webank.weid.protocol.amop.GetPolicyAndPreCredentialArgs;
import com.webank.weid.protocol.amop.GetWeIdAuthArgs;
import com.webank.weid.protocol.amop.IssueCredentialArgs;
import com.webank.weid.protocol.amop.RequestVerifyChallengeArgs;
import com.webank.weid.protocol.response.AmopNotifyMsgResult;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.GetEncryptKeyResponse;
import com.webank.weid.protocol.response.GetPolicyAndChallengeResponse;
import com.webank.weid.protocol.response.GetWeIdAuthResponse;
import com.webank.weid.protocol.response.PolicyAndPreCredentialResponse;
import com.webank.weid.protocol.response.RequestIssueCredentialResponse;
import com.webank.weid.protocol.response.RequestVerifyChallengeResponse;
import com.webank.weid.service.impl.base.AmopCommonArgs;

/**
 * Created by junqizhang on 08/07/2017. 业务方需要继承DirectRouteCallback，并实现需要实现的方法.
 */
public class AmopCallback implements PushNotifyAllCallback {

    private static final String MSG_HEALTH = "I am alive!";
    private static final String ERROR_MSG_NO_OVERRIDE =
        "server side have not handle this type of message!";

    @Override
    public AmopNotifyMsgResult onPush(CheckAmopMsgHealthArgs arg) {

        AmopNotifyMsgResult result = new AmopNotifyMsgResult();
        result.setMessage(MSG_HEALTH);
        result.setErrorCode(ErrorCode.SUCCESS.getCode());
        result.setMessage(ErrorCode.SUCCESS.getCodeDesc());
        return result;
    }

    /**
     * 默认针对TYPE_TRANSPORTATION消息的回调处理.
     *
     * @param arg AMOP请求参数
     * @return AMOP相应体
     */
    public AmopResponse onPush(AmopCommonArgs arg) {

        AmopResponse result = new AmopResponse();
        result.setErrorCode(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCode());
        result.setErrorMessage(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCodeDesc());
        return result;
    }

    /**
     * 默认获取秘钥的回调处理.
     *
     * @param arg 获取秘钥需要的参数
     * @return 返回秘钥的响应体
     */
    public GetEncryptKeyResponse onPush(GetEncryptKeyArgs arg) {

        GetEncryptKeyResponse result = new GetEncryptKeyResponse();
        result.setErrorCode(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCode());
        result.setErrorMessage(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCodeDesc());
        return result;
    }

    /**
     * 默认获取PolicyAndChallenge的回调处理.
     *
     * @param arg 获取PolicyAndChallenge需要的参数
     * @return 返回PolicyAndChallenge的响应体
     */
    public GetPolicyAndChallengeResponse onPush(GetPolicyAndChallengeArgs arg) {

        GetPolicyAndChallengeResponse result = new GetPolicyAndChallengeResponse();
        result.setErrorCode(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCode());
        result.setErrorMessage(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCodeDesc());
        return result;
    }

    /**
     * 默认获取PolicyAndChallenge的回调处理.
     *
     * @param args 获取PolicyAndChallenge需要的参数
     * @return 返回PolicyAndChallenge的响应体
     */
    public PolicyAndPreCredentialResponse onPush(GetPolicyAndPreCredentialArgs args) {

        PolicyAndPreCredentialResponse result = new PolicyAndPreCredentialResponse();
        result.setErrorCode(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCode());
        result.setErrorMessage(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCodeDesc());
        return result;
    }

    /**
     * 默认获取PolicyAndChallenge的回调处理.
     *
     * @param args 获取PolicyAndChallenge需要的参数
     * @return 返回PolicyAndChallenge的响应体
     */
    public RequestIssueCredentialResponse onPush(IssueCredentialArgs args) {

        RequestIssueCredentialResponse result = new RequestIssueCredentialResponse();
        result.setErrorCode(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCode());
        result.setErrorMessage(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCodeDesc());
        return result;
    }

    /**
     * 默认获取weIdAuthObj回调.
     *
     * @param args 获取weIdAuthObj需要的参数
     * @return 返回weIdAuthObj的响应体
     */
    public GetWeIdAuthResponse onPush(GetWeIdAuthArgs args) {

        GetWeIdAuthResponse result = new GetWeIdAuthResponse();
        result.setErrorCode(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCode());
        result.setErrorMessage(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCodeDesc());
        return result;
    }

    /**
     * 默认获取weIdAuthObj回调.
     *
     * @param args 获取weIdAuthObj需要的参数
     * @return 返回weIdAuthObj的响应体
     */
    public RequestVerifyChallengeResponse onPush(RequestVerifyChallengeArgs args) {

        RequestVerifyChallengeResponse result = new RequestVerifyChallengeResponse();
        result.setErrorCode(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCode());
        result.setErrorMessage(ErrorCode.AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE.getCodeDesc());
        return result;
    }
}
