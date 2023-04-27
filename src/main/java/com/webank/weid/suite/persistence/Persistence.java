

package com.webank.weid.suite.persistence;

import java.util.List;
import java.util.Map;

import com.webank.weid.blockchain.protocol.base.CptBaseInfo;
import com.webank.weid.blockchain.protocol.base.WeIdDocument;
import com.webank.weid.blockchain.protocol.base.WeIdDocumentMetadata;
import com.webank.weid.protocol.request.TransactionArgs;
import com.webank.weid.blockchain.protocol.response.ResponseData;

/**
 * Data access driver.
 *
 * @author tonychen 2019年3月18日
 */
public interface Persistence {

    /**
     * add data to storage.
     *
     * @param domain the domain of the data.
     * @param id the key of the data.
     * @param data which you want to store to the storage.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> add(String domain, String id, String data);

    /**
     * batch add data to storage.
     *
     * @param domain the domain of the data.
     * @param keyValueList list of id-data.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> batchAdd(String domain, Map<String, String> keyValueList);

    /**
     * query data from storage by id.
     *
     * @param domain the domain of the data.
     * @param id the key of the data.
     * @return the data you stored.
     */
    public ResponseData<String> get(String domain, String id);

    /**
     * delete data by id.
     *
     * @param domain the domain of the data.
     * @param id the key of the data.
     * @return the data you stored.
     */
    public ResponseData<Integer> delete(String domain, String id);

    /**
     * update data by id.
     *
     * @param domain the domain of the data.
     * @param id the key you store with.
     * @param data the data you want to update into.
     * @return execute status of the "update" operation.
     */
    public ResponseData<Integer> update(String domain, String id, String data);

    /**
     * add data to storage if not exist, others for update.
     *
     * @param domain the domain of the data.
     * @param id the key of the data.
     * @param data which you want to store to the storage.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> addOrUpdate(String domain, String id, String data);

    /**
     * add transaction to storage.
     *
     * @param transactionArgs the transaction info.
     * @return execute status of the "addTransaction" operation.
     */
    public ResponseData<Integer> addTransaction(TransactionArgs transactionArgs);
    /**
     * add data to storage.
     *
     * @param domain the domain of the data.
     * @param weId the key of the data.
     * @param documentSchema which you want to store to the storage.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> addWeId(String domain, String weId, String documentSchema);
    /**
     * add data to storage.
     *
     * @param domain the domain of the data.
     * @param weId the key of the data.
     * @param documentSchema which you want to store to the storage.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> updateWeId(String domain, String weId, String documentSchema);
    /**
     * query WeIdDocumentMetadata from storage by id.
     *
     * @param domain the domain of the data.
     * @param weId the key of the data.
     * @return the data you stored.
     */
    public ResponseData<WeIdDocument> getWeIdDocument(String domain, String weId);
    /**
     * query WeIdDocumentMetadata from storage by id.
     *
     * @param domain the domain of the data.
     * @param weId the key of the data.
     * @return the data you stored.
     */
    public ResponseData<WeIdDocumentMetadata> getMeta(String domain, String weId);

    /**
     * deactivateWeId by id.
     *
     * @param domain the domain of the data.
     * @param weId the weId you want to deactivate.
     * @param state the state you want to change.
     * @return execute status of the "update" operation.
     */
    public ResponseData<Integer> deactivateWeId(String domain, String weId, Boolean state);

    /**
     * get several weId.
     *
     * @param domain the domain of the data.
     * @param first the first index of weId.
     * @param last the last index of weId.
     * @return execute status of the "update" operation.
     */
    public ResponseData<List<String>> getWeIdList(String domain, Integer first, Integer last);

    /**
     * get total amounts of weId.
     *
     * @param domain the domain of the data.
     * @return execute status of the "update" operation.
     */
    public ResponseData<Integer> getWeIdCount(String domain);

    /**
     * query Cpt from storage by cptId.
     *
     * @param domain the domain of the cpt.
     * @param cptId the cptId.
     * @return the data you stored.
     */
    public ResponseData<CptValue> getCpt(String domain, int cptId);
    /**
     * save Cpt to storage with cptId.
     *
     * @param domain the domain of the cpt.
     * @param cptId the cptId.
     * @param publisher the publisher of the cpt.
     * @param description the description of the cpt.
     * @param cptSchema the cptSchema of the cpt.
     * @param cptSignature the cptSignature of the cpt.
     * @return the data you stored.
     */
    public ResponseData<CptBaseInfo> addCpt(String domain, int cptId, String publisher, String description, String cptSchema, String cptSignature);
    /**
     * query Policy from storage by policyId.
     *
     * @param domain the domain of the Policy.
     * @param policyId the policyId.
     * @return the data you stored.
     */
    public ResponseData<PolicyValue> getPolicy(String domain, int policyId);
    /**
     * save Policy to storage with policyId.
     *
     * @param domain the domain of the Policy.
     * @param policyId the policyId.
     * @param publisher the publisher of the Policy.
     * @param description the description of the Policy.
     * @param cptSchema the cptSchema of the Policy.
     * @param cptSignature the cptSignature of the Policy.
     * @return the data you stored.
     */
    public ResponseData<Integer> addPolicy(String domain, int policyId, String publisher, String description, String cptSchema, String cptSignature);
    /**
     * query presentation from storage by presentationId.
     *
     * @param domain the domain of the presentation.
     * @param presentationId the presentationId.
     * @return the data you stored.
     */
    public ResponseData<PresentationValue> getPresentation(String domain, int presentationId);
    /**
     * save Policy to storage with policyId.
     *
     * @param domain the domain of the presentation.
     * @param presentationId the presentationId.
     * @param creator the creator of the presentation.
     * @param policies the policies of the presentation.
     * @return the data you stored.
     */
    public ResponseData<Integer> addPresentation(String domain, int presentationId, String creator, String policies);
    /**
     * update Cpt to storage with cptId.
     *
     * @param domain the domain of the cpt.
     * @param cptId the cptId.
     * @param cptVersion the cptVersion of the cpt.
     * @param publisher the publisher of the cpt.
     * @param description the description of the cpt.
     * @param cptSchema the cptSchema of the cpt.
     * @param cptSignature the cptSignature of the cpt.
     * @return the data you stored.
     */
    public ResponseData<Integer> updateCpt(String domain, int cptId, int cptVersion, String publisher, String description, String cptSchema, String cptSignature);
    /**
     * save Cpt to storage with cptId.
     *
     * @param domain the domain of the cpt.
     * @param cptId the cptId.
     * @param credentialPublicKey the publicKey of credential template.
     * @param credentialProof the proof of credential template.
     * @return the data you stored.
     */
    public ResponseData<Integer> updateCredentialTemplate(String domain, int cptId, String credentialPublicKey, String credentialProof);
    /**
     * save Cpt to storage with cptId.
     *
     * @param domain the domain of the cpt.
     * @param cptId the cptId.
     * @param policies the policy id list.
     * @return the data you stored.
     */
    public ResponseData<Integer> updateCptClaimPolicies(String domain, int cptId, String policies);
    /**
     * get several cptId.
     *
     * @param domain the domain of the data.
     * @param first the first index of cptId.
     * @param last the last index of cptId.
     * @return execute status of the "update" operation.
     */
    public ResponseData<List<Integer>> getCptIdList(String domain, Integer first, Integer last);
    /**
     * get total amounts of cpt.
     *
     * @param domain the domain of the data.
     * @return execute status of the "update" operation.
     */
    public ResponseData<Integer> getCptCount(String domain);
    /**
     * get several policyId.
     *
     * @param domain the domain of the data.
     * @param first the first index of policyId.
     * @param last the last index of policyId.
     * @return execute status of the "update" operation.
     */
    public ResponseData<List<Integer>> getPolicyIdList(String domain, Integer first, Integer last);
    /**
     * get total amounts of policy.
     *
     * @param domain the domain of the data.
     * @return execute status of the "update" operation.
     */
    public ResponseData<Integer> getPolicyCount(String domain);
    /**
     * add authority issuer to storage.
     *
     * @param domain the domain of the data.
     * @param weId the key of the data.
     * @param name which you want to store to the storage.
     * @param desc which you want to store to the storage.
     * @param accValue which you want to store to the storage.
     * @param extraStr which you want to store to the storage.
     * @param extraInt which you want to store to the storage.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> addAuthorityIssuer(String domain, String weId, String name, String desc, String accValue, String extraStr, String extraInt);
    /**
     * remove authority issuer from storage.
     *
     * @param domain the domain of the data.
     * @param weId the key of the data.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> removeAuthorityIssuer(String domain, String weId);

    /**
     * query authority issuer by weid.
     *
     * @param domain the domain of the data.
     * @param weId the key of the data.
     * @return execute status of the "add" operation.
     */
    public ResponseData<AuthorityIssuerInfo> getAuthorityIssuerByWeId(String domain, String weId);

    /**
     * query authority issuer by name.
     *
     * @param domain the domain of the data.
     * @param name which you want to store to the storage.
     * @return execute status of the "add" operation.
     */
    public ResponseData<AuthorityIssuerInfo> getAuthorityIssuerByName(String domain, String name);

    /**
     * updateRole role for weId to storage.
     *
     * @param domain the domain of the data.
     * @param weId the key of the data.
     * @param recognize whether recognize or not
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> updateAuthorityIssuer(String domain, String weId, Integer recognize);

    /**
     * get total AuthorityIssuer.
     *
     * @param domain the domain of the data.
     * @return execute status of the "update" operation.
     */
    public ResponseData<Integer> getAuthorityIssuerCount(String domain);

    /**
     * get total recognized AuthorityIssuer.
     *
     * @param domain the domain of the data.
     * @return execute status of the "update" operation.
     */
    public ResponseData<Integer> getRecognizedIssuerCount(String domain);

    /**
     * add role for weId to storage.
     *
     * @param domain the domain of the data.
     * @param weId the key of the data.
     * @param roleValue which kind of role you want to add for weId.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> addRole(String domain, String weId, Integer roleValue);

    /**
     * query role by weId.
     *
     * @param domain the domain of the data.
     * @param weId the key of the data.
     * @return execute status of the "add" operation.
     */
    public ResponseData<RoleValue> getRole(String domain, String weId);

    /**
     * updateRole role for weId to storage.
     *
     * @param domain the domain of the data.
     * @param weId the key of the data.
     * @param roleValue which kind of role you want to add for weId.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> updateRole(String domain, String weId, Integer roleValue);

    /**
     * add SpecificType with typeName to storage.
     *
     * @param domain the domain of the data.
     * @param typeName the typeName of the SpecificType.
     * @param owner the owner of the SpecificType
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> addSpecificType(String domain, String typeName, String owner);

    /**
     * query SpecificType by typeName.
     *
     * @param domain the domain of the data.
     * @param typeName the typeName of the SpecificType.
     * @return execute status of the "add" operation.
     */
    public ResponseData<SpecificTypeValue> getSpecificType(String domain, String typeName);

    /**
     * remove authority issuer from storage.
     *
     * @param domain the domain of the data.
     * @param typeName the key of the data.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> removeSpecificType(String domain, String typeName);

    /**
     * updateRole role for weId to storage.
     *
     * @param domain the domain of the data.
     * @param typeName the key of the data.
     * @param fellow new fellow list of this specific type
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> updateSpecificTypeFellow(String domain, String typeName, String fellow);

    /**
     * get total IssuerType.
     *
     * @param domain the domain of the data.
     * @return execute status of the "update" operation.
     */
    public ResponseData<Integer> getIssuerTypeCount(String domain);

    /**
     * get several cptId.
     *
     * @param domain the domain of the data.
     * @param first the first index of cptId.
     * @param last the last index of cptId.
     * @return execute status of the "update" operation.
     */
    public ResponseData<List<String>> getIssuerTypeList(String domain, Integer first, Integer last);

    /**
     * add evidence with hashValue to storage.
     *
     * @param domain the domain of the data.
     * @param hashValue the hashValue of the evidence.
     * @param signer the signer of the evidence
     * @param signature the signature of the evidence
     * @param log the log of the evidence.
     * @param updated the updated of the evidence.
     * @param revoked the revoked of the evidence.
     * @param extraKey the extraKey of the evidence.
     * @param group_id the group_id of the evidence.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> addEvidenceByHash(String domain, String hashValue, String signer, String signature, String log, String updated, String revoked, String extraKey, String group_id);

    /**
     * query evidence by hash.
     *
     * @param domain the domain of the data.
     * @param hash the key of the data.
     * @return execute status of the "add" operation.
     */
    public ResponseData<EvidenceValue> getEvidenceByHash(String domain, String hash);

    /**
     * update evidence with log and signature to storage.
     *
     * @param domain the domain of the data.
     * @param hashValue the hashValue of the evidence.
     * @param signer the signer of the evidence.
     * @param signature the signature of the evidence
     * @param log the log of the evidence.
     * @param updated the updated of the evidence.
     * @param revoked the revoked of the evidence.
     * @param extraKey the revoked of the evidence.
     * @return execute status of the "add" operation.
     */
    public ResponseData<Integer> addSignatureAndLogs(String domain, String hashValue, String signer, String signature, String log, String updated, String revoked, String extraKey);

    /**
     * query evidence by hash.
     *
     * @param domain the domain of the data.
     * @param extraKey the extraKey of the data.
     * @return execute status of the "add" operation.
     */
    public ResponseData<EvidenceValue> getEvidenceByExtraKey(String domain, String extraKey);
}
