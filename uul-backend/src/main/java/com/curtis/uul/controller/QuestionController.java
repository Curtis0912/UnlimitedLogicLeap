package com.curtis.uul.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.curtis.uul.annotation.AuthCheck;
import com.curtis.uul.common.BaseResponse;
import com.curtis.uul.common.DeleteRequest;
import com.curtis.uul.common.ErrorCode;
import com.curtis.uul.common.ResultUtils;
import com.curtis.uul.constant.UserConstant;
import com.curtis.uul.exception.BusinessException;
import com.curtis.uul.exception.ThrowUtils;
import com.curtis.uul.manager.AIManager;
import com.curtis.uul.model.dto.question.*;
import com.curtis.uul.model.entity.App;
import com.curtis.uul.model.entity.Question;
import com.curtis.uul.model.entity.User;
import com.curtis.uul.model.enums.AppTypeEnum;
import com.curtis.uul.model.vo.QuestionVO;
import com.curtis.uul.service.AppService;
import com.curtis.uul.service.QuestionService;
import com.curtis.uul.service.UserService;
import com.zhipu.oapi.service.v4.model.ModelData;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 题目接口
 *
 * @author curtis
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private AppService appService;
    
    @Resource
    private AIManager aiManager;

    @Resource
    private Scheduler vipScheduler;

    // region 增删改查

    /**
     * 创建题目
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 在此处将实体类和 DTO 进行转换   题目内容需要转成String
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<QuestionContentDTO> questionContentDTO = questionAddRequest.getQuestionContent();
        question.setQuestionContent(JSONUtil.toJsonStr(questionContentDTO));
        // 数据校验
        questionService.validQuestion(question, true);
        // 填充默认值
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除题目
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题目（仅管理员可用）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<QuestionContentDTO> questionContentDTO = questionUpdateRequest.getQuestionContent();
        question.setQuestionContent(JSONUtil.toJsonStr(questionContentDTO));
        // 数据校验
        questionService.validQuestion(question, false);
        // 判断是否存在
        long id = questionUpdateRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题目（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 分页获取题目列表（仅管理员可用）
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }

    /**
     * 分页获取题目列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前登录用户创建的题目列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 编辑题目（给用户使用）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<QuestionContentDTO> questionContentDTO = questionEditRequest.getQuestionContent();
        question.setQuestionContent(JSONUtil.toJsonStr(questionContentDTO));
        // 数据校验
        questionService.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = questionEditRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    //region

    private static final String GENERATE_QUESTION_SYSTEM_MESSAGE =
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
                    "        \"result\": \"I\",\n" +
                    "        \"score\": \"1\",\n" +
                    "        \"key\": \"A\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"value\": \"\",\n" +
                    "        \"result\": \"E\",\n" +
                    "        \"score\": \"0\",\n" +
                    "        \"key\": \"B\"\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"title\": \"题目标题\"\n" +
                    "  }\n" +
                    "]\n" +
                    "```\n" +
                    "title是题目，options是选项，每个选项的key安装英文字母序（如A、B、C、D)以此类推，\n" +
                    "value是选项内容，result是选项对应的结果类型（如I、E），score是选项对应的得分（正确选项得分为1，错误为0）\n" +
                    "3.当应用类型是测评类时，score全设为空，当应用类型是得分类时，每道题只有一个正确选项并且result全设为空\n" +
                    "3.检查题目是否包含序号，若包含序号则去除序号\n" +
                    "4.返回的题目列表格式必须为JSON数组" +
                    "5.检查应用类型是得分类时，每道题是否只有一个正确选项";

    private static final String GENERATE_MBTI_QUESTION_SYSTEM_MESSAGE =
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
            "        \"result\": \"I\",\n" +
            "        \"key\": \"A\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"value\": \"\",\n" +
            "        \"result\": \"E\",\n" +
            "        \"key\": \"B\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"title\": \"题目标题\"\n" +
            "  }\n" +
            "]\n" +
            "```\n" +
            "title是题目，options是选项，每个选项的key安装英文字母序（如A、B、C、D)以此类推，value是选项内容,result是选项对应的结果（如I、E）\n" +
            "3.检查题目是否包含序号，若包含序号则去除序号\n" +
            "4.返回的题目列表格式必须为JSON数组\n" +
            "5.result对应的结果依据MBTI16种人格类型评估，要求四个维度题目分布平衡";

    /**
     * 生成用户消息
     * @param app
     * @param questionNumber
     * @param optionNumber
     * @return
     */
    public String getGenerateQuestionUserMessage(App app, int questionNumber, int optionNumber) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        userMessage.append(AppTypeEnum.getEnumByValue(app.getAppType()).getText()).append("\n");
        userMessage.append(questionNumber).append("\n");
        userMessage.append(optionNumber);
        return userMessage.toString();
    }

    /**
     * AI生成题目
     * @param aiGenerateQuestionRequest
     * @return
     */
    @PostMapping("/ai_generate")
    public BaseResponse<List<QuestionContentDTO>> AiGenerateQuestion(@RequestBody AIGenerateQuestionRequest aiGenerateQuestionRequest) {
        ThrowUtils.throwIf(aiGenerateQuestionRequest == null, ErrorCode.PARAMS_ERROR);
        //获取参数
        Long appId = aiGenerateQuestionRequest.getAppId();
        int questionNumber = aiGenerateQuestionRequest.getQuestionNumber();
        int optionNumber = aiGenerateQuestionRequest.getOptionNumber();
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR);
        //封装prompt
        String userMessage = getGenerateQuestionUserMessage(app, questionNumber, optionNumber);
        //AI生成
        String result = aiManager.doSyncRequest(GENERATE_QUESTION_SYSTEM_MESSAGE, userMessage, null);
        //结果处理
        int start = result.indexOf("[");
        int end = result.lastIndexOf("]");
        String json = result.substring(start, end+1);
        List<QuestionContentDTO> questionContentDTOList = JSONUtil.toList(json, QuestionContentDTO.class);
        return ResultUtils.success(questionContentDTOList);
    }

    /**
     * AI生成题目(流式数据流）
     * @param aiGenerateQuestionRequest
     * @return
     */
    @GetMapping("/ai_generate/sse")
    public SseEmitter AiGenerateQuestionSSE(AIGenerateQuestionRequest aiGenerateQuestionRequest,HttpServletRequest request) {
        ThrowUtils.throwIf(aiGenerateQuestionRequest == null, ErrorCode.PARAMS_ERROR);
        //获取参数
        Long appId = aiGenerateQuestionRequest.getAppId();
        int questionNumber = aiGenerateQuestionRequest.getQuestionNumber();
        int optionNumber = aiGenerateQuestionRequest.getOptionNumber();
        //获取应用信息
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR);
        //封装prompt
        String userMessage = getGenerateQuestionUserMessage(app, questionNumber, optionNumber);
        //建立SSE连接对象，0 表示不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        //AI生成，sse流式返回
        Flowable<ModelData> modelDataFlowable = aiManager.doStreamRequest(GENERATE_QUESTION_SYSTEM_MESSAGE, userMessage, null);
        //拼接完整题目
        StringBuilder contentBuilder = new StringBuilder();
        //调用一个原子类，因为方法是异步的，可能会有多线程
        //左括号计数器，除了默认值外，当回归为0时，表示左括号等于右括号，可以截取
        AtomicInteger counter = new AtomicInteger(0);

        //获取用户信息
//        User loginUser = userService.getLoginUser(request); ??很奇怪，获取不到request
        Long userId = app.getUserId();
        User user = userService.getById(userId);
        String userRole = user.getUserRole();

        //默认全局线程池
        Scheduler scheduler = Schedulers.io();
        //如果是用户是vip，则使用定制线程池，先用admin代替
        if ("admin".equals(userRole)) {
            scheduler = vipScheduler;
        }
        modelDataFlowable
                //异步线程池执行
                .observeOn(scheduler)//使用io型线程池持续处理
                .map(chunk -> chunk.getChoices().get(0).getDelta().getContent())//拿到生成的内容，是一块一块的，可能会有无用的字符，如空白
                .map(message -> message.replaceAll("\\s",""))//把所有特殊字符 转换成空字符
                .filter(StrUtil::isNotBlank)//过滤，只保留非空的
                .flatMap(message -> {//把一个流变成多条流（即分流）
                    //将字符串转换成 List<Character>
                    List<Character> charList = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        charList.add(c);
                    }
                    return Flowable.fromIterable(charList);
                })
                .doOnNext(c -> {
                            {
                                //识别第一个 { 表示开始AI传输json数据，用counter开始判断是否结束
                                //如果是'{'，计数器counter+1
                                if (c =='{') {
                                    counter.addAndGet(1);
                                }
                                //当计数器大于1时，开始拼接
                                if (counter.get() > 0) {
                                    contentBuilder.append(c);
                                }
                                if (c == '}') {
                                    counter.addAndGet(-1);
                                    if (counter.get() == 0) {

                                        //输出当前线程名称
                                        System.out.println(Thread.currentThread().getName());

                                        //表示已经展示出完整的一道题目，可以拼接，并且通过SSE返回给前端
                                        sseEmitter.send(JSONUtil.toJsonStr(contentBuilder.toString()));
                                        //重置，准备拼接下一道题
                                        contentBuilder.setLength(0);
                                    }
                                }
                            }
                })
                .doOnError((e) -> log.error("sse error",e))
                .doOnComplete(sseEmitter::complete)
                .subscribe();
        return sseEmitter;
    }

    /**
     * AI生成题目(流式数据流）   仅测试隔离线程池使用
     * @param aiGenerateQuestionRequest
     * @return
     */
    @Deprecated
    @GetMapping("/ai_generate/sse/test")
    public SseEmitter AiGenerateQuestionSSETest(AIGenerateQuestionRequest aiGenerateQuestionRequest,boolean isVip) {
        ThrowUtils.throwIf(aiGenerateQuestionRequest == null, ErrorCode.PARAMS_ERROR);
        //获取参数
        Long appId = aiGenerateQuestionRequest.getAppId();
        int questionNumber = aiGenerateQuestionRequest.getQuestionNumber();
        int optionNumber = aiGenerateQuestionRequest.getOptionNumber();
        //获取应用信息
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR);
        //封装prompt
        String userMessage = getGenerateQuestionUserMessage(app, questionNumber, optionNumber);
        //建立SSE连接对象，0 表示不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        //AI生成，sse流式返回
        Flowable<ModelData> modelDataFlowable = aiManager.doStreamRequest(GENERATE_MBTI_QUESTION_SYSTEM_MESSAGE, userMessage, null);
        //拼接完整题目
        StringBuilder contentBuilder = new StringBuilder();
        //调用一个原子类，因为方法是异步的，可能会有多线程
        //左括号计数器，除了默认值外，当回归为0时，表示左括号等于右括号，可以截取
        AtomicInteger counter = new AtomicInteger(0);

        //默认全局线程池
        Scheduler scheduler = Schedulers.single();

        //如果是用户是vip，则使用定制线程池，先用admin代替
        if (isVip) {
            scheduler = vipScheduler;
        }
        modelDataFlowable
                //异步线程池执行
                .observeOn(scheduler)//使用io型线程池持续处理
                .map(chunk -> chunk.getChoices().get(0).getDelta().getContent())//拿到生成的内容，是一块一块的，可能会有无用的字符，如空白
                .map(message -> message.replaceAll("\\s",""))//把所有特殊字符 转换成空字符
                .filter(StrUtil::isNotBlank)//过滤，只保留非空的
                .flatMap(message -> {//把一个流变成多条流（即分流）
                    //将字符串转换成 List<Character>
                    List<Character> charList = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        charList.add(c);
                    }
                    return Flowable.fromIterable(charList);
                })
                .doOnNext(c -> {
                    {
                        //识别第一个 { 表示开始AI传输json数据，用counter开始判断是否结束
                        //如果是'{'，计数器counter+1
                        if (c =='{') {
                            counter.addAndGet(1);
                        }
                        //当计数器大于1时，开始拼接
                        if (counter.get() > 0) {
                            contentBuilder.append(c);
                        }
                        if (c == '}') {
                            counter.addAndGet(-1);
                            if (counter.get() == 0) {

                                //便于测试线程池
                                //输出当前线程名称
                                System.out.println(Thread.currentThread().getName());
                                //模拟普通用户阻塞
                                if (!isVip) {
                                    Thread.sleep(10000L);
                                }
                                //表示已经展示出完整的一道题目，可以拼接，并且通过SSE返回给前端
                                sseEmitter.send(JSONUtil.toJsonStr(contentBuilder.toString()));
                                //重置，准备拼接下一道题
                                contentBuilder.setLength(0);
                            }
                        }
                    }
                })
                .doOnError((e) -> log.error("sse error",e))
                .doOnComplete(sseEmitter::complete)
                .subscribe();
        return sseEmitter;
    }
    //endregion
}
