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

import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.response.ResponseData;
import java.util.List;

/**
 * Service inf for operation on CPT (Claim protocol Type).
 *
 * @author junqizhang
 */
public interface PolicyService {

    /**
     *
     * @param cptId
     * @param policyJson
     * @param weIdAuthentication
     * @return claimPolicyId
     */
    ResponseData<Integer> registerClaimPolicy(Integer cptId, String policyJson, WeIdAuthentication weIdAuthentication);

    //ResponseData<ClaimPolicyBaseInfo> registerClaimPolicy(Integer policyId, Integer cptId, String policyJson, WeIdAuthentication weIdAuthentication);

    ResponseData<ClaimPolicy> getClaimPolicy(Integer policyId);

    ResponseData<Integer> registerPresentationPolicy(List<Integer> ClaimPolicyIdList, WeIdAuthentication weIdAuthentication);

    ResponseData<PresentationPolicyE> getPresentationPolicy(Integer presentationPolicyId);
}
