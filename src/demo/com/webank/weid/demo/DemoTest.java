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

package com.webank.weid.demo;

import java.text.ParseException;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.util.DateUtils;

/**
 * WeIdentity DID demo.
 *
 * @author v_wbgyang
 */
public class DemoTest extends DemoBase {

    /** jsonSchema. */
    public final static String SCHEMA =
        "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"title\""
        + ":\"/etc/fstab\",\"description\":\"JSON representation of /etc/fstab\""
        + ",\"type\":\"object\",\"properties\":{\"swap\":{\"$ref\":\"#/definitions/mntent\"}}"
        + ",\"patternProperties\":{\"^/([^/]+(/[^/]+)*)?$\":{\"$ref\":\"#/definitions/mntent\"}}"
        + ",\"required\":[\"/\",\"swap\"],\"additionalProperties\":false,\"definitions\""
        + ":{\"mntent\":{\"title\":\"mntent\",\"description\":\"An fstab entry\",\"type\""
        + ":\"object\",\"properties\":{\"device\":{\"type\":\"string\"},\"fstype\""
        + ":{\"type\":\"string\"},\"options\":{\"type\":\"array\",\"minItems\":1,\"items\""
        + ":{\"type\":\"string\"}},\"dump\":{\"type\":\"integer\",\"minimum\":0},\"fsck\""
        + ":{\"type\":\"integer\",\"minimum\":0}},\"required\":[\"device\",\"fstype\"]"
        + ",\"additionalItems\":false}}}";

    /** claim. */
    public final static String SCHEMADATA =
        "{\"/\":{\"device\":\"/dev/sda2\",\"fstype\":\"btrfs\",\"options\":[\"ssd\"]},\"swap\""
        + ":{\"device\":\"/dev/sda2\",\"fstype\":\"swap\"},\"/tmp\":{\"device\""
        + ":\"tmpfs\",\"fstype\":\"tmpfs\",\"options\":[\"size=64M\"]},\"/var/lib/mysql\""
        + ":{\"device\":\"/dev/data/mysql\",\"fstype\":\"btrfs\"}}";

    /**
     * main of demo.
     * @throws ParseException the parseException
     * @throws RuntimeException the runtimeException
     */
    public static void main(String[] args) throws RuntimeException, ParseException {

        // get service instance
        DemoService demo = context.getBean(DemoService.class);

        // create weId
        CreateWeIdDataResult createWeId = demo.createWeId();
        BeanUtil.print(createWeId);

        // set WeIdentity DID
        demo.setPublicKey(createWeId, "secp256k1");
        demo.setService(createWeId,
            "drivingCardService",
            "https://weidentity.webank.com/endpoint/8377464");
        demo.setAuthenticate(createWeId, "RsaSignatureAuthentication2018");

        // get WeId Dom
        WeIdDocument weIdDom = demo.getWeIdDom(createWeId.getWeId());
        BeanUtil.print(weIdDom);

        // regist authority issuer
        demo.registerAuthorityIssuer(createWeId, "webank", "0");

        // registCpt
        CptBaseInfo cptResult = demo.registCpt(createWeId, SCHEMA);
        BeanUtil.print(cptResult);

        // create Credential
        Credential credential = demo.createCredential(createWeId,
            cptResult.getCptId(),
            SCHEMADATA,
            DateUtils.convertStringToDate("2019-10-11T18:09:42Z").getTime());
        BeanUtil.print(credential);

        boolean result = demo.verifyCredential(credential);
        if (result) {
            BeanUtil.print("verify success");
        } else {
            BeanUtil.print("verify fail");
        }
    }
}
