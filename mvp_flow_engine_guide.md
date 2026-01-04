# 最小流程引擎（MVP）开发指南

本文整理了开发最小流程引擎（MVP）的思路、架构、落地方法及开源策略，帮助开发者在不依赖审批流的情况下，打造可复用、可开源、用户友好的流程引擎。

---

## 一、MVP 核心原则

1. **单独模块化**：流程引擎必须独立于若依或其他应用框架。若依仅作为示例宿主。
2. **从最小功能开始**：先做顺序执行、状态持久化、步骤执行器，不做审批流、图形化设计器。
3. **可复用性**：设计成可打包发布的模块，方便在其他项目中集成。
4. **可维护性**：代码与设计决策必须清晰，避免过度抽象。
5. **真实痛点**：解决特定问题，而不是做泛用工具。

---

## 二、MVP 流程引擎架构

```
flow-engine
├── flow-core               // 核心引擎（独立模块）
├── flow-spi                // 扩展接口（Step, Hook）
├── flow-persistence        // 状态持久化接口
├── flow-persistence-jdbc   // JDBC 实现
├── flow-spring             // Spring 集成
├── flow-spring-boot-starter// Starter
├── flow-admin-ruoyi        // 若依管理台示例
├── flow-admin-simple       // 极简 Web UI 示例
└── samples                 // 示例流程
```

- **第一阶段 MVP**：仅需 `flow-core` + `flow-persistence` + 单元测试。无需前端。
- **第二阶段 Starter**：添加 Spring Boot Starter 方便其他项目集成。
- **第三阶段示例**：若依管理台或极简 Web UI，用于展示和传播。

---

## 三、MVP 核心模块

### 1. Flow Definition（流程定义）
- JSON 或 YAML 格式定义流程
- 支持顺序步骤
- 不依赖用户或审批

示例：
```yaml
id: user_register
steps:
  - id: validate_input
    type: http
    config:
      url: /api/validate
  - id: save_user
    type: db
  - id: send_email
    type: task
```

### 2. Flow Parser（解析与校验）
- 校验流程合法性
- 校验 step id 唯一
- 校验 type 是否支持

### 3. Flow Engine（核心调度）
- 控制流程执行
- 管理当前 step、状态、上下文
- 支持失败、重试、恢复

### 4. Step Runner（步骤执行器）
- 每个 step type 对应一个执行器
- 可扩展：HTTP、DB、Script、Task 等

### 5. State Store（状态持久化）
- 执行完每步持久化状态
- 支持进程重启恢复
- 数据结构包含：当前 step、上下文、状态

---

## 四、MVP 开发流程

### 阶段 1（基础 MVP）
- flow-core + flow-persistence
- 顺序流程，内存执行 + SQLite
- 单元测试执行流程

### 阶段 2（Spring 集成 & API）
- Spring Boot Starter
- 提供 API: start / retry / query
- 状态持久化支持 JDBC

### 阶段 3（前端示例）
- 若依或极简 Web UI
- 流程定义管理页面
- 流程实例查看页面
- 权限控制、审计

---

## 五、开源与传播策略

### 1. 降低无人用风险
- 第一版只解决一个具体问题，例如“后台任务编排引擎”
- 10 分钟内可跑起来的示例
- README 写清真实痛点

### 2. 降低被复制风险
- 项目定位清晰：解决特定工程痛点，不追求全能
- 坚持工程边界与设计克制
- Issue 响应及时，社区积累形成护城河

### 3. 核心护城河
1. 问题选择与定位
2. 社区积累（Issue、PR、讨论）
3. 长期维护
4. 文档质量
5. 代码本身（最弱）

### 4. 发布与复用
- 模块化设计，提供 Starter / Core / Persistence 包
- Maven 发布可复用
- 若依仅作为示例宿主，不是核心依赖

---

## 六、示例 MVP 路线图

| 周数 | 任务 |
|------|------|
| 第 1 周 | flow-core + flow-persistence，实现顺序执行与持久化，内存/SQLite 测试流程 |
| 第 2 周 | JDBC 持久化 + Spring Boot Starter，提供 API：start/retry/query |
| 第 3 周 | 编写 README、示例流程，若依管理台或简单 Web UI 可选 |

---

## 七、关键注意事项

1. 不要一开始就做审批流或图形化设计器
2. 核心引擎必须独立，可嵌入其他项目
3. MVP 阶段不要做前端页面，先保证 API 和核心执行正确
4. 流程引擎价值在于**边界控制和状态管理**，不是花哨功能
5. 项目定位必须清晰，先解决一个具体痛点

---

## 八、总结

- MVP 最小流程引擎 = 核心执行 + 状态持久化 + 步骤执行器
- 若依仅作为示例宿主，不能成为引擎依赖
- 开源关键在定位、边界、示例与文档，而非代码炫技
- 第一版目标：可运行、解决真实工程痛点、易于复用
- 后续阶段可逐步添加条件分支、手动重试、UI 示例、扩展插件

