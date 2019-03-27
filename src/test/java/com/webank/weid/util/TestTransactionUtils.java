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

package com.webank.weid.util;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.junit.Assert;
import org.junit.Test;

/**
 * test DateUtils.
 *
 * @author chaoxinhu
 */
public class TestTransactionUtils {

    @Test
    public void testParams() {
        BigInteger nonce = TransactionUtils.getNonce();
        StaticArray<Int256> array = TransactionUtils.getParamCreated(8);
        Map<String, Object> cptJsonSchemaMap = new LinkedHashMap<>();
        cptJsonSchemaMap.put("title", "a CPT schema");
        String cptJsonSchemaStr = JsonUtil.objToJsonStr(cptJsonSchemaMap);
        String completeStr = TransactionUtils.complementCptJsonSchema(cptJsonSchemaStr);
        StaticArray<Bytes32> array1 = TransactionUtils.getParamJsonSchema(completeStr);
        Assert.assertNotNull(nonce);
        Assert.assertNotNull(array);
        Assert.assertNotNull(array1);
    }

    @Test
    public void testBuildParams() throws Exception {
        String weidInput = "{\"publicKey\":\"70537665785763632951200438731252630131035197449894"
            + "31221067702996992390039255438365261578388688523991111186187372079349839639924734"
            + "406270591552495358668267\"}";
        String cptInput = "{\"cptJsonSchema\":{\"title\":\"a CPT schema\"},\"cptSignature\":\"HJ"
            + "PbDmoi39xgZBGi/aj1zB6VQL5QLyt4qTV6GOvQwzfgUJEZTazKZXe1dRg5aCt8Q44GwNF2k+l1rfhpY1h"
            + "c/ls=\",\"weId\":\"did:weid:0xc0594581636589876d8bf3455e1844f0cc0d8c19\"}";
        String auInput = "{\"name\":\"Sample College\",\"weId\":\"did:weid:0xc5ead7a40f13a8b7b6"
            + "111691043f5936537a55ac\"}";
        List<Type> weidList = TransactionUtils.buildCreateWeIdInputParameters(weidInput);
        List<Type> cptList = TransactionUtils.buildRegisterCptInputParameters(cptInput);
        List<Type> auList = TransactionUtils.buildAuthorityIssuerInputParameters(auInput);
        Assert.assertNotNull(weidList);
        Assert.assertNotNull(cptList);
        Assert.assertNotNull(auList);
    }

    @Test
    public void sendTransactionNullTest() throws Exception {
        Assert.assertNull(TransactionUtils.sendTransaction(null, StringUtils.EMPTY));
    }
}
