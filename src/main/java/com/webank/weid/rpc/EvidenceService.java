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

import com.webank.weid.protocol.base.CredentialPojo;
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
     * Create a new evidence together with log and custom key. Other guys can use this custom key,
     * OR its hash value, to lookup the detailed information of this evidence. Multiple calls of
     * this method will yield multiple log entries as the same of addLog().
     *
     * @param object the given Java object
     * @param weIdPrivateKey the signer WeID's private key
     * @param log log entry
     * @param customKey custom key determined by creator - it cannot be a hash value though
     * @return evidence hash value
     */
    ResponseData<String> createEvidenceWithLogAndCustomKey(
        Hashable object,
        WeIdPrivateKey weIdPrivateKey,
        String log,
        String customKey
    );

    /**
     * Add log entry for an existing evidence. This log will be recorded on blockchain permanently,
     * and finally it will be fetched as a list when trying to get evidence.
     *
     * @param hashValue hash value
     * @param log log entry - can be null or empty
     * @param weIdPrivateKey the signer WeID's private key
     * @return true if succeeded, false otherwise
     */
    ResponseData<Boolean> addLogByHash(
        String hashValue,
        String log,
        WeIdPrivateKey weIdPrivateKey
    );

    /**
     * Add log entry for an existing evidence. This log will be recorded on blockchain permanently,
     * and finally it will be fetched as a list when trying to get evidence.
     *
     * @param customKey custom key
     * @param log log entry - can be null or empty
     * @param weIdPrivateKey the signer WeID's private key
     * @return true if succeeded, false otherwise
     */
    ResponseData<Boolean> addLogByCustomKey(
        String customKey,
        String log,
        WeIdPrivateKey weIdPrivateKey
    );

    /**
     * Get the evidence info from blockchain using hash as key.
     *
     * @param hashValue the hash, on chain
     * @return The EvidenceInfo
     */
    ResponseData<EvidenceInfo> getEvidence(String hashValue);

    /**
     * Get the evidence info from blockchain using custom key.
     *
     * @param customKey the custom key, on chain
     * @return The EvidenceInfo
     */
    ResponseData<EvidenceInfo> getEvidenceByCustomKey(String customKey);

    /**
     * Generate hash value of any passed-in param.
     *
     * @param object param to be hashed
     * @param <T> type of param
     * @return the hash string
     */
    <T> ResponseData<HashString> generateHash(T object);

    /**
     * Validate whether a credential created the evidence, and this evidence is signed by this WeID
     * - will perform on-Chain key check.
     *
     * @param credentialPojo the credentialPojo
     * @param evidenceInfo the evidence info fetched from chain
     * @param weId the WeID
     * @return true if yes, false otherwise
     */
    ResponseData<Boolean> verifySigner(
        CredentialPojo credentialPojo,
        EvidenceInfo evidenceInfo,
        String weId
    );

    /**
     * Validate whether a credential created the evidence, and this evidence is signed by this WeID
     * based on the passed-in publicKey.
     *
     * @param credentialPojo the credentialPojo
     * @param evidenceInfo the evidence info fetched from chain
     * @param weId the WeID
     * @param publicKey the public key
     * @return true if yes, false otherwise
     */
    ResponseData<Boolean> verifySigner(
        CredentialPojo credentialPojo,
        EvidenceInfo evidenceInfo,
        String weId,
        String publicKey
    );

    /**
     * A direct pass-thru method to create raw evidence where all inputs can be customized.
     *
     * @param hashValue the hash value
     * @param signature the signature value
     * @param log the log
     * @param timestamp the timestamp
     * @param extraKey the extra data
     * @param privateKey the private key
     * @return true if yes, false otherwise
     */
    @Deprecated
    ResponseData<Boolean> createRawEvidenceWithCustomKey(
        String hashValue,
        String signature,
        String log,
        Long timestamp,
        String extraKey,
        String privateKey
    );

    /**
     * A direct pass-thru method to create raw evidence where all inputs, including signer, can be
     * customized.
     *
     * @param hashValue the hash value
     * @param signature the signature value
     * @param log the log
     * @param timestamp the timestamp
     * @param extraKey the extra data
     * @param signer the signer
     * @param privateKey the private key
     * @return true if yes, false otherwise
     */
    ResponseData<Boolean> createRawEvidenceWithSpecificSigner(
        String hashValue,
        String signature,
        String log,
        Long timestamp,
        String extraKey,
        String signer,
        String privateKey
    );
}
