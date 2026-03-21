# 项目架构说明

## 三层架构目录

```
com.example.wmsnew
├── entity/                    # AutoCrud 实体（快速 CRUD）
├── repository/                # AutoCrud 生成的 Repository
├── service/                   # AutoCrud 生成的 Service
├── controller/                # AutoCrud 生成的 Controller
│
└── wms/                       # WMS 业务模块
    ├── entity/                # 实体层：Product, Sku, Store, Inventory 等
    ├── repository/            # 数据访问层：*Repo, *Repository
    ├── service/               # 服务层：业务逻辑
    ├── controller/            # 控制层：REST API
    ├── dto/                   # 请求/响应 DTO
    ├── config/                # 配置（SecurityConfig 等）
    └── common/                # 公共类（ApiError, ApiExceptionHandler）
```

## 说明

- **AutoCrud**：`entity`、`repository`、`service`、`controller` 为 @AutoCrud 专用
- **WMS 业务**：`wms.entity`、`wms.repository`、`wms.service`、`wms.controller` 为原有业务分层
