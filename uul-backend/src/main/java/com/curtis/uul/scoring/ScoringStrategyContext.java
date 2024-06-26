package com.curtis.uul.scoring;

import com.curtis.uul.common.ErrorCode;
import com.curtis.uul.exception.BusinessException;
import com.curtis.uul.model.entity.App;
import com.curtis.uul.model.entity.UserAnswer;
import com.curtis.uul.model.enums.AppScoringStrategyEnum;
import com.curtis.uul.model.enums.AppTypeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Deprecated
public class ScoringStrategyContext {
    @Resource
    private CustomScoreScoringStrategy customScoreScoringStrategy;

    @Resource
    private CustomTestScoringStrategy customTestScoringStrategy;

    /**
     * 评分
     */
    public UserAnswer doScore(List<String> choiceList,App app) throws Exception {
        AppTypeEnum appTypeEnum = AppTypeEnum.getEnumByValue(app.getAppType());
        AppScoringStrategyEnum appScoringStrategyEnum = AppScoringStrategyEnum.getEnumByValue(app.getScoringStrategy());
        if(appTypeEnum == null || appScoringStrategyEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"appTypeEnum or appScoringStrategyEnum is null");
        }
        //根据不同的应用类别和评分策略，选择对应的策略执行
        switch (appTypeEnum){
            case SCORE:
                switch (appScoringStrategyEnum){
                    case CUSTOM:
                        return customScoreScoringStrategy.doScore(choiceList,app);
                    case AI:
                        break;
                }
                break;
            case TEST:
                switch (appScoringStrategyEnum){
                    case CUSTOM:
                        return customTestScoringStrategy.doScore(choiceList,app);
                    case AI:
                        break;
                }
                break;
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"应用配置有误，未找到匹配的策略");
    }
}
