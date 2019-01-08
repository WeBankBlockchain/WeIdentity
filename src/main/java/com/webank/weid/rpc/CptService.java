/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

package com.webank.weid.rpc;

import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.request.CptMapArgs;
import com.webank.weid.protocol.request.CptStringArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * Service interface for operation on CPT (Claim protocol Type).
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
     * Register a new CPT to the blockchain.
     *
     * @param args the args
     * @return The registered CPT info
     */
    ResponseData<CptBaseInfo> registerCpt(CptStringArgs args);

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
     * @return The updated CPT info
     */
    ResponseData<CptBaseInfo> updateCpt(CptMapArgs args, Integer cptId);

    /**
     * Update the data fields of a registered CPT.
     *
     * @param args the args
     * @return The updated CPT info
     */
    ResponseData<CptBaseInfo> updateCpt(CptStringArgs args, Integer cptId);
}
