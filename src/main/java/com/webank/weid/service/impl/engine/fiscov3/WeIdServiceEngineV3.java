

package com.webank.weid.service.impl.engine.fiscov3;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.v3.WeIdContract;
import com.webank.weid.protocol.base.AuthenticationProperty;
import com.webank.weid.protocol.base.ServiceProperty;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdDocumentMetadata;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.TransactionInfo;
import com.webank.weid.service.impl.engine.BaseEngine;
import com.webank.weid.service.impl.engine.WeIdServiceEngine;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.DateUtils;
import com.webank.weid.util.Multibase.Multibase;
import com.webank.weid.util.Multicodec.Multicodec;
import com.webank.weid.util.Multicodec.MulticodecEncoder;
import com.webank.weid.util.WeIdUtils;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple6;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

//import org.fisco.bcos.sdk.v3.client.protocol.model.Transaction;

/**
 * WeIdServiceEngine call weid contract which runs on FISCO BCOS 2.0.
 *
 * @author tonychen 2019年6月21日
 */
public class WeIdServiceEngineV3 extends BaseEngine implements WeIdServiceEngine {

    private static final Logger logger = LoggerFactory.getLogger(
        WeIdServiceEngineV3.class);

    /**
     * WeIdentity DID contract object, for calling weIdentity DID contract.
     */
    private static WeIdContract weIdContract;

    /**
     * 构造函数.
     */
    public WeIdServiceEngineV3() {
        if (weIdContract == null) {
            reload();
        }
    }

    /**
     * 重新加载静态合约对象.
     */
    @Override
    public void reload() {
        weIdContract = getContractService(fiscoConfig.getWeIdAddress(), WeIdContract.class);
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.WeIdController#isWeIdExist(java.lang.String)
     */
    @Override
    public ResponseData<Boolean> isWeIdExist(String weId) {
        try {

            boolean isExist = weIdContract
                .isIdentityExist(WeIdUtils.convertWeIdToAddress(weId)).booleanValue();
            return new ResponseData<>(isExist, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[isWeIdExist] execute failed. Error message :{}", e);
            return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.WeIdController#isDeactivated(java.lang.String)
     */
    @Override
    public ResponseData<Boolean> isDeactivated(String weId) {
        try {

            boolean isExist = weIdContract
                    .isDeactivated(WeIdUtils.convertWeIdToAddress(weId));
            return new ResponseData<>(isExist, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[isDeactivated] execute failed. Error message :{}", e);
            return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.WeIdController#getWeIdDocument(java.lang.String)
     */
    @Override
    public ResponseData<WeIdDocument> getWeIdDocument(String weId) {
        WeIdDocument result = new WeIdDocument();

        result.setId(weId);
        try {
            String identityAddr = WeIdUtils.convertWeIdToAddress(weId);
            Tuple6<String, String, Boolean, BigInteger, List<String>, List<String>> document = weIdContract
                    .resolve(identityAddr);
            if (document == null) {
                return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
            }

            List<AuthenticationProperty> authentications = new ArrayList<>();
            for(int i = 0; i < document.getValue5().size(); i++){
                authentications.add(AuthenticationProperty.fromString(document.getValue5().get(i)));
            }
            result.setAuthentication(authentications);
            if(document.getValue6().size()>0){
                List<ServiceProperty> serviceProperties= new ArrayList<>();
                for(int i = 0; i < document.getValue6().size(); i++){
                    serviceProperties.add(ServiceProperty.fromString(document.getValue6().get(i)));
                }
                result.setService(serviceProperties);
            }
            return new ResponseData<>(result, ErrorCode.SUCCESS);
        }  catch (Exception e) {
            //由于合约中要求weid存在才返回对应的document，如果weid不存在，会抛出异常
            logger.error("[getWeIdDocument]: exception.", e);
            return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
        }
    }

    @Override
    public ResponseData<WeIdDocumentMetadata> getWeIdDocumentMetadata(String weId) {
        WeIdDocumentMetadata result = new WeIdDocumentMetadata();

        try {
            String identityAddr = WeIdUtils.convertWeIdToAddress(weId);
            Tuple6<String, String, Boolean, BigInteger, List<String>, List<String>> document = weIdContract
                    .resolve(identityAddr);
            if (document == null) {
                return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
            }
            result.setCreated(Long.getLong(document.getValue1()));
            result.setUpdated(Long.getLong(document.getValue2()));
            result.setDeactivated(document.getValue3());
            result.setVersionId(document.getValue4().intValue());
            return new ResponseData<>(result, ErrorCode.SUCCESS);
        }  catch (Exception e) {
            logger.error("[getWeIdDocument]: exception.", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.WeIdController
     * #createWeId(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ResponseData<Boolean> createWeId(
            String weAddress,
            String publicKey,
            String privateKey) {

        AuthenticationProperty authenticationProperty = new AuthenticationProperty();
        //在创建weid时默认添加一个id为#keys-0的verification method
        authenticationProperty.setId(WeIdUtils.convertAddressToWeId(weAddress) + "#keys-" + DataToolUtils.hash(publicKey).substring(58));
        //verification method controller默认为自己
        authenticationProperty.setController(WeIdUtils.convertAddressToWeId(weAddress));
        //这里把publicKey用multicodec编码，然后使用Multibase格式化，国密和非国密使用不同的编码
        byte[] publicKeyEncode = MulticodecEncoder.encode(DataToolUtils.cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE? Multicodec.ED25519_PUB:Multicodec.SM2_PUB,
                publicKey.getBytes(StandardCharsets.UTF_8));
        authenticationProperty.setPublicKeyMultibase(Multibase.encode(Multibase.Base.Base58BTC, publicKeyEncode));
        List<String> authList = new ArrayList<>();
        authList.add(authenticationProperty.toString());
        List<String> serviceList = new ArrayList<>();
        ServiceProperty serviceProperty = new ServiceProperty();
        serviceProperty.setServiceEndpoint("https://github.com/WeBankBlockchain/WeIdentity");
        serviceProperty.setType("WeIdentity");
        serviceProperty.setId(authenticationProperty.getController() + '#' + DataToolUtils.hash(serviceProperty.getServiceEndpoint()).substring(58));
        serviceList.add(serviceProperty.toString());
        org.fisco.bcos.sdk.v3.model.TransactionReceipt receipt;
        com.webank.weid.contract.v3.WeIdContract weIdContract =
                reloadContract(fiscoConfig.getWeIdAddress(), privateKey, com.webank.weid.contract.v3.WeIdContract.class);
        try {
            receipt = weIdContract.createWeId(
                    weAddress,
                    DateUtils.getNoMillisecondTimeStamp().toString(),
                    authList,
                    serviceList
            );

            TransactionInfo info = new TransactionInfo(receipt);
            //合约层面去掉了对交易发送者的地址是否等于所创建的weid地址的检查（为了允许可以代替其他人创建weid），所以交易失败的唯一可能是weid已经存在
            if (!receipt.isStatusOK()) {
                logger.error(
                        "Create WeId failed: " + receipt.getMessage() + ". weid address is {}",
                        weAddress
                );
                return new ResponseData<>(false, ErrorCode.WEID_ALREADY_EXIST, info);
            }
            return new ResponseData<>(true, ErrorCode.SUCCESS, info);
        } catch (Exception e) {
            logger.error("[createWeId] create weid has error, Error Message：{}", e);
            return new ResponseData<>(false, ErrorCode.WEID_ALREADY_EXIST);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.WeIdController
     * #updateWeId(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ResponseData<Boolean> updateWeId(
            WeIdDocument weIdDocument,
            String weAddress,
            String privateKey) {
        List<String> authList = new ArrayList<>();
        if(weIdDocument.getAuthentication().size() > 0){
            for(int i=0; i<weIdDocument.getAuthentication().size(); i++){
                authList.add(weIdDocument.getAuthentication().get(i).toString());
            }
        }
        List<String> serviceList = new ArrayList<>();
        if(weIdDocument.getService().size() > 0){
            for(int j=0; j<weIdDocument.getService().size(); j++){
                serviceList.add(weIdDocument.getService().get(j).toString());
            }
        }
        org.fisco.bcos.sdk.v3.model.TransactionReceipt receipt;
        com.webank.weid.contract.v3.WeIdContract weIdContract =
                reloadContract(fiscoConfig.getWeIdAddress(), privateKey, com.webank.weid.contract.v3.WeIdContract.class);
        try {
            receipt = weIdContract.updateWeId(
                    weAddress,
                    DateUtils.getNoMillisecondTimeStamp().toString(),
                    authList,
                    serviceList
            );

            TransactionInfo info = new TransactionInfo(receipt);
            //更新weid document限制只能是本人更新，如果更新不成功，则是传入的私钥不对，接口层已做weid是否存在的检查
            if (!receipt.isStatusOK()) {
                logger.error(
                        "Update WeId failed: " + receipt.getMessage() + ". weid address is {}",
                        weAddress
                );
                return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH, info);
            }
            return new ResponseData<>(true, ErrorCode.SUCCESS, info);
        } catch (Exception e) {
            logger.error("[updateWeId] update weid has error, Error Message：{}", e);
            return new ResponseData<>(false, ErrorCode.WEID_DOES_NOT_EXIST);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.WeIdController
     * #deactivateWeId(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ResponseData<Boolean> deactivateWeId(
            String weAddress,
            String privateKey) {
        List<String> authList = new ArrayList<>();
        org.fisco.bcos.sdk.v3.model.TransactionReceipt receipt;
        com.webank.weid.contract.v3.WeIdContract weIdContract =
                reloadContract(fiscoConfig.getWeIdAddress(), privateKey, com.webank.weid.contract.v3.WeIdContract.class);
        try {
            receipt = weIdContract.deactivateWeId(
                    weAddress,
                    true
            );

            TransactionInfo info = new TransactionInfo(receipt);
            //注销WeId失败的原因可能是私钥和WeId不匹配
            if (!receipt.isStatusOK()) {
                logger.error(
                        "Deactivate WeId failed: " + receipt.getMessage() + ". weid address is {}",
                        weAddress
                );
                return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH, info);
            }
            return new ResponseData<>(true, ErrorCode.SUCCESS, info);
        } catch (Exception e) {
            logger.error("[deactivateWeId] deactivate WeId has error, Error Message：{}", e);
            return new ResponseData<>(false, ErrorCode.WEID_DOES_NOT_EXIST);
        }
    }

    @Override
    public ResponseData<List<String>> getWeIdList(
            Integer first,
            Integer last
    ) {
        try {
            List addressList = weIdContract.getWeId(BigInteger.valueOf(first), BigInteger.valueOf(last));
            List<String> result = new ArrayList<>();
            for (Object o : addressList) {
                result.add(WeIdUtils.convertAddressToWeId(o.toString()));
            }
            return new ResponseData<>(result, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[getWeIdTotal]: get weId total has unknow error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    @Override
    public ResponseData<Integer> getWeIdCount() {
        try {
            Integer total = weIdContract.getWeIdCount().intValue();
            return new ResponseData<>(total, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[getWeIdTotal]: get weId total has unknow error. ", e);
            return new ResponseData<>(0, ErrorCode.UNKNOW_ERROR);
        }
    }
}
