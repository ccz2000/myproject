package com.ccz.department.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccz.department.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author : Chenchunze
 * @description : 用户资料数据访问层
 * @createDate : 2025/6/6 11:41
 */

@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {

    /**
     * 根据用户ID查询用户资料
     * @param userId 用户ID
     * @return 用户资料信息
     */
    @Select("SELECT up.*, u.username, u.email FROM user_profile up " +
            "LEFT JOIN users u ON up.user_id = u.id " +
            "WHERE up.user_id = #{userId}")
    UserProfile selectProfileWithUserInfo(@Param("userId") Long userId);

    /**
     * 检查昵称是否已存在（排除当前用户）
     * @param nickname 昵称
     * @param userId 当前用户ID
     * @return 存在的数量
     */
    @Select("SELECT COUNT(*) FROM user_profile WHERE nickname = #{nickname} AND user_id != #{userId}")
    int checkNicknameExists(@Param("nickname") String nickname, @Param("userId") Long userId);
} 