package com.duu.matchPartner.model.enums;

import lombok.Data;

/**
 * @author : duu
 * @data : 2023/11/4
 * @from ：https://github.com/0oHo0
 **/
public enum TeamStatusEnum {
    PUBLIC(0,"公开"),
    PRIVATE(1,"加密"),
    SECRET(2,"保密");

    private int value;

    private String text;

    public static TeamStatusEnum getTeamStatusEnum(Integer value) {
        if(value == null)
            return null;
        for (TeamStatusEnum teamStatusEnum : TeamStatusEnum.values()) {
            if(teamStatusEnum.getValue() == value)
                return teamStatusEnum;
        }
        return null;
    }

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
}
