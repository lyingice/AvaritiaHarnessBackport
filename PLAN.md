# Avaritia Harness Backport — 开发计划

> 目标：把 Re:Avaritia（无尽贪婪：重生）高版本中的 **星空挽具 / Neutron Harness** 移植到 **1.20.1 / 1.21.1**，并兼容两个快乐恶魂回溯模组：
> - **Vanilla Backport**（`vanillabackport`）
> - **Happy Ghast Legacy**（`happy_ghast_legacy`）

## 实现状态（2026-11-13）

- ✅ **1.21.1 NeoForge**：物品、属性、保护 X、不朽掉落、终焉锻造配方、VB 数据兼容、HGL 交互 + 自定义渲染层全部完成。
  - 9 个 GameTest 全部通过（注册/配方、保护 X、VB/HGL 属性、配方匹配、HGL 右键装备、+0.45 骑乘速度、80% 减伤、护盾保护、HGL 速度 Mixin）。
  - 客户端截图确认 VB 与 HGL 两只快乐恶魂都能正确渲染星空挽具。
- ✅ **1.20.1 Forge**：同功能移植完成；reobf jar 构建成功；生产 Forge 服务端加载 Re:Avaritia + VB + HGL + Platform + Architectury 成功，配置生成，两种恶魂的 CHEST 槽均能写入星空挽具。
- ✅ **26.1.2 专属机制**：召唤坐骑、立体防御护盾、80% 恶魂减伤、+0.45 骑乘速度已按 `NeutronHarnessHandler` / `HarnessShieldState` / `HarnessOwner` / `C2SHarnessActionPacket` 移植；NeoForge GameTest 验证，Forge 生产服务端加载验证。
- ✅ **HGL 骑乘速度**：软失败 Mixin（`@Pseudo` + `@ModifyConstant`），仅在玩家骑乘时把 `0.09F` 乘以 `(0.32+0.45)/0.32 ≈ 2.40625`。
- ⏳ **待办**：1.20.1 Forge 客户端视觉复查、Forge GameTest、Fabric（P6，可选）、发布页/CI。

---

## 1. 技术基线（已核对源码）

### 1.1 上游星空挽具

已在 Re:Avaritia 的 `upstream/neo/1.21.11`、`upstream/neo/26.3` 分支找到实现：

| 项目 | 内容 |
|---|---|
| 注册名 | `avaritia:neutron_harness` |
| 中文名 | 星空挽具（旧名「中子战魂铠」） |
| 装备槽 | 高版本 `EquipmentSlot.BODY` |
| 属性 | +50 护甲、+10 盔甲韧性；骑乘时 +0.45 飞行速度（26.1.2） |
| 固定附魔 | 保护 X（通过 `InitEnchantItem` 注入） |
| 其他 | 火焰抗性、不可损坏、不掉落为普通物品（`ImmortalItemEntity`）、无附魔光效 |
| 合成 | 终焉锻造台：羽化模板 + 任意挽具 + 缓降药水 + 无瑕核心 + 羽毛 |
| 贴图 | 物品 16×16；实体 128×128，64×64 UV 布局 |

> 注：MC百科页面提到的「80% 减伤 / 护盾 / 召唤穿铠恶魂 / +0.45 骑乘速度」**不在 `NeutronHarnessItem` 本身**，而在 26.1.2 分支的 `NeutronHarnessHandler`、`HarnessShieldState`、`HarnessOwner`、`C2SHarnessActionPacket`、`ModDataAttachments` 中。本移植已按 26.1.2 实现。26.1.2 的 item 没有 +0.95 基础飞行速度；+0.45 是骑乘时由 handler 添加。1.21.11/26.3 的 item 有 +0.95 但没有这套 handler；页面把两代内容混在一起，且页面写 5/15 秒与 26.1.2 代码默认 15/20 秒不一致。

### 1.2 两个快乐恶魂回溯模组

| 项目 | Vanilla Backport | Happy Ghast Legacy |
|---|---|---|
| Mod ID | `vanillabackport` | `happy_ghast_legacy` |
| 快乐恶魂实体 | `minecraft:happy_ghast` | `happy_ghast_legacy:happy_ghast` |
| 挽具槽位 | `EquipmentSlot.CHEST` | `EquipmentSlot.CHEST` |
| 挽具识别 | 物品标签 `minecraft:harnesses` | 严格类检查 `instanceof HarnessItem` |
| 挽具标签 | `minecraft:harnesses` | `happy_ghast_legacy:harness` |
| 实体贴图 | 数据驱动：`assets/<ns>/equipment/ghast/<item>.json` | 代码固定：`<color>_harness.png` |
| 飞行速度 | 使用 `Attributes.FLYING_SPEED` | `travel()` 内硬编码 `0.09F`，不使用该属性 |
| 许可 | All Rights Reserved | All Rights Reserved |

### 1.3 目标版本 Re:Avaritia 现状

| 分支 | 版本 | 星空挽具 | 可用 API |
|---|---|---|---|
| `neo/1.21.1` | 1.4.3 / NeoForge 21.1.215 | ❌ 不存在 | `InitEnchantItem`（`api.iface.item`）、`ImmortalItemEntity`、`ModRarities`、`extreme_smithing` |
| `forge/1.20.1` | 1.4.2 / Forge 47.4.20 | ❌ 不存在 | `InitEnchantItem`（`api.iface`）、`ImmortalItemEntity`、`ModRarities`、`extreme_smithing` |
| `origin/fabric/1.20.1` | 1.3.9.x | ❌ 不存在 | 有 `InitEnchantItem` / `ImmortalItemEntity`，但需单独适配 |

Re:Avaritia 为 **MIT** 许可，可复用其代码与贴图，需保留版权声明。HGL / VB 为 ARR，**不得复制其代码或美术资源**，只做公开数据格式与事件层兼容。

---

## 2. 兼容性矩阵（建议范围）

| 优先级 | Minecraft | Loader | Re:Avaritia | Vanilla Backport | Happy Ghast Legacy |
|---|---|---|---|---|---|
| **P0** | 1.21.1 | NeoForge 21.1.215 | 1.4.3 | 1.2.2 | 1.8 |
| **P0** | 1.20.1 | Forge 47.4.20 | 1.4.2 | 1.2.2 | 1.8 |
| P2（可选） | 1.20.1 | Fabric | 1.3.9.x | 最新 | 1.8 |
| 不支持 | 1.21.1 | Fabric | 无 1.21.1 Fabric 构建 | — | — |

> 1.21.1 Fabric 无法硬依赖 Re:Avaritia，因此不建议纳入首版范围。

---

## 3. 总体技术方案

### 3.1 物品实现

- 注册名：`avaritia_harness_backport:neutron_harness`
- 使用 `Item`（非 `AnimalArmorItem`），装备槽统一为 **CHEST**，以同时适配 VB/HGL。
- 属性：
  - 1.21.1：`ItemAttributeModifiers` + `EquipmentSlotGroup.CHEST`
  - 1.20.1：重写 `getDefaultAttributeModifiers(EquipmentSlot.CHEST)`
  - 仅 +50 护甲 / +10 韧性；+0.45 飞行速度由 `NeutronHarnessHandler` 在玩家骑乘时动态添加/移除（26.1.2 行为）
- 固定保护 X：
  - 1.21.1 实现 `committee.nova.mods.avaritia.api.iface.item.InitEnchantItem`
  - 1.20.1 实现 `committee.nova.mods.avaritia.api.iface.InitEnchantItem`
  - 复用 Re:Avaritia 的 `EnchantmentHelperMixin`，实际生效
- 火焰抗性、`stacksTo(1)`、`RARE`、`setNoRepair()`、无光效。
- 掉落物：复用 Re:Avaritia `ImmortalItemEntity`（`hasCustomEntity` / `createEntity`）。
- 加入 Re:Avaritia 创造模式标签页（两版本 API 不同，分别适配）。

### 3.2 合成配方

- 配方类型：`avaritia:extreme_smithing`
- 模板：`avaritia:upgrade_smithing_template`
- 基础：自定义标签 `#avaritia_harness_backport:harnesses`
  - 包含 `#minecraft:harnesses`（VB，`required:false`）
  - 包含 `#happy_ghast_legacy:harness`（HGL，`required:false`）
- 添加物：
  - 缓降药水（1.21.1 用 `neoforge:components`；1.20.1 用 `forge:nbt`）
  - `avaritia:enhancement_core`
  - `minecraft:feather`
- 若两个回溯模组都未安装，基础标签为空，配方自动不可用；物品提示玩家需要安装其中一个模组。

### 3.3 Vanilla Backport 兼容（纯数据，推荐）

1. 把本模组物品加入 `minecraft:harnesses` 标签。
2. 提供 `assets/avaritia_harness_backport/equipment/ghast/neutron_harness.json`：
   ```json
   { "texture": "avaritia_harness_backport:neutron_harness" }
   ```
3. 实体贴图放到 `assets/avaritia_harness_backport/textures/entity/neutron_harness.png`。
4. VB 自己完成：右键装备、CHEST 槽、骑乘、护甲/韧性/飞行速度属性、渲染、剪羊毛返还。

> 该方案不需要 Mixin，也不依赖 VB 的 Java 类。

### 3.4 Happy Ghast Legacy 兼容（事件 + 自定义渲染层）

由于 HGL 只接受 `instanceof HarnessItem`，不能直接装备本模组物品，采用：

1. **装备**：监听玩家对实体的交互事件（Forge/NeoForge：`PlayerInteractEvent.EntityInteract`；Fabric：`UseEntityCallback`）
   - 目标实体注册名 == `happy_ghast_legacy:happy_ghast`
   - 手持本模组挽具、目标 CHEST 槽为空、非幼年
   - 服务端 `setItemSlot(EquipmentSlot.CHEST, stack.copyWithCount(1))`，创造模式不消耗
   - 取消原交互，播放皮革装备音效 + `GameEvent.EQUIP`
2. **渲染**：向 HGL 的渲染器动态添加自定义 `RenderLayer`
   - 按实体注册名找到 `LivingEntityRenderer`，调用公开的 `addLayer`
   - 自定义 64×64 模型（16×16×16 护具 + 护目镜），使用本模组 128×128 贴图
   - 仅在 CHEST 槽为本模组物品且非幼年时渲染
   - 不引用 HGL 的 Java 类，避免映射/许可问题
3. **标签**：把本模组物品加入 `happy_ghast_legacy:harness`，让 HGL 的绳索渲染正常。
4. **骑乘速度（已实现）**：
   - HGL 的 `travel()` 硬编码 `0.09F`，原版属性不生效
   - 软失败 Mixin：`@ModifyConstant(method="travel", constant=@Constant(floatValue=0.09F))`
   - 仅在玩家骑乘时把 `0.09F` 乘以 `(0.32+0.45)/0.32 ≈ 2.40625`
   - HGL 缺失/实现变化时自动跳过

### 3.5 双模组共存

- 两个模组的快乐恶魂实体注册名不同，按注册名分别处理，互不干扰。
- 本模组物品同时加入两套标签；VB 走数据驱动，HGL 走事件 + 自定义层。
- 两个模组都安装时，分别测试两种实体。

---

## 4. 分阶段计划表

| 阶段 | 任务 | 主要产出 | 验收标准 | 预估 |
|---|---|---|---|---|
| **P0 方案确认** | 确认 loader/版本范围、是否硬依赖 Re:Avaritia、HGL 速度策略、名称与包名 | 本计划定稿 | 关键决策签字 | 0.5 天 |
| **P1 1.21.1 NeoForge 基础** | 搭建 Gradle 工程；接入 NeoForge 21.1.215 + Re:Avaritia 1.4.3；注册物品；属性/保护 X/不朽掉落；创造标签；贴图/模型/语言 | 可编译的 NeoForge jar | 物品可在创造模式取得；属性与提示正确；火焰/岩浆不销毁 | 2–3 天 |
| **P2 配方与 VB 兼容** | 编写 extreme_smithing 配方；合并挽具标签；VB `equipment/ghast` JSON；JEI/REI/EMI 检查 | 可合成、可在 VB 恶魂上装备/渲染 | 任意 VB 挽具可合成；右键装备；骑乘；速度/护甲/保护生效；剪刀返还 | 0.5–1 天 |
| **P3 HGL 兼容** | 交互事件装备；自定义 RenderLayer + 模型；HGL 标签；双装测试 | HGL 恶魂可装备/渲染 | 同 P2 验收；与 VB 共存不冲突；无 HGL 类引用 | 1–2 天 |
| **P4 1.20.1 Forge 移植** | 复制工程到 Forge 47.4.20；适配 `RegistryObject`、属性 API、`InitEnchantItem` 包名、`ModEntities`、配方 JSON 路径与格式 | Forge 1.20.1 jar | 同 P2/P3 验收 | 2–3 天 |
| **P5 打磨与发布** | 配置项、成就/配方解锁、图标、README、LICENSE/ATTRIBUTION、CI、发布页 | 发布包与文档 | 专用服务器无崩溃；安装/卸载无残留；发布检查通过 | 1–2 天 |
| **P6 Fabric 1.20.1（可选）** | Fabric Loom；Re:Avaritia Fabric 1.3.9.x；Yarn 映射适配；Fabric 事件/渲染回调；不朽掉落适配 | Fabric 1.20.1 jar | 同 P2/P3 验收 | 3–5 天 |

**MVP（P0–P5）预计：7–11 个开发日（约 1.5–2 周）。**
**加 Fabric 预计：+3–5 个开发日。**

---

## 5. 建议的工程结构

```text
AvaritiaHarnessBackport/
├─ PLAN.md
├─ README.md
├─ LICENSE
├─ ATTRIBUTION.md
├─ shared/
│  ├─ assets/                      # 两版本共用的贴图、模型、语言文件
│  └─ data/
│     ├─ 1.20.1/                   # recipes / tags/items / advancements
│     └─ 1.21.1/                   # recipe / tags/item / advancement
├─ neoforge-1.21.1/
│  ├─ build.gradle
│  └─ src/main/java/.../
└─ forge-1.20.1/
   ├─ build.gradle
   └─ src/main/java/.../
```

推荐包名占位：`dev.<yourname>.aharness`；Mod ID：`avaritia_harness_backport`。
不建议使用 `committee.nova.mods` 命名空间（属于 Re:Avaritia 作者）。

### 核心类（每版本各一份）

| 类 | 职责 |
|---|---|
| `AvaritiaHarnessBackport` | Mod 入口、注册、配置 |
| `AHBItems` | 物品注册 |
| `NeutronHarnessItem` | 属性、保护 X、不朽掉落、提示 |
| `AHBTags` | 自定义挽具标签 |
| `HGLCompat` | HGL 实体交互装备 |
| `VBCompat` | 仅数据，可无 Java 类 |
| `client/NeutronHarnessModel` | HGL 用 64×64 装备模型 |
| `client/HGLHarnessLayer` | HGL 渲染层 |
| `client/ClientEvents` | 模型层/渲染层注册（Dist.CLIENT） |

---

## 6. 资源清单

| 资源 | 路径（1.21.1 示例） | 说明 |
|---|---|---|
| 物品模型 | `assets/avaritia_harness_backport/models/item/neutron_harness.json` | `item/generated` |
| 物品贴图 | `assets/avaritia_harness_backport/textures/item/neutron_harness.png` | 来自 Re:Avaritia（MIT） |
| 实体贴图 | `assets/avaritia_harness_backport/textures/entity/neutron_harness.png` | 128×128，复用上游 |
| VB 装备定义 | `assets/avaritia_harness_backport/equipment/ghast/neutron_harness.json` | VB 数据驱动 |
| 中文语言 | `assets/avaritia_harness_backport/lang/zh_cn.json` | 「星空挽具」 |
| 英文语言 | `assets/avaritia_harness_backport/lang/en_us.json` | "Neutron Harness" |
| 配方 | `data/avaritia_harness_backport/recipe/neutron_harness.json` | 1.20.1 为 `recipes` |
| 自定义挽具标签 | `data/avaritia_harness_backport/tags/item/harnesses.json` | 1.20.1 为 `tags/items` |
| VB 挽具标签注入 | `data/minecraft/tags/item/harnesses.json` | 加入本模组物品 |
| HGL 挽具标签注入 | `data/happy_ghast_legacy/tags/item/harness.json` | 加入本模组物品 |
| 配方解锁 | `data/avaritia_harness_backport/advancement/recipes/misc/neutron_harness.json` | 可选 |

> 1.20.1 与 1.21.1 的 `pack_format`、目录名（`recipe/recipes`、`tags/item`/`tags/items`、`advancement/advancements`）不同，构建时按版本放置。

---

## 7. 验收清单

### 7.1 物品与配方

- [ ] 创造模式能取得「星空挽具」。
- [ ] 终焉锻造台可用任意 VB/HGL 挽具 + 缓降药水 + 无瑕核心 + 羽毛 + 羽化模板合成。
- [ ] JEI/REI/EMI 正确显示配方与基础挽具标签。
- [ ] 两个回溯模组都未安装时：不崩溃；物品存在；提示需要安装其中一个模组；配方不可用。
- [ ] 只安装 HGL 时，仍可用 HGL 挽具合成（自定义标签合并 HGL 标签）。
- [ ] 只安装 VB 时，仍可用 VB 挽具合成。

### 7.2 装备与行为

- [x] VB 快乐恶魂：右键装备、渲染、骑乘、骑乘速度、护甲、韧性、保护 X 生效。
- [x] HGL 快乐恶魂：右键装备、渲染、骑乘、护甲、韧性、保护 X、骑乘速度生效。
- [x] HGL 速度：骑乘时 +0.45 加成生效（x2.40625 Mixin）。
- [x] 穿铠恶魂受到 80% 减伤。
- [x] 骑着穿铠恶魂按键展开半径 4 的护盾；范围内友方/中立免伤，敌对不受保护。
- [x] 腾空按键召唤最近穿铠恶魂并骑乘；不抢其他玩家名下的恶魂。
- [x] 骑乘时 +0.45 飞行速度；下马后移除。
- [ ] 幼年恶魂不能装备。
- [ ] 已装备时再次右键不会重复装备。
- [ ] 剪刀可取下并返还物品。
- [ ] 创造模式装备不消耗物品。
- [ ] 恶魂死亡/岩浆/爆炸后，挽具以不朽物品实体掉落，不消失。
- [ ] 存档退出重进后，装备状态保留。

### 7.3 兼容与稳定性

- [ ] 同时安装 VB + HGL：两种恶魂都能装备、渲染、骑乘，互不干扰。
- [ ] 专用服务器启动/运行无客户端类加载崩溃。
- [ ] 安装/卸载模组后存档可加载（物品变为缺失物品，不崩溃）。
- [ ] 与 Re:Avaritia 1.4.3 / 1.4.2 分别测试。
- [ ] 与常见优化模组（Sodium/Embeddium、Iris/Oculus）无渲染冲突（至少冒烟测试）。

---

## 8. 风险与对策

| 风险 | 影响 | 概率 | 对策 |
|---|---|---|---|
| HGL 严格 `instanceof HarnessItem` | 物品无法装备 | 高 | 交互事件拦截 + 自定义渲染层，不引用 HGL 类 |
| HGL 飞行速度硬编码 | 速度属性无效 | 高 | 已用软失败 Mixin 补齐骑乘 +0.45（x2.40625） |
| 双快乐恶魂实体 | 兼容冲突 | 中 | 按注册名分别处理；双装回归测试 |
| `minecraft:harnesses` 标签仅 VB 提供 | HGL 单装时配方失效 | 中 | 自定义标签合并两套标签；`required:false` |
| Re:Avaritia 两版本 API 差异 | 编译/运行错误 | 中 | 分版本源码；锁定 1.4.3 / 1.4.2 |
| 实体贴图 UV 不匹配 | 渲染错位 | 中 | 先用上游 128×128 实机预览；必要时重绘 64×64 |
| HGL/VB 为 ARR | 法律风险 | 中 | 不复制代码/资源；只做标签、数据格式、事件兼容 |
| 专用服务器加载客户端类 | 崩溃 | 低 | 客户端类隔离 + `Dist.CLIENT` |
| 1.21.1 无 BODY 槽 | 属性不生效 | 低 | 使用 `EquipmentSlotGroup.CHEST` / `EquipmentSlot.CHEST` |
| `LivingEntityRenderer.addLayer` 可见性 | 无法挂载渲染层 | 低 | 1.21.1 已确认 public；1.20.1 必要时加 AccessTransformer |

---

## 9. 待确认决策

| # | 决策 | 推荐默认 | 备选 |
|---|---|---|---|
| 1 | 首发版本 | 1.21.1 NeoForge | 1.20.1 Forge 优先 |
| 2 | 版本/加载器范围 | 1.21.1 Neo + 1.20.1 Forge | 再加 1.20.1 Fabric |
| 3 | Re:Avaritia 依赖 | 硬依赖（配方/材料/API 一致） | 软依赖或独立实现 |
| 4 | HGL 飞行速度 | 已实现：骑乘 +0.45 软失败 Mixin | - |
| 5 | 物品中文名 | 星空挽具（英文 Neutron Harness） | 保留旧名「中子战魂铠」 |
| 6 | 无回溯模组时 | 配方不可用 + 提示 | 增加备用配方（如皮革） |
| 7 | 包名/作者命名空间 | `dev.<yourname>.aharness` | 其他 |

---

## 10. 发布与合规

- 本模组建议使用 **MIT** 许可，与 Re:Avaritia 兼容。
- `ATTRIBUTION.md` 注明：
  - Neutron Harness 的物品/实体贴图与行为来自 Re:Avaritia（MIT，Copyright (c) 2022 cnlimiter）。
  - Vanilla Backport、Happy Ghast Legacy 仅做兼容，不包含其代码或资源。
- 发布页注明依赖：
  - 必需：Re:Avaritia（对应版本）。
  - 可选：Vanilla Backport / Happy Ghast Legacy（至少安装一个才能获得基础挽具）。
- 版本号建议：`1.0.0+1.21.1-neoforge`、`1.0.0+1.20.1-forge`。
