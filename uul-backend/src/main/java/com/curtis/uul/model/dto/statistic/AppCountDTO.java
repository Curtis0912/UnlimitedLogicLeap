package com.curtis.uul.model.dto.statistic;

import lombok.Data;

/**
 * 应用类型统计
 */
@Data
public class AppCountDTO {

    //应用类型
    private int appType;

    //应用数量
    private int appCount;
}
