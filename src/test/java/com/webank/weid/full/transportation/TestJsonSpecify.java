package com.webank.weid.full.transportation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.suite.api.transportation.TransportationFactory;
import com.webank.weid.suite.api.transportation.inf.Transportation;

public class TestJsonSpecify extends TestBaseService {

    /**
     * case: weid exist.
     */
    @Test
    public void testJsonSpecify() {
        Transportation transportation = TransportationFactory.newJsonTransportation();
        List<String> weIdList = new ArrayList<>();
        weIdList.add(createWeIdResult.getWeId());
        weIdList.add(createWeIdNew.getWeId());
        transportation = transportation.specify(weIdList);
        Assert.assertNotNull(transportation);
        
    }
    
    /**
     * case: specify again.
     */
    @Test
    public void testJsonSpecify_again() {
        Transportation transportation = TransportationFactory.newJsonTransportation();
        List<String> weIdList = new ArrayList<>();
        weIdList.add(createWeIdResult.getWeId());
        weIdList.add(createWeIdNew.getWeId());
        try {
            transportation = transportation.specify(weIdList).specify(weIdList);
            Assert.assertTrue(false);
        } catch (WeIdBaseException e) {
            Assert.assertTrue(true);
        }
    }
}
