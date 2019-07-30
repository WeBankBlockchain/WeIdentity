package com.webank.weid.full.transportation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.suite.api.transportation.TransportationFactory;
import com.webank.weid.suite.api.transportation.inf.JsonTransportation;

public class TestJsonSpecify extends TestBaseServcie {

    private static final Logger logger = LoggerFactory.getLogger(TestJsonSpecify.class);

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
        System.out.println(jsonTransportation);
    }
}
