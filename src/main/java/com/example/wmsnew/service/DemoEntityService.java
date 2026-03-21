package com.example.wmsnew.service;

import com.example.wmsnew.entity.DemoEntity;
import com.example.wmsnew.repository.DemoEntityRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Auto-generated. DO NOT EDIT. */
@Service
public class DemoEntityService {
    private final DemoEntityRepository repo;

    public DemoEntityService(DemoEntityRepository repo) {
        this.repo = repo;
    }

    public DemoEntity save(DemoEntity e) { return repo.save(e); }
    public Optional<DemoEntity> findById(Long id) { return repo.findById(id); }
    public Iterable<DemoEntity> findAll() { return repo.findAll(); }
    public void deleteById(Long id) { repo.deleteById(id); }
}
