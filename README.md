# ClassPi 课堂派课程设计说明

## 项目结构

- 后端：`ClassPiServer`（Spring Boot + MyBatis，端口 9090）
- 前端：`classPai`（Vue 3 + Vite，端口 5174）

## 启动前准备

### 环境要求（全队统一）

| 工具 | 版本 |
|------|------|
| JDK | **17 或 21**（与 `pom.xml` 中 `java.version` 一致） |
| Maven | 项目自带 `mvnw.cmd`，可不单独安装 |
| MySQL | 8.x，库名 `t_class` |
| Node.js | 18+（仅前端） |

> **重要：** 只复制 `pom.xml` **不能**解决 IDE 报红。`pom` 只是清单，Spring Boot 的 jar 要靠 Maven **联网下载**到本机 `~/.m2`，且 IDEA 必须 **JDK + Maven 导入** 都配对。

### 队友拉代码后必做（IDE 全红时按顺序做）

1. **用 IDEA 打开 `ClassPiServer` 目录**（里面有 `pom.xml` 的那一层，不是 `classPai`）
2. 安装并选用 **JDK 17 或 21**：`File → Project Structure → Project SDK`
3. 安装 **Lombok 插件**，并开启：`Settings → Annotation Processors → Enable`
4. **国内网络**：把仓库里的 `maven-settings-example.xml` 复制到  
   `C:\Users\你的用户名\.m2\settings.xml`（阿里云镜像）
5. 在 `ClassPiServer` 目录打开终端，执行：
   ```bash
   mvnw.cmd clean compile -U
   ```
   必须看到 **`BUILD SUCCESS`**。若 `BUILD FAILURE`，把终端完整报错发给队长。
6. IDEA 右侧 **Maven → Reload Project**（刷新图标）
7. 仍报红：`File → Invalidate Caches → Invalidate and Restart`
8. 复制 `src/main/resources/application.yaml.example` 为 `application.yaml`，改 MySQL 密码
9. 在 MySQL 执行一次 `src/main/resources/schema_update.sql`

### 常见误区

| 现象 | 原因 |
|------|------|
| `Cannot resolve symbol 'RestController'` | 依赖没下载成功，或没 Reload Maven |
| 只有 Spring 包全红，自己的类不红 | JDK 版本不对（如还在用 JDK 8） |
| 命令行编译成功，IDE 仍红 | Project SDK 与 Maven 用的 JDK 不一致 |
| 复制了 pom 仍没用 | pom 不含 jar；必须执行第 5 步下载依赖 |

1. 启动 MySQL，确保存在数据库 `t_class`
2. **执行一次**数据库升级脚本：

```sql
-- 文件位置：src/main/resources/schema_update.sql
```

3. 启动后端 `ClassPiServerApplication`
4. 启动前端：

```bash
cd classPai
npm run dev
```

## 对照课程设计文档已实现功能

### 基本功能（及格要求）

| 功能 | 状态 |
|------|------|
| 教师/学生注册、登录 | ✅ |
| 教师创建/加入课程 | ✅ |
| 学生加课码选修课程 | ✅ |
| 教师布置作业 | ✅ |
| 学生提交作业（文字） | ✅ |
| 教师作业评分 | ✅ |

### 文档要求扩展（中/良）

| 功能 | 状态 |
|------|------|
| 课程成员查看 | ✅ |
| 教师编辑课程信息 | ✅ |
| 课程归档/恢复 | ✅ |
| 学生/教师退课 | ✅ |
| 我学的 / 我教的 筛选 | ✅ |
| 发布作业通知 | ✅ |
| 催交作业通知 | ✅ |
| 消息通知中心 | ✅ |
| 登录路由守卫 | ✅ |
| 行政班级 + 学生归属班级 | ✅ |
| 建课自动加入班级学生 | ✅ |

### 选做功能（未完整实现）

- 资料管理（附件/外链）
- 话题讨论
- 备课区
- 作业附件上传、在线批改 Word/PDF
- 课程拖动排序

## API 文档

启动后端后访问：`http://localhost:9090/swagger-ui.html`

## AI 使用报告提示

课程要求提交《AI辅助编程使用报告》，建议记录：
- 使用的 AI 工具（如 Cursor）
- 用于哪些环节（接口设计、前后端联调、Bug 修复等）
- 对 AI 生成代码的审查与修改说明
