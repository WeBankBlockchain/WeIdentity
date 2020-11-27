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

import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.response.ResponseData;

/**
 * Service inf for operation on Policy on blockchain (Claim protocol Type).
 *
 * @author junqizhang 2020.8
 */
public interface PolicyService {

    /**
     * Register Claim Policy on blockchain and assign it under a CPT ID.
     *
     * @param cptId CPT ID
     * @param policyJson Policy Json file
     * @param weIdAuthentication WeID auth
     * @return claimPolicyId the Claim policy ID on-chain
     */
    ResponseData<Integer> registerClaimPolicy(Integer cptId, String policyJson,
        WeIdAuthentication weIdAuthentication);

    /**
     * Get Claim Policy Json from blockchain given a policy ID.
     *
     * @param policyId the Claim Policy ID on-chain
     * @return the claim Json
     */
    ResponseData<ClaimPolicy> getClaimPolicy(Integer policyId);

    /**
     * Get all claim policies from this CPT ID.
     *
     * @param cptId cpt id
     * @return claim policies list
     */
    ResponseData<List<Integer>> getClaimPoliciesFromCpt(Integer cptId);

    /**
     * Register Presentation Policy which contains a number of claim policies.
     *
     * @param claimPolicyIdList claim policies list
     * @param weIdAuthentication weid auth
     * @return the presentation policy id
     */
    ResponseData<Integer> registerPresentationPolicy(List<Integer> claimPolicyIdList,
        WeIdAuthentication weIdAuthentication);

    /**
     * Get Presentation policies under this id from chain.
     *
     * @param presentationPolicyId presentation policy id
     * @return the full presentation policy
     */
    ResponseData<PresentationPolicyE> getPresentationPolicy(Integer presentationPolicyId);

    /**
     * Get all claim policies from chain.
     *
     * @param startPos start position
     * @param num batch number
     * @return claim policy list
     */
    ResponseData<List<Integer>> getAllClaimPolicies(Integer startPos, Integer num);
    
    /**
     * Get Policy count.
     *
     * @return the Policy count
     */
    ResponseData<Integer> getPolicyCount();
}
