

package com.webank.weid.service.rpc;

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
