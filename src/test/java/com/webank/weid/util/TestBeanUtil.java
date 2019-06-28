/*
 *       Copyright© (2018) WeBank Co., Ltd.
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
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.response.ResponseData;

/**
 * test for BeanUtil.
 * @author v_wbgyang
 *
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