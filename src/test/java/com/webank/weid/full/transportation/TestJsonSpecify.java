package com.webank.weid.full.transportation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.suite.api.transportation.TransportationFactory;
import com.webank.weid.suite.api.transportation.inf.JsonTransportation;

public class TestJsonSpecify extends TestBaseServcie {

    /**
     * case: weid exist.
     */
    @Test
    public void testJsonSpecify() {
        JsonTransportation jsonTransportation = TransportationFactory.newJsonTransportation();
        List<String> weIdList = new ArrayList<>();
        weIdList.add(createWeIdResult.getWeId());
        weIdList.add(createWeIdNew.getWeId());
        jsonTransportation = jsonTransportation.specify(weIdList);
        Assert.assertNotNull(jsonTransportation);
        
    }
}
