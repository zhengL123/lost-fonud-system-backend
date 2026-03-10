# 景区失物招领平台后端

## 项目简介
景区失物招领平台后端是一个基于Spring Boot的Java后端服务，旨在解决景区内游客丢失物品的问题，提供失物登记、认领管理、数据统计等功能，帮助景区工作人员更高效地管理失物信息，提升游客体验。

## 技术栈
- **后端框架**：Spring Boot 2.7.6
- **持久层**：MyBatis Plus 3.5.3.1
- **数据库**：MySQL 8.0+
- **数据库连接池**：Druid 1.2.15
- **缓存**：Redis 5.0+
- **认证**：JWT (JSON Web Token)
- **安全框架**：Spring Security
- **API文档**：SpringDoc OpenAPI 1.6.14
- **文件上传**：Commons FileUpload 1.4
- **工具类库**：
  - Lombok 1.18.24
  - Commons Lang3 3.12.0
  - Commons IO 2.11.0
- **监控**：Spring Boot Actuator
- **开发工具**：Spring Boot DevTools
- **构建工具**：Maven 3.8+

## 核心功能
1. **用户管理**：注册、登录、个人信息管理、权限控制、角色管理
2. **失物管理**：发布失物信息、查询失物、更新失物状态、图片上传、分类管理
3. **认领管理**：提交认领申请、审核认领信息、处理认领结果、认领记录管理
4. **感谢留言**：用户提交感谢留言、管理员管理留言
5. **数据统计**：系统统计、失物类型分布、高发丢失地点、失物状态统计、月度失物统计
6. **系统公告**：管理员发布公告、用户查看公告、公告分类、公告搜索
7. **文件上传**：支持上传失物图片，自动处理文件存储和访问路径
8. **热门搜索**：热门丢失地点统计、热门搜索词管理
9. **缓存管理**：Redis缓存支持、缓存预热、缓存清理
10. **安全保障**：JWT认证、密码加密、权限控制、CORS配置

## 项目结构
```
lost-found-backend/
├── src/main/java/com/lostfound/server/
│   ├── config/          # 配置类
│   │   ├── CorsConfig.java           # CORS跨域配置
│   │   ├── FileUploadConfig.java     # 文件上传配置
│   │   ├── HotSearchInitializer.java # 热门搜索初始化
│   │   ├── MyMetaObjectHandler.java  # MyBatis-Plus元数据处理
│   │   ├── MybatisPlusConfig.java    # MyBatis-Plus配置
│   │   ├── PasswordEncoderConfig.java # 密码加密配置
│   │   ├── RedisConfig.java          # Redis配置
│   │   ├── SecurityConfig.java       # Spring Security配置
│   │   └── WebMvcConfig.java         # Spring MVC配置
│   ├── controller/      # 控制器（API接口）
│   │   ├── AdminController.java       # 管理员控制器
│   │   ├── AnnouncementController.java # 公告控制器
│   │   ├── AuthController.java        # 认证控制器
│   │   ├── CacheController.java       # 缓存控制器
│   │   ├── ClaimRecordController.java # 认领记录控制器
│   │   ├── FileUploadController.java  # 文件上传控制器
│   │   ├── HotSearchController.java   # 热门搜索控制器
│   │   ├── ItemCategoryController.java # 物品分类控制器
│   │   ├── LostItemController.java    # 失物控制器
│   │   ├── MessageController.java     # 消息控制器
│   │   ├── PermissionController.java  # 权限控制器
│   │   ├── RedisExampleController.java # Redis示例控制器
│   │   ├── RoleController.java        # 角色控制器
│   │   ├── StatisticsController.java  # 统计控制器
│   │   ├── ThankNoteController.java   # 感谢留言控制器
│   │   └── UserController.java        # 用户控制器
│   ├── dto/             # 数据传输对象
│   │   ├── HotLocationDTO.java           # 热门地点DTO
│   │   ├── ItemStatusStatisticsDTO.java  # 物品状态统计DTO
│   │   ├── ItemTypeDistributionDTO.java  # 物品类型分布DTO
│   │   ├── LostItemDTO.java              # 失物DTO
│   │   ├── MonthlyItemStatisticsDTO.java # 月度物品统计DTO
│   │   └── SystemStatisticsDTO.java      # 系统统计DTO
│   ├── entity/          # 实体类
│   │   ├── Announcement.java     # 公告实体
│   │   ├── ClaimRecord.java      # 认领记录实体
│   │   ├── ItemCategory.java     # 物品分类实体
│   │   ├── LostItem.java         # 失物实体
│   │   ├── Message.java          # 消息实体
│   │   ├── Permission.java       # 权限实体
│   │   ├── Role.java             # 角色实体
│   │   ├── RolePermission.java   # 角色权限关联实体
│   │   ├── ThankNote.java        # 感谢留言实体
│   │   └── User.java             # 用户实体
│   ├── exception/       # 异常处理
│   │   ├── BusinessException.java        # 业务异常
│   │   ├── ForbiddenException.java       # 禁止访问异常
│   │   ├── GlobalExceptionHandler.java   # 全局异常处理器
│   │   ├── ResourceNotFoundException.java # 资源未找到异常
│   │   └── UnauthorizedException.java    # 未授权异常
│   ├── interceptor/     # 拦截器
│   │   └── AdminInterceptor.java         # 管理员拦截器
│   ├── mapper/          # 数据访问层
│   │   ├── AnnouncementMapper.java    # 公告Mapper
│   │   ├── ClaimRecordMapper.java     # 认领记录Mapper
│   │   ├── ItemCategoryMapper.java    # 物品分类Mapper
│   │   ├── LostItemMapper.java        # 失物Mapper
│   │   ├── MessageMapper.java         # 消息Mapper
│   │   ├── PermissionMapper.java      # 权限Mapper
│   │   ├── RoleMapper.java            # 角色Mapper
│   │   ├── RolePermissionMapper.java  # 角色权限关联Mapper
│   │   ├── StatisticsMapper.java      # 统计Mapper
│   │   ├── ThankNoteMapper.java       # 感谢留言Mapper
│   │   └── UserMapper.java            # 用户Mapper
│   ├── security/        # 安全相关
│   │   ├── JwtAuthenticationFilter.java # JWT认证过滤器
│   │   └── UserDetailsServiceImpl.java  # 用户详情服务实现
│   ├── service/         # 业务逻辑层
│   │   ├── impl/        # 服务实现
│   │   │   ├── AdminServiceImpl.java       # 管理员服务实现
│   │   │   ├── AnnouncementServiceImpl.java # 公告服务实现
│   │   │   ├── ClaimRecordServiceImpl.java  # 认领记录服务实现
│   │   │   ├── ItemCategoryServiceImpl.java # 物品分类服务实现
│   │   │   ├── LostItemServiceImpl.java     # 失物服务实现
│   │   │   ├── MessageServiceImpl.java      # 消息服务实现
│   │   │   ├── PermissionServiceImpl.java   # 权限服务实现
│   │   │   ├── RoleServiceImpl.java         # 角色服务实现
│   │   │   ├── StatisticsServiceImpl.java   # 统计服务实现
│   │   │   ├── ThankNoteServiceImpl.java    # 感谢留言服务实现
│   │   │   └── UserServiceImpl.java         # 用户服务实现
│   │   ├── AnnouncementService.java        # 公告服务
│   │   ├── ClaimRecordManagementService.java # 认领记录管理服务
│   │   ├── ClaimRecordService.java         # 认领记录服务
│   │   ├── ItemCategoryService.java        # 物品分类服务
│   │   ├── LostItemManagementService.java  # 失物管理服务
│   │   ├── LostItemService.java            # 失物服务
│   │   ├── MessageService.java             # 消息服务
│   │   ├── PermissionService.java          # 权限服务
│   │   ├── RoleService.java                # 角色服务
│   │   ├── StatisticsService.java          # 统计服务
│   │   ├── ThankNoteService.java           # 感谢留言服务
│   │   ├── UserManagementService.java      # 用户管理服务
│   │   └── UserService.java                # 用户服务
│   ├── util/            # 工具类
│   │   ├── AdminUtils.java               # 管理员工具类
│   │   ├── ClaimRecordServiceHelper.java # 认领记录服务辅助类
│   │   ├── FileUploadUtil.java           # 文件上传工具类
│   │   ├── JwtUtil.java                  # JWT工具类
│   │   ├── LostItemServiceHelper.java    # 失物服务辅助类
│   │   ├── PageResult.java               # 分页结果类
│   │   ├── PasswordCryptoUtil.java       # 密码加密工具类
│   │   ├── RedisService.java             # Redis服务类
│   │   ├── Result.java                   # 响应结果类
│   │   └── UserServiceHelper.java        # 用户服务辅助类
│   └── LostFoundApplication.java  # 启动类
├── src/main/resources/
│   ├── mapper/          # MyBatis映射文件
│   └── application.yml  # 配置文件
├── .gitignore           # Git忽略文件
├── pom.xml              # Maven依赖配置
└── README.md            # 项目说明
```

## 快速开始

### 环境要求
- JDK 11+
- MySQL 8.0+
- Redis 5.0+
- Maven 3.8+

### 安装与运行
1. **克隆仓库**：
   ```bash
   git clone https://github.com/yourusername/lost-found-backend.git
   cd lost-found-backend
   ```

2. **配置数据库**：
   - 修改 `src/main/resources/application.yml` 中的数据库连接信息
   - 创建数据库 `lost_and_found`（或根据配置修改数据库名）
   - 数据库表结构会自动生成（基于MyBatis-Plus的自动建表功能）

3. **配置环境变量**：
   - 可选择设置环境变量覆盖默认配置，如数据库密码、JWT密钥等
   - 详见配置说明部分的环境变量表

4. **构建项目**：
   ```bash
   mvn clean install
   ```

5. **运行项目**：
   ```bash
   mvn spring-boot:run
   ```
   或使用IDE直接运行 `LostFoundApplication.java`

6. **访问API文档**：
   - 启动后访问：`http://localhost:8083/swagger-ui.html`
   - API文档地址：`http://localhost:8083/v3/api-docs`

## API接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

### 失物管理接口
- `POST /api/lost-items` - 发布失物信息
- `GET /api/lost-items/{id}` - 查询失物详情
- `GET /api/lost-items/page` - 分页查询失物列表
- `GET /api/lost-items/all` - 查询所有失物（不分页）
- `GET /api/lost-items/status/{status}` - 根据状态查询失物列表
- `GET /api/lost-items/latest` - 获取最新失物信息
- `GET /api/lost-items/debug/images/{id}` - 调试接口 - 检查图片数据
- `PUT /api/lost-items/{id}` - 更新失物信息
- `DELETE /api/lost-items/{id}` - 删除失物信息

### 认领管理接口
- `POST /api/claim-records` - 提交认领申请
- `GET /api/claim-records/page` - 分页查询认领记录
- `GET /api/claim-records/{id}` - 查询认领记录详情
- `GET /api/claim-records/item/{itemId}` - 根据物品ID查询认领记录
- `GET /api/claim-records/user/{userId}` - 根据用户ID查询认领记录
- `GET /api/claim-records/creator/{creatorId}` - 根据物品创建者ID查询认领记录
- `GET /api/claim-records/status/{status}` - 根据状态查询认领记录
- `GET /api/claim-records/pending` - 获取待处理的认领申请
- `PUT /api/claim-records/{id}` - 更新认领记录
- `PUT /api/claim-records/{id}/process` - 处理认领申请（管理员操作）
- `DELETE /api/claim-records/{id}` - 删除认领记录

### 统计分析接口
- `GET /api/statistics/system` - 获取系统统计数据
- `GET /api/statistics/item-types` - 获取失物类型分布统计
- `GET /api/statistics/hot-locations` - 获取高发丢失地点统计（支持 limit 参数）
- `GET /api/statistics/item-status` - 获取失物状态统计
- `GET /api/statistics/monthly-items` - 获取月度失物统计（支持 months 参数）
- `GET /api/statistics/yearly-items/{year}` - 获取指定年份的月度失物统计

### 公告管理接口
- `POST /api/announcements` - 创建公告
- `GET /api/announcements/page` - 分页查询公告
- `GET /api/announcements/published` - 获取已发布的公告列表
- `GET /api/announcements/type/{announcementType}` - 按类型获取公告列表
- `GET /api/announcements/latest` - 获取最新公告列表（支持 limit 参数）
- `GET /api/announcements/{id}` - 查询公告详情
- `GET /api/announcements/search` - 搜索公告（支持 keyword 参数）
- `PUT /api/announcements/{id}` - 更新公告
- `PUT /api/announcements/{id}/publish` - 发布公告
- `PUT /api/announcements/{id}/unpublish` - 取消发布公告
- `DELETE /api/announcements/{id}` - 删除公告
- `DELETE /api/announcements/batch` - 批量删除公告
- `PUT /api/announcements/batch/publish` - 批量发布公告
- `PUT /api/announcements/batch/unpublish` - 批量取消发布公告

## 配置说明

### 环境变量
项目支持通过环境变量覆盖配置文件中的默认值：

| 环境变量 | 描述 | 默认值 |
|---------|------|-------|
| DB_HOST | 数据库主机地址 | localhost |
| DB_PORT | 数据库端口 | 3306 |
| DB_USERNAME | 数据库用户名 | root |
| DB_PASSWORD | 数据库密码 | 123456 |
| REDIS_HOST | Redis主机地址 | localhost |
| REDIS_PORT | Redis端口 | 6379 |
| REDIS_PASSWORD | Redis密码 | 空 |
| JWT_SECRET | JWT密钥 | secret_key_for_jwt_token_generation_please_change_in_production |
| JWT_EXPIRATION | JWT过期时间(毫秒) | 36000000 |
| FRONTEND_URL | 前端地址 | http://localhost:5173 |
| PASSWORD_CRYPTO_SECRET | 密码加密密钥 | secret_key_for_password_encryption |
| FILE_UPLOAD_PATH | 文件上传路径 | uploads |
| FILE_UPLOAD_DOMAIN | 文件访问域名 | http://localhost:8083 |
| SERVER_PORT | 服务器端口 | 8083 |

### 配置文件
主要配置文件为 `src/main/resources/application.yml`，包含数据库连接、Redis配置、JWT配置等。

## 开发指南

### 代码规范
- 遵循Java代码规范，使用驼峰命名法
- 类名使用大驼峰命名法
- 方法名和变量名使用小驼峰命名法
- 常量使用全大写，单词间用下划线分隔
- 方法和类添加适当的注释

### 分支管理
- `main` - 主分支，用于发布稳定版本
- `develop` - 开发分支，用于集成新功能
- `feature/*` - 功能分支，用于开发新功能
- `bugfix/*` - 修复分支，用于修复bug

### 提交规范
提交信息格式：
```
<type>(<scope>): <subject>

<body>

<footer>
```

类型说明：
- `feat` - 新功能
- `fix` - 修复bug
- `docs` - 文档更新
- `style` - 代码风格修改
- `refactor` - 代码重构
- `test` - 测试相关
- `chore` - 构建或依赖更新

## 部署

### 本地部署
1. 确保环境要求中的软件已安装
2. 按照快速开始中的步骤执行

### 生产部署
1. **构建可执行jar包**：
   ```bash
   mvn clean package
   ```
   构建完成后，jar包会生成在 `target/backend.jar`

2. **运行jar包**：
   ```bash
   java -jar target/backend.jar
   ```
   或使用环境变量运行：
   ```bash
   DB_PASSWORD=your_password JWT_SECRET=your_jwt_secret java -jar target/backend.jar
   ```

3. **Docker容器化部署**（推荐）：
   - 创建 `Dockerfile`：
     ```dockerfile
     FROM openjdk:11-jre-slim
     WORKDIR /app
     COPY target/backend.jar /app/
     EXPOSE 8083
     ENV DB_HOST=localhost
     ENV DB_PORT=3306
     ENV DB_USERNAME=root
     ENV DB_PASSWORD=123456
     ENV JWT_SECRET=secret_key_for_jwt_token_generation_please_change_in_production
     ENV FRONTEND_URL=http://localhost:5173
     CMD ["java", "-jar", "backend.jar"]
     ```
   - 构建镜像：
     ```bash
     docker build -t lost-found-backend .
     ```
   - 运行容器：
     ```bash
     docker run -d -p 8083:8083 --name lost-found-backend lost-found-backend
     ```

4. **配置文件说明**：
   - 生产环境建议使用外部配置文件或环境变量覆盖默认配置
   - 敏感信息（如数据库密码、JWT密钥）应通过环境变量设置
   - 可通过 `-Dspring.config.location` 指定外部配置文件：
     ```bash
     java -Dspring.config.location=file:/path/to/application.yml -jar target/backend.jar
     ```

## 监控与日志
- 系统使用Spring Boot Actuator提供健康检查和监控
- 日志配置在 `application.yml` 中，默认输出到控制台
- 可根据需要配置日志文件输出

## 安全措施
- 使用JWT进行无状态认证
- 密码使用BCrypt加密存储
- 实现了基于角色的权限控制
- 配置了CORS跨域支持
- 防止SQL注入和XSS攻击

## 贡献
欢迎提交Issue和Pull Request！

1. Fork本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'feat: add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 打开Pull Request

## 许可证
MIT License

---

**注意**：本项目为景区失物招领平台的后端服务，需要与前端服务配合使用。