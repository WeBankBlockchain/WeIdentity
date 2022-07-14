

package com.webank.weid.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.RawTransactionService;

/**
 * Service interface for operations on direct transactions on blockchain.
 *
 * @author chaoxinhu 2019.4
 */
public class RawTransactionServiceImpl extends AbstractService implements RawTransactionService {

    private static final Logger logger = LoggerFactory.getLogger(RawTransactionServiceImpl.class);

    /**
     * Create a WeIdentity DID from the provided public key, with preset transaction hex value.
     *
     * @param transactionHex the transaction hex value
     * @return Error message if any
     */
    @Override
    public ResponseData<String> createWeId(String transactionHex) {

        if (StringUtils.isEmpty(transactionHex)) {
            logger.error("WeID transaction error");
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
        }
        return rawEngine.createWeId(transactionHex);
    }

    /**
     * Register a new Authority Issuer on Chain with preset transaction hex value. The inputParam is
     * a Json String, with two keys: WeIdentity DID and Name. Parameters will be ordered as
     * mentioned after validity check; then transactionHex will be sent to blockchain.
     *
     * @param transactionHex the transaction hex value
     * @return true if succeeds, false otherwise
     */
    @Override
    public ResponseData<String> registerAuthorityIssuer(String transactionHex) {
        if (StringUtils.isEmpty(transactionHex)) {
            logger.error("AuthorityIssuer transaction error");
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
        }
        return rawEngine.registerAuthorityIssuer(transactionHex);
    }


    /**
     * Register a new CPT to the blockchain with preset transaction hex value.
     *
     * @param transactionHex the transaction hex value
     * @return The registered CPT info
     */
    public ResponseData<String> registerCpt(String transactionHex) {
        if (StringUtils.isEmpty(transactionHex)) {
            logger.error("CptService transaction error");
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
        }
        return rawEngine.registerCpt(transactionHex);
    }
}