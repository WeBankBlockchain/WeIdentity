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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.DynamicArray;
import org.bcos.web3j.abi.datatypes.DynamicBytes;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.bcos.web3j.abi.datatypes.generated.Uint8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.exception.DataTypeCastException;

/**
 * Data type conversion utilities between solidity data type and java data type.
 *
 * @author lingfenghe
 */
public final class DataTypetUtils {

    private static final Logger logger = LoggerFactory.getLogger(DataTypetUtils.class);

    /**
     * Bytes array to bytes 32.
     *
     * @param byteValue the byte value
     * @return the bytes 32
     */
    public static Bytes32 bytesArrayToBytes32(byte[] byteValue) {

        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return new Bytes32(byteValueLen32);
    }

    /**
     * String to bytes 32.
     *
     * @param string the string
     * @return the bytes 32
     */
    public static Bytes32 stringToBytes32(String string) {

        byte[] byteValueLen32 = new byte[32];
        if (StringUtils.isEmpty(string)) {
            return new Bytes32(byteValueLen32);
        }
        try {
            byte[] byteValue = string.getBytes(WeIdConstant.UTF_8);
            System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        } catch (UnsupportedEncodingException e) {
            logger.error("stringToBytes32 is exception", e);
            throw new DataTypeCastException(e.getCause());
        }
        return new Bytes32(byteValueLen32);
    }

    /**
     * Bytes 32 to bytes array.
     *
     * @param bytes32 the bytes 32
     * @return the byte[]
     */
    public static byte[] bytes32ToBytesArray(Bytes32 bytes32) {

        byte[] bytesArray = new byte[32];
        byte[] bytes32Value = bytes32.getValue();
        System.arraycopy(bytes32Value, 0, bytesArray, 0, 32);
        return bytesArray;
    }

    /**
     * Convert a Byte32 data to Java String. IMPORTANT NOTE: Byte to String is not 1:1 mapped. So -
     * Know your data BEFORE do the actual transform! For example, Deximal Bytes, or ASCII Bytes are
     * OK to be in Java String, but Encrypted Data, or raw Signature, are NOT OK.
     *
     * @param bytes32 the bytes 32
     * @return String
     */
    public static String bytes32ToString(Bytes32 bytes32) {

        String str = null;
        try {
            str = new String(bytes32.getValue(), WeIdConstant.UTF_8);
        } catch (UnsupportedEncodingException e) {
            logger.error("bytes32ToString is exception", e);
            throw new DataTypeCastException(e.getCause());
        }
        return str.trim();
    }

    /**
     * Bytes 32 to string without trim.
     *
     * @param bytes32 the bytes 32
     * @return the string
     */
    public static String bytes32ToStringWithoutTrim(Bytes32 bytes32) {

        byte[] strs = bytes32.getValue();
        String str = null;
        try {
            str = new String(strs, WeIdConstant.UTF_8);
        } catch (UnsupportedEncodingException e) {
            logger.error("bytes32ToStringWithoutTrim is exception", e);
            throw new DataTypeCastException(e.getCause());
        }
        return str;
    }

    /**
     * Int to uint 256.
     *
     * @param value the value
     * @return the uint 256
     */
    public static Uint256 intToUint256(int value) {
        return new Uint256(new BigInteger(String.valueOf(value)));
    }

    /**
     * Uint 256 to int.
     *
     * @param value the value
     * @return the int
     */
    public static int uint256ToInt(Uint256 value) {
        return value.getValue().intValue();
    }

    /**
     * String to dynamic bytes.
     *
     * @param input the input
     * @return the dynamic bytes
     */
    public static DynamicBytes stringToDynamicBytes(String input) {

        DynamicBytes dynamicBytes = null;
        try {
            dynamicBytes = new DynamicBytes(input.getBytes(WeIdConstant.UTF_8));
        } catch (UnsupportedEncodingException e) {
            logger.error("stringToDynamicBytes is exception", e);
            throw new DataTypeCastException(e.getCause());
        }
        return dynamicBytes;
    }

    /**
     * Dynamic bytes to string.
     *
     * @param input the input
     * @return the string
     */
    public static String dynamicBytesToString(DynamicBytes input) {

        String str = null;
        try {
            str = new String(input.getValue(), WeIdConstant.UTF_8);
        } catch (UnsupportedEncodingException e) {
            logger.error("dynamicBytesToString is exception", e);
            throw new DataTypeCastException(e.getCause());
        }
        return str;
    }

    /**
     * Int to int 256.
     *
     * @param value the value
     * @return the int 256
     */
    public static Int256 intToInt256(int value) {
        return new Int256(value);
    }

    /**
     * Int 256 to int.
     *
     * @param value the value
     * @return the int
     */
    public static int int256ToInt(Int256 value) {
        return value.getValue().intValue();
    }

    /**
     * Int to unt 8.
     *
     * @param value the value
     * @return the uint 8
     */
    public static Uint8 intToUnt8(int value) {
        return new Uint8(value);
    }

    /**
     * Uint 8 to int.
     *
     * @param value the value
     * @return the int
     */
    public static int uint8ToInt(Uint8 value) {
        return value.getValue().intValue();
    }

    /**
     * Long to int 256.
     *
     * @param value the value
     * @return the int 256
     */
    public static Int256 longToInt256(long value) {
        return new Int256(value);
    }

    /**
     * Int 256 to long.
     *
     * @param value the value
     * @return the long
     */
    public static long int256ToLong(Int256 value) {
        return value.getValue().longValue();
    }

    /**
     * Long array to int 256 static array.
     *
     * @param longArray the long array
     * @return the static array
     */
    public static StaticArray<Int256> longArrayToInt256StaticArray(long[] longArray) {
        List<Int256> int256List = new ArrayList<Int256>();
        for (int i = 0; i < longArray.length; i++) {
            int256List.add(longToInt256(longArray[i]));
        }
        StaticArray<Int256> in256StaticArray = new StaticArray<Int256>(int256List);
        return in256StaticArray;
    }

    /**
     * String array to bytes 32 static array.
     *
     * @param stringArray the string array
     * @return the static array
     */
    public static StaticArray<Bytes32> stringArrayToBytes32StaticArray(String[] stringArray) {

        List<Bytes32> bytes32List = new ArrayList<Bytes32>();
        for (int i = 0; i < stringArray.length; i++) {
            if (StringUtils.isNotEmpty(stringArray[i])) {
                bytes32List.add(stringToBytes32(stringArray[i]));
            } else {
                bytes32List.add(stringToBytes32(StringUtils.EMPTY));
            }
        }
        StaticArray<Bytes32> bytes32StaticArray = new StaticArray<Bytes32>(bytes32List);
        return bytes32StaticArray;
    }

    /**
     * String array to bytes 32 static array.
     *
     * @param addressArray the string array
     * @return the static array
     */
    public static StaticArray<Address> addressArrayToAddressStaticArray(Address[] addressArray) {

        List<Address> addressList = new ArrayList<>();
        for (int i = 0; i < addressArray.length; i++) {
            addressList.add(addressArray[i]);
        }
        StaticArray<Address> addressStaticArray = new StaticArray<Address>(addressList);
        return addressStaticArray;
    }

    /**
     * Bytes 32 dynamic array to string array without trim.
     *
     * @param bytes32DynamicArray the bytes 32 dynamic array
     * @return the string[]
     */
    public static String[] bytes32DynamicArrayToStringArrayWithoutTrim(
        DynamicArray<Bytes32> bytes32DynamicArray) {

        List<Bytes32> bytes32List = bytes32DynamicArray.getValue();
        String[] stringArray = new String[bytes32List.size()];
        for (int i = 0; i < bytes32List.size(); i++) {
            stringArray[i] = bytes32ToStringWithoutTrim(bytes32List.get(i));
        }
        return stringArray;
    }

    /**
     * Int 256 dynamic array to long array.
     *
     * @param int256DynamicArray the int 256 dynamic array
     * @return the long[]
     */
    public static long[] int256DynamicArrayToLongArray(DynamicArray<Int256> int256DynamicArray) {

        List<Int256> int256list = int256DynamicArray.getValue();
        long[] longArray = new long[int256list.size()];
        for (int i = 0; i < int256list.size(); i++) {
            longArray[i] = int256ToLong(int256list.get(i));
        }
        return longArray;
    }
}
