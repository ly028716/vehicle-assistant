# 车主用车助手

一款面向个人车主的离线 Android 应用，用于管理一辆车的档案、保养和维修记录，并演示 Kotlin、Compose、Room 与本地 Mock AI 的组合开发能力。

## 功能

- 单车辆档案：品牌、车型、里程和能源类型。
- 保养、维修记录：新增、编辑、删除与本地持久化。
- 首页概览：当前车辆、最近记录、费用汇总和应用内保养提醒。
- 数据管理：导出不含车牌号的 JSON；二次确认后删除全部数据。
- 本地用车助手：查询车辆、保养、维修和费用数据；不联网、不使用 API Key，也不进行故障诊断。

## 技术栈

Kotlin、Jetpack Compose、Material 3、ViewModel、StateFlow、Coroutines、Room、Navigation Compose、JUnit。

## 架构

`Compose UI → ViewModel / StateFlow → VehicleRepository → Room`

Mock AI 通过 `VehicleAssistantService` 从 Repository 读取本地数据，并按关键词生成可追溯回答。

## 构建与测试

在 `vehicle-assistant` 目录执行：

```powershell
.\gradlew.bat '-Pkotlin.compiler.execution.strategy=in-process' test assembleDebug
```

如需运行设备测试，连接真机后执行：

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

## 真机演示流程

1. 创建车辆档案。
2. 添加一条保养记录和一条维修记录。
3. 查看首页的最近记录、费用汇总和保养提醒。
4. 在“记录”页打开“本地用车助手”，点击快捷问题或输入问题。
5. 在“我的”页导出 JSON，确认文件不含车牌号。
6. 删除全部车辆数据，重启应用后确认首页回到空状态。

## 隐私与限制

所有业务数据只保存在本地 Room 数据库。应用不接入车辆硬件、OBD、蓝牙、后端、云同步、网络模型或系统通知；本地助手不提供故障诊断或维修建议。
