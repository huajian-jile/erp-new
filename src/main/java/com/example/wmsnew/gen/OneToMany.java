package com.example.wmsnew.gen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在父实体的 List&lt;Child&gt; 字段上，声明一对多关系。
 * 子实体需为当前类的内部静态类，并标注 @Table。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OneToMany {
    /** 子表名，默认从子类名推导 wms_xxx */
    String childTable() default "";
    /** 子表中外键列名，默认 parent_id（如 order_id） */
    String fkColumn() default "";
}
