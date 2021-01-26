package com.webank.weid.protocol.response;

import java.util.List;

import lombok.Data;

/**
 * get WeId and errorCode by pubkeyList response.
 *
 */
@Data
public class WeIdListResult {

    private List<String> weIdList;

    private List<Integer> errorCodeList;
}
