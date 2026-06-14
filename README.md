# Web 信息管理系统

基于 B/S 模式的 Web 信息管理系统，实现学生、班级、班主任教师的信息管理，支持增删改查与角色权限控制。

## 技术栈

| 层级 | 技术 |
|---|---|
| 前端 | HTML + CSS + JavaScript |
| 后端 | Java Servlet |
| 数据库 | MySQL 8.0 |
| 缓存/会话 | Redis |
| 构建工具 | Maven |
| 服务器 | Tomcat |
| JDK | 23 |
| JSON | Fastjson2 2.0.51 |
| JDBC | MySQL Connector/J 8.0.33 |
| Redis 客户端 | Jedis 5.1.0 |

## 功能特性

- 统一登录认证，支持管理员/教师/学生三种角色
- 基于 Redis 的 Token 会话管理
- 学生信息增删改查与关键字搜索
- 班级信息增删改查（含学院、专业字段）
- 教师信息增删改查
- 角色权限控制（管理员全局管理，教师仅管本班，学生仅查看个人信息）
- 班级删除前校验（有学生则禁止删除）
- 教师与班级绑定关系维护

## 项目结构

```
src/main/java/org/example/
├── auth/               # 认证授权（AuthService、Roles、AuthContext）
├── dao/                # 数据访问层（StudentDAO、TeacherDAO、ClassInfoDAO、AdminDAO、UserDAO）
├── entity/             # 实体类（Student、Teacher、ClassInfo、Admin、User）
├── exception/          # 自定义异常（BusinessesException）
├── service/            # 业务接口
├── servicrImpl/        # 业务实现
├── servlet/            # 控制层 Servlet
└── utils/              # 工具类（DBUtils、DaoUtils、RedisUtils、Result、WriteJson 等）

src/main/resources/
└── db.properties       # 数据库与 Redis 配置文件

src/main/webapp/
├── login.html          # 登录页
├── adminHome.html      # 管理员工作台
├── teacherHome.html    # 教师工作台
├── studentHome.html    # 学生主页
├── index.html          # 学生管理页
├── class.html          # 班级管理页
├── script.js / js.js   # 前端脚本
├── style.css / 1.css   # 样式
└── home.css            # 主页样式
```

## 环境要求

- JDK 23+
- Tomcat 9/10
- MySQL 8.0+
- Redis 7.0+

## 快速开始

### 1. 创建数据库

```sql
CREATE DATABASE student_manager DEFAULT CHARSET utf8mb4;
```

然后导入项目附带的建表 SQL（或手动创建以下表）：

- `user` — 统一登录账号表
- `admin` — 管理员信息表
- `teacher` — 教师信息表
- `student` — 学生信息表
- `class_info` — 班级信息表

### 2. 修改配置文件

编辑 `src/main/resources/db.properties`：

```properties
jdbc.url=jdbc:mysql://localhost:3306/student_manager?useSSL=false&serverTimezone=Asia/Shanghai
jdbc.username=root
jdbc.password=你的密码

redis.host=localhost
redis.port=6379
redis.password=
redis.database=0
redis.timeout=2000
```

### 3. 编译部署

```bash
mvn clean package -DskipTests
```

将生成的 `target/test5-1.0-SNAPSHOT.war` 部署到 Tomcat 的 `webapps` 目录。

### 4. 启动

1. 启动 MySQL
2. 启动 Redis
3. 启动 Tomcat
4. 访问 `http://localhost:8080/test5-1.0-SNAPSHOT/login.html`

## API 概览

| URL | 说明 |
|---|---|
| `/user?action=login` | 登录（无需 token） |
| `/admin` | 管理员操作（查/改个人信息，管理教师） |
| `/teacher` | 教师操作（查/改个人信息） |
| `/student` | 学生管理（增删改查搜索） |
| `/class` | 班级管理（增删改查） |

> 除 login 外，所有接口都需要在请求头携带 `token`

## 权限矩阵

| 角色 | 登录 | 教师管理 | 学生管理 | 班级管理 | 个人信息 |
|---|---|---|---|---|---|
| 管理员 | √ | 增/删/改/查全部 | 增/删/改/查全部 | 增/删/改/查全部 | 查/改 |
| 教师 | √ | — | 仅本班 | 仅所属班级 | 查/改 |
| 学生 | √ | — | — | — | 查看 |

## 注意事项

- 新增教师/学生时，系统自动在 `user` 表创建登录账号，默认密码 `88888888`
- 删除教师/学生时，同步删除对应 `user` 表账号
- 班级存在学生时不允许删除
- Redis 未启动时登录功能不可用
