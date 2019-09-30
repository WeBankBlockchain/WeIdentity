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

package com.webank.weid.service.impl.engine;

import java.util.List;

import org.bcos.web3j.crypto.Sign;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.base.EvidenceInfo;
import com.webank.weid.protocol.response.ResponseData;

public interface EvidenceServiceEngine {

    ResponseData<String> createEvidence(
        Sign.SignatureData sigData,
        List<String> hashAttributes,
        List<String> extraValueList,
        String privateKey,
        List<String> signerWeIdList
    );

    ResponseData<Boolean> addSignature(Sign.SignatureData sigData, String privateKey,
        String eviAddress);

    ResponseData<Boolean> setHashValue(List<String> hashAttributes, String privateKey,
        String eviAddress);

    ResponseData<EvidenceInfo> getInfo(String evidenceAddress);

    /**
     * verify create evidence event.
     *
     * @param eventRetCode eventRetCode
     * @param address evidence contract address
     * @return ErrorCode
     */
    default ErrorCode verifyCreateEvidenceEvent(Integer eventRetCode, String address) {
        if (eventRetCode == null || address == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (eventRetCode
            .equals(ErrorCode.CREDENTIAL_EVIDENCE_CONTRACT_FAILURE_ILLEAGAL_INPUT.getCode())) {
            return ErrorCode.CREDENTIAL_EVIDENCE_CONTRACT_FAILURE_ILLEAGAL_INPUT;
        }
        return ErrorCode.SUCCESS;
    }
}
