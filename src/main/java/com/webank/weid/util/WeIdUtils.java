/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

import java.math.BigInteger;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;

import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.base.WeIdPrivateKey;


/**
 * The WeIdentity DID Utils.
 *
 * @author tonychen
 */
public final class WeIdUtils {

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
            && StringUtils.isNotEmpty(StringUtils.splitByWholeSeparator(weId, ":")[2])
            );
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
        return (null != weIdPrivateKey && StringUtils.isNotEmpty(weIdPrivateKey.getPrivateKey()));
    }

    /**
     * check if the public key matchs the private key.
     */
    public static boolean isKeypairMatch(String privateKey, String publicKey) {
        try {
            ECKeyPair keyPair = ECKeyPair.create(new BigInteger(privateKey));
            return StringUtils.equals(String.valueOf(keyPair.getPublicKey()), publicKey);
        } catch (Exception e) {
            return false;
        }
    }
}
