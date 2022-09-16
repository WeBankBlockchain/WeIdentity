

package com.webank.weid.service.impl.engine;

import java.util.List;

import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.base.IssuerType;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * 作为调用支持不同FISCO BCOS平台版本的AuthorityIssuer合约的service engine， 根据FISCO BCOS版本不同，
 * 调用不同版本的智能合约方法，并处理合约返回的数据.
 *
 * @author tonychen 2019年6月25日
 */
public interface AuthorityIssuerServiceEngine extends ReloadStaticContract {

    /**
     * call authority issuer contract method to add an authority issuer.
     *
     * @param args parameters
     * @return result
     */
    public ResponseData<Boolean> addAuthorityIssuer(RegisterAuthorityIssuerArgs args);

    /**
     * call authority issuer contract method to remove an authority issuer.
     *
     * @param args parameters
     * @return result
     */
    public ResponseData<Boolean> removeAuthorityIssuer(RemoveAuthorityIssuerArgs args);

    /**
     * check if the authority issuer is registered on the blockchain.
     *
     * @param address identity address
     * @return result
     */
    public ResponseData<Boolean> isAuthorityIssuer(String address);

    /**
     * call authority issuer contract method to query information of an authority issuer.
     *
     * @param weId the entity's weidentity did
     * @return result
     */
    public ResponseData<AuthorityIssuer> getAuthorityIssuerInfoNonAccValue(String weId);

    /**
     * call authority issuer contract method to query all authority issuer address.
     *
     * @param index the index
     * @param num the num
     * @return authority issuer address list
     */
    public List<String> getAuthorityIssuerAddressList(Integer index, Integer num);

    /**
     * call specific issuer contract method to remove a issuer.
     *
     * @param issuerType issuerType value
     * @param issuerAddress issuer address
     * @param privateKey the caller's private key
     * @return result
     */
    public ResponseData<Boolean> removeIssuer(
        String issuerType,
        String issuerAddress,
        String privateKey
    );

    /**
     * call specific issuer contract to check the address is a specific issuer .
     *
     * @param issuerType issuerType value
     * @param address issuer's address
     * @return result
     */
    public ResponseData<Boolean> isSpecificTypeIssuer(String issuerType, String address);

    /**
     * call specific issuer contract to query specific issuer address.
     *
     * @param issuerType issuerType value
     * @param index index
     * @param num num
     * @return result
     */
    public ResponseData<List<String>> getSpecificTypeIssuerList(
        String issuerType,
        Integer index,
        Integer num
    );

    /**
     * call specific issuer contract to register issuer type.
     *
     * @param issuerType issuerType value
     * @param privateKey the caller's private key
     * @return result
     */
    public ResponseData<Boolean> registerIssuerType(String issuerType, String privateKey);

    /**
     * call specific issuer contract to add issuer.
     *
     * @param issuerType issuerType value
     * @param issuerAddress issuer's address
     * @param privateKey the caller's private key
     * @return result
     */
    public ResponseData<Boolean> addIssuer(
        String issuerType,
        String issuerAddress,
        String privateKey
    );

    public ResponseData<String> getWeIdFromOrgId(String orgId);

    public ResponseData<Boolean> recognizeWeId(Boolean isRecognize, String addr, String privateKey);

    /**
     * get the issuer count.
     * @return the all issuer
     */
    public ResponseData<Integer> getIssuerCount();

    /**
     * get the issuer count with Recognized.
     * @return the all issuer with Recognized
     */
    public ResponseData<Integer> getRecognizedIssuerCount();

    /**
     * get the issuer size in issuerType.
     * @param issuerType the issuerType
     * @return the all issuer in issuerType
     */
    public ResponseData<Integer> getSpecificTypeIssuerSize(String issuerType);

    /**
     * get the issuer type count.
     * @return the all issuer type
     */
    public ResponseData<Integer> getIssuerTypeCount();

    /**
     * remove the issuerType.
     * @param issuerType the issuerType name
     * @param privateKey the privateKey
     * @return true is success, false is fail
     */
    public ResponseData<Boolean> removeIssuerType(String issuerType, String privateKey);

    /**
     * get the issuerType list.
     * @param index the start index
     * @param num the page size
     * @return the issuerType list
     */
    public ResponseData<List<IssuerType>> getIssuerTypeList(Integer index, Integer num);
}
