package com.example.wmsnew.gen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在实体类上：只写此实体，自动生成对应表 DDL、Repository 接口，实现默认 CRUD。
 * 实体需配合 @Table 指定表名、@Id 标记主键。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface AutoCrud {
    /**
     * 可选。若为空则从类名推导表名（驼峰转 snake_case，加 wms_ 前缀）。
     * 若有 @Table 则优先用 @Table 的表名。
     */
    String tableName() default "";
}
