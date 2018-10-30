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

package com.webank.weid.full;

/**
 * The function of this class is to test the basic data required
 * 
 * @author v_wbgyang
 *
 */
public class TestData {
    
    /**
     * you may need to add public key after you create weIdentity DID,This property is the 
     * public key type,but for now the value is fixed and you don't need to modify it
     * 
     */
    public static String publicKeyType = "Secp256k1" ;
    
    /**
     * after you create weIdentity DID,you may need to add service information which 
     * indicates the type of service,You can modify this value but note that
     * there is a length limit
     * 
     */
    public static String serviceType = "drivingCardService";
    
    /**
     * after you create weIdentity DID,you may need to add service information which 
     * indicates the serviceEndpoint of service,You can modify this value
     * 
     */
    public static String serviceEndpoint = "https://weidentity.webank.com/endpoint/xxxxx";
    
    /**
     * you may need to add authorization after you create weId,This property is the 
     * public key type,but for now the value is fixed and you don't need to modify it
     * 
     */
    public static String authenticationType = "RsaSignatureAuthentication2018";
    
    /**
     * when you register an authority,you may need to get a name,which indicates
     * the name of the authority
     * 
     */
    public static String authorityIssuerName = "weBank";
    
    /**
     * when you register an authority,you may need to get a accValue,which indicates
     * the accValue of the authority, the default value for testing is 0
     * 
     */
    public static String authorityIssuerAccValue = "0";
    
    /**
     * a valid jsonSchema template needed to register CPT
     */
    public static String schema =
        "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"title\":\"/etc/fstab\",\"description\":\"JSON representation of /etc/fstab\",\"type\":\"object\",\"properties\":{\"swap\":{\"$ref\":\"#/definitions/mntent\"}},\"patternProperties\":{\"^/([^/]+(/[^/]+)*)?$\":{\"$ref\":\"#/definitions/mntent\"}},\"required\":[\"/\",\"swap\"],\"additionalProperties\":false,\"definitions\":{\"mntent\":{\"title\":\"mntent\",\"description\":\"An fstab entry\",\"type\":\"object\",\"properties\":{\"device\":{\"type\":\"string\"},\"fstype\":{\"type\":\"string\"},\"options\":{\"type\":\"array\",\"minItems\":1,\"items\":{\"type\":\"string\"}},\"dump\":{\"type\":\"integer\",\"minimum\":0},\"fsck\":{\"type\":\"integer\",\"minimum\":0}},\"required\":[\"device\",\"fstype\"],\"additionalItems\":false}}}";

    /**
     * valid data corresponding to template in CPT
     */
    public static String schemaData =
        "{\"/\":{\"device\":\"/dev/sda2\",\"fstype\":\"btrfs\",\"options\":[\"ssd\"]},\"swap\":{\"device\":\"/dev/sda2\",\"fstype\":\"swap\"},\"/tmp\":{\"device\":\"tmpfs\",\"fstype\":\"tmpfs\",\"options\":[\"size=64M\"]},\"/var/lib/mysql\":{\"device\":\"/dev/data/mysql\",\"fstype\":\"btrfs\"}}";

    /**
     * an invalid template may be required in the CPT registration case
     */
    public static String schemaDataInvalid =
        "{\"/\":{\"device111\":\"/dev/sda2\",\"fstype\":\"btrfs\",\"options\":[\"ssd\"]},\"swap\":{\"device\":\"/dev/sda2\",\"fstype\":\"swap\"},\"/tmp\":{\"device\":\"tmpfs\",\"fstype\":\"tmpfs\",\"options\":[\"size=64M\"]},\"/var/lib/mysql\":{\"device\":\"/dev/data/mysql\",\"fstype\":\"btrfs\"}}";
 
}
