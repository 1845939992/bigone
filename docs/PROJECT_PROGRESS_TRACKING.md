# CampusHub 项目进度跟踪

**最后更新**: 2026年5月21日  
**当前版本**: V1.1 (API 基础设施就绪)  
**参考文档**: [需求规格说明书](./CampusHub需求规格说明书.md) | [后端集成指南](../BACKEND_INTEGRATION_GUIDE.md) | [项目实现计划](./PROJECT_IMPLEMENTATION_PLAN.md)

## 一、整体进度概览

| 模块 | 用例数 | 已完成 | 进行中 | 待实现 | 完成率 |
|------|--------|--------|--------|--------|--------|
| 用户模块 | 4 | 4 | 0 | 0 | 100% |
| 社区模块 | 3 | 3 | 0 | 0 | 100% |
| 评论模块 | 2 | 2 | 0 | 0 | 100% |
| 搜索模块 | 1 | 1 | 0 | 0 | 100% |
| 个人中心模块 | 7 | 7 | 0 | 0 | 100% |
| 后端对接 | - | 7 | 0 | 9 | 83% (API 框架 + 网络处理完成) |
| **总计** | **17 用例** | **17** | **0** | **后端对接** | **前端 100%** |

---

## 二、已完成功能模块

### 2.1 用户模块 ✅ (100%)

#### UC1001 用户注册
- **状态**: ✅ 已完成
- **实现文件**:
  - [RegisterScreen.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/ui/screen/register/RegisterScreen.kt)
  - [RegisterViewModel.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/ui/screen/register/RegisterViewModel.kt)
- **功能清单**:
  - 用户名/密码/确认密码/昵称输入与校验
  - 用户名唯一性检查
  - 注册成功后自动跳转登录页
  - 用户数据持久化到 SharedPreferences

#### UC1002 用户登录
- **状态**: ✅ 已完成
- **实现文件**:
  - [LoginScreen.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/ui/screen/login/LoginScreen.kt)
  - [LoginViewModel.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/ui/screen/login/LoginViewModel.kt)
- **功能清单**:
  - 用户名/密码登录验证
  - 记住密码（SharedPreferences + Base64 编码）
  - 自动登录（独立复选框，仅在勾选时自动登录）
  - 登录成功后跳转首页

#### UC1003 退出登录
- **状态**: ✅ 已完成
- **实现位置**: ProfileScreen + ProfileViewModel
- **功能清单**:
  - 清除当前登录状态
  - 清除记住密码和自动登录信息
  - 跳转到登录页面

#### UC1004 密码修改
- **状态**: ✅ 已完成
- **实现位置**: ChangePasswordDialog (ProfileScreen 内嵌)
- **功能清单**:
  - 原密码验证
  - 新密码/确认新密码输入与校验
  - 密码更新持久化

---

### 2.2 社区模块 ✅ (100%)

#### UC3001 / UC4001 浏览帖子 + 发布新帖子
- **状态**: ✅ 已完成
- **实现文件**:
  - [HomeScreen.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/ui/screen/home/HomeScreen.kt) + [HomeViewModel.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/ui/screen/home/HomeViewModel.kt)
  - [PostScreen.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/ui/screen/post/PostScreen.kt) + [PostViewModel.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/ui/screen/post/PostViewModel.kt)
  - [PostCard.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/ui/component/PostCard.kt)
- **功能清单**:
  - 首页帖子列表（LazyColumn，倒序排列）
  - 帖子卡片：标题/内容摘要/作者/点赞数/评论数/发布时间
  - 发帖：标题 + 正文 + 多图选择/预览/删除
  - 图片从本地相册选择（ActivityResultContracts.GetMultipleContents）
  - Coil 异步图片加载（最多展示3张，+N 叠加层）
  - 发帖后自动刷新首页和我的帖子（StateFlow 响应式）
  - 帖子数据本地持久化（JSON → SharedPreferences）

#### UC4002 点赞帖子
- **状态**: ✅ 已完成
- **实现文件**:
  - HomeViewModel.kt（optimistic toggleLike）
  - SearchViewModel.kt（optimistic toggleLike）
  - PostDetailViewModel.kt（同步点赞状态）
- **功能清单**:
  - 乐观 UI 更新（即时切换点赞状态 + 后台同步）
  - 点赞/取消点赞切换
  - 点赞数实时更新
  - 所有页面点赞状态同步（首页/详情/搜索/个人中心）

#### UC4003 收藏帖子
- **状态**: ✅ 已完成
- **实现文件**:
  - HomeViewModel.kt（optimistic toggleFavorite）
  - SearchViewModel.kt（optimistic toggleFavorite）
  - PostDetailViewModel.kt
- **功能清单**:
  - 乐观 UI 更新（即时切换收藏状态）
  - 收藏/取消收藏切换
  - 收藏状态跨页面同步（首页/详情/个人中心）
  - 收藏数据持久化（isFavorite 字段序列化）

---

### 2.3 评论模块 ✅ (100%)

#### UC4004 发表一级评论 + UC4005 发表二级评论
- **状态**: ✅ 已完成
- **实现文件**:
  - [PostDetailScreen.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/ui/screen/post/PostDetailScreen.kt) + [PostDetailViewModel.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/ui/screen/post/PostDetailViewModel.kt)
- **功能清单**:
  - 帖子详情展示（完整内容 + 图片）
  - 底部评论输入栏（一级评论）
  - 评论列表展示（评论者昵称/内容/时间）
  - 嵌套二级回复（缩进显示 + ReplyDialog）
  - "回复@用户名"交互提示
  - 评论内容校验（非空 + 200字限制）

---

### 2.4 搜索模块 ✅ (100%)

#### UC3002 搜索帖子
- **状态**: ✅ 已完成
- **实现文件**:
  - [SearchScreen.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/ui/screen/search/SearchScreen.kt) + [SearchViewModel.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/ui/screen/search/SearchViewModel.kt)
- **功能清单**:
  - 搜索输入框 + 执行搜索
  - 标题模糊匹配
  - 搜索结果列表展示
  - 搜索结果中点赞/收藏
  - 搜索结果导航到帖子详情
  - TopAppBar 返回按钮

---

### 2.5 个人中心模块 ✅ (100%)

#### UC2001 查看个人信息 / UC2002 修改个人信息
- **状态**: ✅ 已完成
- **实现文件**:
  - [ProfileScreen.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/ui/screen/profile/ProfileScreen.kt) + [ProfileViewModel.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/ui/screen/profile/ProfileViewModel.kt)
- **功能清单**:
  - 头像/用户名/昵称展示
  - 头像条件渲染（有头像用 AsyncImage，无头像用 Icon）
  - 编辑资料 Dialog（头像选择 + 昵称修改）
  - 修改密码 Dialog

#### UC2003 查看我的帖子 / UC2004 编辑 / UC2005 删除
- **状态**: ✅ 已完成
- **实现文件**: ProfileScreen.kt（EditablePostCard 组件）
- **功能清单**:
  - 我的帖子列表（按时间倒序）
  - 编辑帖子（标题/内容/图片修改）
  - 删除帖子（确认对话框）
  - 空状态提示"暂无帖子"
  - 编辑/删除后列表实时刷新

#### UC2006 查看我的收藏 / UC2007 取消收藏
- **状态**: ✅ 已完成
- **实现文件**: ProfileScreen.kt
- **功能清单**:
  - 收藏帖子列表
  - 取消收藏按钮
  - 空状态提示"暂无收藏"
  - 收藏状态与首页同步

---

### 2.6 基础设施 ✅ (100%)

| 组件 | 状态 | 说明 |
|------|------|------|
| Navigation 导航 | ✅ | NavGraph + Screen 路由 + BottomBar |
| PreferencesManager | ✅ | 用户凭证/用户列表/帖子列表持久化 |
| MockData | ✅ | 内存数据库 + StateFlow 响应式 |
| PostCard 组件 | ✅ | 可复用的帖子卡片组件 |
| Repository 接口 | ✅ | UserRepository + PostRepository 接口 |
| StateFlow 响应式 | ✅ | postsFlow + collectLatest 驱动 UI 更新 |
| 数据持久化 | ✅ | 用户 + 帖子跨应用重启保存 |

### 2.7 API 基础设施 🆕 (框架完成)

#### 新增文件（5个）

| 文件 | 路径 | 说明 |
|------|------|------|
| ApiModels.kt | [data/api/ApiModels.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/data/api/ApiModels.kt) | 通用 ApiResponse 包装 + 所有请求体模型 |
| ApiService.kt | [data/api/ApiService.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/data/api/ApiService.kt) | Retrofit 接口定义 (19 个端点) |
| RetrofitClient.kt | [data/api/RetrofitClient.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/data/api/RetrofitClient.kt) | OkHttp + Retrofit 单例客户端 |
| ApiUserRepository.kt | [data/repository/impl/ApiUserRepository.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/data/repository/impl/ApiUserRepository.kt) | UserRepository 的 API 实现 |
| ApiPostRepository.kt | [data/repository/impl/ApiPostRepository.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/data/repository/impl/ApiPostRepository.kt) | PostRepository 的 API 实现 |
| NetworkUtils.kt | [utils/NetworkUtils.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/utils/NetworkUtils.kt) | 网络连接检测 + Flow 监听 |

#### 切换方法

当后端 API 就绪后，只需修改 ViewModel 构造函数默认参数即可完成切换：

```kotlin
// 当前（Mock 模式）
class HomeViewModel(
    private val postRepository: PostRepository = MockPostRepository()
)

// 切换到 API 模式（改一行）
class HomeViewModel(
    private val postRepository: PostRepository = ApiPostRepository(RetrofitClient.apiService)
)
```

### 2.8 网络异常处理与离线降级 ✅

| 能力 | 实现位置 | 说明 |
|------|----------|------|
| 网络状态检测 | [NetworkUtils.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/utils/NetworkUtils.kt) | `isNetworkAvailable()` + `observeNetworkState()` Flow |
| Token 自动附加 | [RetrofitClient.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/data/api/RetrofitClient.kt) | `Bearer {token}` 自动注入请求头 |
| API 异常捕获 | [ApiUserRepository.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/data/repository/impl/ApiUserRepository.kt) | `safeCall()` 封装网络检测 + IOException 处理 |
| API 异常捕获 | [ApiPostRepository.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/data/repository/impl/ApiPostRepository.kt) | 同上 + Flow 读取操作本地缓存降级 |
| context 初始化 | [MainActivity.kt](file:///d:/code/android/bigone/app/src/main/java/com/example/campushub/MainActivity.kt) | 注册 `NetworkUtils.init(this)` |

### 2.9 代码质量优化 ✅

| 优化项 | 涉及文件 | 说明 |
|--------|----------|------|
| `first()` → `firstOrNull()` | PostDetailViewModel, PostViewModel, ProfileViewModel | 防止空 Flow 抛异常 |
| `isLoading` → `finally` 块 | PostDetailViewModel, PostViewModel, ProfileViewModel | 确保加载状态正确恢复 |
| 外层 try/catch 保护 | PostViewModel, ProfileViewModel | 防止 unexpected exception 导致 UI 卡死 |
| 评论刷新安全包裹 | PostDetailViewModel | `getComments()` 异常不影响主流程 |

---

## 三、架构现状

### 当前架构模式

```
┌─────────────────────────────────────────────────────┐
│                    UI Layer (Compose)                │
│  Screen ←→ ViewModel (UI State + Events)            │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│               Repository Layer (Interface)           │
│  UserRepository ←→ PostRepository                   │
└──────┬───────────────────────────────┬──────────────┘
       │                               │
┌──────▼──────────┐          ┌─────────▼──────────────┐
│  MockUserRepo   │          │  MockPostRepository    │
│  (MockData)     │          │  (MockData + StateFlow)│
└──────┬──────────┘          └─────────┬──────────────┘
       │                               │
┌──────▼───────────────────────────────▼──────────────┐
│          MockData (In-Memory + SP Persistence)      │
│  - users: MutableList<User>                         │
│  - posts: MutableList<Post>                         │
│  - postsFlow: MutableStateFlow<List<Post>>          │
│  - _comments: Map<PostId, List<Comment>>            │
│  - _currentUser: MutableStateFlow<User?>            │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│          PreferencesManager (Disk Layer)             │
│  - Credentials (Base64 encoded)                     │
│  - Registered Users (JSON array)                    │
│  - Posts (JSON array with isFavorite/isLiked)       │
│  - autoLogin flag                                   │
└─────────────────────────────────────────────────────┘
```

### 已完成的架构优化

- [x] ViewModel 与 MockData 完全解耦（通过 Repository 接口）
- [x] PreferencesManager 调用集中在 Repository 实现中
- [x] StateFlow 响应式驱动所有数据变更
- [x] 乐观 UI 更新模式（点赞/收藏即时反映）
- [x] 所有数据模型支持 JSON 序列化（isFavorite/isLiked 字段完整）

---

## 四、待实现功能

### 4.1 后端接口对接（P0 - 最高优先级）

#### 准备工作（参考资料：[BACKEND_INTEGRATION_GUIDE.md](../BACKEND_INTEGRATION_GUIDE.md)）

| 任务 ID | 任务 | 优先级 | 说明 |
|----------|------|--------|------|
| API-001 | 添加 Retrofit/Gson 依赖 | P0 | ✅ 已完成 - build.gradle.kts |
| API-002 | 创建 ApiModels.kt | P0 | ✅ 已完成 - 请求/响应数据模型 |
| API-003 | 创建 ApiService.kt | P0 | ✅ 已完成 - Retrofit 接口定义 |
| API-004 | 创建 RetrofitClient.kt | P0 | ✅ 已完成 - 单例 + OkHttpClient + Token 拦截器 |
| API-005 | 创建 ApiUserRepository.kt | P0 | ✅ 已完成 - 替换 MockUserRepository |
| API-006 | 创建 ApiPostRepository.kt | P0 | ✅ 已完成 - 替换 MockPostRepository |
| API-007 | Token 管理 | P0 | ✅ 已完成 - PreferencesManager + RetrofitClient |
| API-008 | 网络异常处理 + 离线降级 | P0 | ✅ 已完成 - NetworkUtils + Repository try/catch |
| API-009 | 代码质量优化 | P0 | ✅ 已完成 - first()/finally/try-catch 防御性编程 |
| API-010 | 接入用户注册 API | P0 | ⬜ 等待后端就绪后切换 |
| API-011 | 接入用户登录 API | P0 | ⬜ 等待后端就绪后切换 |
| API-012 | 接入帖子 CRUD API | P0 | ⬜ 等待后端就绪后切换 |
| API-013 | 接入评论 API | P0 | ⬜ 等待后端就绪后切换 |
| API-014 | 接入回复 API | P1 | ⬜ 等待后端就绪后切换 |
| API-015 | 接入点赞 API | P0 | ⬜ 等待后端就绪后切换 |
| API-016 | 接入收藏 API | P0 | ⬜ 等待后端就绪后切换 |
| API-017 | 接入搜索 API | P1 | ⬜ 等待后端就绪后切换 |
| API-018 | 图片上传 API | P1 | ⬜ 等待后端就绪后实现 |

#### ViewModel 修改计划

| 文件 | 修改内容 | 状态 |
|------|----------|------|
| LoginViewModel.kt | 替换 login() 调用为 ApiRepository | ⬜ |
| RegisterViewModel.kt | 替换 register() 调用为 ApiRepository | ⬜ |
| HomeViewModel.kt | 替换 getPosts()/toggleLike() 为 ApiRepository | ⬜ |
| PostViewModel.kt | 替换 createPost() 为 ApiRepository | ⬜ |
| PostDetailViewModel.kt | 替换评论/回复调用为 ApiRepository | ⬜ |
| ProfileViewModel.kt | 替换用户数据获取为 ApiRepository | ⬜ |
| SearchViewModel.kt | 替换搜索调用为 ApiRepository | ⬜ |

### 4.2 已知问题与改进

| 问题 | 严重性 | 说明 |
|------|--------|------|
| 网络异常无降级 | 中 | 接入后端后需添加网络状态判断和错误提示 |
| 图片为大文件无压缩 | 低 | 上传前需添加图片压缩 |
| 无离线模式 | 低 | 可考虑 Room 数据库做离线缓存 |
| 无推送通知 | 低 | 可扩展消息通知功能 |

---

## 五、用例覆盖状态表

| 用例编号 | 用例名称 | 前端实现 | 后端对接 | 测试 |
|----------|----------|----------|----------|------|
| UC1001 | 用户注册 | ✅ | ⬜ | ⬜ |
| UC1002 | 用户登录 | ✅ | ⬜ | ⬜ |
| UC1003 | 退出登录 | ✅ | ⬜ | ⬜ |
| UC1004 | 找回密码 | ✅ | ⬜ | ⬜ |
| UC2001 | 查看个人信息 | ✅ | ⬜ | ⬜ |
| UC2002 | 修改个人信息 | ✅ | ⬜ | ⬜ |
| UC2003 | 查看我的帖子 | ✅ | ⬜ | ⬜ |
| UC2004 | 编辑我的帖子 | ✅ | ⬜ | ⬜ |
| UC2005 | 删除我的帖子 | ✅ | ⬜ | ⬜ |
| UC2006 | 查看我的收藏 | ✅ | ⬜ | ⬜ |
| UC2007 | 取消我的收藏 | ✅ | ⬜ | ⬜ |
| UC3001 | 浏览帖子 | ✅ | ⬜ | ⬜ |
| UC3002 | 搜索帖子 | ✅ | ⬜ | ⬜ |
| UC3003 | 查看帖子详细 | ✅ | ⬜ | ⬜ |
| UC4001 | 发布新帖子 | ✅ | ⬜ | ⬜ |
| UC4002 | 点赞帖子 | ✅ | ⬜ | ⬜ |
| UC4003 | 收藏帖子 | ✅ | ⬜ | ⬜ |
| UC4004 | 发表一级评论 | ✅ | ⬜ | ⬜ |
| UC4005 | 发表二级评论 | ✅ | ⬜ | ⬜ |

---

## 六、下一步工作建议

### 当前阶段已完成 ✅

1. **API 基础设施已就绪** — Retrofit/OkHttp、ApiModels、ApiService（19 个端点）、ApiUserRepository、ApiPostRepository
2. **网络异常处理已完善** — NetworkUtils 检测 + Repository try/catch + 离线缓存降级
3. **Token 管理已集成** — Bearer Token 自动注入请求头
4. **代码质量已优化** — first()→firstOrNull()、isLoading→finally、try/catch 防御性编程
5. **当前项目可正常运行** — ViewModels 保持 Mock 默认值

### 下一步（等待后端就绪后执行）

6. **切换到 API 模式**
   - 修改 ViewModel 默认参数（改一行：Mock → Api）
   - 确保后端 API 端点匹配 ApiService 接口
   - 逐个功能回归测试

7. **全面测试**
   - 功能覆盖测试（19 个用例全覆盖）
   - 网络异常场景测试（断网/弱网/超时）
   - UI 兼容性测试（主流设备屏幕适配）

---

*本文档根据项目实际开发进度持续更新*
