/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

import org.bcos.web3j.abi.datatypes.DynamicArray;
import org.bcos.web3j.abi.datatypes.DynamicBytes;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.bcos.web3j.abi.datatypes.generated.Uint8;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * test DataTypetUtils.
 * 
 * @author v_wbjnzhang
 *
 */
public class TestDataTypetUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(TestDataTypetUtils.class);

    @Test
    public void testBytesArrayToBytes32() {

        byte[] bytes1 = new byte[32];
        Bytes32 result = DataTypetUtils.bytesArrayToBytes32(bytes1);
        Assert.assertNotNull(result);
    }

    @Test
    public void testStringToBytes32() {

        String str = "abcdefghijklmnopqrstuvwxyzABCDEF";// length is 32
        Bytes32 result = DataTypetUtils.stringToBytes32(str);
        Assert.assertNotNull(result);
    }

    @Test
    public void testBytes32ToBytesArray() {

        String str = "abcdefghijklmnopqrstuvwxyzABCDEF";
        Bytes32 bytes32 = DataTypetUtils.stringToBytes32(str);
        byte[] result = DataTypetUtils.bytes32ToBytesArray(bytes32);
        Assert.assertNotNull(result);
    }

    @Test
    public void testBytes32ToString() {

        String str = "abcdefghijklmnopqrstuvwxyzABCDEF";
        Bytes32 bytes32 = DataTypetUtils.stringToBytes32(str);
        String result = DataTypetUtils.bytes32ToString(bytes32);
        Assert.assertEquals(result, str);

        str = "";
        bytes32 = DataTypetUtils.stringToBytes32(str);
        result = DataTypetUtils.bytes32ToString(bytes32);
        Assert.assertEquals(result, str);

        str = " slfjds ljlfs ";
        bytes32 = DataTypetUtils.stringToBytes32(str);
        result = DataTypetUtils.bytes32ToString(bytes32);
        Assert.assertEquals(result, str.trim());
    }

    @Test
    public void testBytes32ToStringWithoutTrim() {

        String str = "abcdefghijklmnopqrstuvwxyzABCDEF";// length is 32
        Bytes32 bytes32 = DataTypetUtils.stringToBytes32(str);
        String result = DataTypetUtils.bytes32ToStringWithoutTrim(bytes32);
        Assert.assertEquals(result, str);

        str = "";
        bytes32 = DataTypetUtils.stringToBytes32(str);
        result = DataTypetUtils.bytes32ToStringWithoutTrim(bytes32);
        Assert.assertEquals(result.length(), 32);

        str = " slfjds ljlfs ";
        bytes32 = DataTypetUtils.stringToBytes32(str);
        result = DataTypetUtils.bytes32ToStringWithoutTrim(bytes32);
        Assert.assertEquals(result.length(), 32);
    }

    @Test
    public void testIntToUint256() {
        
        int n = 0;
        Uint256 result = DataTypetUtils.intToUint256(n);
        int m = DataTypetUtils.uint256ToInt(result);
        Assert.assertEquals(n, m);

        n = 9999999;
        result = DataTypetUtils.intToUint256(n);
        m = DataTypetUtils.uint256ToInt(result);
        Assert.assertEquals(n, m);
    }

    @Test
    public void testIntToInt256() {
        
        int n = 0;
        Int256 result = DataTypetUtils.intToInt256(n);
        int m = DataTypetUtils.int256ToInt(result);
        Assert.assertEquals(n, m);

        n = 9999999;
        result = DataTypetUtils.intToInt256(n);
        m = DataTypetUtils.int256ToInt(result);
        Assert.assertEquals(n, m);

        n = -9999999;
        result = DataTypetUtils.intToInt256(n);
        m = DataTypetUtils.int256ToInt(result);
        Assert.assertEquals(n, m);
    }

    @Test
    public void testLongToInt256() {
        
        long n = 0L;
        Int256 result = DataTypetUtils.longToInt256(n);
        long m = DataTypetUtils.int256ToLong(result);
        Assert.assertEquals(n, m);

        n = 999999999999999L;
        result = DataTypetUtils.longToInt256(n);
        m = DataTypetUtils.int256ToLong(result);
        Assert.assertEquals(n, m);

        n = -999999999999999L;
        result = DataTypetUtils.longToInt256(n);
        m = DataTypetUtils.int256ToLong(result);
        Assert.assertEquals(n, m);
    }

    @Test
    public void testLongArrayToInt256StaticArray() {
        
        long[] array = {8L, 5555L, 64L, 0L, -147L};
        StaticArray<Int256> result = DataTypetUtils.longArrayToInt256StaticArray(array);
        DynamicArray<Int256> result1 = new DynamicArray<Int256>(result.getValue());

        long[] array1 = DataTypetUtils.int256DynamicArrayToLongArray(result1);
        for (int i = 0; i < array.length; i++) {
            Assert.assertEquals(array[i], array1[i]);
        }
    }

    @Test
    public void teststringArrayToBytes32StaticArray() {
        
        String[] array = {"book", "student", "person", "apple"};
        StaticArray<Bytes32> result = DataTypetUtils.stringArrayToBytes32StaticArray(array);
        DynamicArray<Bytes32> result1 = new DynamicArray<Bytes32>(result.getValue());

        String[] array1 = DataTypetUtils.bytes32DynamicArrayToStringArrayWithoutTrim(result1);
        for (int i = 0; i < array.length; i++) {
            logger.info("---" + array1[i] + "---");
        }
        Assert.assertNotNull(array1);
    }

    @Test
    public void testIntToUint8() {

        int n = 0;
        Uint8 result = DataTypetUtils.intToUnt8(n);
        int m = DataTypetUtils.uint8ToInt(result);
        Assert.assertEquals(n, m);
        n = 255;
        result = DataTypetUtils.intToUnt8(n);
        m = DataTypetUtils.uint8ToInt(result);
        Assert.assertEquals(n, m);
    }

    @Test
    public void testStringToDynamicBytes() {

        String str = "";
        DynamicBytes result = DataTypetUtils.stringToDynamicBytes(str);
        String newstr = DataTypetUtils.dynamicBytesToString(result);
        Assert.assertEquals(str, newstr);

        str = "dssfdsgs";
        result = DataTypetUtils.stringToDynamicBytes(str);
        newstr = DataTypetUtils.dynamicBytesToString(result);
        Assert.assertEquals(str, newstr);

        str = "  dssfdsdd   gs  ";
        result = DataTypetUtils.stringToDynamicBytes(str);
        newstr = DataTypetUtils.dynamicBytesToString(result);
        Assert.assertEquals(str, newstr);

        str = "aaaaaaaaaabbbbbbbbbbccccccccccddaaaa";
        result = DataTypetUtils.stringToDynamicBytes(str);
        newstr = DataTypetUtils.dynamicBytesToString(result);
        Assert.assertEquals(str, newstr);
    }
}
