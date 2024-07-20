package com.curtis.uul.mapper;

import com.curtis.uul.model.dto.statistic.AppCountDTO;
import com.curtis.uul.model.entity.App;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author ZhangJiaHao
* @description 针对表【app(应用)】的数据库操作Mapper
* @createDate 2024-06-23 16:46:28
* @Entity generator.domain.App
*/
public interface AppMapper extends BaseMapper<App> {

    @Select("select appType,COUNT(appType) as appCount from app\n" +
            "group by appType order by appCount desc;")
    List<AppCountDTO> doAppCount();
}




