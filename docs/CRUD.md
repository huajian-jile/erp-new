# WMS 项目：CRUD 最省事实现方式（Spring Data JDBC）

> 结论先说：**“只建表就自动生成 CRUD 接口”**在你当前技术栈（Spring Boot + Spring Data JDBC）里**默认做不到**。  
> 但可以做到两种“接近你想要的效果”的方式：
>
> - **方式 A（推荐，当前项目零额外依赖）**：表 + 极少量代码（Entity + Repository）→ 立刻拥有 CRUD 能力（`save/find/delete`）。再按需加一个 Controller 暴露 HTTP。
> - **方式 B（你说的“建表就自带 CRUD”最接近）**：引入**代码生成器**（如 MyBatis-Plus Generator / jOOQ / 自研脚手架）→ 由表结构一键生成 Entity/Mapper/Service/Controller/前端页面骨架。

---

## 方式 A：当前项目最省事（“写 2 个文件就有 CRUD”）

### 1）先建表（必须）

- SQL 放到 `src/main/resources/sql/01_wms_schema.sql`
- 表至少需要：
  - `id BIGINT PRIMARY KEY AUTO_INCREMENT`
  - 其它业务字段

> Spring Data JDBC **不会**帮你自动建表（除非你启用 `dev` profile 的 SQL init）。

### 2）写 Entity（1 个文件）

- 放在对应模块包下，例如：`com.example.wmsnew.wms.<module>`
- 关键点：
  - `@Table("表名")`
  - 主键字段加 `@Id`

### 3）写 Repository（1 个文件）

- **必须是“顶层接口文件”**（不要写成嵌套接口，否则 Spring 扫不到，之前你遇到过这个坑）
- 继承 `CrudRepository<Entity, Long>`

写完这 2 个文件后，你已经拥有：

- **新增/更新**：`repo.save(entity)`
- **按 id 查询**：`repo.findById(id)`
- **全量查询**：`repo.findAll()`
- **删除**：`repo.deleteById(id)`

此外你还可以加“派生查询”（按方法名自动生成 SQL），例如：

- `Optional<Sku> findBySku(String sku);`

### 4）要不要 Controller？

- 如果你只是在后端内部调用：**不需要** Controller
- 如果你要对外提供 HTTP 接口（前端页面/外部系统调用）：**需要** Controller（通常再包一层 Service）

你项目里现成范例：

- `wms/catalog/product/`：商品 CRUD
- `wms/catalog/sku/`：SKU CRUD + 调价接口
- `wms/store/`：店铺 + 店长 + 签发密钥

---

## 方式 B：你想要的“建表就自带 CRUD”（代码生成）

你真正想要的是：**我只关心建表，然后就能得到：**

- Entity / DTO
- Repository/Mapper
- Service
- Controller（REST CRUD）
- （可选）前端页面（列表/编辑/详情）

这必须靠 **生成器**实现。主流可选路线：

### 方案 B1：MyBatis-Plus Generator（最常见）

适合“快速 CRUD + 复杂 SQL 混写”场景，生成出来的代码量相对多，但上手快。

### 方案 B2：jOOQ（强类型 SQL）

适合“SQL 很重、复杂报表/分析很多”的系统，生成强类型代码，写原生 SQL 体验很好；但引入成本稍高。

### 方案 B3：自研脚手架（按你们团队规范）

你已经非常强调“目录/模块边界”，自研脚手架最能保证一致性：

- 输入：表名 + 字段 + 模块名
- 输出：固定目录结构 + 统一命名 + 统一异常/返回体 + 可选页面

---

## 你现在应该怎么选？

如果目标是**尽快把 WMS 跑起来并持续迭代**：

- **短期（现在）**：用方式 A（Entity + Repository）快速扩功能，不阻塞业务推进
- **中期（模块稳定后）**：上方式 B（生成器），把重复劳动一次性压掉

---

## Dev 环境一键初始化（建表 + 视图 + 函数/物化替代 + 模拟数据）

项目里已提供：

- `src/main/resources/application-dev.yml`
- `src/main/resources/sql/01_wms_schema.sql`
- `src/main/resources/sql/02_wms_views.sql`
- `src/main/resources/sql/03_wms_functions_and_materialized.sql`
- `src/main/resources/sql/10_wms_mock_data.sql`

启动时指定 profile：

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
.\mvnw.cmd spring-boot:run
```

---

## 下一步（如果你同意我来做）

我可以按你的“模块目录规范”，在项目里加一个**简单的 CRUD 生成器**（不引入大框架也行）：

- 输入：模块名 + 表名 + 主键类型 + 字段清单
- 输出：Entity/Repo/Controller（可选 Service/DTO）

你告诉我你更想要哪种：

- **生成后端代码即可**
- **后端 + 前端页面一起生成**

