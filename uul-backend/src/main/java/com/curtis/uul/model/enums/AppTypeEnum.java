package com.curtis.uul.model.enums;

import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * app 应用类型枚举
 */
public enum AppTypeEnum {
    SCORE("得分类",0),
    TEST("测评类",1);

    private final String text;

    private final int value;

    AppTypeEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value 获取枚举
     * @return
     */
    public static AppTypeEnum getEnumByValue(int value){
        if(ObjectUtils.isEmpty(value)){
            return null;
        }
        for (AppTypeEnum reviewStatusEnum : AppTypeEnum.values()) {
            if (reviewStatusEnum.getValue() == value){
                return reviewStatusEnum;
            }
        }
        return null;
    }

    /**
     * 获取值列表
     * @return
     */
    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public String getText() {
        return text;
    }

    public int getValue() {
        return value;
    }
}
