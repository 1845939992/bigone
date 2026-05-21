# CampusHub UI 视觉全面优化 — 设计文档

**Version 1.0**  
**2026-05-21**

---

## 1. 概述

在 CampusHub 需求规格说明书（r.1.1.0）框架内，对现有界面进行大幅度的视觉与布局重构，提升色彩丰富度、层次感与现代感，遵循 B 方向（大胆重构风）。

### 1.1 约束

- 严格保持原有业务逻辑不变
- 严格在需求规格说明书定义的功能框架内创新
- 不做分类功能、不做内容类型标签
- 保留现有 Compose + Material3 技术栈

---

## 2. 色彩系统扩展

### 2.1 主色 / 辅助色

| 色系 | Token | 色值 | 用途 |
|------|-------|------|------|
| Primary Blue | `PrimaryBlue` | `#2563EB` | 品牌主色、主按钮、导航 |
| Primary Blue Dark | `PrimaryBlueDark` | `#1D4ED8` | 深色变体 |
| Primary Blue Light | `PrimaryBlueLight` | `#3B82F6` | 浅色变体、暗色模式 |
| Accent Orange | `AccentOrange` | `#F97316` | 强调色、收藏、FAB |
| Accent Orange Light | `AccentOrangeLight` | `#FB923C` | 暗色模式橙色 |
| Violet | `AccentViolet` | `#7C3AED` | 个人中心渐变、装饰 |
| Teal | `AccentTeal` | `#0D9488` | 成功状态 |
| Rose | `AccentRose` | `#E11D48` | 点赞红色、错误态 |

### 2.2 表面色

| Token | 浅色 | 深色 | 用途 |
|-------|------|------|------|
| `Background` | `#F1F5F9` | `#0F172A` | 页面底色 |
| `Surface` | `#FFFFFF` | `#1E293B` | 卡片底色 |
| `SurfaceVariant` | `#F8FAFC` | `#334155` | 次要表面 |

### 2.3 渐变定义

| 名称 | 起止色 | 方向 | 使用场景 |
|------|--------|------|----------|
| `amberOrangeGradient` | `#F59E0B → #F97316` | 水平 | 首页头部 |
| `blueVioletGradient` | `#2563EB → #7C3AED` | 水平 | 个人中心头部、登录注册按钮 |
| `primaryAlphaGradient` | primary(8%) → background | 垂直 | 区域过渡 |

---

## 3. 页面级设计

### 3.1 首页 `HomeScreen`

**结构调整**：
- 顶部引入渐变头部区域（高 90dp），使用 `amberOrangeGradient`
- 头部内：用户头像（40dp CircleShape）+ 昵称 + "浏览校园新鲜事" 副标题
- 帖子列表保持 LazyColumn，去除分类胶囊

**帖子卡片 `PostCard` 增强**：
- 圆角增大到 20dp
- 阴影：`defaultElevation=2.dp`，`pressedElevation=6.dp`
- 背景：纯白 `Surface` 色
- 保留点赞/收藏弹跳动画
- 保留图片网格 + "+N" 叠加

**FAB**：保持主题蓝色，圆形

**交互**：保留 `ScaleInItem` 错峰入场 + `pressScale` 按压反馈

### 3.2 帖子详情 `PostDetailScreen`

**结构调整**：
- 大标题 fontSize 保持 22sp，颜色 `OnSurface`
- 作者行：浅蓝底色圆角卡片（`primaryContainer` 色 + 12dp 圆角），包裹头像(40dp) + 名字 + 时间
- 正文行间距增大至 28sp
- 图片：独立白色卡片容器，16dp 圆角，带 1dp 阴影
- 评论区：每条评论左侧 3dp 宽、`primary` 色竖线 + 12dp 左边距，形成视觉层级
- 底部评论输入框：24dp 全圆角，带阴影

### 3.3 个人中心 `ProfileScreen`

**结构调整**：
- 顶部大面积渐变头部（高 140dp），使用 `blueVioletGradient`
- 头像：80dp CircleShape，白色边框 3dp
- 昵称大号字体居中，学校名 `primary` 色
- 三格数据统计：获赞/帖子/收藏，白色半透明底（`Surface.copy(alpha=0.85f)`），12dp 圆角
- Tab 栏：背景透明，选中态用 `primary` 色下划线 + 加粗文字

**EditablePostCard**：与 PostCard 保持一致的 20dp 圆角 + 阴影

### 3.4 登录 `LoginScreen` / 注册 `RegisterScreen`

**结构调整**：
- 保留品牌 Hub 图标圆形（72dp）
- 图标背景升级为 `blueVioletGradient`，加外发光效果（shadow）
- 页面底部增加装饰性半透明几何圆（`primary.copy(alpha=0.06f)`），直径120dp、80dp，**不影响交互**
- 输入框聚焦态下划线渐变
- 按钮：`blueVioletGradient`，14dp 圆角，保留 `pressScale`
- 保留密码显隐切换、键盘 IME 联动、自动登录等所有功能

### 3.5 发帖 `PostScreen`

**结构调整**：
- 顶部标题/内容输入：保持透明底色风格
- 发布按钮：渐变蓝色胶囊形（20dp 圆角）
- 图片网格：圆角 10dp，保留入场/退场动画
- 添加图片按钮：`primary` 色图标

### 3.6 搜索 `SearchScreen`

**结构调整**：
- 搜索框：14dp 圆角，聚焦蓝色边框
- 空状态/无结果：图标 + 双行提示文案
- 搜索结果：`ScaleInItem` 错峰入场
- Loading：骨架屏 `SkeletonPostCard`

---

## 4. 文件变更清单

| 文件 | 变更性质 | 说明 |
|------|----------|------|
| `ui/theme/Color.kt` | 修改 | 新增 Violet/Teal/Rose 色值及渐变定义 |
| `ui/theme/Theme.kt` | 修改 | 更新 Light/Dark 色表，加入新色 |
| `ui/component/PostCard.kt` | 修改 | 圆角→20dp，阴影→2dp/6dp，移除侧边色条 |
| `ui/screen/home/HomeScreen.kt` | 大改 | 新增渐变头部、用户头像问候语 |
| `ui/screen/post/PostDetailScreen.kt` | 大改 | 作者卡片、评论左侧竖线、行间距增大 |
| `ui/screen/profile/ProfileScreen.kt` | 大改 | 蓝紫渐变头部、三格统计、头像白边框 |
| `ui/screen/login/LoginScreen.kt` | 修改 | 图标渐变圆、背景装饰圆、按钮渐变 |
| `ui/screen/register/RegisterScreen.kt` | 修改 | 同步登录页装饰+渐变风格 |
| `ui/screen/post/PostScreen.kt` | 修改 | 发布按钮渐变蓝色 |
| `ui/screen/search/SearchScreen.kt` | 修改 | 搜索框圆角+聚焦态优化 |
| `ui/component/UIAnimation.kt` | 不变 | — |
| `navigation/NavGraph.kt` | 不变 | — |

---

## 5. 不变项

- 所有 ViewModel 逻辑
- 所有 Repository/数据层
- 所有 Model 类
- 导航路由
- API 基础设施

---

## 6. 实施顺序

1. **Color.kt** — 新增色值
2. **Theme.kt** — 更新色彩方案
3. **PostCard.kt** — 卡片视觉增强
4. **HomeScreen.kt** — 渐变头部重构
5. **ProfileScreen.kt** — 蓝紫渐变头部 + 统计
6. **PostDetailScreen.kt** — 作者卡片 + 评论竖线
7. **LoginScreen.kt + RegisterScreen.kt** — 装饰+渐变
8. **PostScreen.kt + SearchScreen.kt** — 细节优化
