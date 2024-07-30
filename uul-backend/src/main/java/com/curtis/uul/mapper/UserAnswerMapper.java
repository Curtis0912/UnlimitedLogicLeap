package com.curtis.uul.mapper;

import com.curtis.uul.model.dto.statistic.AppAnswerCountDTO;
import com.curtis.uul.model.dto.statistic.AppAnswerResultCountDTO;
import com.curtis.uul.model.entity.UserAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author ZhangJiaHao
* @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
* @createDate 2024-06-23 16:46:28
* @Entity generator.domain.UserAnswer
*/
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {

    @Select("SELECT appId, appName ,COUNT(user_answer.userId) as answerCount\n" +
            "FROM app\n" +
            "JOIN user_answer ON app.id = user_answer.appId\n" +
            "GROUP BY appId\n" +
            "ORDER BY answerCount DESC;")
    List<AppAnswerCountDTO> doAppAnswerCount();

    @Select("select resultName, COUNT(resultName) as resultCount from user_answer\n" +
            "where appId = #{appId} group by resultName order by resultCount desc;")
    List<AppAnswerResultCountDTO> doAppAnswerResultCount(Long appId);
}