package com.ccz.department.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccz.department.entity.EmailVerification;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : Chenchunze
 * @description : 邮箱验证码数据访问层
 * @createDate : 2025/6/5 18:05
 */

@Mapper
public interface EmailVerificationMapper extends BaseMapper<EmailVerification> {
} 