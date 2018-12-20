package com.webank.weid.constant;

public enum ResolveEventLogStatus {

    STATUS_SUCCESS(0),
    STATUS_EVENTLOG_NULL(-1),
    STATUS_RES_NULL(-2),
    STATUS_WEID_NOT_MATCH(-3),
    STATUS_EVENT_NULL(-4);

    private Integer value;

    ResolveEventLogStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
