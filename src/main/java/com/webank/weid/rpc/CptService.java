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

package com.webank.weid.rpc;

import java.util.List;

import com.webank.wedpr.selectivedisclosure.CredentialTemplateEntity;

import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.request.CptMapArgs;
import com.webank.weid.protocol.request.CptStringArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * Service inf for operation on CPT (Claim protocol Type).
 *
 * @author lingfenghe
 */
public interface CptService {

    /**
     * Register a new CPT to the blockchain.
     *
     * @param args the args
     * @return The registered CPT info
     */
    ResponseData<CptBaseInfo> registerCpt(CptMapArgs args);

    /**
     * Register a new CPT with a pre-set CPT ID, to the blockchain.
     *
     * @param args the args
     * @param cptId the CPT ID
     * @return The registered CPT info
     */
    ResponseData<CptBaseInfo> registerCpt(CptMapArgs args, Integer cptId);

    /**
     * Register a new CPT to the blockchain.
     *
     * @param args the args
     * @return The registered CPT info
     */
    ResponseData<CptBaseInfo> registerCpt(CptStringArgs args);

    /**
     * Register a new CPT with a pre-set CPT ID, to the blockchain.
     *
     * @param args the args
     * @param cptId the CPT ID
     * @return The registered CPT info
     */
    ResponseData<CptBaseInfo> registerCpt(CptStringArgs args, Integer cptId);

    /**
     * Query the latest CPT version.
     *
     * @param cptId the cpt id
     * @return The registered CPT info
     */
    ResponseData<Cpt> queryCpt(Integer cptId);

    /**
     * Update the data fields of a registered CPT.
     *
     * @param args the args
     * @param cptId the cpt id
     * @return The updated CPT info
     */
    ResponseData<CptBaseInfo> updateCpt(CptMapArgs args, Integer cptId);

    /**
     * Update the data fields of a registered CPT.
     *
     * @param args the args
     * @param cptId the cpt id
     * @return The updated CPT info
     */
    ResponseData<CptBaseInfo> updateCpt(CptStringArgs args, Integer cptId);

    /**
     * Update the data fields of a registered CPT.
     *
     * @param cptId the cpt id
     * @return The updated CPT info
     */
    ResponseData<CredentialTemplateEntity> queryCredentialTemplate(Integer cptId);
    
    /**
     * Get CPTIDS from chain.
     *
     * @param startPos start position
     * @param num batch number
     * @return CPTID list
     */
    ResponseData<List<Integer>> getCptIdList(Integer startPos, Integer num);
    
    /**
     * Get CPT count.
     *
     * @return the cpt count
     */
    ResponseData<Integer> getCptCount();
}
