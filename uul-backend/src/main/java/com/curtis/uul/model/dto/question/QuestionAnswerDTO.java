package com.curtis.uul.model.dto.question;

import lombok.Data;

/**
 * 题目答案封装类 (AI测评类评分用）
 */
@Data
public class QuestionAnswerDTO {

    /**
     * 题目
     */
    private String title;

    /**
     * 用户答案
     */
    private String userAnswer;
}
