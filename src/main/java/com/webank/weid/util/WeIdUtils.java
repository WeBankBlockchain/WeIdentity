

package com.webank.weid.util;

import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.deploy.v3.DeployContractV3;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.BaseService;
import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Objects;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The WeIdentity DID Utils.
 *
 * @author tonychen
 */
public final class WeIdUtils {

    /**
     * log4j object, for recording log.
     */
    private static final Logger logger = LoggerFactory.getLogger(WeIdUtils.class);

    private static String getChainId() {
        return BaseService.getChainId();
    }

    public static CreateWeIdDataResult createWeId() {
        CreateWeIdDataResult result = new CreateWeIdDataResult();
        CryptoKeyPair keyPair = DataToolUtils.cryptoSuite.createKeyPair();
        String publicKey = DataToolUtils.hexStr2DecStr(keyPair.getHexPublicKey());
        String privateKey = DataToolUtils.hexStr2DecStr(keyPair.getHexPrivateKey());
        WeIdPublicKey userWeIdPublicKey = new WeIdPublicKey();
        userWeIdPublicKey.setPublicKey(publicKey);
        result.setUserWeIdPublicKey(userWeIdPublicKey);
        WeIdPrivateKey userWeIdPrivateKey = new WeIdPrivateKey();
        userWeIdPrivateKey.setPrivateKey(privateKey);
        result.setUserWeIdPrivateKey(userWeIdPrivateKey);
        //替换国密
        String weId = WeIdUtils.convertPublicKeyToWeId(publicKey);
        result.setWeId(weId);
        return result;
    }

    /**
     * Convert a WeIdentity DID to a fisco account address.
     *
     * @param weId the WeIdentity DID
     * @return weId related address, empty if input WeIdentity DID is illegal
     */
    public static String convertWeIdToAddress(String weId) {
        if (StringUtils.isEmpty(weId) || !StringUtils.contains(weId, WeIdConstant.WEID_PREFIX)) {
            return StringUtils.EMPTY;
        }
        String[] weIdFields = StringUtils.splitByWholeSeparator(weId, WeIdConstant.WEID_SEPARATOR);
        return weIdFields[weIdFields.length - 1];
    }

    /**
     * Convert an account address to WeIdentity DID.
     *
     * @param address the address
     * @return a related WeIdentity DID, or empty string if the input is illegal.
     */
    public static String convertAddressToWeId(String address) {
        if (StringUtils.isEmpty(address)) {
            return StringUtils.EMPTY;
        }
        return buildWeIdByAddress(address);
    }

    /**
     * Check if the input WeIdentity DID is legal or not.
     *
     * @param weId the WeIdentity DID
     * @return true if the WeIdentity DID is legal, false otherwise.
     */
    public static boolean isWeIdValid(String weId) {
        return StringUtils.isNotEmpty(weId)
            && StringUtils.startsWith(weId, WeIdConstant.WEID_PREFIX)
            && isMatchTheChainId(weId)
            && isValidAddress(convertWeIdToAddress(weId));
    }

    /**
     * Convert a public key to a WeIdentity DID.
     *
     * @param publicKey the public key (decimal)
     * @return WeIdentity DID
     */
    public static String convertPublicKeyToWeId(String publicKey) {
        try {
            //String address = Keys.getAddress(new BigInteger(publicKey));
            String address = DataToolUtils.addressFromPublic(new BigInteger(publicKey));
            return buildWeIdByAddress(address);
        } catch (Exception e) {
            logger.error("convert publicKey to weId error.", e);
            return StringUtils.EMPTY;
        }
    }

    private static String buildWeIdByAddress(String address) {
        if (StringUtils.isEmpty(getChainId())) {
            throw new WeIdBaseException("the chain Id is illegal.");
        }
        StringBuffer weId = new StringBuffer();
        weId.append(WeIdConstant.WEID_PREFIX)
            .append(getChainId())
            .append(WeIdConstant.WEID_SEPARATOR);
        if (!StringUtils.contains(address, WeIdConstant.HEX_PREFIX)) {
            weId.append(WeIdConstant.HEX_PREFIX);
        }
        weId.append(address);
        return weId.toString();
    }

    /**
     * check if private key is empty.
     *
     * @param weIdPrivateKey the WeIdentity DID private key
     * @return true if the private key is not empty, false otherwise.
     */
    public static boolean isPrivateKeyValid(WeIdPrivateKey weIdPrivateKey) {
        return (null != weIdPrivateKey && StringUtils.isNotEmpty(weIdPrivateKey.getPrivateKey())
            && NumberUtils.isDigits(weIdPrivateKey.getPrivateKey())
            && new BigInteger(weIdPrivateKey.getPrivateKey()).compareTo(BigInteger.ZERO) > 0);
    }

    /**
     * check if the public key matchs the private key.
     *
     * @param privateKey the WeIdentity DID private key
     * @param publicKey the WeIdentity DID publicKey key
     * @return true if the private and publicKey key is match, false otherwise.
     */
    /*public static boolean isEcdsaKeypairMatch(String privateKey, String publicKey) {
        try {
            *//*ECKeyPair keyPair = ECKeyPair.create(new BigInteger(privateKey));
            return StringUtils.equals(String.valueOf(keyPair.getPublicKey()), publicKey);*//*
            CryptoKeyPair keyPair = DataToolUtils.createKeyPairFromPrivate(
                    new BigInteger(privateKey));
            byte[] bytePub = Numeric.hexStringToByteArray(keyPair.getHexPublicKey());
            return StringUtils.equals(new BigInteger(1, bytePub).toString(), publicKey);
        } catch (Exception e) {
            return false;
        }
    }*/

    /**
     * check if the public key matchs the private key.
     *
     * @param privateKey BigInt of decimal private key
     * @param publicKey the WeIdentity DID publicKey key
     * @return true if the private and publicKey key is match, false otherwise. todo key pair match
     */
    public static boolean isKeypairMatch(BigInteger privateKey, String publicKey) {
        /*ECKeyPair keyPair = DataToolUtils.createKeyPairFromPrivate(new BigInteger(privateKey));
        return StringUtils.equals(String.valueOf(keyPair.getPublicKey()), publicKey);*/
        String pubFromPri = DataToolUtils.publicKeyStrFromPrivate(privateKey);
        return StringUtils.equals(pubFromPri, publicKey);
    }

    /**
     * check if the given string is a valid address.
     *
     * @param addr given string
     * @return true if yes, false otherwise.
     */
    public static boolean isValidAddress(String addr) {
        if (StringUtils.isEmpty(addr)
            || !Pattern.compile(WeIdConstant.FISCO_BCOS_ADDRESS_PATTERN).matcher(addr).matches()) {
            return false;
        }
        try {
            //return WalletUtils.isValidAddress(addr);
            //TODO java-sdk去掉了WalletUtils.isValidAddress, 此处为先将原逻辑迁移出来
            String addressNoPrefix = Numeric.cleanHexPrefix(addr);
            return addressNoPrefix.length() == 40;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * check if the chainId.
     *
     * @param weId given weId
     * @return true if yes, false otherwise.
     */
    public static boolean isMatchTheChainId(String weId) {
        String[] weIdFields = StringUtils.splitByWholeSeparator(weId, WeIdConstant.WEID_SEPARATOR);
        if (weIdFields.length == 4) {
            return weIdFields[2].equals(getChainId());
        }
        return true;
    }

    /**
     * check if the given Address is empty.
     *
     * @param addr given Address
     * @return true if yes, false otherwise.
     */
    public static boolean isEmptyAddress(Address addr) {
        if (addr == null) {
            return false;
        }
        return addr.getValue().equals(BigInteger.ZERO);
    }

    /**
     * check if the given Address is empty.
     *
     * @param addr given Address
     * @return true if yes, false otherwise.
     */
    public static boolean isEmptyStringAddress(String addr) {
        return Numeric.toBigInt(addr).equals(BigInteger.ZERO);
    }

    /**
     * check the weId is match the private key.
     *
     * @param privateKey the private key
     * @param weId the weId
     * @return true if match, false mismatch
     */
    public static boolean validatePrivateKeyWeIdMatches(WeIdPrivateKey privateKey, String weId) {
        boolean isMatch = false;

        try {
            String address1 = DataToolUtils.addressFromPrivate(new BigInteger(privateKey.getPrivateKey()));
            String address2 = WeIdUtils.convertWeIdToAddress(weId);
            if (address1.equals(address2)) {
                isMatch = true;
            }
        } catch (Exception e) {
            logger.error("Validate private key We Id matches failed. Error message :{}", e);
            return false;
        }

        return isMatch;
    }

    /**
     * get address from private key.
     *
     * @param privateKey private key
     * @return address
     */
    public static String getWeIdFromPrivateKey(String privateKey) {
        /*BigInteger publicKey = DataToolUtils
            .publicKeyFromPrivate(new BigInteger(privateKey));*/
        String publicKey = DataToolUtils.publicKeyStrFromPrivate(new BigInteger(privateKey));
        return convertPublicKeyToWeId(publicKey);
    }

    /**
     * Check private key length.
     *
     * @param privateKey private key string in decimal
     * @return true if OK, false otherwise
     */
    public static boolean isPrivateKeyLengthValid(String privateKey) {
        if (StringUtils.isBlank(privateKey)) {
            return false;
        }
        WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
        weIdPrivateKey.setPrivateKey(privateKey);
        try {
            BigInteger privKeyBig = new BigInteger(privateKey, 10);
            BigInteger maxPrivKeyValue = new BigInteger(
                "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
            return (privKeyBig.compareTo(maxPrivKeyValue) <= 0);
        } catch (Exception e) {
            return false;
        }
    }
}
