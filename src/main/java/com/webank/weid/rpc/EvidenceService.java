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

import java.util.List;

import com.webank.weid.protocol.base.EvidenceInfo;
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
     * Create a new evidence to blockchain, and return the evidence address on-chain. Supports
     * following types of input: Credential, CredentialWrapper, CredentialPojo, plain hash value.
     *
     * @param object the given Java object
     * @param weIdPrivateKey the signer WeID's private key
     * @return evidence address. Return empty string if failed due to any reason.
     */
    ResponseData<String> createEvidence(Hashable object, WeIdPrivateKey weIdPrivateKey);

    /**
     * Create a new evidence with multiple signers to blockchain, and return the evidence address
     * on-chain. Supports following types input: Credential, CredentialWrapper, CredentialPojo, and
     * plain hash value. This allows multiple WeIDs to be declared as signers. Here, one signer must
     * provide his/her private key to create evidence. The rest of signers can append their
     * signature via AddSignature() in future.
     *
     * @param object the given Java object
     * @param signers declared signers WeID
     * @param weIdPrivateKey the signer WeID's private key - must belong to one of the signers
     * @return evidence address. Return empty string if failed due to any reason.
     */
    ResponseData<String> createEvidence(Hashable object, List<String> signers,
        WeIdPrivateKey weIdPrivateKey);

    /**
     * Add new signatures to an existing evidence to increase its credibility. Supports following
     * types of input: Credential, CredentialWrapper, CredentialPojo, and plain hash value. Succeeds
     * if and only if the sender is one of the signer WeID defined in this evidence.
     *
     * @param object the given Java object
     * @param evidenceAddress the evidence address on chain
     * @param weIdPrivateKey the signer WeID's private key
     * @return true if succeed, false otherwise
     */
    ResponseData<Boolean> addSignature(Hashable object, String evidenceAddress,
        WeIdPrivateKey weIdPrivateKey);

    /**
     * Get the evidence from blockchain.
     *
     * @param evidenceAddress the evidence address on chain
     * @return The EvidenceInfo
     */
    ResponseData<EvidenceInfo> getEvidence(String evidenceAddress);

    /**
     * Verify a Hash value based against the provided Evidence info. Supports following types of
     * input: Credential, CredentialWrapper, CredentialPojo, and plain hash value. This will
     * traverse all the listed signatures against its singers.
     *
     * @param object the given Java object
     * @param evidenceAddress the evidence address to be verified
     * @return true if succeeds, false otherwise
     */
    ResponseData<Boolean> verify(Hashable object, String evidenceAddress);
}
