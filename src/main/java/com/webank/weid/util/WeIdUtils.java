/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.util;

import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import java.math.BigInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;
import org.bcos.web3j.crypto.WalletUtils;
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

    /**
     * Convert a WeIdentity DID to a fisco account address.
     *
     * @param weid the WeIdentity DID
     * @return weId related address, empty if input WeIdentity DID is illegal
     */
    public static String convertWeIdToAddress(String weid) {
        if (StringUtils.isEmpty(weid) || !StringUtils.contains(weid, WeIdConstant.WEID_PREFIX)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.splitByWholeSeparator(weid, ":")[2];
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
        return new StringBuffer().append(WeIdConstant.WEID_PREFIX).append(address).toString();
    }

    /**
     * Check if the input WeIdentity DID is legal or not.
     *
     * @param weId the WeIdentity DID
     * @return true if the WeIdentity DID is legal, false otherwise.
     */
    public static boolean isWeIdValid(String weId) {
        return (StringUtils.isNotEmpty(weId)
            && StringUtils.startsWith(weId, WeIdConstant.WEID_PREFIX)
            && StringUtils.isNotEmpty(StringUtils.splitByWholeSeparator(weId, ":")[2]));
    }

    /**
     * Convert a public key to a WeIdentity DID.
     *
     * @param publicKey the public key
     * @return WeIdentity DID
     */
    public static String convertPublicKeyToWeId(String publicKey) {
        String address = Keys.getAddress(new BigInteger(publicKey));
        String weId =
            new StringBuffer().append(WeIdConstant.WEID_PREFIX).append("0x").append(address)
                .toString();
        return weId;
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
    public static boolean isKeypairMatch(String privateKey, String publicKey) {
        try {
            ECKeyPair keyPair = ECKeyPair.create(new BigInteger(privateKey));
            return StringUtils.equals(String.valueOf(keyPair.getPublicKey()), publicKey);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * check if the given string is a valid address.
     *
     * @param addr given string
     * @return true if yes, false otherwise.
     */
    public static boolean isValidAddress(String addr) {
        if (StringUtils.isEmpty(addr)) {
            return false;
        }
        try {
            return WalletUtils.isValidAddress(addr);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * check if the given Address is empty.
     *
     * @param addr given Address
     * @return true if yes, false otherwise.
     */
    public static boolean isEmptyAddress(Address addr) {
        return addr.getValue().equals(BigInteger.ZERO);
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
            BigInteger publicKey = DataToolUtils
                .publicKeyFromPrivate(new BigInteger(privateKey.getPrivateKey()));
            String address1 = "0x" + Keys.getAddress(publicKey);
            String address2 = WeIdUtils.convertWeIdToAddress(weId);
            if (address1.equals(address2)) {
                isMatch = true;
            }
        } catch (Exception e) {
            logger.error("Validate private key We Id matches failed. Error message :{}", e);
            return isMatch;
        }
        
        return isMatch;
    }
}
