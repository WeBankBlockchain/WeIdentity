

package com.webank.weid.rpc;

import com.webank.weid.protocol.response.ResponseData;

/**
 * Service interface for operations on direct transactions on blockchain.
 *
 * @author chaoxinhu 2019.5
 */
public interface RawTransactionService {

    /**
     * Create a WeIdentity DID by sending preset transaction hex value to chain.
     *
     * @param transactionHex the transaction hex value
     * @return Error message if any
     */
    ResponseData<String> createWeId(String transactionHex);

    /**
     * Register a new CPT to the blockchain by sending preset transaction hex value.
     *
     * @param transactionHex the transaction hex value
     * @return The registered CPT info
     */
    ResponseData<String> registerCpt(String transactionHex);

    /**
     * Register a new Authority Issuer on Chain by sending preset transaction hex value.
     *
     * @param transactionHex the transaction hex value
     * @return true if succeeds, false otherwise
     */
    ResponseData<String> registerAuthorityIssuer(String transactionHex);
}