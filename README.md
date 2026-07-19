# Telecom Ops Platform

运营商网络运维平台 — 合成 Benchmark 项目，用于 GitNexus（图数据库代码分析）与 grep/rg 的 AI Coding 能力对比实验。

## 业务背景

模拟运营商网络运维场景：系统采集设备运行指标，标准化后进入告警引擎；告警引擎根据阈值和规则生成告警；告警达到预警线后自动创建工单；人工接单、派单、处理、升级和关闭工单；看板服务聚合设备健康、告警和工单数据。

**⚠️ 本项目是合成 benchmark，不连接真实设备、网管或内网系统。所有数据存储在内存中。**

## 模块说明

| 模块 | 职责 |
|------|------|
| `common-domain` | 共享领域对象、事件、枚举、DTO、异常 |
| `device-collector-service` | 设备注册、指标采集、厂商适配 |
| `alarm-engine-service` | 阈值规则评估、告警生成、去重、关联 |
| `workorder-service` | 自动工单创建、状态机流转、派单、升级 |
| `notification-service` | 通知模板渲染、多渠道发送（短信/邮件/企微） |
| `ops-gateway-service` | 运维看板 API、跨服务数据聚合 |
| `network-change-service` | 网络变更编排：软件升级、配置下发、审批、执行、回滚、审计、事件通知（Java/Spring 动态语义评测模块） |

## 技术栈

- Java 17 + Spring Boot 3.x
- Maven multi-module
- JUnit 5
- Spring MVC 注解
- In-memory repository + In-memory event bus

## 运行

```bash
mvn test           # 运行所有测试
mvn -q -DskipTests compile  # 仅编译
```

## Benchmark Cases

本项目包含 3 个基准测试用例，用于对比 Graph-based（GitNexus）与 grep-based 工具在 AI Coding 场景下的表现：

| Case | 级别 | 任务 |
|------|------|------|
| Case A | L1 执行流追踪 | 追踪设备指标从 REST API 进入 → 告警 → 自动工单的完整调用链 |
| Case B | L2 影响分析 | RuleEvaluator.evaluate() 增加参数的影响面分析 |
| Case C | L3 字段重命名 | DeviceInfo.regionCode → maintenanceRegionCode，区分干扰字段 |

详见 `docs/benchmark-cases/` 目录。

## 工具策略

- **grep-only**：仅使用 `grep`/`rg` 进行文本搜索，模拟传统代码检索方式。
- **graph-only**：仅使用 GitNexus 知识图谱进行结构化查询（context、impact、query、cypher）。
- **mixed**：结合 grep 和图谱，互补使用。

## 设计要求

本项目的架构和代码设计为 GitNexus vs grep benchmark 优化，包含以下故意设计的检索难点：

1. **同名方法噪音**：`evaluate`、`process`、`send`、`publish` 等方法名在多个模块重复出现
2. **接口多态**：5+ 接口、17+ 实现类，调用通过 registry + 接口变量完成
3. **字段传播不同名**：regionCode 在下游以不同语义名称传播（deviceRegionCode、alarmRegionCode、maintenanceRegionCode）
4. **跨模块事件链**：Publisher → DomainEventBus → Consumer 形成跨服务调用追踪场景

## Network Change Service（Java/Spring 动态语义专项）

`network-change-service` 是用于评测代码图谱产品对 Java/Spring 动态语义理解能力的专项模块。它实现了运营商网络变更编排场景，包含 8 个 Benchmark Case（H～O），覆盖注解、路由、DI、事务、JPA、动态分派、重载/回调、框架/反射等语义。

### 固定配置

| 配置项 | 值 | 用途 |
|--------|-----|------|
| `spring.profiles.active` (test) | `benchmark` | 默认测试 profile |
| `telecom.change.remote.enabled` | `false` | 默认关闭远程回滚 |
| `telecom.change.plugin-class-name` | `com.example.telecom.change.plugin.BenchmarkChangeValidationPlugin` | 反射插件类名 |

### H2 测试数据库

```properties
spring.datasource.url=jdbc:h2:mem:changedb;DB_CLOSE_DELAY=-1
spring.jpa.hibernate.ddl-auto=create-drop
```

### ServiceLoader 配置

`META-INF/services/com.example.telecom.change.plugin.ChangeValidationPlugin` 注册 `BenchmarkChangeValidationPlugin`。

### 运行测试

```bash
mvn -pl network-change-service -am test       # 仅本模块 + 依赖
mvn test                                       # 全项目
```

### Benchmark Cases

| Case | 测试类 | 评测能力 |
|------|--------|----------|
| H | `CaseHRouteMatrixTest` | Spring MVC 路由矩阵 |
| I | `CaseIAnnotationBindingTest`, `CaseIRepeatableAnnotationTest` | 注解类型、元素、repeatable、meta-annotation |
| J | `CaseJDependencySelectionTest`, `CaseJDryRunProfileTest` | @Qualifier、@Primary、@Profile、集合注入 |
| K | `CaseKTransactionBoundaryTest`, `CaseKTransactionRuntimeTest` | @Transactional、propagation、readOnly、self-invocation |
| L | `CaseLJpaMappingTest` | JPA entity、relationship、derived/JPQL/native query |
| M | `CaseMDynamicDispatchTest` | 接口动态分派、模板方法、default method |
| N | `CaseNOverloadCallbackTest`, `ChangeCommandBusOverloadTest` | 方法重载、lambda、method reference、callback |
| O | `CaseOFrameworkReflectionTest` | ServiceLoader、Class.forName、Method.invoke、AOP |

