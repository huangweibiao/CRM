# Quick Start Guide - CRM System

## 快速启动指南

### 前置要求

确保您的系统已安装以下软件：

- Java 17 或更高版本
- Maven 3.6+
- Node.js 20.x
- MySQL 8.0+

### 步骤 1: 数据库配置

1. 启动 MySQL 服务
2. 创建数据库：

```sql
CREATE DATABASE crm_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 配置数据库连接：

编辑 `src/main/resources/application.yml` 文件，修改数据库用户名和密码：

```yaml
spring:
  datasource:
    username: your_username
    password: your_password
```

### 步骤 2: OAuth2 配置

由于系统已经集成了 OAuth2，您需要：

1. 配置 OAuth2 服务器地址（在 `application.yml` 中）
2. 设置正确的 client-id 和 client-secret
3. 确保 OAuth2 服务器可访问

如果暂时没有 OAuth2 服务器，可以修改 `SecurityConfig.java` 临时禁用 OAuth2：

```java
.formLogin(form -> form
    .loginPage("/login")
    .defaultSuccessUrl("/dashboard")
)
.oauth2Login(oauth2 -> oauth2.disable())
```

### 步骤 3: 构建和运行

#### 完整构建（推荐用于生产环境）

```bash
# 构建整个项目（包括前端）
mvn clean install

# 运行应用
mvn spring-boot:run
```

#### 开发模式（推荐用于开发）

```bash
# 终端 1: 运行 Spring Boot
mvn spring-boot:run

# 终端 2: 运行 Vue 开发服务器
cd frontend
npm install
npm run dev
```

### 步骤 4: 访问应用

应用启动后，通过浏览器访问：

- **主页**: http://localhost:8080
- **登录页**: http://localhost:8080/login
- **仪表板**: http://localhost:8080/dashboard
- **客户管理**: http://localhost:8080/customers
- **联系人管理**: http://localhost:8080/contacts
- **API 文档**: http://localhost:8080/api

### 初始数据

系统会自动执行 `schema.sql` 和 `data.sql` 脚本，创建以下初始数据：

- **用户**: 2 个示例用户
- **客户**: 6 个示例客户
- **联系人**: 7 个示例联系人
- **交易**: 5 个示例交易
- **活动**: 6 个示例活动

### 常见问题

#### 1. 数据库连接失败

**问题**: `Communications link failure`

**解决方案**:
- 确保 MySQL 服务正在运行
- 检查 `application.yml` 中的数据库 URL、用户名和密码
- 确保数据库 `crm_db` 已创建

#### 2. OAuth2 登录失败

**问题**: OAuth2 登录后无法重定向

**解决方案**:
- 确保 OAuth2 服务器可访问
- 检查 `application.yml` 中的 OAuth2 配置
- 验证 redirect URI 在 OAuth2 服务器中配置正确

#### 3. 前端构建失败

**问题**: `npm install` 失败

**解决方案**:
```bash
# 清理 node_modules
rm -rf frontend/node_modules
cd frontend
npm install
```

#### 4. 端口冲突

**问题**: 端口 8080 已被占用

**解决方案**:

修改 `application.yml` 中的 server.port：

```yaml
server:
  port: 8081  # 或其他可用端口
```

### 开发提示

#### 热重载

- Spring Boot: 使用 DevTools 自动重启
- Vue: `npm run dev` 支持热重载

#### 调试

启用调试模式：

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--debug
```

或设置环境变量：

```bash
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

#### 查看日志

日志文件位于：
- 控制台输出
- 如有配置，查看 `logs/` 目录

### 下一步

1. 自定义 OAuth2 配置以匹配您的认证服务器
2. 根据业务需求扩展实体类和功能
3. 添加更多的前端组件和页面
4. 实现权限控制和角色管理
5. 添加单元测试和集成测试

### 技术支持

如有问题，请查看：

- [完整文档](README.md)
- Spring Boot 文档: https://spring.io/projects/spring-boot
- Vue 3 文档: https://vuejs.org/
- MySQL 文档: https://dev.mysql.com/doc/

---

祝您使用愉快！🚀
