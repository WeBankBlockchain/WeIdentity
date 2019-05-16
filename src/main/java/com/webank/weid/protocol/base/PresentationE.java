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
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.protocol.inf.RawSerializer;

/**
 * Created by Junqi Zhang on 2019/4/4.
 */
@Data
@EqualsAndHashCode
public class PresentationE implements RawSerializer {

    /**
     * Required: The context field.
     */
    private List<String> context = new ArrayList<String>();

    private List<String> type = new ArrayList<String>();

    private List<CredentialPojo> verifiableCredential;

    private Map<String, String> proof;

    /**
     * 获取公钥Id，用于验证的时候识别publicKey.
     * @return
     */
    public String getVerificationMethod(){
        return getValueFromProof(ParamKeyConstant.VERIFICATION_METHOD);
    }
    
    /**
     * 获取签名值Signature.
     * @return 返回签名字符串Signature.
     */
    public String getSignature() {
        return getValueFromProof(ParamKeyConstant.PRESENTATION_SIGNATURE);
    }
    
    /**
     * 获取challenge随机值.
     * @return 返回challenge随机值.
     */
    public String getNonce() {
        return getValueFromProof(ParamKeyConstant.NONCE);
    }
    
    private String getValueFromProof(String key){
        if (proof != null) {
            return proof.get(key);
        }
        return StringUtils.EMPTY; 
    }
}
