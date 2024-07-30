package com.curtis.uul.model.dto.statistic;

import lombok.Data;

/**
 * 热门应用统计
 */
@Data
public class AppAnswerCountDTO {

    private Long appId;

    //应用统计数
    private Long answerCount;

    private String appName;
}
