

package com.webank.weid.suite.transmission;

import lombok.Data;

import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.suite.api.transportation.params.TransType;

@Data
public class TransmissionRequest<T> {
    private String amopId;
    private T args;
    private String serviceType;
    private TransType transType;
    private WeIdAuthentication weIdAuthentication;
}
