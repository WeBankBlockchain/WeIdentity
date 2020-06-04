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
import com.webank.weid.protocol.base.WeIdAuthentication;
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
     * value can be used as key to lookup on blockchain. This will fail if evidence already exists.
     *
     * @param object the given Java object
     * @param weIdPrivateKey the signer WeID's private key
     * @return evidence hash value. Return empty string if failed, e.g. already existed.
     */
    ResponseData<String> createEvidence(Hashable object, WeIdPrivateKey weIdPrivateKey);

    /**
     * Create a new evidence to blockchain and return the hash value, with appending log. This will
     * fail if evidence already exists.
     *
     * @param object the given Java object
     * @param log appendable log entry - can be null or empty
     * @param weIdAuthentication weid authentication (only checks private key)
     */
    ResponseData<String> createEvidenceWithLog(
        Hashable object,
        String log,
        WeIdAuthentication weIdAuthentication
    );

    /**
     * Create a new evidence together with log and custom key. Other guys can use this custom key,
     * OR its hash value, to lookup the detailed information of this evidence. Multiple calls of
     * this method will yield multiple log entries as the same of addLog().
     *
     * @param object the given Java object
     * @param weIdPrivateKey the signer WeID's private key
     * @param log appendable log entry - can be null or empty
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
     * Add log entry for an existing evidence, appending on existing log entries. This log will be
     * recorded on blockchain permanently, and finally it will be fetched as a list when trying to
     * get evidence. Log must not be empty.
     *
     * @param hashValue hash value
     * @param log Not null log entry
     * @param weIdPrivateKey the signer WeID's private key
     * @return true if succeeded, false otherwise
     */
    ResponseData<Boolean> addLogByHash(
        String hashValue,
        String log,
        WeIdPrivateKey weIdPrivateKey
    );

    /**
     * Add signature and log as a new signer to an existing evidence, appending on existing log
     * entries. Log must not be empty. The signer might be different than the existing signers.
     *
     * @param hashValue hash value
     * @param log Not null log entry
     * @param weIdPrivateKey the signer WeID's private key
     * @return true if succeeded, false otherwise
     */
    ResponseData<Boolean> addSignatureAndLogByHash(
        String hashValue,
        String log,
        WeIdPrivateKey weIdPrivateKey
    );

    /**
     * Add log entry for an existing evidence, appending on existing log entries. This log will be
     * recorded on blockchain permanently, and finally it will be fetched as a list when trying to
     * get evidence. Log must not be empty. It will firstly try to fetch the hash value given the
     * custom key, and if the hash value does not exist, it will use the supplementing hash value
     * (1st parameter) to make up.
     *
     * @param hashValueSupplement the hash value supplement if the custom key does not exist
     * @param customKey custom key
     * @param log Not null log entry
     * @param weIdPrivateKey the signer WeID's private key
     * @return true if succeeded, false otherwise
     */
    ResponseData<Boolean> addLogByCustomKey(
        String hashValueSupplement,
        String customKey,
        String log,
        WeIdPrivateKey weIdPrivateKey
    );

    /**
     * Add signature and log as a new signer to an existing evidence. Log can be empty.
     *
     * @param hashValueSupplement the hash value supplement if the custom key does not exist
     * @param customKey custom key
     * @param log Not null log entry
     * @param weIdPrivateKey the signer WeID's private key
     * @return true if succeeded, false otherwise
     */
    ResponseData<Boolean> addSignatureAndLogByCustomKey(
        String hashValueSupplement,
        String customKey,
        String log,
        WeIdPrivateKey weIdPrivateKey
    );

    /**
     * Revoke an evidence - which can be un-revoked.
     *
     * @param object the object
     * @param weIdAuthentication the weid authentication
     * @return true if yes, false otherwise, with error codes
     */
    ResponseData<Boolean> revoke(Hashable object, WeIdAuthentication weIdAuthentication);

    /**
     * Un-revoke an evidence.
     *
     * @param object the object
     * @param weIdAuthentication the weid authentication
     * @return true if yes, false otherwise, with error codes
     */
    ResponseData<Boolean> unRevoke(Hashable object, WeIdAuthentication weIdAuthentication);

    /**
     * Check whether this evidence is revoked by this WeID.
     *
     * @param evidenceInfo the EvidenceInfo
     * @param weId the signer WeID
     * @return true if revoked, false otherwise
     */
    ResponseData<Boolean> isRevoked(EvidenceInfo evidenceInfo, String weId);

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
