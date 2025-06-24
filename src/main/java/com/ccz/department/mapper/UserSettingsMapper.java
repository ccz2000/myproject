package com.ccz.department.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccz.department.entity.UserSettings;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author : Chenchunze
 * @description : 用户设置数据访问层
 * @createDate : 2025/6/6 11:41
 */

@Mapper
public interface UserSettingsMapper extends BaseMapper<UserSettings> {

    /**
     * 根据用户ID查询用户设置
     * @param userId 用户ID
     * @return 用户设置信息
     */
    @Select("SELECT * FROM user_settings WHERE user_id = #{userId}")
    UserSettings selectByUserId(@Param("userId") Long userId);
} 