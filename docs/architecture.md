# Telecom Ops Platform — Architecture

## 概述

本项目是一个合成 benchmark 微服务系统，模拟运营商网络运维平台的业务场景。不连接真实设备或内网系统，所有数据存储在内存中。

## 模块架构

```
┌─────────────────────────────────────────────────────────────┐
│                    ops-gateway-service                       │
│         (看板聚合 / 跨服务查询 / Dashboard API)               │
└──────────────────┬──────────────┬────────────────────────────┘
                   │              │
        ┌──────────▼─────┐  ┌────▼──────────────────┐
        │  alarm-engine  │  │  workorder-service     │
        │  (告警评估)     │  │  (工单流转/状态机)      │
        └────────┬───────┘  └────┬───────────────────┘
                 │               │
        ┌────────▼───────┐  ┌────▼───────────────────┐
        │  device-collector  │  notification-service  │
        │  (设备采集/适配)    │  (通知发送)             │
        └────────┬───────┘  └────────────────────────┘
                 │
        ┌────────▼───────┐
        │  common-domain  │
        │  (共享领域模型)  │
        └────────────────┘
```

## 事件流

```
DeviceMetricEvent  →  AlarmEvent  →  WorkOrderEvent
     │                    │                │
     ▼                    ▼                ▼
MetricEventPublisher  AlarmEventPublisher  WorkOrderEventPublisher
     │                    │                │
     └────────────────────┼────────────────┘
                          ▼
                   DomainEventBus (interface)
                          │
                          ▼
               InMemoryDomainEventBus
                    │       │       │
                    ▼       ▼       ▼
          metricConsumer  alarmConsumer  workOrderConsumer
```

## 核心调用链（Case A）

```
POST /api/devices/{deviceId}/metrics
  → DeviceMetricIngestController.ingestMetric
    → DeviceRegistryService.findActiveDevice
    → VendorAdapterRegistry.resolve
    → VendorAdapter.normalizeRawMetric
    → MetricCollectorService.acceptMetric
      → DeviceMetricValidator.validate
      → DeviceMetricNormalizer.normalize
      → DeviceMetricRepository.save
      → MetricEventPublisher.publish
        → DomainEventBus.publish(DeviceMetricEvent)
          → InMemoryDomainEventBus.publish(DeviceMetricEvent)
            → DeviceMetricEventConsumer.onMetric
              → AlarmEvaluationService.evaluate
                → ThresholdRuleService.loadRules
                → RuleEvaluatorRegistry.resolve
                → RuleEvaluator.evaluate
                → AlarmSeverityClassifier.classify
                → AlarmDeduplicationService.deduplicate
                → AlarmCorrelationService.correlate
                → AlarmRepository.save
                → AlarmEventPublisher.publish
                  → DomainEventBus.publish(AlarmEvent)
                    → InMemoryDomainEventBus.publish(AlarmEvent)
                      → AlarmEventConsumer.onAlarmCreated
                        → AutoWorkOrderService.createForAlarm
                          → WorkOrderRepository.save
                          → WorkOrderFlowService.assign
                            → WorkOrderAssignmentService.assign
                              → AssigneeSelectorRegistry.resolve
                              → AssigneeSelector.select
                            → WorkOrderStateMachine.transition
                            → WorkOrderEventPublisher.publish
                              → DomainEventBus.publish(WorkOrderEvent)
```

## 状态机（WorkOrder）

```
CREATED ──→ ASSIGNED ──→ PROCESSING ──→ RESOLVED ──→ CLOSED
              │               │
              │               ▼
              │           ESCALATED ──→ PROCESSING (回路)
              │
              ▼
          CANCELLED
```

## 数据库

本项目不使用真实数据库。所有存储使用 `ConcurrentHashMap` 实现的 in-memory repository。

## 接口多态

| 接口 | 实现数 | 实现类 |
|------|--------|--------|
| VendorAdapter | 4 | Huawei, Zte, FiberHome, GenericSnmp |
| RuleEvaluator | 5 | CpuUsage, MemoryUsage, OpticalPower, PacketLoss, Temperature |
| AssigneeSelector | 3 | RegionBased, SkillBased, LoadBalanced |
| EscalationPolicy | 2 | SeverityBased, SlaBased |
| NotificationChannel | 3 | Sms, Email, WeCom |

每个接口都有一个对应的 Registry 类，通过接口变量进行方法调用。
