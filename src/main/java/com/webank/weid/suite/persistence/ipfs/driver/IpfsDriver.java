package com.webank.weid.suite.persistence.ipfs.driver;

import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.blockchain.protocol.base.CptBaseInfo;
import com.webank.weid.blockchain.protocol.base.WeIdDocument;
import com.webank.weid.blockchain.protocol.base.WeIdDocumentMetadata;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.request.TransactionArgs;
import com.webank.weid.suite.persistence.*;
import com.webank.weid.suite.persistence.ipfs.IpfsConfig;
import com.webank.weid.suite.persistence.ipfs.IpfsDomain;
import com.webank.weid.suite.persistence.ipfs.IpfsExecutor;
import com.webank.weid.util.DataToolUtils;
import io.ipfs.api.IPFS;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author uwepppp
 * @date 2023/08/21
 */
public class IpfsDriver implements Persistence {
    private static final Logger logger = LoggerFactory.getLogger(
            IpfsDriver.class);

    private static final Integer FAILED_STATUS = DataDriverConstant.IPFS_EXECUTE_FAILED_STATUS;

    private static final ErrorCode KEY_INVALID = ErrorCode.PRESISTENCE_DATA_KEY_INVALID;

     public IpfsConfig config =new IpfsConfig();
     IPFS client =config.ipfsClient();




    @Override
    public ResponseData<Integer> add(String domain, String id, String data) {
        return null;
    }

    @Override
    public ResponseData<Integer> batchAdd(String domain, Map<String, String> keyValueList) {
        return null;
    }

    @Override
    public ResponseData<String> get(String domain, String id) {
        return null;
    }

    @Override
    public ResponseData<Integer> delete(String domain, String id) {
        return null;
    }

    @Override
    public ResponseData<Integer> update(String domain, String id, String data) {
        return null;
    }

    @Override
    public ResponseData<Integer> addOrUpdate(String domain, String id, String data) {
        return null;
    }

    @Override
    public ResponseData<Integer> addTransaction(TransactionArgs transactionArgs) {
        return null;
    }

    @Override
    public ResponseData<Integer> addWeId(String domain, String weId, String documentSchema) {
        if (StringUtils.isEmpty(weId)) {
            logger.error("[ipfs->addWeId] the weId is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            IpfsDomain ipfsDomain = new IpfsDomain(domain);
            Date now = ipfsDomain.getNow();
            Object[] datas = {weId, now, now, 1, 0, documentSchema};
            return new IpfsExecutor(ipfsDomain).execute(client,DataDriverConstant.IPFS_WEID_PATH,weId,WeIdDocumentValue.class,datas);
        } catch (WeIdBaseException e) {
            logger.error("[ipfs->addWeId] add the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> updateWeId(String domain, String weId, String documentSchema) {
        if (StringUtils.isEmpty(weId)) {
            logger.error("[ipfs->updateWeId] the weId is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        Date date = new Date();
        try {
            IpfsDomain ipfsDomain = new IpfsDomain(domain);
            ResponseData<String> response = new IpfsExecutor(ipfsDomain)
                    .executeQuery(DataDriverConstant.IPFS_WEID_PATH, weId, client);
            if (response.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                WeIdDocumentValue tableData = DataToolUtils.deserialize(
                        response.getResult(), WeIdDocumentValue.class);
                if(tableData.getDeactivated() == 1){
                    logger.error("[ipfs->updateWeId] the weid is deactivated.");
                    return new ResponseData<>(FAILED_STATUS,
                            ErrorCode.WEID_HAS_BEEN_DEACTIVATED);
                }
                if (StringUtils.isNotBlank(tableData.getDocument_schema())) {
                    int version = tableData.getVersion();
                    version++;
                    Object[] datas = {weId,tableData.getCreated(),date, version, tableData.getDeactivated(), documentSchema};
                    return new IpfsExecutor(ipfsDomain).execute(client,DataDriverConstant.IPFS_WEID_PATH,weId,WeIdDocumentValue.class,datas);
                }
            }
            return new ResponseData<>(FAILED_STATUS, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[ipfs->updateWeId] update the weid error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<WeIdDocument> getWeIdDocument(String domain, String weId) {
        if (StringUtils.isEmpty(weId)) {
            logger.error("[ipfs->getWeIdDocument] the weId is empty.");
            return new ResponseData<>(null, KEY_INVALID);
        }
        try {
            IpfsDomain ipfsDomain = new IpfsDomain(domain);
            ResponseData<String> response = new IpfsExecutor(ipfsDomain)
                    .executeQuery(DataDriverConstant.IPFS_WEID_PATH, weId, client);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                WeIdDocumentValue tableData = DataToolUtils.deserialize(
                        response.getResult(), WeIdDocumentValue.class);
                if (StringUtils.isNotBlank(tableData.getDocument_schema())) {
                    return new ResponseData<>(WeIdDocument.fromJson(tableData.getDocument_schema()), ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
            }
            return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
        } catch (WeIdBaseException e) {
            logger.error("[ipfs->getWeIdDocument] get the weIdDocument error.", e);
            return new ResponseData<>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<WeIdDocumentMetadata> getMeta(String domain, String weId) {
        if (StringUtils.isEmpty(weId)) {
            logger.error("[ipfs->getMeta] the weId is empty.");
            return new ResponseData<>(null, KEY_INVALID);
        }
        try {
            IpfsDomain ipfsDomain = new IpfsDomain(domain);
            /////
            ResponseData<String> response = new IpfsExecutor(ipfsDomain)
                    .executeQuery(DataDriverConstant.IPFS_WEID_PATH, weId, client);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                WeIdDocumentValue tableData = DataToolUtils.deserialize(
                        response.getResult(), WeIdDocumentValue.class);
                if (StringUtils.isNotBlank(tableData.getDocument_schema())) {
                    WeIdDocumentMetadata weIdDocumentMetadata = new WeIdDocumentMetadata();
                    weIdDocumentMetadata.setCreated(tableData.getCreated().getTime());
                    weIdDocumentMetadata.setUpdated(tableData.getUpdated().getTime());
                    weIdDocumentMetadata.setVersionId(tableData.getVersion());
                    weIdDocumentMetadata.setDeactivated(tableData.getDeactivated() == 1);
                    return new ResponseData<>(weIdDocumentMetadata, ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
            }
            return new ResponseData<>(null, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[ipfs->getMeta] getMeta error.", e);
            return new ResponseData<>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> deactivateWeId(String domain, String weId, Boolean state) {

        if (StringUtils.isEmpty(weId)) {
            logger.error("[ipfs->deactivateWeId] the weId is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        String dataKey = DataToolUtils.hash(weId);
        Date date = new Date();
        try {
            IpfsDomain ipfsDomain = new IpfsDomain(domain);
            ResponseData<String> response = new IpfsExecutor(ipfsDomain)
                    .executeQuery(DataDriverConstant.IPFS_WEID_PATH, weId, client);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                WeIdDocumentValue tableData = DataToolUtils.deserialize(
                        response.getResult(), WeIdDocumentValue.class);
                if(tableData.getDeactivated() == 1){
                    logger.error("[mysql->deactivateWeId] the weid is deactivated.");
                    return new ResponseData<>(FAILED_STATUS,
                            ErrorCode.WEID_HAS_BEEN_DEACTIVATED);
                }
                if (StringUtils.isNotBlank(tableData.getDocument_schema())) {
                    Object[] datas = {date, tableData.getVersion(), state ? 1:0, tableData.getDocument_schema(), weId};
                    return   new IpfsExecutor(ipfsDomain).execute(client,DataDriverConstant.IPFS_WEID_PATH,weId,WeIdDocumentValue.class,datas);
                }
            }
            return new ResponseData<>(FAILED_STATUS, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[ipfs->deactivateWeId] deactivate the weId error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<List<String>> getWeIdList(String domain, Integer first, Integer last) {
        try {
            IpfsDomain ipfsDomain = new IpfsDomain(domain);
            int[] datas = {first, last - first + 1,DataDriverConstant.IPFS_ONLY_ID_LINES};
            ResponseData<List<String>> response = new IpfsExecutor(ipfsDomain).executeQueryLines(
                    DataDriverConstant.IPFS_WEID_PATH,datas, client);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                return new ResponseData<>(response.getResult(), ErrorCode.SUCCESS);
            }
            return new ResponseData<>(null, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[ipfs->getWeIdList] get the data error.", e);
            return new ResponseData<>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> getWeIdCount(String domain) {
        try {
            IpfsDomain ipfsDomain = new IpfsDomain(domain);
            ResponseData<Integer> response = new IpfsExecutor(ipfsDomain)
                    .executeQueryCount(DataDriverConstant.IPFS_WEID_PATH, client);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                return new ResponseData<>(response.getResult(), ErrorCode.SUCCESS);
            }
            return new ResponseData<>(0, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[ipfs->getWeIdCount] get the data error.", e);
            return new ResponseData<>(0, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<CptValue> getCpt(String domain, int cptId) {
        return null;
    }

    @Override
    public ResponseData<CptBaseInfo> addCpt(String domain, int cptId, String publisher, String description, String cptSchema, String cptSignature) {
        return null;
    }

    @Override
    public ResponseData<PolicyValue> getPolicy(String domain, int policyId) {
        return null;
    }

    @Override
    public ResponseData<Integer> addPolicy(String domain, int policyId, String publisher, String description, String cptSchema, String cptSignature) {
        return null;
    }

    @Override
    public ResponseData<PresentationValue> getPresentation(String domain, int presentationId) {
        return null;
    }

    @Override
    public ResponseData<Integer> addPresentation(String domain, int presentationId, String creator, String policies) {
        return null;
    }

    @Override
    public ResponseData<Integer> updateCpt(String domain, int cptId, int cptVersion, String publisher, String description, String cptSchema, String cptSignature) {
        return null;
    }

    @Override
    public ResponseData<Integer> updateCredentialTemplate(String domain, int cptId, String credentialPublicKey, String credentialProof) {
        return null;
    }

    @Override
    public ResponseData<Integer> updateCptClaimPolicies(String domain, int cptId, String policies) {
        return null;
    }

    @Override
    public ResponseData<List<Integer>> getCptIdList(String domain, Integer first, Integer last) {
        return null;
    }

    @Override
    public ResponseData<Integer> getCptCount(String domain) {
        return null;
    }

    @Override
    public ResponseData<List<Integer>> getPolicyIdList(String domain, Integer first, Integer last) {
        return null;
    }

    @Override
    public ResponseData<Integer> getPolicyCount(String domain) {
        return null;
    }

    @Override
    public ResponseData<Integer> addAuthorityIssuer(String domain, String weId, String name, String desc, String accValue, String extraStr, String extraInt) {
        return null;
    }

    @Override
    public ResponseData<Integer> removeAuthorityIssuer(String domain, String weId) {
        return null;
    }

    @Override
    public ResponseData<AuthorityIssuerInfo> getAuthorityIssuerByWeId(String domain, String weId) {
        return null;
    }

    @Override
    public ResponseData<AuthorityIssuerInfo> getAuthorityIssuerByName(String domain, String name) {
        return null;
    }

    @Override
    public ResponseData<Integer> updateAuthorityIssuer(String domain, String weId, Integer recognize) {
        return null;
    }

    @Override
    public ResponseData<Integer> getAuthorityIssuerCount(String domain) {
        return null;
    }

    @Override
    public ResponseData<Integer> getRecognizedIssuerCount(String domain) {
        return null;
    }

    @Override
    public ResponseData<Integer> addRole(String domain, String weId, Integer roleValue) {
        return null;
    }

    @Override
    public ResponseData<RoleValue> getRole(String domain, String weId) {
        return null;
    }

    @Override
    public ResponseData<Integer> updateRole(String domain, String weId, Integer roleValue) {
        return null;
    }

    @Override
    public ResponseData<Integer> addSpecificType(String domain, String typeName, String owner) {
        return null;
    }

    @Override
    public ResponseData<SpecificTypeValue> getSpecificType(String domain, String typeName) {
        return null;
    }

    @Override
    public ResponseData<Integer> removeSpecificType(String domain, String typeName) {
        return null;
    }

    @Override
    public ResponseData<Integer> updateSpecificTypeFellow(String domain, String typeName, String fellow) {
        return null;
    }

    @Override
    public ResponseData<Integer> getIssuerTypeCount(String domain) {
        return null;
    }

    @Override
    public ResponseData<List<String>> getIssuerTypeList(String domain, Integer first, Integer last) {
        return null;
    }

    @Override
    public ResponseData<Integer> addEvidenceByHash(String domain, String hashValue, String signer, String signature, String log, String updated, String revoked, String extraKey, String group_id) {
        return null;
    }

    @Override
    public ResponseData<EvidenceValue> getEvidenceByHash(String domain, String hash) {
        return null;
    }

    @Override
    public ResponseData<Integer> addSignatureAndLogs(String domain, String hashValue, String signer, String signature, String log, String updated, String revoked, String extraKey) {
        return null;
    }

    @Override
    public ResponseData<EvidenceValue> getEvidenceByExtraKey(String domain, String extraKey) {
        return null;
    }
}