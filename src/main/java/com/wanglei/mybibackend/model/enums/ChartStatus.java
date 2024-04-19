package com.wanglei.mybibackend.model.enums;

public enum ChartStatus {
    WAITING("wait"),
    RUNNING("running"),
    SUCCESS("succeed"),
    FAILED("failed");

    private final String value;


    ChartStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
