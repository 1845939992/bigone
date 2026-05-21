# CampusHub 项目实现计划

**版本**: v1.0  
**日期**: 2026年5月  
**基于**: [CampusHub需求规格说明书](./CampusHub需求规格说明书.md) V1.1.0

---

## 一、项目概述

CampusHub 是一款面向高校学生的 Android 移动社区平台，提供帖子发布、浏览、评论、收藏、点赞以及个人信息管理等功能。项目采用 Android Jetpack Compose + Kotlin 技术栈进行开发。

---

## 二、技术栈选择

### 前端（Android 客户端）

| 技术 | 选型 | 说明 |
|------|------|------|
| 开发语言 | Kotlin | Google 官方推荐的 Android 开发语言 |
| UI 框架 | Jetpack Compose + Material3 | 声明式 UI，高效开发，现代设计 |
| 导航 | Navigation Compose | 官方导航组件，支持类型安全传参 |
| 图片加载 | Coil (Compose) | 轻量级 Kotlin 图片加载库 |
| 网络请求 | Retrofit + OkHttp | 业界标准 HTTP 客户端 |
| JSON 解析 | Gson / Moshi | JSON 序列化/反序列化 |
| 异步处理 | Kotlin Coroutines + Flow | 协程异步编程，StateFlow 响应式数据流 |
| 本地存储 | SharedPreferences | 轻量级键值对存储（凭证/用户/帖子缓存） |
| 构建工具 | Gradle (Kotlin DSL) | Android 官方构建系统 |

### 后端

| 技术 | 选型 | 说明 |
|------|------|------|
| 数据库 | MySQL | 关系型数据库（需求规格指定） |
| API 风格 | RESTful API | 标准 HTTP REST 接口 |
| 数据格式 | JSON | 前后端数据交换格式 |

### 开发工具

| 工具 | 用途 |
|------|------|
| Android Studio | IDE & 调试 |
| Git | 版本控制 |
| Postman/Apifox | API 调试 |

---

## 三、开发阶段划分

### 阶段一：项目初始化与环境搭建（已完成）

- **时间**: 第1周
- **任务**:
  - [x] 创建 Android 项目，配置 Gradle
  - [x] 引入核心依赖（Compose, Navigation, Coil, Coroutines）
  - [x] 搭建项目包结构（data/ui/navigation/utils）
  - [x] 实现基础导航框架（BottomBar + NavGraph）
  - [x] 配置 Material3 主题

### 阶段二：用户模块开发（已完成）

- **时间**: 第1-2周
- **对应用例**: UC1001, UC1002, UC1003, UC1004
- **任务**:
  - [x] 用户注册页面（RegisterScreen + RegisterViewModel）
  - [x] 用户登录页面（LoginScreen + LoginViewModel）
  - [x] 记住密码功能（SharedPreferences 本地持久化）
  - [x] 自动登录功能（独立复选框控制）
  - [x] 退出登录功能
  - [x] 密码修改功能（ChangePasswordDialog）
  - [x] 用户数据模型定义（User data class）
  - [x] 用户 Repository 接口及 Mock 实现

### 阶段三：社区模块开发（已完成）

- **时间**: 第2-3周
- **对应用例**: UC3001, UC4001, UC4002, UC4003
- **任务**:
  - [x] 帖子数据模型定义（Post data class，含 imageUrls）
  - [x] 帖子 Repository 接口及 Mock 实现
  - [x] 首页帖子浏览（HomeScreen + HomeViewModel）
  - [x] 帖子列表（LazyColumn + PostCard 组件）
  - [x] 发布新帖子（PostScreen + PostViewModel）
  - [x] 图片选择与展示（ActivityResultContracts + Coil）
  - [x] 点赞功能（乐观 UI 更新 + StateFlow 同步）
  - [x] 收藏功能（乐观 UI 更新 + 跨页面同步）
  - [x] 帖子数据本地持久化（SharedPreferences JSON）
  - [x] 响应式数据流（StateFlow + notifyPostsChanged）

### 阶段四：帖子详情与评论模块开发（已完成）

- **时间**: 第3周
- **对应用例**: UC3003, UC4004, UC4005
- **任务**:
  - [x] 帖子详情页面（PostDetailScreen + PostDetailViewModel）
  - [x] 一级评论发表（底部输入栏 + 发送按钮）
  - [x] 二级回复（嵌套评论 + 回复对话框）
  - [x] 评论数据模型与存储
  - [x] 帖子卡片点击导航到详情页
  - [x] 详情页点赞/收藏状态同步

### 阶段五：搜索模块开发（已完成）

- **时间**: 第3-4周
- **对应用例**: UC3002
- **任务**:
  - [x] 搜索页面（SearchScreen + SearchViewModel）
  - [x] 搜索输入框 + 搜索按钮
  - [x] 搜索结果列表展示
  - [x] 搜索关键词标题模糊匹配
  - [x] 搜索结果中点赞/收藏功能
  - [x] 搜索结果导航到帖子详情

### 阶段六：个人中心模块开发（已完成）

- **时间**: 第4周
- **对应用例**: UC2001, UC2002, UC2003, UC2004, UC2005, UC2006, UC2007
- **任务**:
  - [x] 个人信息查看（ProfileScreen + ProfileViewModel）
  - [x] 个人信息编辑（头像选择 + 昵称修改）
  - [x] 我的帖子列表（含编辑/删除功能）
  - [x] 帖子编辑功能
  - [x] 帖子删除功能（确认对话框）
  - [x] 我的收藏列表（含取消收藏）
  - [x] 退出登录入口

### 阶段七：架构优化与重构（已完成）

- **时间**: 第4-5周
- **任务**:
  - [x] ViewModel 与 MockData 解耦（Repository 模式）
  - [x] PreferencesManager 统一管理本地存储
  - [x] StateFlow 响应式数据流升级
  - [x] 数据持久化完善（用户 + 帖子跨重启保存）
  - [x] Model 序列化完善（isFavorite/isLiked 字段）
  - [x] 后端集成准备（BACKEND_INTEGRATION_GUIDE.md）
  - [x] ApiService / RetrofitClient 接口定义

### 阶段八：后端接口对接（进行中 — API 基础设施已完成）

- **时间**: 第5-6周
- **任务**:
  - [x] 添加 Retrofit/OkHttp/Gson 依赖
  - [x] 创建 ApiModels（请求/响应体模型）
  - [x] 创建 ApiService（Retrofit 接口定义，19 个端点）
  - [x] 创建 RetrofitClient（OkHttp + Token 拦截器）
  - [x] 实现 ApiUserRepository（替换 MockUserRepository）
  - [x] 实现 ApiPostRepository（替换 MockPostRepository）
  - [x] Token 管理（PreferencesManager + RetrofitClient）
  - [x] 网络异常处理（NetworkUtils + Repository try/catch + 离线缓存降级）
  - [ ] 接入用户注册/登录 API（等待后端就绪）
  - [ ] 接入帖子 CRUD API（等待后端就绪）
  - [ ] 接入评论/回复 API（等待后端就绪）
  - [ ] 接入点赞/收藏 API（等待后端就绪）
  - [ ] 接入搜索 API（等待后端就绪）
  - [ ] 图片上传 API（等待后端就绪）
  - [ ] 网络异常处理与离线降级

### 阶段九：测试与优化（待完成）

- **时间**: 第6-7周
- **任务**:
  - [ ] 功能测试（所有用例全覆盖）
  - [ ] UI 兼容性测试（多设备屏幕适配）
  - [ ] 网络异常场景测试
  - [ ] 性能优化（列表滚动、图片加载）
  - [ ] 内存泄漏检查
  - [ ] 代码规范审查
  - [ ] 用户反馈收集与修复

---

## 四、关键里程碑

| 里程碑 | 目标日期 | 交付物 | 状态 |
|--------|----------|--------|------|
| M1: 项目启动 | 第1周末 | 项目骨架、导航框架 | ✅ 已完成 |
| M2: 用户模块完成 | 第2周末 | 注册/登录/自动登录/记住密码 | ✅ 已完成 |
| M3: 社区核心功能完成 | 第3周末 | 帖子浏览/发帖/点赞/收藏 | ✅ 已完成 |
| M4: 评论与搜索完成 | 第4周末 | 帖子详情/评论/搜索 | ✅ 已完成 |
| M5: 个人中心完成 | 第4周末 | 个人信息/我的帖子/我的收藏 | ✅ 已完成 |
| M6: 架构优化完成 | 第5周末 | 后端集成准备就绪 | ✅ 已完成 |
| M7: 后端对接完成 | 第6周末 | API 基础设施就绪，待后端就绪后切换 | 🔄 进行中 |
| M8: 测试与发布 | 第7周末 | 稳定版 APK | ⬜ 待完成 |

---

## 五、任务分解详表

### 5.1 用户模块（19个用例，4个）

| ID | 任务 | 优先级 | 预估工时 | 状态 |
|----|------|--------|----------|------|
| T001 | 用户注册页面 UI | P0 | 4h | ✅ |
| T002 | 用户注册逻辑（校验 + Mock 存储） | P0 | 4h | ✅ |
| T003 | 用户登录页面 UI（含记住密码/自动登录） | P0 | 4h | ✅ |
| T004 | 用户登录逻辑（凭证验证） | P0 | 4h | ✅ |
| T005 | 记住密码本地存储 | P1 | 2h | ✅ |
| T006 | 自动登录功能 | P1 | 3h | ✅ |
| T007 | 退出登录（清除状态） | P0 | 1h | ✅ |
| T008 | 密码修改 Dialog | P1 | 2h | ✅ |
| T009 | 用户数据模型 | P0 | 1h | ✅ |
| T010 | UserRepository 接口定义 | P0 | 1h | ✅ |
| T011 | MockUserRepository 实现 | P0 | 3h | ✅ |
| T012 | ApiUserRepository 实现 | P0 | 4h | ⬜ |
| T013 | 用户 API 接口定义（Retrofit） | P0 | 2h | ⬜ |

### 5.2 帖子模块

| ID | 任务 | 优先级 | 预估工时 | 状态 |
|----|------|--------|----------|------|
| T020 | 帖子数据模型（Post data class） | P0 | 1h | ✅ |
| T021 | PostRepository 接口定义 | P0 | 1h | ✅ |
| T022 | MockPostRepository 实现 | P0 | 3h | ✅ |
| T023 | 首页帖子列表 UI（HomeScreen） | P0 | 4h | ✅ |
| T024 | PostCard 组件（标题/内容/图片/按钮） | P0 | 3h | ✅ |
| T025 | 发帖页面 UI（PostScreen） | P0 | 4h | ✅ |
| T026 | 图片选择与预览 | P1 | 3h | ✅ |
| T027 | 发布帖子逻辑（StateFlow 通知） | P0 | 2h | ✅ |
| T028 | 点赞功能（乐观更新） | P0 | 2h | ✅ |
| T029 | 收藏功能（乐观更新 + 同步） | P0 | 2h | ✅ |
| T030 | 帖子数据持久化（JSON → SP） | P0 | 3h | ✅ |
| T031 | ApiPostRepository 实现 | P0 | 4h | ⬜ |
| T032 | 帖子 API 接口定义（Retrofit） | P0 | 2h | ⬜ |

### 5.3 评论模块

| ID | 任务 | 优先级 | 预估工时 | 状态 |
|----|------|--------|----------|------|
| T040 | 评论数据模型 | P0 | 1h | ✅ |
| T041 | 帖子详情页 UI（PostDetailScreen） | P0 | 4h | ✅ |
| T042 | 评论列表展示 | P0 | 2h | ✅ |
| T043 | 一级评论发表 | P0 | 2h | ✅ |
| T044 | 二级回复（嵌套评论 + Dialog） | P1 | 3h | ✅ |
| T045 | 评论 API 接口定义 | P0 | 1h | ⬜ |
| T046 | 评论 API 实现 | P0 | 2h | ⬜ |

### 5.4 搜索模块

| ID | 任务 | 优先级 | 预估工时 | 状态 |
|----|------|--------|----------|------|
| T050 | 搜索页面 UI（SearchScreen） | P0 | 3h | ✅ |
| T051 | 搜索逻辑（标题模糊匹配） | P0 | 2h | ✅ |
| T052 | 搜索结果列表 + 交互 | P0 | 2h | ✅ |
| T053 | 搜索 API 接口定义 | P0 | 1h | ⬜ |

### 5.5 个人中心模块

| ID | 任务 | 优先级 | 预估工时 | 状态 |
|----|------|--------|----------|------|
| T060 | 个人中心页面 UI（ProfileScreen） | P0 | 4h | ✅ |
| T061 | 个人信息展示（头像/用户名/昵称） | P0 | 2h | ✅ |
| T062 | 编辑个人信息（头像选择 + 昵称） | P1 | 3h | ✅ |
| T063 | 我的帖子列表 + 编辑/删除 | P0 | 4h | ✅ |
| T064 | 我的收藏列表 + 取消收藏 | P0 | 3h | ✅ |

### 5.6 基础设施

| ID | 任务 | 优先级 | 预估工时 | 状态 |
|----|------|--------|----------|------|
| T070 | 项目 Gradle 配置与依赖管理 | P0 | 2h | ✅ |
| T071 | 主题与样式配置（Material3） | P0 | 2h | ✅ |
| T072 | Navigation 导航框架 | P0 | 3h | ✅ |
| T073 | PreferencesManager 工具类 | P0 | 3h | ✅ |
| T074 | RetrofitClient 配置 | P0 | 2h | ⬜ |
| T075 | ApiModels（请求/响应模型） | P0 | 2h | ⬜ |
| T076 | Token 管理（拦截器 + 刷新） | P0 | 3h | ⬜ |
| T077 | 网络异常处理 + 错误提示 | P1 | 2h | ⬜ |

---

## 六、资源分配

| 角色 | 人数 | 职责 |
|------|------|------|
| Android 开发 | 1-2人 | UI 实现、业务逻辑、架构设计 |
| 后端开发 | 1人 | API 设计、数据库设计、接口开发 |
| 测试 | 1人（可兼任） | 功能测试、兼容性测试、Bug 跟踪 |

---

## 七、风险与应对

| 风险 | 可能性 | 影响 | 应对措施 |
|------|--------|------|----------|
| 后端接口延迟交付 | 中 | 高 | 前期使用 Mock 数据开发，前后端解耦 |
| 图片上传性能问题 | 低 | 中 | 使用压缩上传 + 分页加载策略 |
| Android 版本兼容性 | 低 | 中 | 目标 API 34，最低 API 26，覆盖主流设备 |
| Token 过期处理 | 中 | 中 | 实现自动刷新机制 + 401 拦截跳转登录 |

---
*本文档基于 CampusHub需求规格说明书 V1.1.0 编写*
