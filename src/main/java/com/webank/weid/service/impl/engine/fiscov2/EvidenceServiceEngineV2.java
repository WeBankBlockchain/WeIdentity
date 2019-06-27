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

package com.webank.weid.service.impl.engine.fiscov2;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.crypto.Sign;
import org.bcos.web3j.crypto.Sign.SignatureData;
import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tuples.generated.Tuple6;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v2.Evidence;
import com.webank.weid.contract.v2.EvidenceFactory;
import com.webank.weid.contract.v2.EvidenceFactory.CreateEvidenceLogEventResponse;
import com.webank.weid.protocol.base.EvidenceInfo;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.TransactionInfo;
import com.webank.weid.service.impl.engine.BaseEngine;
import com.webank.weid.service.impl.engine.EvidenceServiceEngine;
import com.webank.weid.util.DataToolUtils;

/**
 * EvidenceServiceEngine calls evidence contract which runs on FISCO BCOS 2.0.
 */
public class EvidenceServiceEngineV2 extends BaseEngine implements EvidenceServiceEngine {

    private static final Logger logger = LoggerFactory.getLogger(EvidenceServiceEngineV2.class);

    @Override
    public ResponseData<String> createEvidence(
        Sign.SignatureData sigData,
        List<String> hashAttributes,
        List<String> extraValueList,
        String privateKey
    ) {
        try {
            List<byte[]> hashAttributesByte = new ArrayList<>();
            for (String extraValue : extraValueList) {
                hashAttributesByte.add(DataToolUtils.stringToByte32Array(extraValue));
            }
            List<byte[]> extraValueListByte = new ArrayList<>();
            for (String hashValue : hashAttributes) {
                extraValueListByte.add(DataToolUtils.stringToByte32Array(hashValue));
            }
            List<String> signer = new ArrayList<>();
            ECKeyPair keyPair = ECKeyPair.create(new BigInteger(privateKey));
            signer.add(Keys.getAddress(keyPair));

            EvidenceFactory evidenceFactory =
                reloadContract(
                    fiscoConfig.getEvidenceAddress(),
                    privateKey,
                    EvidenceFactory.class
                );
            TransactionReceipt receipt =
                evidenceFactory.createEvidence(
                    hashAttributesByte,
                    signer,
                    sigData.getR(),
                    sigData.getS(),
                    BigInteger.valueOf(sigData.getV()),
                    extraValueListByte
                ).send();

            TransactionInfo info = new TransactionInfo(receipt);
            List<CreateEvidenceLogEventResponse> eventResponseList =
                evidenceFactory.getCreateEvidenceLogEvents(receipt);
            CreateEvidenceLogEventResponse event = eventResponseList.get(0);

            if (event != null) {
                ErrorCode innerResponse = verifyCreateEvidenceEvent(event.retCode.intValue(),
                    event.addr);
                if (ErrorCode.SUCCESS.getCode() != innerResponse.getCode()) {
                    return new ResponseData<>(StringUtils.EMPTY, innerResponse, info);
                }
                return new ResponseData<>(event.addr.toString(), ErrorCode.SUCCESS, info);
            } else {
                logger.error(
                    "create evidence failed due to transcation event decoding failure."
                );
                return new ResponseData<>(StringUtils.EMPTY,
                    ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR, info);
            }
        } catch (Exception e) {
            logger.error("create evidence failed due to system error. ", e);
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }

    @Override
    public ResponseData<EvidenceInfo> getInfo(String evidenceAddress) {
        try {
            Evidence evidence = (Evidence) getContractService(evidenceAddress, Evidence.class);
            Tuple6<
                List<byte[]>,
                List<String>,
                List<byte[]>,
                List<byte[]>,
                List<BigInteger>,
                List<byte[]>
                > rawResult = evidence.getInfo().send();
            if (rawResult == null) {
                return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
            }

            List<byte[]> credentialHashList = rawResult.getValue1();
            List<String> issuerList = rawResult.getValue2();

            EvidenceInfo evidenceInfoData = new EvidenceInfo();
            evidenceInfoData.setCredentialHash(
                WeIdConstant.HEX_PREFIX + new String(credentialHashList.get(0))
                    + new String(credentialHashList.get(1)));

            List<String> signerStringList = new ArrayList<>();
            for (String addr : issuerList) {
                signerStringList.add(addr);
            }
            evidenceInfoData.setSigners(signerStringList);

            List<String> signaturesList = new ArrayList<>();
            List<byte[]> rlist = rawResult.getValue3();
            List<byte[]> slist = rawResult.getValue4();
            List<BigInteger> vlist = rawResult.getValue5();
            byte v;
            byte[] r;
            byte[] s;
            for (int index = 0; index < rlist.size(); index++) {
                v = (byte) (vlist.get(index).intValue());
                r = rlist.get(index);
                s = slist.get(index);
                SignatureData sigData = new SignatureData(v, r, s);
                signaturesList.add(
                    new String(
                        DataToolUtils.base64Encode(
                            DataToolUtils.simpleSignatureSerialization(sigData)
                        ),
                        StandardCharsets.UTF_8
                    )
                );
            }
            evidenceInfoData.setSignatures(signaturesList);
            return new ResponseData<>(evidenceInfoData, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("get evidence failed.", e);
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }
}
