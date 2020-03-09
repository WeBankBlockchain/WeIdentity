/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.rpc;

import java.util.Map;

import com.webank.weid.protocol.base.EvidenceInfo;
import com.webank.weid.protocol.base.HashString;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.inf.Hashable;
import com.webank.weid.protocol.response.ResponseData;

/**
 * Service inf for operations on Evidence for Credentials.
 *
 * @author chaoxinhu 2019.1
 */
public interface EvidenceService {

    /**
     * Create a new evidence to blockchain, and return the evidence's hash value on-chain. Supports
     * following types of input: Credential, CredentialWrapper, CredentialPojo, plain hash String,
     * After a successful creation, the hash value will be recorded onto blockchain, and this hash
     * value can be used as key to lookup on blockchain.
     *
     * @param object the given Java object
     * @param weIdPrivateKey the signer WeID's private key
     * @return evidence hash value. Return empty string if failed due to any reason.
     */
    ResponseData<String> createEvidence(Hashable object, WeIdPrivateKey weIdPrivateKey);

    /**
     * Create a new evidence together with uploaded extra values.
     *
     * @param object the given Java object
     * @param weIdPrivateKey the signer WeID's private key
     * @param extra the extra value blob
     * @return evidence hash value
     */
    ResponseData<String> createEvidence(
        Hashable object,
        WeIdPrivateKey weIdPrivateKey,
        Map<String, String> extra
    );

    /**
     * Create a new evidence together with uploaded extra values.
     *
     * @param object the given Java object
     * @param weIdPrivateKey the signer WeID's private key
     * @param extra the extra value blob
     * @param extraKey extra key
     * @return evidence hash value
     */
    ResponseData<String> createEvidenceWithExtraKey(
        Hashable object,
        WeIdPrivateKey weIdPrivateKey,
        Map<String, String> extra,
        String extraKey
    );

    /**
     * Get the evidence info from blockchain.
     *
     * @param evidenceKey the hash, on chain
     * @return The EvidenceInfo
     */
    ResponseData<EvidenceInfo> getEvidence(String evidenceKey);

    /**
     * Get the evidence info from blockchain.
     *
     * @param extraKey the hash, on chain
     * @return The EvidenceInfo
     */
    ResponseData<EvidenceInfo> getEvidenceByExtraKey(String extraKey);

    /**
     * Generate hash value of any passed-in param.
     *
     * @param object param to be hashed
     * @param <T> type of param
     * @return the hash string
     */
    <T> ResponseData<HashString> generateHash(T object);

    /**
     * Validate whether an evidence is signed by this WeID - will perform on-Chain key check.
     *
     * @param evidenceInfo the evidence info fetched from chain
     * @param weId the WeID
     * @return true if yes, false otherwise
     */
    ResponseData<Boolean> verifySigner(EvidenceInfo evidenceInfo, String weId);

    /**
     * Validate whether an evidence is signed by this WeID with passed-in public key.
     *
     * @param evidenceInfo the evidence info fetched from chain
     * @param weId the WeID
     * @param publicKey the public key
     * @return true if yes, false otherwise
     */
    ResponseData<Boolean> verifySigner(EvidenceInfo evidenceInfo, String weId, String publicKey);
}
