package com.curtis.uul.controller;

import com.curtis.uul.common.BaseResponse;
import com.curtis.uul.common.ErrorCode;
import com.curtis.uul.common.ResultUtils;
import com.curtis.uul.exception.ThrowUtils;
import com.curtis.uul.mapper.AppMapper;
import com.curtis.uul.mapper.UserAnswerMapper;
import com.curtis.uul.model.dto.statistic.AppAnswerCountDTO;
import com.curtis.uul.model.dto.statistic.AppAnswerResultCountDTO;
import com.curtis.uul.model.dto.statistic.AppCountDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/app/statistic")
@Slf4j
public class AppStatisticController {

    @Resource
    private UserAnswerMapper userAnswerMapper;

    @Resource
    private AppMapper appMapper;

    @GetMapping("answer_count")
    public BaseResponse<List<AppAnswerCountDTO>> GetAppAnswerCount(){
        return ResultUtils.success(userAnswerMapper.doAppAnswerCount());
    }

    @GetMapping("answer_result_count")
    public BaseResponse<List<AppAnswerResultCountDTO>> GetAppAnswerResultCount(Long appId){
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userAnswerMapper.doAppAnswerResultCount(appId));
    }

    @GetMapping("app_count")
    public BaseResponse<List<AppCountDTO>> GetAppCount(){
        return ResultUtils.success(appMapper.doAppCount());
    }
}
