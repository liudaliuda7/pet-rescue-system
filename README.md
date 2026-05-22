# 流浪动物救助与领养系统

基于 SpringBoot + MyBatis-Plus + Vue 2 + ElementUI 的流浪动物救助与领养管理系统。

## 技术栈

- 后端: Spring Boot 2.7.18 / Java 17 / MyBatis-Plus 3.5.5 / H2 (默认) 或 MySQL
- 前端: Vue 2.7 / Vue Router 3 / Element UI 2.15 / Axios / Vite

## 运行环境要求

- JDK 17+
- Maven 3.6+
- Node.js 16+
- npm 8+

## 启动方式

### 后端

```bash
cd backend
mvn -DskipTests package
java -jar target/rescue-backend.jar
```

后端默认运行在 **http://localhost:8080**，所有 API 路径以 `/api` 开头。
首次启动会自动初始化 H2 文件数据库 (`backend/data/`) 并预置测试数据。

### 前端

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在 **http://localhost:5173**，已配置 `/api` 代理到后端 8080 端口。

## 测试账号 (密码均为 `123456`)

| 角色 | 账号 |
|---|---|
| 系统管理员 | `admin` |
| 救助站 1 | `station1` |
| 救助站 2 | `station2` |
| 普通用户 1 | `user1` |
| 普通用户 2 | `user2` |

## 模块结构

### 前台用户模块 (`/`)
- 首页 / 求助信息 / 流浪动物 / 公告信息 / 个人中心
- 普通用户可提交求助、申请领养、管理个人资料

### 后台管理员模块 (`/admin`)
- 主页、用户管理、救助站管理、求助信息管理、求助记录管理、动物种类管理、流浪动物管理、回访领养管理、健康档案管理、系统管理（公告/个人中心）

### 后台救助站模块 (`/station`)
- 主页、求助信息管理、求助记录管理、流浪动物管理、宠物领养管理、回访档案管理、健康档案管理、个人中心
- 救助站账号只能看到自己所属救助站的数据

## 切换到 MySQL

修改 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/rescue?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: yourpass
```

将 `db/schema.sql` 中的 `BIGINT AUTO_INCREMENT` 等保持不变即可（已用 MySQL 兼容语法）。

## 主要业务流程

1. 用户注册 / 登录 → 浏览动物 / 公告 → 提交求助 / 申请领养
2. 救助站登录 → 处理求助（添加处理记录）→ 维护动物档案 → 审核领养 → 录入回访 / 健康档案
3. 管理员登录 → 管理用户 / 救助站 / 动物种类 / 公告 → 总览全局数据
