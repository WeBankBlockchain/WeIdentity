/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

package com.webank.weid.service.impl.engine;

import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.RsvSignature;

/**
 * 针对不同版本的FISCO BCOS，做不同的CPT合约接口调用和数据处理 目前分为支持FISCO BCOS 1.3.x和FISCO BCOS 2.0版本
 *
 * @author tonychen 2019年6月25日
 */
public interface CptServiceEngine {

    /**
     * call cpt contract to update cpt based on cptid.
     *
     * @param cptId cptid
     * @param address publisher's address
     * @param cptJsonSchemaNew cpt content
     * @param rsvSignature signature
     * @param privateKey private key
     * @return result
     */
    ResponseData<CptBaseInfo> updateCpt(
        int cptId,
        String address,
        String cptJsonSchemaNew,
        RsvSignature rsvSignature,
        String privateKey
    );

    /**
     * call cpt contract to register cpt with the specific cptid.
     *
     * @param cptId cptid
     * @param address publisher's address
     * @param cptJsonSchemaNew cpt content
     * @param rsvSignature signature
     * @param privateKey private key
     * @return result
     */
    ResponseData<CptBaseInfo> registerCpt(
        int cptId,
        String address,
        String cptJsonSchemaNew,
        RsvSignature rsvSignature,
        String privateKey
    );

    /**
     * call cpt contract to register cpt.
     *
     * @param address publisher's address
     * @param cptJsonSchemaNew cpt content
     * @param rsvSignature signature
     * @param privateKey private key
     * @return result
     */
    ResponseData<CptBaseInfo> registerCpt(
        String address,
        String cptJsonSchemaNew,
        RsvSignature rsvSignature,
        String privateKey
    );

    /**
     * call cpt contract method to query cpt info from blockchain.
     *
     * @param cptId the id of the cpt
     * @return cpt info
     */
    ResponseData<Cpt> queryCpt(int cptId);
}
