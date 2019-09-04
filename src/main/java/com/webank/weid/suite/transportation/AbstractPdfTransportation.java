package com.webank.weid.suite.transportation;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.BaseService;
import com.webank.weid.service.impl.WeIdServiceImpl;
import com.webank.weid.suite.api.transportation.inf.PdfTransportation;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;
import com.webank.weid.util.WeIdUtils;

public abstract class AbstractPdfTransportation
    extends BaseService
    implements PdfTransportation {

    private static final Logger logger =
        LoggerFactory.getLogger(AbstractPdfTransportation.class);
    private static WeIdService weidService = new WeIdServiceImpl();
    private List<String> verifierWeIdList;
    private WeIdAuthentication weIdAuthentication;

    /**
     * 验证协议配置.
     *
     * @param encodeProperty 协议配置实体
     * @return Error Code and Message
     */
    protected ErrorCode checkEncodeProperty(ProtocolProperty encodeProperty) {
        if (encodeProperty == null) {
            return ErrorCode.TRANSPORTATION_PROTOCOL_PROPERTY_ERROR;
        }
        if (encodeProperty.getEncodeType() == null) {
            return ErrorCode.TRANSPORTATION_PROTOCOL_ENCODE_ERROR;
        }
        return ErrorCode.SUCCESS;
    }

    /**
     * 验证WeIdAuthentication有效性.
     *
     * @param weIdAuthentication 身份信息
     * @return Error Code and Message
     */
    protected ErrorCode checkWeIdAuthentication(WeIdAuthentication weIdAuthentication) {
        if (weIdAuthentication == null
            || weIdAuthentication.getWeIdPrivateKey() == null
            || weIdAuthentication.getWeId() == null) {
            return ErrorCode.WEID_AUTHORITY_INVALID;
        }
        if (!WeIdUtils.validatePrivateKeyWeIdMatches(
            weIdAuthentication.getWeIdPrivateKey(),
            weIdAuthentication.getWeId())) {
            return ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH;
        }
        ResponseData<Boolean> isExists = weidService.isWeIdExist(weIdAuthentication.getWeId());
        if (!isExists.getResult()) {
            return ErrorCode.WEID_DOES_NOT_EXIST;
        }
        return ErrorCode.SUCCESS;
    }


    /**
     * 验证wrapper数据.
     *
     * @param obj wrapper数据,作为协议的rawData部分
     * @return Error Code and Message
     */
    protected ErrorCode checkProtocolData(Object obj) {
        if (obj == null) {
            return ErrorCode.TRANSPORTATION_PROTOCOL_DATA_INVALID;
        }
        return ErrorCode.SUCCESS;
    }

    protected List<String> getVerifiers() {
        return verifierWeIdList;
    }

    private void setVerifier(List<String> verifierWeIdList) {
        this.verifierWeIdList = verifierWeIdList;
    }

    @Override
    public PdfTransportation specify(List<String> verifierWeIdList) {
        if (CollectionUtils.isEmpty(verifierWeIdList)) {
            String errorMessage = ErrorCode.ILLEGAL_INPUT.getCode() + "-"
                + ErrorCode.ILLEGAL_INPUT.getCodeDesc();
            logger.error("[specify] {}, the verifiers is null.", errorMessage);
            throw new WeIdBaseException(errorMessage);
        }
        for (String weid : verifierWeIdList) {
            ResponseData<Boolean> isExists = weidService.isWeIdExist(weid);
            if (!isExists.getResult()) {
                String errorMessage = ErrorCode.WEID_DOES_NOT_EXIST.getCode() + "-"
                    + ErrorCode.WEID_DOES_NOT_EXIST.getCodeDesc();
                logger.error("[specify] {} , weid = {} .", errorMessage, weid);
                throw new WeIdBaseException(errorMessage);
            }
        }
        this.setVerifier(verifierWeIdList);
        return this;
    }

    protected Method getFromJsonMethod(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        Method targetMethod = null;
        for (Method method : methods) {
            if (method.getName().equals("fromJson")
                && method.getParameterTypes().length == 1
                && method.getParameterTypes()[0] == String.class) {
                targetMethod = method;
            }
        }
        return targetMethod;
    }

    protected WeIdAuthentication getWeIdAuthentication() {
        return weIdAuthentication;
    }

    protected void setWeIdAuthentication(WeIdAuthentication weIdAuthentication) {
        this.weIdAuthentication = weIdAuthentication;
    }
}
