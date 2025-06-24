package com.ccz.department.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author : Chenchunze
 * @description : 用户数据传输对象
 * @createDate : 2025/6/4 10:12
 */

@Data
public class UsersDTO {
    private Long id;
    private String username;
    private String email;
    private Integer status;
    private String permission;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
