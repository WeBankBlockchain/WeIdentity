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

package com.webank.weid.common;

import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RegisterCptArgs;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.request.UpdateCptArgs;
import com.webank.weid.protocol.request.VerifyCredentialArgs;
import com.webank.weid.util.DateUtils;
import java.text.ParseException;
import java.util.Date;

/**
 * requestUtil
 *
 * @author v_wbgyang
 */
public class RequestUtil {

    /**
     * jsonSchema for register
     */
    static String schema =
        "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"title\":\"/etc/fstab\",\"description\":\"JSON representation of /etc/fstab\",\"type\":\"object\",\"properties\":{\"swap\":{\"$ref\":\"#/definitions/mntent\"}},\"patternProperties\":{\"^/([^/]+(/[^/]+)*)?$\":{\"$ref\":\"#/definitions/mntent\"}},\"required\":[\"/\",\"swap\"],\"additionalProperties\":false,\"definitions\":{\"mntent\":{\"title\":\"mntent\",\"description\":\"An fstab entry\",\"type\":\"object\",\"properties\":{\"device\":{\"type\":\"string\"},\"fstype\":{\"type\":\"string\"},\"options\":{\"type\":\"array\",\"minItems\":1,\"items\":{\"type\":\"string\"}},\"dump\":{\"type\":\"integer\",\"minimum\":0},\"fsck\":{\"type\":\"integer\",\"minimum\":0}},\"required\":[\"device\",\"fstype\"],\"additionalItems\":false}}}";

    /**
     * jsonSchema for update
     */
    static String schema1 =
        "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"title\":\"/etc/fstab1\",\"description\":\"JSON representation of /etc/fstab\",\"type\":\"object\",\"properties\":{\"swap\":{\"$ref\":\"#/definitions/mntent\"}},\"patternProperties\":{\"^/([^/]+(/[^/]+)*)?$\":{\"$ref\":\"#/definitions/mntent\"}},\"required\":[\"/\",\"swap\"],\"additionalProperties\":false,\"definitions\":{\"mntent\":{\"title\":\"mntent\",\"description\":\"An fstab entry\",\"type\":\"object\",\"properties\":{\"device\":{\"type\":\"string\"},\"fstype\":{\"type\":\"string\"},\"options\":{\"type\":\"array\",\"minItems\":1,\"items\":{\"type\":\"string\"}},\"dump\":{\"type\":\"integer\",\"minimum\":0},\"fsck\":{\"type\":\"integer\",\"minimum\":0}},\"required\":[\"device\",\"fstype\"],\"additionalItems\":false}}}";

    /**
     * claimData for register
     */
    static String schema1Data =
        "{\"/\":{\"device\":\"/dev/sda1\",\"fstype\":\"btrfs\",\"options\":[\"ssd\"]},\"swap\":{\"device\":\"/dev/sda2\",\"fstype\":\"swap\"},\"/tmp\":{\"device\":\"tmpfs\",\"fstype\":\"tmpfs\",\"options\":[\"size=64M\"]},\"/var/lib/mysql\":{\"device\":\"/dev/data/mysql\",\"fstype\":\"btrfs\"}}";

    /**
     * claimData for update
     */
    static String schemaData =
        "{\"/\":{\"device\":\"/dev/sda2\",\"fstype\":\"btrfs\",\"options\":[\"ssd\"]},\"swap\":{\"device\":\"/dev/sda2\",\"fstype\":\"swap\"},\"/tmp\":{\"device\":\"tmpfs\",\"fstype\":\"tmpfs\",\"options\":[\"size=64M\"]},\"/var/lib/mysql\":{\"device\":\"/dev/data/mysql\",\"fstype\":\"btrfs\"}}";

    /**
     * request param for CptService.registerCpt
     */
    public static RegisterCptArgs registerCpt(int scene) throws Exception {

        RegisterCptArgs args = new RegisterCptArgs();
        WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
        args.setCptPublisherPrivateKey(weIdPrivateKey);
        switch (scene) {
            case 1: // scene is 1
                args.setCptJsonSchema(RequestUtil.schema);
                args.setCptPublisher("did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
                weIdPrivateKey.setPrivateKey(
                    "84259158061731800175730035500197147557630375762366333000754891654353899157503");
                break;
            case 2:
                break;
        }
        return args;
    }

    /**
     * request param for CptService.queryCpt
     */
    public static Integer queryCpt(int scene) {

        Integer value = null;
        switch (scene) {
            case 1: // scene is 1
                value = new Integer(148);
                break;
            case 2:
                break;
        }
        return value;
    }

    /**
     * request param for CptService.updateCpt
     */
    public static UpdateCptArgs updateCpt(int scene) throws Exception {

        UpdateCptArgs args = new UpdateCptArgs();
        WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
        args.setCptPublisherPrivateKey(weIdPrivateKey);
        switch (scene) {
            case 1: // scene is 1
                args.setCptJsonSchema(RequestUtil.schema1);
                args.setCptPublisher("did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
                weIdPrivateKey.setPrivateKey(
                    "84259158061731800175730035500197147557630375762366333000754891654353899157503");

                break;
            case 2:
                break;
        }
        return args;
    }

    /**
     * request param for AuthorityIssuerService.registerAuthorityIssuer
     */
    public static RegisterAuthorityIssuerArgs registerAuthorityIssuer(int scene) throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs = new RegisterAuthorityIssuerArgs();
        registerAuthorityIssuerArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        AuthorityIssuer args = new AuthorityIssuer();
        registerAuthorityIssuerArgs.setAuthorityIssuer(args);

        switch (scene) {
            case 1: // scene is 1
                args.setWeId("did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
                args.setCreated(new Date().getTime());
                args.setName("webank1");
                args.setAccValue("0");

                registerAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey("");
                break;
            case 2:
                break;
        }
        return registerAuthorityIssuerArgs;
    }

    /**
     * request param for AuthorityIssuerService.removeAuthorityIssuer
     */
    public static RemoveAuthorityIssuerArgs removeAuthorityIssuer(int scene) {

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs = new RemoveAuthorityIssuerArgs();
        removeAuthorityIssuerArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        switch (scene) {
            case 1: // scene is 1
                removeAuthorityIssuerArgs
                    .setWeId("did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
                removeAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey("");
                break;
            case 2:
                break;
        }
        return removeAuthorityIssuerArgs;
    }

    /**
     * request param for AuthorityIssuerService.isAuthorityIssuer
     */
    public static String isAuthorityIssuer(int scene) {

        String args = "";
        switch (scene) {
            case 1: // scene is 1
                args = "did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
                break;
            case 2:
                break;
        }
        return args;
    }

    /**
     * request param for AuthorityIssuerService.queryAuthorityIssuerInfo
     */
    public static String queryAuthorityIssuerInfo(int scene) {

        String args = "";
        switch (scene) {
            case 1: // scene is 1
                args = "did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
                break;
            case 2:
                break;
        }
        return args;
    }

    /**
     * request param for CredentialService.createCredential
     */
    public static CreateCredentialArgs createCredential(int scene) throws ParseException {

        CreateCredentialArgs args = new CreateCredentialArgs();
        switch (scene) {
            case 1: // scene is 1
                args.setClaim(RequestUtil.schema1Data);
                args.setCptId(155);
                args.setExpirationDate(
                    DateUtils.convertStringToDate("2019-10-11T18:09:42Z").getTime());
                args.setIssuer("did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
                WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
                weIdPrivateKey.setPrivateKey(
                    "84259158061731800175730035500197147557630375762366333000754891654353899157503");
                args.setWeIdPrivateKey(weIdPrivateKey);
                break;
        }
        return args;
    }

    /**
     * request param for CredentialService.verifyCredential
     */
    public static VerifyCredentialArgs verifyCredential(int scene, Credential result) {

        VerifyCredentialArgs args = new VerifyCredentialArgs();
        switch (scene) {
            case 1: // scene is 1
                args.setCredential(result);
                break;
            case 2:
                break;
        }
        return args;
    }

    /**
     * request param for CredentialService.verifyCredential
     */
    public static VerifyCredentialArgs verifyCredentialWithSpecifiedPubKey(
        int scene, Credential result) {

        VerifyCredentialArgs args = new VerifyCredentialArgs();
        switch (scene) {
            case 1: // scene is 1
                args.setCredential(result);
                WeIdPublicKey weIdPublicKey = new WeIdPublicKey();
                weIdPublicKey.setPublicKey(
                    "13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342");
                args.setWeIdPublicKey(weIdPublicKey);

                break;
            case 2:
                break;
        }
        return args;
    }

    /**
     * request param for WeIDService.createWeID
     */
    public static CreateWeIdArgs createWeId(int scene) {

        CreateWeIdArgs args = new CreateWeIdArgs();
        switch (scene) {
            case 1: // scene is 1
                args.setPublicKey(
                    "13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342");
                break;
            case 2:
                break;
        }
        return args;
    }

    /**
     * request param for WeIDService.getWeIDDocument
     */
    public static String getWeIdDocument(int scene) {

        String args = null;
        switch (scene) {
            case 1: // scene is 1
                args = "did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
                break;
            case 2:
                break;
        }
        return args;
    }

    /**
     * request param for WeIDService.setPublicKey
     */
    public static SetPublicKeyArgs setPublicKey(int scene) {

        SetPublicKeyArgs args = new SetPublicKeyArgs();
        switch (scene) {
            case 1: // scene is 1
                args.setWeId("did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
                args.setType("Secp256k1");
                args.setPublicKey(
                    "13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342");
                break;
            case 2:
                break;
        }
        return args;
    }

    /**
     * request param for WeIDService.setService
     */
    public static SetServiceArgs setService(int scene) {

        SetServiceArgs args = new SetServiceArgs();
        switch (scene) {
            case 1: // scene is 1
                args.setWeId("did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
                args.setType("drivingCardService");
                args.setServiceEndpoint("https://weidentity.webank.com/endpoint/8377464");
                break;
            case 2:
                break;
        }
        return args;
    }

    /**
     * request param for WeIDService.setAuthenticate
     */
    public static SetAuthenticationArgs setAuthentication(int scene) {

        SetAuthenticationArgs args = new SetAuthenticationArgs();
        switch (scene) {
            case 1: // scene is 1
                args.setWeId("did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
                args.setPublicKey(
                    "13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342");
                args.setType("RsaSignatureAuthentication2018");
                break;
            case 2:
                break;
        }
        return args;
    }
}
