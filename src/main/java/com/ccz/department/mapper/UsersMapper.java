package com.ccz.department.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccz.department.entity.Users;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Chenchunze
 * @description : 用户数据访问层
 * @createDate : 2025/6/5 15:35
 */

@Mapper
public interface UsersMapper extends BaseMapper<Users> {
} 