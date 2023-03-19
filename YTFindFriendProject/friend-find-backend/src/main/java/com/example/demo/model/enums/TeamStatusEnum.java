package com.example.demo.model.enums;

/**
 * 队伍状态
 */
public enum TeamStatusEnum {

    PUBLIC(0,"公开"),
    PRIVATE(1, "私密"),
    SECRET(2, "加密")
    ;

    private int value;

    private String text;

    TeamStatusEnum(int value, String text) {
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

    public static TeamStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        TeamStatusEnum[] enums = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : enums) {
            if (teamStatusEnum.getValue() == value.intValue()) {
                return teamStatusEnum;
            }
        }
        return null;
    }
}
