package com.curtis.uul.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.curtis.uul.manager.AIManager;
import com.curtis.uul.model.dto.question.QuestionAnswerDTO;
import com.curtis.uul.model.dto.question.QuestionContentDTO;
import com.curtis.uul.model.entity.App;
import com.curtis.uul.model.entity.Question;
import com.curtis.uul.model.entity.UserAnswer;
import com.curtis.uul.model.vo.QuestionVO;
import com.curtis.uul.service.QuestionService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * AI 测评类应用评分策略
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AITestScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private AIManager aiManager;

    private static final String GENERATE_AI_TEST_SCORING_SYSTEM_MESSAGE =
            "你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为[{\"title\":\"题目\",\"answer\":\"用户回答\"}]\n" +
            "```\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1.要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于200字）\n" +
            "2.严格按照下面的json格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\":\"评价名称\",\"resultDesc\":\"评价描述\"}\n" +
            "```\n" +
            "3.返回格式必须为json对象";

    /**
     * 生成用户消息
     * @param app
     * @param questionContentDTOList
     * @param choices
     * @return
     */
    public String getGenerateAiTestScoringUserMessage(App app, List<QuestionContentDTO> questionContentDTOList,List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        //将选项题目和用户选择的答案封装成QuestionAnswerDTO
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            questionAnswerDTO.setTitle(questionContentDTOList.get(i).getTitle());
            questionAnswerDTO.setUserAnswer(choices.get(i));
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        //将用户消息和用户选择的答案封装成json字符串
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        //1.根据id查询到题目
        Question question = questionService.getOne(Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId));
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

        //2.调用AI获取结果
        //封装Prompt
        String userMessage = getGenerateAiTestScoringUserMessage(app,questionContent, choices);
        //AI生成
        String result = aiManager.doSyncStableRequest(GENERATE_AI_TEST_SCORING_SYSTEM_MESSAGE, userMessage);
        //结果处理
        int start = result.indexOf("{");
        int end = result.lastIndexOf("}");
        String json = result.substring(start,end+1);

        //3.构造返回值，填充答案对象的属性
        UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);

        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        return userAnswer;
    }
}
