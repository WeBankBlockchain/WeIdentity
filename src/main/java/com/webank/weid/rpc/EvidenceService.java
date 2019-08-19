/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.CredentialWrapper;
import com.webank.weid.protocol.base.EvidenceInfo;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.response.ResponseData;

/**
 * Service inf for operations on Evidence for Credentials.
 *
 * @author chaoxinhu 2019.1
 */
public interface EvidenceService {

    /**
     * Create a new evidence to blockchain, and return the evidence address on-chain.
     *
     * @param credential the given credential
     * @param weIdPrivateKey the signer WeID's private key
     * @return evidence address. Return empty string if failed due to any reason.
     */
    ResponseData<String> createEvidence(Credential credential, WeIdPrivateKey weIdPrivateKey);

    /**
     * Create a new evidence to blockchain, and return the evidence address on-chain.
     *
     * @param credentialWrapper the given credentialWrapper
     * @param weIdPrivateKey the signer WeID's private key
     * @return evidence address. Return empty string if failed due to any reason.
     */
    ResponseData<String> createEvidence(CredentialWrapper credentialWrapper,
        WeIdPrivateKey weIdPrivateKey);

    /**
     * Create a new evidence to blockchain, and return the evidence address on-chain.
     *
     * @param credentialPojo the given credentialPojo
     * @param weIdPrivateKey the signer WeID's private key
     * @return evidence address. Return empty string if failed due to any reason.
     */
    ResponseData<String> createEvidence(CredentialPojo credentialPojo,
        WeIdPrivateKey weIdPrivateKey);

    /**
     * Get the evidence from blockchain.
     *
     * @param evidenceAddress the evidence address on chain
     * @return The EvidenceInfo
     */
    ResponseData<EvidenceInfo> getEvidence(String evidenceAddress);

    /**
     * Verify a Credential based against the provided Evidence info.
     *
     * @param credential the given credential
     * @param evidenceAddress the evidence address to be verified
     * @return true if succeeds, false otherwise
     */
    ResponseData<Boolean> verify(Credential credential, String evidenceAddress);

    /**
     * Verify a (possibly) selectively-disclosed Credential against the provided Evidence info.
     *
     * @param credentialWrapper the given CredentialWrapper
     * @param evidenceAddress the evidence address to be verified
     * @return true if succeeds, false otherwise
     */
    ResponseData<Boolean> verify(CredentialWrapper credentialWrapper, String evidenceAddress);

    /**
     * Verify a (possibly) selectively-disclosed CredentialPojo against the provided Evidence info.
     *
     * @param credentialPojo the given credentialPojo
     * @param evidenceAddress the evidence address to be verified
     * @return true if succeeds, false otherwise
     */
    ResponseData<Boolean> verify(CredentialPojo credentialPojo, String evidenceAddress);
}
