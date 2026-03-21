package com.example.wmsnew.wms.repository;
import org.springframework.data.repository.Repository;
import com.example.wmsnew.wms.repository.StoreApiKeyRepo;
import com.example.wmsnew.wms.repository.StoreManagerRepo;
import com.example.wmsnew.wms.repository.StoreRepo;
import com.example.wmsnew.wms.repository.StoreRepositories;

/**
 * 已废弃：Spring Data 通常不会扫描“嵌套在类里的 Repository 接口”。
 * 请使用同包下的顶层接口：{@link StoreRepo} / {@link StoreManagerRepo} / {@link StoreApiKeyRepo}
 */
@Deprecated
public final class StoreRepositories {
    private StoreRepositories() {}
}

