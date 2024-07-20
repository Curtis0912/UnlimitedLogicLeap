package com.curtis.uul.model.dto.statistic;

import lombok.Data;

/**
 * 应用回答结果分布
 */
@Data
public class AppAnswerResultCountDTO {

    //结果名称
    private String resultName;

    //结果数量
    private String resultCount;
}
