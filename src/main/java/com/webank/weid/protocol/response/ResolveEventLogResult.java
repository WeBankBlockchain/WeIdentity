package com.webank.weid.protocol.response;

import lombok.Data;

import com.webank.weid.constant.ResolveEventLogStatus;

@Data
public class ResolveEventLogResult {

    private ResolveEventLogStatus resultStatus;

    private Integer previousBlock;

    private static final int PREVIOUS_BLOCK = -32768;

    /**
     * set value for resultStatus.
     * 
     * @param resolveEventLogStatus the enum is ResolveEventLogStatus.
     */
    public void setResolveEventLogStatus(ResolveEventLogStatus resolveEventLogStatus) {

        if (!ResolveEventLogStatus.STATUS_SUCCESS.equals(resolveEventLogStatus)) {
            this.previousBlock = PREVIOUS_BLOCK;
        }
        this.resultStatus = resolveEventLogStatus;
    }
}
