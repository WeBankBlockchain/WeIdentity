package com.webank.weid.full.persistence.TestRedis;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.transportation.TestBaseTransportation;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.persistence.inf.Persistence;
import com.webank.weid.suite.persistence.redis.driver.RedisDriver;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TestAdd extends TestBaseTransportation {

    private static final Logger logger = LoggerFactory.getLogger(TestAdd.class);

    private static Persistence persistence = null;

    private static String idname = "test";

    @Override
    public synchronized void testInit() {
        //super.mockMysqlDriver();
//        persistence = new MysqlDriver();
        persistence = new RedisDriver();
    }

    @Test
    /**
     * case:test add.
     */
    public void testSave_success() {

        idname = "test" + Math.random() + "select";
        ResponseData<Integer> res = persistence.add(
            "domain.defaultInfo",
            idname,
            "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());

        ResponseData<String> data = persistence.get(null, idname);
        Assert.assertEquals("data123456", data.getResult());
    }

    @Test
    /**
     * case:test batch add.
     */
    public void testBatchSave_success() {
        persistence.delete("domain.defaultInfo", "12345");
        persistence.delete("domain.defaultInfo", "123456");
        List<String> ids = new ArrayList<>();
        ids.add("12345");
        ids.add("123456");
        List<String> datas = new ArrayList<>();
        datas.add("12345");
        datas.add("~！@#￥%……&&*（）？》《");

        ResponseData<Integer> res = persistence.batchAdd(
            "domain.defaultInfo", ids, datas);
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());
        Assert.assertNotNull(res.getResult());

        ResponseData<String> data = persistence.get(null, "12345");
        Assert.assertEquals("12345", data.getResult());
        ResponseData<String> data1 = persistence.get("", "123456");
        Assert.assertNotNull(data1.getResult());
        Assert.assertTrue(Arrays.equals(
            "~！@#￥%……&&*（）？》《".getBytes(StandardCharsets.ISO_8859_1), 
            data1.getResult().getBytes(StandardCharsets.ISO_8859_1)));
    }

    @Test
    /**
     * case:test batch add.
     */
    public void testBatchSave_sizeNotEqual() {
        persistence.delete("domain.defaultInfo", "12345");
        persistence.delete("domain.defaultInfo", "123456");
        List<String> ids = new ArrayList<>();
        ids.add("12345");
        List<String> datas = new ArrayList<>();
        datas.add("12345");
        datas.add("~！@#￥%……&&*（）？》《");
        ResponseData<Integer> res = persistence.batchAdd(
            "domain.defaultInfo", ids, datas);
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(
            ErrorCode.PRESISTENCE_BATCH_SAVE_DATA_MISMATCH.getCode(),
            res.getErrorCode().intValue()
        );
        Assert.assertEquals(0, res.getResult().intValue());
    }

    @Test
    /**
     * case:insert data into a same database again.
     */
    public void testSave_repeat() {

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
        Assert.assertEquals(ErrorCode.REDIS_EXECUTE_FAILED.getCode(), res1.getErrorCode().intValue());
    }

    @Test
    /**
     * case:domain is not spit by : .
     */
    public void testSave_domainNoSpit() {
        if (persistence.get(null, idname).getResult() != null) {
            persistence.delete(null, idname);
        }

        ResponseData<Integer> res = persistence.add("datasource1",
            idname, "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(
            ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL.getCode(), res.getErrorCode().intValue());
    }

    @Test
    /**
     * case:domain spit by : but the databases and table begin with space and end with space.
     */
    //Expected :100903
    //Actual   :100902
    public void testSave_domainContainSpace() {
        ResponseData<Integer> res = persistence.add(
            " datasource1 : sdk_all_data ", "123456", "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(
            ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL.getCode(), res.getErrorCode().intValue());
    }

    @Test
    /**
     * case:domain is null.
     */
    public void testSave_domainNull() {

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

    @Test
    /**
     * case:domain is blank.
     */
    public void testSave_domainBlank() {
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

    @Test
    /**
     * case:database is zh.
     */

    //Expected :0
    //Actual   :100902
    public void testSave_domainContainZh() {
        String id = idname + System.currentTimeMillis();
        ResponseData<Integer> res = persistence.add("datasource1:夏石龙",
                id, "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL.getCode(), res.getErrorCode().intValue());
    }

    @Test
    /**
     * case:domain has special character.
     */
    public void testSave_domainContainSpecialChar() {

        ResponseData<Integer> res = persistence
            .add("datasource1:mnj><:??li", "123456", "data123456");
        LogUtil.info(logger, "persistence", res);

        Assert.assertEquals(
            ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL.getCode(), res.getErrorCode().intValue());
    }

    @Test
    /**
     * case:id is zh.
     */
    public void testSave_idZh() {

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

    @Test
    /**
     * case:id contains special char.
     */
    public void testSave_idContainSpecialChar() {

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

    @Test
    /**
     * case:id contains special char.
     */
    public void testSave_idContainKeyWord() {

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

    @Test
    /**
     * case:data contains zh.
     */
    public void testSave_dataZh() {

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

    @Test
    /**
     * case:data contains special char.
     */
    public void testSave_dataContainSpeciaChar() {

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

    @Test
    /**
     * case:data contains special char.
     */
    public void testSave_dataIsTooLong() {
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
