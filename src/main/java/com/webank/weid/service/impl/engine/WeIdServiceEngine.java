/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.service.impl.engine;

import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.response.ResponseData;

/**
 * this service engine calls the contract methods and process blockchain response data on FISCO BCOS
 * 1.3.x or on FISCO BCOS 2.0.
 *
 * @author tonychen 2019年6月21日
 */
public interface WeIdServiceEngine {

    /**
     * call weid contract to create a new weid.
     *
     * @param weAddress identity on blockchain
     * @param publicKey public key of the identity
     * @param privateKey privateKey identity's private key
     * @return result
     */
    ResponseData<Boolean> createWeId(String weAddress, String publicKey, String privateKey);

    /**
     * write attribute to blockchain.
     *
     * @param weAddress identity on blockchain
     * @param attributeKey the key of the attribute
     * @param value the value of the attribute
     * @param privateKey identity's private key
     * @return result
     */
    ResponseData<Boolean> setAttribute(
        String weAddress,
        String attributeKey,
        String value,
        String privateKey
    );

    /**
     * check if the weid exists on blockchain.
     *
     * @param weId the weid of the entity
     * @return result
     */
    ResponseData<Boolean> isWeIdExist(String weId);

    /**
     * get weid document from blockchain.
     *
     * @param weId the entity's weid
     * @return weid document
     */
    ResponseData<WeIdDocument> getWeIdDocument(String weId);
}
