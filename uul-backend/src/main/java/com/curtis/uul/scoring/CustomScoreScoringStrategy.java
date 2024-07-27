package com.curtis.uul.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.curtis.uul.model.dto.question.QuestionContentDTO;
import com.curtis.uul.model.entity.App;
import com.curtis.uul.model.entity.Question;
import com.curtis.uul.model.entity.ScoringResult;
import com.curtis.uul.model.entity.UserAnswer;
import com.curtis.uul.model.vo.QuestionVO;
import com.curtis.uul.service.QuestionService;
import com.curtis.uul.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义打分类应用评分策略
 */
@ScoringStrategyConfig(appType = 0, scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        //1.根据id查询到题目和题目结果信息
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class)
                        .eq(ScoringResult::getAppId, appId)
                        .orderByDesc(ScoringResult::getResultScoreRange)
        );

        //2.统计用户的总得分
        int totalScore = 0;
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

        //遍历题目列表  错误 重复累加
//        for(QuestionContentDTO questionContentDTO : questionContent){
//            //遍历我作答的答案列表（我提交的答案，不是题目的答案）
//            for(String answer : choices){
//                //遍历题目中的选项
//                for(QuestionContentDTO.Option option : questionContentDTO.getOptions()){
//                    //如果答案和选项的key匹配,累加分数
//                    if(option.getKey().equals(answer)){
//                        //如果有分数，则累加，没分数则赋值为0进行累加
//                        int score = Optional.of(option.getScore()).orElse(0);
//                        totalScore += score;
//                    }
//                }
//            }
//        }



        // 初始化一个List，用于存储题目的正确答案
        List<String> correctAnswers = new ArrayList<>();

        // 遍历题目列表，将题目的正确答案存储到List中
        for (QuestionContentDTO questionContentDTO : questionContent) {
            for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                if (option.getScore() > 0) {
                    correctAnswers.add(option.getKey());
                }
            }
        }

        // 遍历用户提交的答案列表，逐一进行匹配和评分
        for (int i = 0; i < choices.size(); i++) {
            String userAnswer = choices.get(i);
            String correctAnswer = correctAnswers.get(i);
            if (userAnswer.equals(correctAnswer)) {
                // 遍历当前题目的选项，找到与用户答案匹配的正确选项
                for (QuestionContentDTO.Option option : questionContent.get(i).getOptions()) {
                    if (option.getKey().equals(userAnswer)) {
                        totalScore += option.getScore();
                        break;
                    }
                }
            }
        }





        //3.遍历得分结果，找到第一个用户分数大于得分范围的结果，作为最终结果
        ScoringResult maxScoringResult = scoringResultList.get(0);
        for(ScoringResult scoringResult : scoringResultList){
            if(totalScore >= scoringResult.getResultScoreRange()){
                maxScoringResult = scoringResult;
                break;
            }
        }

        //4.构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        userAnswer.setResultScore(totalScore);
        return userAnswer;
    }
}
