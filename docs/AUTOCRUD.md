# AutoCrud 使用说明

只写实体类，自动生成表结构、Repository、Service、Controller（REST API），全部输出到 `src` 目录。

## 项目目录结构（三层架构）

```
com.example.wmsnew
├── entity/          # 实体层：@AutoCrud 实体放此包
├── repository/      # 数据访问层：生成的 Repository
├── service/         # 服务层：生成的 Service
├── controller/      # 控制层：生成的 REST Controller
└── gen/             # 生成器与注解
```

## 快速开始

### 1. 新建实体类

在 `com.example.wmsnew.entity` 包下新建类：

```java
package com.example.wmsnew.entity;

import com.example.wmsnew.gen.AutoCrud;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@AutoCrud
@Table("wms_english_entity")
public class EnglishEntity {
    @Id
    private Long id;
    private String name;
    private Integer status;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

### 2. 构建

```bash
mvn clean package
```

会生成：

- `src/main/java/com/example/wmsnew/repository/EnglishEntityRepository.java`
- `src/main/java/com/example/wmsnew/service/EnglishEntityService.java`
- `src/main/java/com/example/wmsnew/controller/EnglishEntityController.java`
- `src/main/resources/sql/99_auto_crud.sql`（建表 DDL）

### 3. 启动应用

```bash
mvn spring-boot:run
```

### 4. REST API（外部调用）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/crud/english-entity | 查询全部 |
| GET | /api/crud/english-entity/{id} | 按 ID 查询 |
| POST | /api/crud/english-entity | 新增 |
| PUT | /api/crud/english-entity/{id} | 更新 |
| DELETE | /api/crud/english-entity/{id} | 删除 |

需登录且具备 `data-read`（GET）、`wms-write`（POST/PUT/DELETE）权限。

### 5. 前端 CRUD 页面

构建后还会生成 `crud-entities.json`，前端「CRUD 数据管理」菜单会根据该配置自动渲染列表、新增、编辑、删除界面，直接对接上述 REST API。需 `crud-admin` 权限才能访问。

---

## 实体规则

- **@AutoCrud**：必须
- **@Table**：推荐，指定表名；不写则从类名推导
- **@Id**：必须，主键字段名 `id`，类型 `Long`
- 字段名自动转 snake_case（如 `createdAt` → `created_at`）

## 支持类型

| Java | MySQL |
|------|-------|
| Long, Integer | BIGINT, INT |
| String | VARCHAR(255) |
| LocalDateTime | DATETIME |
| Boolean | TINYINT(1) |

## 示例

参见 `com.example.wmsnew.entity` 下的 DemoEntity、EnglishEntity。
