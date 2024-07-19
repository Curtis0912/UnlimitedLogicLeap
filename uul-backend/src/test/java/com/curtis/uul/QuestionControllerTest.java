package com.curtis.uul;

import com.curtis.uul.controller.QuestionController;
import com.curtis.uul.model.dto.question.AIGenerateQuestionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
@SpringBootTest
public class QuestionControllerTest {

    @Resource
    private QuestionController questionController;

    @Test
    void aiGenerateQuestionSSEVIPTest() throws InterruptedException {
        AIGenerateQuestionRequest request = new AIGenerateQuestionRequest();
        request.setAppId(3L);
        request.setQuestionNumber(10);
        request.setOptionNumber(2);

        questionController.AiGenerateQuestionSSETest(request, false);
        questionController.AiGenerateQuestionSSETest(request, false);
        questionController.AiGenerateQuestionSSETest(request, true);

        Thread.sleep(1000000L);
    }

}
