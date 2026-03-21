package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.dto.StoreRequests;
import com.example.wmsnew.wms.entity.Store;
import com.example.wmsnew.wms.entity.StoreApiKey;
import com.example.wmsnew.wms.entity.StoreManager;
import com.example.wmsnew.wms.repository.StoreApiKeyRepo;
import com.example.wmsnew.wms.repository.StoreManagerRepo;
import com.example.wmsnew.wms.repository.StoreRepo;
import com.example.wmsnew.wms.service.StoreService;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

@Service
public class StoreService {
    private final StoreRepo storeRepo;
    private final StoreManagerRepo managerRepo;
    private final StoreApiKeyRepo apiKeyRepo;

    public StoreService(StoreRepo storeRepo,
                        StoreManagerRepo managerRepo,
                        StoreApiKeyRepo apiKeyRepo) {
        this.storeRepo = storeRepo;
        this.managerRepo = managerRepo;
        this.apiKeyRepo = apiKeyRepo;
    }

    public Store createStore(StoreRequests.CreateStore req) {
        try {
            return storeRepo.save(new Store(null, req.platformId(), req.code(), req.name()));
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("store.code 已存在: " + req.code());
        }
    }

    public StoreManager createManager(StoreRequests.CreateManager req) {
        return managerRepo.save(new StoreManager(null, req.storeId(), req.username(), req.displayName()));
    }

    public List<Store> listStores() {
        List<Store> res = new ArrayList<>();
        storeRepo.findAll().forEach(res::add);
        return res;
    }

    public List<Store> listStoresByPlatform(Long platformId) {
        List<Store> res = new ArrayList<>();
        storeRepo.findByPlatformId(platformId).forEach(res::add);
        return res;
    }

    public ApiKeyIssued issueApiKey(Long storeId) {
        String keyId = "k_" + randomUrlSafe(18);
        String secret = "s_" + randomUrlSafe(24);
        String hash = sha256Hex(secret);
        apiKeyRepo.save(new StoreApiKey(null, storeId, keyId, hash, "ACTIVE"));
        return new ApiKeyIssued(keyId, secret);
    }

    private static String randomUrlSafe(int bytes) {
        byte[] b = new byte[bytes];
        new SecureRandom().nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    private static String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(dig);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public record ApiKeyIssued(String keyId, String secret) {}
}

