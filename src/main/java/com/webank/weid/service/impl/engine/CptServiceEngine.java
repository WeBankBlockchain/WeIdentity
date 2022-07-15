

package com.webank.weid.service.impl.engine;

import java.util.List;

import com.webank.wedpr.selectivedisclosure.CredentialTemplateEntity;

import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.RsvSignature;

/**
 * 针对不同版本的FISCO BCOS，做不同的CPT合约接口调用和数据处理 目前分为支持FISCO BCOS 1.3.x和FISCO BCOS 2.0版本.
 *
 * @author tonychen 2019年6月25日
 */
public interface CptServiceEngine extends ReloadStaticContract {

    /**
     * call cpt contract to update cpt based on cptid.
     *
     * @param cptId cptid
     * @param address publisher's address
     * @param cptJsonSchemaNew cpt content
     * @param rsvSignature signature
     * @param privateKey private key
     * @param dataStorageIndex 0 is cpt, 1 is policy
     * @return result
     */
    ResponseData<CptBaseInfo> updateCpt(
        int cptId,
        String address,
        String cptJsonSchemaNew,
        RsvSignature rsvSignature,
        String privateKey,
        int dataStorageIndex
    );

    /**
     * call cpt contract to register cpt with the specific cptid.
     *
     * @param cptId cptid
     * @param address publisher's address
     * @param cptJsonSchemaNew cpt content
     * @param rsvSignature signature
     * @param privateKey private key
     * @param dataStorageIndex 0 is cpt, 1 is policy
     * @return result
     */
    ResponseData<CptBaseInfo> registerCpt(
        int cptId,
        String address,
        String cptJsonSchemaNew,
        RsvSignature rsvSignature,
        String privateKey,
        int dataStorageIndex
    );

    /**
     * call cpt contract to register cpt.
     *
     * @param address publisher's address
     * @param cptJsonSchemaNew cpt content
     * @param rsvSignature signature
     * @param privateKey private key
     * @param dataStorageIndex 0 is cpt, 1 is policy
     * @return result
     */
    ResponseData<CptBaseInfo> registerCpt(
        String address,
        String cptJsonSchemaNew,
        RsvSignature rsvSignature,
        String privateKey,
        int dataStorageIndex
    );

    /**
     * call cpt contract method to query cpt info from blockchain.
     *
     * @param cptId the id of the cpt
     * @param dataStorageIndex 0 is cpt, 1 is policy
     * @return cpt info
     */
    ResponseData<Cpt> queryCpt(int cptId, int dataStorageIndex);

    /**
     * query cpt credential template.
     *
     * @param cptId the id of the cpt
     * @return Cpt Credential Template
     */
    ResponseData<CredentialTemplateEntity> queryCredentialTemplate(Integer cptId);

    ResponseData<Integer> putPolicyIntoPresentation(List<Integer> policyIdList,
        WeIdPrivateKey weIdPrivateKey);

    ResponseData<PresentationPolicyE> getPolicyFromPresentation(Integer presentationId);

    ResponseData<Integer> putPolicyIntoCpt(Integer cptId, List<Integer> policyIdList,
        WeIdPrivateKey weIdPrivateKey);

    ResponseData<List<Integer>> getPolicyFromCpt(Integer cptId);

    /**
     * 分页查询cptId列表.
     * @param startPos 起始位置
     * @param num 查询数量
     * @param dataStorageIndex 存储类型,0表示CPT,1表示policy
     * @return 返回id列表
     */
    ResponseData<List<Integer>> getCptIdList(int startPos, int num, int dataStorageIndex);
    
    /**
     * 查询cpt或者policy总数.
     * @param dataStorageIndex 存储类型,0表示CPT,1表示policy
     * @return 返回总数
     */
    ResponseData<Integer> getCptCount(int dataStorageIndex);
}
