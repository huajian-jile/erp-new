package com.example.wmsnew.service;

import com.example.wmsnew.entity.wangfanEntity;
import com.example.wmsnew.repository.wangfanEntityRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Auto-generated. DO NOT EDIT. */
@Service
public class wangfanEntityService {
    private final wangfanEntityRepository repo;

    public wangfanEntityService(wangfanEntityRepository repo) {
        this.repo = repo;
    }

    public wangfanEntity save(wangfanEntity e) { return repo.save(e); }
    public Optional<wangfanEntity> findById(Long id) { return repo.findById(id); }
    public Iterable<wangfanEntity> findAll() { return repo.findAll(); }
    public void deleteById(Long id) { repo.deleteById(id); }
}
