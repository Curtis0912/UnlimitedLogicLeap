package com.curtis.uul;

import cn.hutool.json.JSONUtil;
import com.curtis.uul.manager.AIManager;
import com.curtis.uul.model.dto.question.QuestionContentDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ZhiPuAITest {

    @Resource
    private ClientV4 clientV4;

    @Resource
    private AIManager aiManager;

    private String systemMessage =
            "你是一位严谨的出题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "应用类别，\n" +
            "要生成的题目数，\n" +
            "每个题目的选项数\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来出题：\n" +
            "1.要求：题目和选项尽可能简短，题目不要包含序号，每题的选项数以我提供的为主，题目不能重复\n" +
            "2.严格按照下面的json格式输出题目和选项\n" +
            "```\n" +
            "[\n" +
            "  {\n" +
            "    \"options\": [\n" +
            "      {\n" +
            "        \"value\": \"选项内容\",\n" +
            "        \"key\": \"A\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"value\": \"\",\n" +
            "        \"key\": \"B\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"title\": \"题目标题\"\n" +
            "  }\n" +
            "]\n" +
            "```\n" +
            "title是题目，options是选项，每个选项的key安装英文字母序（如A、B、C、D)以此类推，value是选项内容\n" +
            "3.检查题目是否包含序号，若包含序号则去除序号\n" +
            "4.返回的题目列表格式必须为JSON数组";

    private String userMessage =
            "小学数学测验，\n" +
            "【【【小学六年级的数学题】】】，\n" +
            "得分类，\n" +
            "10，\n" +
            "3";

    @Test
    public void test() {
//        ClientV4 client = new ClientV4.Builder("f7781123f62aec04e39a00535b43d213.fhXwsnEKZrffKf58").build();
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "作为一名营销专家，请为UUL(UnlimitedLogicLeap)智能答题应用平台创作一个吸引人的slogan");
        messages.add(chatMessage);
//        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
//                .requestId(requestId)
                .build();
        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        System.out.println("model ouput:" + invokeModelApiResp.getData().getChoices().get(0));

    }
    @Test
    public void test2() {

        String result = aiManager.doSyncRequest(systemMessage,userMessage,null);
        System.out.println(result);
        int start = result.indexOf("[");
        int end = result.lastIndexOf("]");
        String json = result.substring(start,end+1);
        List<QuestionContentDTO> questionContentDTOList = JSONUtil.toList(json, QuestionContentDTO.class);
        System.out.println(questionContentDTOList);

    }

}
