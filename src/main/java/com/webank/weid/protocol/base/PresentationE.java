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

package com.webank.weid.protocol.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.protocol.inf.IProof;
import com.webank.weid.protocol.inf.RawSerializer;

/**
 * Created by Junqi Zhang on 2019/4/4.
 */
@Data
@EqualsAndHashCode
public class PresentationE implements RawSerializer, IProof {

    /**
     * Required: The context field.
     */
    private List<String> context = new ArrayList<String>();

    private List<String> type = new ArrayList<String>();

    private List<CredentialPojo> verifiableCredential;

    private Map<String, Object> proof;

    /**
     * 获取公钥Id，用于验证的时候识别publicKey.
     * @return
     */
    public String getVerificationMethod() {
        return getValueFromProof(proof, ParamKeyConstant.PROOF_VERIFICATION_METHOD).toString();
    }
    
    /**
     * 获取challenge随机值.
     * @return 返回challenge随机值.
     */
    public String getNonce() {
        return getValueFromProof(proof, ParamKeyConstant.PROOF_NONCE).toString();
    }

    /**
     * 获取签名值Signature.
     * @return 返回签名字符串Signature.
     */
    public String getSignature() {
        return getValueFromProof(proof, ParamKeyConstant.PROOF_SIGNATURE).toString();
    }
    
    /**
     * 向proof中添加key-value.
     * @param key proof中的 key
     * @param value proof中key的value
     */
    public void putProofValue(String key, Object value) {
        if (proof == null) {
            proof = new HashMap<>();
        }
        proof.put(key, value);
    }
}
