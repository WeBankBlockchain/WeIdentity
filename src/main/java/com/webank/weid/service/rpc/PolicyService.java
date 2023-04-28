

package com.webank.weid.service.rpc;

import java.util.List;

import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.blockchain.protocol.response.ResponseData;

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
