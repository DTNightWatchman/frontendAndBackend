package com.yt.ytsearch.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SearchTypeEnum {

    POST("帖子", "post"),
    USER("用户", "user"),
    PICTURE("图片", "picture"),
    JAVAAPIDOC("Java api 文档", "javaApiDoc");

    private final String text;

    private final String value;

    SearchTypeEnum(String text, String value) {
        this.text = text;

        this.value = value;
    }

    /**
     * 获取值列表
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public static SearchTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (SearchTypeEnum anEnum : SearchTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
