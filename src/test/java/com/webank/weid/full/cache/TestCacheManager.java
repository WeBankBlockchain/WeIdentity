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

package com.webank.weid.full.cache;

import org.junit.Assert;
import org.junit.Test;

import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.cpt.Cpt103;
import com.webank.weid.suite.cache.CacheManager;
import com.webank.weid.suite.cache.CacheNode;

public class TestCacheManager {

    @Test
    public void testPutAndGetString() {
        CacheNode<String> cacheNode = CacheManager.registerCacheNode("Test", 10000L, 100);
        String value = "abc";
        cacheNode.put("string", value);
        String getValue = cacheNode.get("string");
        Assert.assertEquals(value, getValue);

        String newValue = "newAbc";
        cacheNode.put("string", newValue);

        String newGetValue = cacheNode.get("string");
        Assert.assertEquals(newValue, newGetValue);
    }
    
    @Test
    public void testRegisterAgain() {
        boolean result = false;
        try {
            CacheManager.registerCacheNode("Test1", 10000L, 100);
            CacheManager.registerCacheNode("Test1", 10000L, 100);
            result = true;
        } catch (WeIdBaseException e) {
            result = false;
        }

        Assert.assertFalse(result);
    }

    @Test
    public void testTimeout() throws InterruptedException {
        CacheNode<String> cacheNode = CacheManager.registerCacheNode("TestTimeout", 1000L, 100);
        String value = "test";
        cacheNode.put("timeout", value);
        String getValue = cacheNode.get("timeout");
        Assert.assertEquals(value, getValue);
        Thread.sleep(1500);
        getValue = cacheNode.get("timeout");
        Assert.assertNull(getValue);
    }

    @Test
    public void testPutAndGetObject() {
        CacheNode<Cpt103> cacheNode = CacheManager.registerCacheNode("obj", 10000L, 100);
        Cpt103 cpt = new Cpt103();
        cpt.setId("123456789");
        cacheNode.put("cpt103", cpt);
        Cpt103 getObj = cacheNode.get("cpt103");
        Assert.assertTrue(cpt.equals(getObj));
        Assert.assertEquals(cpt.getId(), getObj.getId());
    }

    @Test
    public void testMaxNum() throws InterruptedException {
        CacheNode<String> cacheNode = CacheManager.registerCacheNode("TestMaxSize", 10000L, 10);
        //初始化10个值
        for (int i = 0; i < 10; i++) {
            cacheNode.put("key" + i, "value" + i);  
        }
        String value = cacheNode.get("key0");
        Assert.assertEquals("value0", value);
        //再次加入一个值
        cacheNode.put("key" + 10, "value" + 10);
        //给清除机制缓冲时间
        Thread.sleep(1000);
        int count = 0;
        for (int i = 0; i <= 10; i++) {
            if (cacheNode.get("key" + i) != null) {
                count++;
            }
        }
        Assert.assertEquals(10, count);
    }
}
