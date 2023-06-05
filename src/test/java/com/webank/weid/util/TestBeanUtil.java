

package com.webank.weid.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.common.PasswordKey;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.blockchain.protocol.response.ResponseData;

/**
 * test for BeanUtil.
 *
 * @author v_wbgyang
 */
public class TestBeanUtil {

    private static final Logger logger = LoggerFactory.getLogger(TestBeanUtil.class);

    @Test
    public void testPrint() {

        logger.info(BeanUtil.objToString(null));

        PasswordKey passwordKey = new PasswordKey();
        passwordKey.setPrivateKey("123");

        logger.info(BeanUtil.objToString(passwordKey));
        logger.info(
            BeanUtil.objToString(new ResponseData<PasswordKey>(passwordKey, ErrorCode.SUCCESS))
        );

        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("key1", passwordKey);
        map.put("key2", "1234");
        map.put("key3", new Date());

        logger.info(BeanUtil.objToString(map));
        logger.info(BeanUtil.objToString(new String[]{"one", "two"}));
        logger.info(BeanUtil.objToString(new Date()));
        logger.info(BeanUtil.objToString(new BigDecimal(23)));
        logger.info(BeanUtil.objToString(new BigInteger("11")));

        List<Object> list = new ArrayList<Object>();
        list.add(1);
        list.add(passwordKey);
        list.add(new Date());
        list.add(null);

        logger.info(BeanUtil.objToString(list));
        logger.info(BeanUtil.objToString(new ResponseData<List<Object>>(list, ErrorCode.SUCCESS)));

        map.put("list", list);
        map.put("response", new ResponseData<List<Object>>(list, ErrorCode.SUCCESS));
        logger.info(BeanUtil.objToString(map));

        Map<Object, Object> newMap = new HashMap<Object, Object>();
        newMap.put("map", map);
        logger.info(BeanUtil.objToString(newMap));

        List<Object> newList = new ArrayList<Object>();
        newList.add(list);
        newList.add(newMap);
        newList.add(null);
        logger.info(BeanUtil.objToString(newList));
        Assert.assertTrue(true);
    }
}