

package com.webank.weid.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.fisco.bcos.sdk.abi.datatypes.DynamicArray;
import org.fisco.bcos.sdk.abi.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.abi.datatypes.StaticArray;
import org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int256;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint8;

import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.WeIdConstant;

/**
 * test DataToolUtils.
 *
 * @author v_wbjnzhang
 */
public class TestDataTypetUtils {

    private static final Logger logger = LoggerFactory.getLogger(TestDataTypetUtils.class);

    @Test
    public void testBytesArrayToBytes32() {

        byte[] bytes1 = new byte[32];
        Bytes32 result = com.webank.weid.blockchain.util.DataToolUtils.bytesArrayToBytes32(bytes1);
        Assert.assertNotNull(result);
    }

    @Test
    public void testStringToBytes32() {

        String str = "abcdefghijklmnopqrstuvwxyzABCDEF";// length is 32
        Bytes32 result = com.webank.weid.blockchain.util.DataToolUtils.stringToBytes32(str);
        Assert.assertNotNull(result);
    }

    @Test
    public void testBytes32ToBytesArray() {

        String str = "abcdefghijklmnopqrstuvwxyzABCDEF";
        Bytes32 bytes32 = com.webank.weid.blockchain.util.DataToolUtils.stringToBytes32(str);
        byte[] result = com.webank.weid.blockchain.util.DataToolUtils.bytes32ToBytesArray(bytes32);
        Assert.assertNotNull(result);
    }

    @Test
    public void testBytes32ToString() {

        String str = "abcdefghijklmnopqrstuvwxyzABCDEF";
        Bytes32 bytes32 = com.webank.weid.blockchain.util.DataToolUtils.stringToBytes32(str);
        String result = com.webank.weid.blockchain.util.DataToolUtils.bytes32ToString(bytes32);
        Assert.assertEquals(result, str);

        str = "";
        bytes32 = com.webank.weid.blockchain.util.DataToolUtils.stringToBytes32(str);
        result = com.webank.weid.blockchain.util.DataToolUtils.bytes32ToString(bytes32);
        Assert.assertEquals(result, str);

        str = " slfjds ljlfs ";
        bytes32 = com.webank.weid.blockchain.util.DataToolUtils.stringToBytes32(str);
        result = com.webank.weid.blockchain.util.DataToolUtils.bytes32ToString(bytes32);
        Assert.assertEquals(result, str.trim());
    }

    @Test
    public void testBytes32ToStringWithoutTrim() {

        String str = "abcdefghijklmnopqrstuvwxyzABCDEF";// length is 32
        Bytes32 bytes32 = com.webank.weid.blockchain.util.DataToolUtils.stringToBytes32(str);
        String result = com.webank.weid.blockchain.util.DataToolUtils.bytes32ToStringWithoutTrim(bytes32);
        Assert.assertEquals(result, str);

        str = "";
        bytes32 = com.webank.weid.blockchain.util.DataToolUtils.stringToBytes32(str);
        result = com.webank.weid.blockchain.util.DataToolUtils.bytes32ToStringWithoutTrim(bytes32);
        Assert.assertEquals(result.length(), 32);

        str = " slfjds ljlfs ";
        bytes32 = com.webank.weid.blockchain.util.DataToolUtils.stringToBytes32(str);
        result = com.webank.weid.blockchain.util.DataToolUtils.bytes32ToStringWithoutTrim(bytes32);
        Assert.assertEquals(result.length(), 32);
    }

    @Test
    public void testIntToUint256() {

        int n = 0;
        Uint256 result = com.webank.weid.blockchain.util.DataToolUtils.intToUint256(n);
        int m = com.webank.weid.blockchain.util.DataToolUtils.uint256ToInt(result);
        Assert.assertEquals(n, m);

        n = 9999999;
        result = com.webank.weid.blockchain.util.DataToolUtils.intToUint256(n);
        m = com.webank.weid.blockchain.util.DataToolUtils.uint256ToInt(result);
        Assert.assertEquals(n, m);
    }

    @Test
    public void testIntToInt256() {

        int n = 0;
        Int256 result = com.webank.weid.blockchain.util.DataToolUtils.intToInt256(n);
        int m = com.webank.weid.blockchain.util.DataToolUtils.int256ToInt(result);
        Assert.assertEquals(n, m);

        n = 9999999;
        result = com.webank.weid.blockchain.util.DataToolUtils.intToInt256(n);
        m = com.webank.weid.blockchain.util.DataToolUtils.int256ToInt(result);
        Assert.assertEquals(n, m);

        n = -9999999;
        result = com.webank.weid.blockchain.util.DataToolUtils.intToInt256(n);
        m = com.webank.weid.blockchain.util.DataToolUtils.int256ToInt(result);
        Assert.assertEquals(n, m);
    }

    @Test
    public void testLongToInt256() {

        long n = 0L;
        Int256 result = com.webank.weid.blockchain.util.DataToolUtils.longToInt256(n);
        long m = com.webank.weid.blockchain.util.DataToolUtils.int256ToLong(result);
        Assert.assertEquals(n, m);

        n = 999999999999999L;
        result = com.webank.weid.blockchain.util.DataToolUtils.longToInt256(n);
        m = com.webank.weid.blockchain.util.DataToolUtils.int256ToLong(result);
        Assert.assertEquals(n, m);

        n = -999999999999999L;
        result = com.webank.weid.blockchain.util.DataToolUtils.longToInt256(n);
        m = com.webank.weid.blockchain.util.DataToolUtils.int256ToLong(result);
        Assert.assertEquals(n, m);
    }

    @Test
    public void testLongArrayToInt256StaticArray() {

        long[] array = {8L, 5555L, 64L, 0L, -147L};
        StaticArray<Int256> result = com.webank.weid.blockchain.util.DataToolUtils.longArrayToInt256StaticArray(array);
        DynamicArray<Int256> result1 = new DynamicArray<Int256>(result.getValue());

        long[] array1 = com.webank.weid.blockchain.util.DataToolUtils.int256DynamicArrayToLongArray(result1);
        for (int i = 0; i < array.length; i++) {
            Assert.assertEquals(array[i], array1[i]);
        }
    }

    @Test
    public void teststringArrayToBytes32StaticArray() {

        String[] array = {"book", "student", "person", "apple"};
        StaticArray<Bytes32> result = com.webank.weid.blockchain.util.DataToolUtils.stringArrayToBytes32StaticArray(array);
        DynamicArray<Bytes32> result1 = new DynamicArray<Bytes32>(result.getValue());

        String[] array1 = com.webank.weid.blockchain.util.DataToolUtils.bytes32DynamicArrayToStringArrayWithoutTrim(result1);
        for (int i = 0; i < array.length; i++) {
            logger.info("---{}----", array1[i]);
        }
        Assert.assertNotNull(array1);
    }

    @Test
    public void testIntToUint8() {

        int n = 0;
        Uint8 result = com.webank.weid.blockchain.util.DataToolUtils.intToUnt8(n);
        int m = com.webank.weid.blockchain.util.DataToolUtils.uint8ToInt(result);
        Assert.assertEquals(n, m);
        n = 255;
        result = com.webank.weid.blockchain.util.DataToolUtils.intToUnt8(n);
        m = com.webank.weid.blockchain.util.DataToolUtils.uint8ToInt(result);
        Assert.assertEquals(n, m);
    }

    @Test
    public void testStringToDynamicBytes() {

        String str = "";
        DynamicBytes result = com.webank.weid.blockchain.util.DataToolUtils.stringToDynamicBytes(str);
        String newstr = com.webank.weid.blockchain.util.DataToolUtils.dynamicBytesToString(result);
        Assert.assertEquals(str, newstr);

        str = "dssfdsgs";
        result = com.webank.weid.blockchain.util.DataToolUtils.stringToDynamicBytes(str);
        newstr = com.webank.weid.blockchain.util.DataToolUtils.dynamicBytesToString(result);
        Assert.assertEquals(str, newstr);

        str = "  dssfdsdd   gs  ";
        result = com.webank.weid.blockchain.util.DataToolUtils.stringToDynamicBytes(str);
        newstr = com.webank.weid.blockchain.util.DataToolUtils.dynamicBytesToString(result);
        Assert.assertEquals(str, newstr);

        str = "aaaaaaaaaabbbbbbbbbbccccccccccddaaaa";
        result = com.webank.weid.blockchain.util.DataToolUtils.stringToDynamicBytes(str);
        newstr = com.webank.weid.blockchain.util.DataToolUtils.dynamicBytesToString(result);
        Assert.assertEquals(str, newstr);
    }

    @Test
    public void testHashConversion() {
        String hash = com.webank.weid.blockchain.util.DataToolUtils.hash(UUID.randomUUID().toString());
        logger.info("hash {}", hash);
        byte[] convertedHash = com.webank.weid.blockchain.util.DataToolUtils.convertHashStrIntoHashByte32Array(hash);
        Assert.assertEquals(convertedHash.length, WeIdConstant.BYTES32_FIXED_LENGTH.intValue());
        String convertedBackStr = com.webank.weid.blockchain.util.DataToolUtils
            .convertHashByte32ArrayIntoHashStr(convertedHash);
        Assert.assertEquals(hash, convertedBackStr);
        Assert.assertEquals(hash.length(), WeIdConstant.BYTES32_FIXED_LENGTH * 2 + 2);
        List<String> listX = new ArrayList(
            Arrays.asList("a", "b", "c", "d", "f", "f", "g"));
        List<String> listY = new ArrayList(Arrays.asList("a", "c", "f", "f", "g"));
        List<Boolean> result = DataToolUtils.strictCheckExistence(listX, listY);
        Assert.assertTrue(result.get(0) && result.get(2));
        Assert.assertFalse(result.get(1) || result.get(3));
        Assert.assertEquals(result.size(), 7);
    }
}
