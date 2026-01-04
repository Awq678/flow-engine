# Flow Engine - 最小流程引擎 (MVP)

Flow Engine 是一个轻量级、插件化、易于集成的顺序流程编排引擎。它专注于解决后台任务编排、状态持久化和异常恢复等工程痛点，而不追求复杂的审批流或图形化设计。

## 🌟 第一阶段成果 (MVP)

目前已完成第一阶段开发，实现了引擎的核心调度与状态管理能力。

### 核心特性

- **轻量级调度**：基于 Java 8 开发，核心代码简洁，易于嵌入任何 Java 项目。
- **配置驱动**：支持通过 YAML/JSON 定义流程步骤，解耦业务逻辑。
- **状态持久化**：每步执行均记录状态，支持流程上下文变量传递。
- **故障恢复 (Resume)**：具备强大的异常处理能力，支持从失败的步骤点重新恢复执行。
- **高度可扩展**：通过 SPI 模式（`Step` 接口）轻松扩展自定义业务步骤。

### 项目结构

- `flow-spi`: 核心接口层，定义了步骤执行器、上下文和结果模型。
- `flow-persistence`: 持久化层，提供流程实例状态存储接口及内存实现（`InMemoryStateStore`）。
- `flow-core`: 引擎核心层，负责流程解析、校验、调度执行及恢复逻辑。

---

## 🚀 快速开始

### 1. 环境要求

- JDK 1.8+
- Maven 3.6+

### 2. 定义流程 (example.yaml)

```yaml
id: user_onboarding_flow
steps:
  - id: check_user
    type: log
    config:
      message: "正在校验用户信息..."
  - id: send_welcome_email
    type: log
    config:
      message: "发送欢迎邮件"
```

### 3. 执行流程

```java
// 1. 注册执行器
StepRunnerRegistry registry = new StepRunnerRegistry();
registry.register("log", new LogStep());

// 2. 初始化引擎
StateStore stateStore = new InMemoryStateStore();
FlowEngine engine = new FlowEngine(registry, stateStore);

// 3. 解析并启动
FlowParser parser = new FlowParser();
FlowDefinition def = parser.parse(new FileInputStream("example.yaml"));
String instanceId = engine.start(def, new FlowContext());
```

---

## 🛠 开发路线图

- [x] **阶段 1：核心 MVP** (当前进度)
  - [x] 核心执行调度逻辑
  - [x] 流程定义解析与校验
  - [x] 状态持久化接口与内存实现
  - [x] 支持失败恢复执行 (Resume)
- [ ] **阶段 2：集成与 API**
  - [ ] Spring Boot Starter 自动装配
  - [ ] 提供 REST API 接口 (start/retry/query)
  - [ ] 支持 JDBC 数据库持久化 (SQLite/MySQL)
- [ ] **阶段 3：管理台与示例**
  - [ ] 若依 (RuoYi) 管理台集成示例
  - [ ] 极简 Web UI 流程监控

---

## 📄 开源协议

Apache License 2.0
