package com.wanglei.mybibackend.model.enums;

/**
 * 队伍状态枚举
 */
public enum TeamStatus {
    PUBLIC(0,"公开"),
    PRIVATE(1,"私密"),
    SECRET(2,"加密")
    ;

    private int value;
    private String text;

    public static TeamStatus getTeamStatusByValue(Integer value){
        if(value == null){
            return null;
        }
        TeamStatus[] values = TeamStatus.values();
        for(TeamStatus teamStatus : values){
            if(value == teamStatus.getValue()){
                return teamStatus;
            }
        }
        return null;
    }
    TeamStatus(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
