/*
 *       Copyright© (2019) WeBank Co., Ltd.
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