

package com.webank.weid.suite.transportation;

import java.math.BigInteger;

import com.webank.weid.service.BaseService;
import org.apache.commons.lang3.StringUtils;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ServiceType;
import com.webank.weid.exception.ProtocolSuiteException;
import com.webank.weid.protocol.amop.GetTransDataArgs;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.suite.api.transportation.params.TransMode;
import com.webank.weid.suite.api.transportation.params.TransType;
import com.webank.weid.suite.entity.TransBaseData;
import com.webank.weid.suite.entity.TransCodeBaseData;
import com.webank.weid.suite.transmission.TransmissionRequest;
import com.webank.weid.util.DataToolUtils;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;

/**
 * 二维码传输协议抽象类定义.
 * @author v_wbgyang
 *
 */
public abstract class AbstractCodeTransportation extends AbstractJsonTransportation {

    protected TransmissionRequest<GetTransDataArgs> buildRequest(
        TransType type, 
        TransCodeBaseData codeData,
        WeIdAuthentication weIdAuthentication
    ) {
        TransmissionRequest<GetTransDataArgs> request = new TransmissionRequest<>();
        request.setAmopId(codeData.getAmopId());
        request.setServiceType(ServiceType.SYS_GET_TRANS_DATA.name());
        request.setWeIdAuthentication(weIdAuthentication);
        request.setArgs(getCodeDataArgs(codeData, weIdAuthentication));
        request.setTransType(type);
        return request;
    }
    
    protected GetTransDataArgs getCodeDataArgs(
        TransCodeBaseData codeData, 
        WeIdAuthentication weIdAuthentication
    ) {
        GetTransDataArgs args = new GetTransDataArgs();
        args.setResourceId(codeData.getId());
        args.setTopic(codeData.getAmopId());
        args.setFromAmopId(fiscoConfig.getAmopId());
        args.setWeId(weIdAuthentication.getWeId());
        args.setClassName(codeData.getClass().getName());
        /*String signValue = DataToolUtils.secp256k1Sign(
            codeData.getId(),
            new BigInteger(weIdAuthentication.getWeIdPrivateKey().getPrivateKey())
        );*/
        //TODO 所有getClient()需要适配V3
        Client client = (Client) getClient();
        SignatureResult signatureResult = DataToolUtils.signToSignature(codeData.getId(), client,
                client.getCryptoSuite().getKeyPairFactory().createKeyPair(new BigInteger(weIdAuthentication.getWeIdPrivateKey().getPrivateKey())));
        String signValue = signatureResult.convertToString();
        args.setSignValue(signValue);
        return args;
    }
    
    /**
     * 根据协议字符串判断协议为下载模式协议还是纯数据模式协议.
     * 
     * @param transString 协议字符串
     * @return 返回TransMode
     */
    protected TransMode getTransMode(String transString) {
        if (StringUtils.isBlank(transString)) {
            throw new ProtocolSuiteException(ErrorCode.TRANSPORTATION_PROTOCOL_STRING_INVALID);
        }
        String[] trans = transString.split(TransBaseData.PARTITION_FOR_SPLIT);
        if (trans.length == 3) {
            return TransMode.DOWNLOAD_MODE;
        }
        return TransMode.DATA_MODE;
    } 
}
