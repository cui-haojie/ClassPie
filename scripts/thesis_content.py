# -*- coding: utf-8 -*-
"""Long-form thesis paragraphs — mirrors reference report depth."""
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
REF_MEDIA = ROOT / "docx_extract" / "word" / "media"


def ref_images():
    """Deprecated: do not use reference PDF images from other projects."""
    return []


def module_design_user():
    return [
        "功能描述：",
        "（1）用户（Account）实体：account 唯一标识；name 显示名称；password 加密密码；status 角色（老师/学生）；mechanism 机构；status_number 学号或工号；avatar_url 头像；department/major/grade_level 扩展资料字段。",
        "（2）注册功能：用户填写账号、密码、姓名、角色、机构等信息；后端验证唯一性与格式；BCrypt 编码密码；写入 accounts 表；返回注册结果。",
        "（3）登录功能：用户提交账号密码；后端查询并 BCrypt 比对；成功则生成 JWT Token 返回；前端存储 Token 并跳转主页。",
        "（4）个人信息管理：用户可修改姓名、院系、专业、年级、入学日期等；上传头像；修改密码需验证旧密码。",
        "（5）权限控制：AuthInterceptor 解析 Token 获取 account；Service 层根据 account 查询 status 判断教师/学生权限。",
        "静态模型：AccountController 为入口；AccountService/AccountServiceImpl 处理业务；AccountMapper 访问 accounts 表；JwtService 负责 Token；RegisterRequest/LoginResponse 等为 DTO。",
        "动态模型：注册流程为 前端→AccountController→AccountService→AccountMapper→MySQL；登录流程增加 JwtService.generateToken 步骤；获取信息流程需携带 Authorization 头。",
    ]


def module_design_course():
    return [
        "功能描述：",
        "（1）课程（Course）实体：id、class_name、teacher_account、class_time、code、semester、is_pinned、school_class_id 等字段。",
        "（2）创建课程：教师填写课程名称、上课时间、关联行政班、学期；系统生成 8 位加课码；写入 courses 与 account_course。",
        "（3）加入课程：学生输入加课码；后端验证 code 存在且未重复加入；写入 account_course 关联。",
        "（4）置顶与归档：updatePinStatus 切换 is_pinned；account_course.is_archived 标记归档；列表接口按置顶、排序、学期过滤。",
        "（5）拖拽排序：前端 TransitionGroup 动画；updateCourseOrder 批量更新 account_course.sort_order。",
        "（6）删除课程：级联删除 account_course、courses_homework、course_activity 及关联数据。",
        "（7）课程成员：getCourseMembers 返回教师与学生列表，含姓名、角色、学号。",
        "（8）关联行政班：course_school_class 表支持一门课对应多个行政班；签到应到人数从行政班学生计算。",
    ]


def module_design_homework():
    return [
        "功能描述：",
        "（1）作业（Homework）实体：homework_id、name、deadline、type、details、attachment_url 等。",
        "（2）发布作业：教师填写信息并上传附件；写入 homework 与 courses_homework；通知学生。",
        "（3）提交作业：学生在截止前提交 content；支持文本与附件；可更新已提交内容。",
        "（4）批阅作业：教师查看 content 列表；打分、写评语；设置 is_graded；可打回。",
        "（5）删除作业：删除 homework、courses_homework、content 及附件文件。",
        "（6）催交作业：remindHomework 向未交学生发送 notification。",
        "（7）统计：作业列表显示已交/未交/已批阅人数。",
    ]


def module_design_interaction():
    return [
        "功能描述：",
        "（1）互动活动存储于 course_activity，type=interaction，interaction_kind 区分 qa/vote/race。",
        "（2）开启互动：教师填写标题与首个问题；interaction_options JSON 设 status=active；通知全班。",
        "（3）实时问答：askInteractionQuestion 发布新轮问题；submitInteraction 学生提交回答；pickRandomStudent 随机点名。",
        "（4）投票：vote_options 存储选项；submitInteraction 带 option_index；实时统计各选项票数与百分比。",
        "（5）抢答：startRace 开启抢答轮次；race_open=true 时学生可抢答；记录先后顺序。",
        "（6）结束互动：closeInteraction 设 status=closed；或结束签到时批量关闭。",
        "（7）WebSocket：LiveEventPublisher 向 interaction:{id} 与 course:{classId} 广播事件。",
    ]


def design_overview_paragraphs():
    return [
        "系统设计是软件开发过程中的关键阶段，它涉及到从需求分析中提炼出的概念转化为具体的技术解决方案。在本章中，我们将深入探讨如何根据前期需求整理与分析的结果，运用科学有效的方法和先进的设计理念，精心构思并设计出一个既满足各项功能需求又高度合理化的新平台。",
        "系统设计的内容广泛且深入。这包括但不限于：确定系统功能，基于需求分析明确核心功能与次要功能；设计思路和方法，采用前后端分离、三层架构、RESTful API；设计多种实现方案，对比 JWT 与 Session、轮询与 WebSocket 等；方案筛选与优化，在可行性、成本与性能间取得平衡。",
        "功能设计是对系统需求的直接转化，将抽象的用户需求具体化为系统的功能模块，构建功能框架并细化每个模块的输入、处理与输出流程。",
        "架构设计关注系统宏观结构，采用模块化与层次化原则，将复杂系统分解为 Controller、Service、Mapper 等相对独立的层次，提高灵活性与可维护性。",
        "数据库设计根据数据需求设计合理的数据模型，包括表结构、索引与关系，并考虑数据安全性与一致性，确保数据库稳定运行。",
    ]


def impl_overview_paragraphs():
    return [
        "项目后端主要基于 Spring Boot 3.4 整合 MyBatis 3.0 框架开发。MyBatis 具有简洁的注解映射与 SQL 控制能力，便于实现复杂查询。使用 Maven 作为项目管理工具，Java 17 运行环境。主要依赖包括 spring-boot-starter-web、mybatis-spring-boot-starter、mysql-connector-j、jjwt、spring-boot-starter-websocket 等。",
        "编写 Spring Boot 的 Service 层代码实现业务逻辑，Controller 层提供 RESTful API。使用 @RestController、@PostMapping 等注解定义接口。配置 JWT 认证机制，AuthInterceptor 保护 API 安全。MyBatis Mapper 接口使用 @Select/@Insert/@Update 注解操作数据库。",
        "平台前端基于 Vue 3 框架，结合 JavaScript 与 Vite 构建。划分 views、components、router、stores、utils 目录。使用单文件组件 .vue 开发页面。Vue Router 管理路由，Pinia 管理 account 等全局状态。Axios 封装于 request.js，统一附加 Token 与错误处理。",
        "项目采用前后端分离，前端负责视图与交互，后端负责数据与接口，各司其职，提高开发效率与代码可维护性。前端通过 proxy 将 /editor 请求转发至后端 9090 端口，WebSocket 在开发环境经同一 host 连接。",
    ]


def test_overview_paragraphs():
    return [
        "在开发过程中，我们对各功能模块进行了持续的功能验证。对于基于 Vue 开发的课堂派管理系统，采用黑盒测试方法，按用例步骤操作并对比预期结果。对 WebSocket 实时功能进行多窗口并行测试，验证互动更新与签到关闭事件的及时性。",
        "测试是一个不断迭代的过程。在主要功能开发完成后，进行了全面的回归测试，确保新改动没有引入新的缺陷。对登录过期、重复提交、截止后提交、无权限操作等异常场景进行了专项测试。",
        "非功能测试方面，关注页面加载速度、拖拽排序动画流畅度、Modal 弹窗层级正确性（如备课区导入弹窗与添加作业弹窗的 z-index）、以及附件上传下载的完整性。",
    ]


def summary_paragraphs():
    return [
        "在本次课堂派课程设计中，我们采用了 B/S 前后端分离的架构，涉及前端、后端服务器与数据库多个层次，每一层承担相应职责，需协同配合方能达到良好效果。",
        "前端基于 Vue 3 架构，页面拆分为子组件，使用 Pinia 与 localStorage 管理状态，Vue Router 管理路由，Axios 进行数据请求。在具体实现中，需考虑界面交互、性能优化、异常处理等因素，使应用程序更加完善和健壮。",
        "后端使用 Spring Boot 整合 MyBatis 连接 MySQL 实现业务与持久化，Maven 构建整个框架。后端业务逻辑是系统的大脑，数据库设计需综合考虑整个业务流程，运用概念结构设计、范式理论，落实到表结构设计中。",
        "课堂派涉及多种功能页面与业务细节，如删除课程时需级联清理关联数据，结束签到时需同步关闭课堂互动，这些都需要严谨的分析与逐步实现。通过本次课设，团队在页面布局、框架使用、文件操作、WebSocket 通信等方面都有了显著提高。",
    ]
