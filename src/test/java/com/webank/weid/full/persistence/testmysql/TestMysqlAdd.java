/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.full.persistence.testmysql;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.transportation.TestBaseTransportation;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.persistence.PersistenceFactory;
import com.webank.weid.suite.api.persistence.inf.Persistence;
import com.webank.weid.suite.api.persistence.params.PersistenceType;
import com.webank.weid.util.PropertyUtils;

public class TestMysqlAdd extends TestBaseTransportation {

    private static final Logger logger = LoggerFactory.getLogger(TestMysqlAdd.class);

    private static Persistence persistence = null;

    private static String idname = "test";

    private static PersistenceType persistenceType = null;

    @Override
    public synchronized void testInit() {

        String type = PropertyUtils.getProperty("persistence_type");
        if (type.equals("mysql")) {
            persistenceType = PersistenceType.Mysql;
        } else if (type.equals("redis")) {
            persistenceType = PersistenceType.Redis;
        }
        persistence = PersistenceFactory.build(persistenceType);
    }


    /**
     * case:test add.
     */
    public void testSave_success() {

        ResponseData<String> response = new ResponseData<>();
        response.setResult("test");
        while (StringUtils.isNotBlank(response.getResult())) {
            response = persistence.get("domain.defaultInfo", idname);
            idname = "test" + Math.random() + "select";
        }

        ResponseData<Integer> res = persistence.add(
                "domain.defaultInfo",
                idname,
                "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());

        ResponseData<String> data = persistence.get(null, idname);
        Assert.assertEquals("data123456", data.getResult());
    }


    /**
     * case:test batch add.
     */
    public void testBatchAdd_success() {
        persistence.delete("domain.defaultInfo", "1111aa");
        persistence.delete("domain.defaultInfo", "1222bb");
        HashMap<String, String> map = new HashMap<>();
        map.put("1111aa", "12345");
        map.put("1222bb", "123456789");
        ResponseData<Integer> res = persistence.batchAdd("domain.defaultInfo", map);
        LogUtil.info(logger, "persistence", res);
        System.out.println(res);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());
        Assert.assertNotNull(res.getResult());

        ResponseData<String> data = persistence.get(null, "1111aa");
        Assert.assertEquals("12345", data.getResult());
        ResponseData<String> data1 = persistence.get("", "1222bb");
        Assert.assertNotNull(data1.getResult());
        Assert.assertTrue(Arrays.equals(
                "123456789".getBytes(StandardCharsets.ISO_8859_1),
                data1.getResult().getBytes(StandardCharsets.ISO_8859_1)));
    }


    /**
     * case:insert data into a same database again.
     */
    public void testAdd_repeat() {

        ResponseData<String> response = new ResponseData<>();
        response.setResult("test");
        while (StringUtils.isNotBlank(response.getResult())) {
            response = persistence.get("domain.defaultInfo", idname);
            idname = "test" + Math.random() + "select";
        }

        ResponseData<Integer> res = persistence
                .add("domain.defaultInfo", idname, "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());

        ResponseData<Integer> res1 = persistence
                .add("domain.defaultInfo", idname, "data123456");
        Assert.assertEquals(ErrorCode.PERSISTENCE_EXECUTE_FAILED.getCode(),
                res1.getErrorCode().intValue());
    }


    /**
     * case:domain is not spit by : .
     */
    public void testAdd_domainNoSpit() {
        if (persistence.get(null, idname).getResult() != null) {
            persistence.delete(null, idname);
        }

        ResponseData<Integer> res = persistence.add("datasource1",
                idname, "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(
                ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL.getCode(), res.getErrorCode().intValue());
    }


    /**
     * case:domain spit by : but the databases and table begin with space and end with space.
     */
    public void testAdd_domainContainSpace() {
        ResponseData<Integer> res = persistence.add(
                " datasource1 : sdk_all_data ", "123456", "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(
                ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL.getCode(), res.getErrorCode().intValue());
    }


    /**
     * case:domain is null.
     */
    public void testAdd_domainNull() {

        if (persistence.get(null, "123456") != null) {
            persistence.delete(null, "123456");
        }
        ResponseData<Integer> res = persistence.add(null,
                "123456", "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());

        ResponseData<String> data = persistence.get(null, "123456");
        Assert.assertEquals("data123456", data.getResult());
    }


    /**
     * case:domain is blank.
     */
    public void testAdd_domainBlank() {
        if (persistence.get("", idname) != null) {
            persistence.delete("", idname);
        }
        ResponseData<Integer> res = persistence.add("",
                idname, "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());

        ResponseData<String> data = persistence.get("", idname);
        Assert.assertEquals("data123456", data.getResult());
    }


    /**
     * case:database is zh.
     */
    public void testAdd_domainContainZh() {
        String id = idname + System.currentTimeMillis();
        ResponseData<Integer> res = persistence.add("datasource1:夏石龙",
                id, "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL.getCode(),
                res.getErrorCode().intValue());

    }


    /**
     * case:domain hasspecial character.
     */
    public void testAdd_domainContainSpecialChar() {

        ResponseData<Integer> res = persistence
                .add("datasource1:mnj><:??li", "123456", "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(
                ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL.getCode(), res.getErrorCode().intValue());
    }


    /**
     * case:id is zh.
     */
    public void testAdd_idZh() {

        ResponseData<String> response = new ResponseData<>();
        response.setResult("test");
        while (StringUtils.isNotBlank(response.getResult())) {
            response = persistence.get("datasource1:夏石龙", idname);
            idname = "test" + Math.random() + "中国";
        }
        ResponseData<Integer> res = persistence
                .add("domain.defaultInfo", idname, "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());

        ResponseData<String> data = persistence.get("domain.defaultInfo", idname);
        Assert.assertEquals("data123456", data.getResult());
    }


    /**
     * case:id contains special char.
     */
    public void testAdd_idContainSpecialChar() {

        ResponseData<String> response = new ResponseData<>();
        response.setResult("test");
        while (StringUtils.isNotBlank(response.getResult())) {
            response = persistence.get("domain.defaultInfo", idname);
            idname = "test" + Math.random() + "0x23！@#￥%……&*-+";
        }
        ResponseData<Integer> res = persistence
                .add("domain.defaultInfo", idname, "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());

        ResponseData<String> data = persistence.get("domain.defaultInfo", idname);
        Assert.assertEquals("data123456", data.getResult());
    }


    /**
     * case:id contains special char.
     */
    public void testAdd_idContainKeyWord() {

        ResponseData<String> response = new ResponseData<>();
        response.setResult("test");
        while (StringUtils.isNotBlank(response.getResult())) {
            response = persistence.get("domain.defaultInfo", idname);
            idname = "test" + Math.random() + "select";
        }

        ResponseData<Integer> res = persistence
                .add("domain.defaultInfo", idname, "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());

        ResponseData<String> data = persistence.get("domain.defaultInfo", idname);
        Assert.assertEquals("data123456", data.getResult());
    }


    /**
     * case:data contains zh.
     */
    public void testAdd_dataZh() {

        ResponseData<String> response = new ResponseData<>();
        response.setResult("test");
        while (StringUtils.isNotBlank(response.getResult())) {
            response = persistence.get("domain.defaultInfo", idname);
            idname = "test" + Math.random();
        }
        ResponseData<Integer> res = persistence.add(
                "domain.defaultInfo", idname, "中国我爱你");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());

        ResponseData<String> data = persistence.get("domain.defaultInfo", idname);
        Assert.assertTrue(Arrays.equals(
                "中国我爱你".getBytes(StandardCharsets.ISO_8859_1),
                data.getResult().getBytes(StandardCharsets.ISO_8859_1)));
    }


    /**
     * case:data contains special char.
     */
    public void testAdd_dataContainSpeciaChar() {

        ResponseData<String> response = new ResponseData<>();
        response.setResult("test");
        while (StringUtils.isNotBlank(response.getResult())) {
            response = persistence.get("domain.defaultInfo", idname);
            idname = "test" + Math.random();
        }

        ResponseData<Integer> res = persistence.add(
                "domain.defaultInfo", idname, "12!@##$$%^^&*()-+?>we");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());
    }


    /**
     * case:data contains special char.
     */
    public void testAdd_dataIsTooLong() {
        char[] chars = new char[1000];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (1000 % 127);
        }

        ResponseData<String> response = new ResponseData<>();
        response.setResult("test");
        while (StringUtils.isNotBlank(response.getResult())) {
            response = persistence.get("domain.defaultInfo", idname);
            idname = "test" + Math.random();
        }

        ResponseData<Integer> res = persistence.add(
                "domain.defaultInfo", idname, String.valueOf(chars));
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());
    }

}
