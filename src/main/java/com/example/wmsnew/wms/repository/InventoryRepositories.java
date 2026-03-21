package com.example.wmsnew.wms.repository;
import com.example.wmsnew.wms.repository.InventoryRepo;
import com.example.wmsnew.wms.repository.InventoryRepositories;
import org.springframework.data.repository.Repository;
import com.example.wmsnew.wms.repository.StockTxnRepo;

/**
 * 已废弃：Spring Data 通常不会扫描“嵌套在类里的 Repository 接口”。
 * 请使用同包下的顶层接口：{@link InventoryRepo} / {@link StockTxnRepo}
 */
@Deprecated
public final class InventoryRepositories {
    private InventoryRepositories() {}
}

