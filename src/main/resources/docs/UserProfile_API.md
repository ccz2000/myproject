# 个人中心功能模块 API 接口文档

## 📋 概述

个人中心功能模块包含用户资料管理和用户设置管理两个主要部分。

## 🔗 基础URL

- 开发环境：`http://localhost:8080/dept-api`
- 用户资料：`/profile`
- 用户设置：`/settings`

## 📝 用户资料接口 (UserProfileController)

### 1. 获取用户资料

- **URL**: `GET /profile/{userId}`
- **描述**: 根据用户ID获取用户资料信息
- **参数**: 
  - `userId` (Path): 用户ID
- **响应**: 
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com",
    "realName": "张三",
    "nickname": "小张",
    "avatar": "/uploads/avatars/avatar_1.jpg",
    "phone": "13800138000",
    "gender": 1,
    "birthday": "1990-01-01",
    "bio": "这是个人简介",
    "address": "北京市朝阳区",
    "company": "某某公司",
    "position": "软件工程师",
    "website": "https://example.com",
    "qq": "123456789",
    "wechat": "wechat123",
    "weibo": "weibo123",

  }
}
```

### 2. 创建用户资料

- **URL**: `POST /profile`
- **描述**: 创建新的用户资料
- **请求体**: `UserProfileDTO`
- **示例**:
```json
{
  "userId": 1,
  "realName": "张三",
  "nickname": "小张",
  "phone": "13800138000",
  "gender": 1,
  "birthday": "1990-01-01",
  "bio": "这是个人简介"
}
```

### 3. 更新用户资料

- **URL**: `PUT /profile`
- **描述**: 更新用户资料信息
- **请求体**: `UserProfileDTO`

### 4. 修改密码

- **URL**: `POST /profile/{userId}/change-password`
- **描述**: 修改用户密码
- **参数**: 
  - `userId` (Path): 用户ID
- **请求体**:
```json
{
  "currentPassword": "oldPassword123",
  "newPassword": "newPassword123",
  "confirmPassword": "newPassword123"
}
```

### 5. 上传头像

- **URL**: `POST /profile/{userId}/upload-avatar`
- **描述**: 上传用户头像
- **参数**: 
  - `userId` (Path): 用户ID
  - `file` (Form): 头像文件（支持jpg、png格式，最大5MB）
- **响应**: 
```json
{
  "code": 200,
  "message": "头像上传成功",
  "data": "/uploads/avatars/avatar_1_uuid.jpg"
}
```

### 6. 检查昵称可用性

- **URL**: `GET /profile/check-nickname`
- **描述**: 检查昵称是否可用
- **参数**: 
  - `nickname` (Query): 昵称
  - `userId` (Query): 当前用户ID
- **响应**: 
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 7. 初始化用户资料

- **URL**: `POST /profile/{userId}/initialize`
- **描述**: 为新用户初始化基础资料
- **参数**: 
  - `userId` (Path): 用户ID

## ⚙️ 用户设置接口 (UserSettingsController)

### 1. 获取用户设置

- **URL**: `GET /settings/{userId}`
- **描述**: 根据用户ID获取用户设置
- **参数**: 
  - `userId` (Path): 用户ID
- **响应**: 
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "theme": "light",
    "language": "zh-CN",
    "timezone": "Asia/Shanghai",
    "emailNotification": 1,
    "smsNotification": 1,
    "privacyLevel": 1
  }
}
```

### 2. 更新用户设置

- **URL**: `PUT /settings`
- **描述**: 更新用户设置
- **请求体**:
```json
{
  "userId": 1,
  "theme": "dark",
  "language": "en-US",
  "timezone": "America/New_York",
  "emailNotification": 0,
  "smsNotification": 1,
  "privacyLevel": 2
}
```

### 3. 初始化用户设置

- **URL**: `POST /settings/{userId}/initialize`
- **描述**: 为新用户初始化默认设置
- **参数**: 
  - `userId` (Path): 用户ID

### 4. 重置设置为默认值

- **URL**: `POST /settings/{userId}/reset`
- **描述**: 重置用户设置为默认值
- **参数**: 
  - `userId` (Path): 用户ID

## 📊 字段说明

### 性别字段 (gender)
- `0`: 未知
- `1`: 男
- `2`: 女

### 通知设置字段
- `0`: 关闭
- `1`: 开启

### 隐私级别 (privacyLevel)
- `0`: 完全公开
- `1`: 部分公开
- `2`: 完全私密

### 主题设置 (theme)
- `light`: 浅色主题
- `dark`: 深色主题

## ❗ 错误码说明

- `200`: 成功
- `400`: 请求参数错误
- `401`: 未授权
- `403`: 禁止访问
- `404`: 资源不存在
- `500`: 服务器内部错误

## 📁 文件上传说明

### 头像上传限制
- **支持格式**: JPG, PNG, GIF
- **文件大小**: 最大 5MB
- **存储路径**: `/uploads/avatars/`
- **命名规则**: `avatar_{userId}_{uuid}.{extension}`

## 🔒 权限说明

所有接口都需要用户登录后才能访问，需要在请求头中包含有效的token。

## 📝 使用示例

### JavaScript 调用示例

```javascript
// 获取用户资料
const getUserProfile = async (userId) => {
  const response = await fetch(`/dept-api/profile/${userId}`, {
    method: 'GET',
    headers: {
      'Authorization': 'Bearer ' + token,
      'Content-Type': 'application/json'
    }
  });
  return response.json();
};

// 更新用户资料
const updateProfile = async (profileData) => {
  const response = await fetch('/dept-api/profile', {
    method: 'PUT',
    headers: {
      'Authorization': 'Bearer ' + token,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(profileData)
  });
  return response.json();
};

// 上传头像
const uploadAvatar = async (userId, file) => {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch(`/dept-api/profile/${userId}/upload-avatar`, {
    method: 'POST',
    headers: {
      'Authorization': 'Bearer ' + token
    },
    body: formData
  });
  return response.json();
};
``` 