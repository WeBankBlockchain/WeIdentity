package com.webank.weid.service.impl.engine;

import com.webank.weid.protocol.response.ResponseData;

/**
 * @author tonychen 2019年6月26日
 */
public interface RawTransactionServiceEngine {

    ResponseData<String> createWeId(String transactionHex);

    ResponseData<String> registerAuthorityIssuer(String transactionHex);

    ResponseData<String> registerCpt(String transactionHex);

}
