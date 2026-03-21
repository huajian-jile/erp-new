package com.example.wmsnew.service;

import com.example.wmsnew.entity.textEntity;
import com.example.wmsnew.repository.textEntityRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Auto-generated. DO NOT EDIT. */
@Service
public class textEntityService {
    private final textEntityRepository repo;

    public textEntityService(textEntityRepository repo) {
        this.repo = repo;
    }

    public textEntity save(textEntity e) { return repo.save(e); }
    public Optional<textEntity> findById(Long id) { return repo.findById(id); }
    public Iterable<textEntity> findAll() { return repo.findAll(); }
    public void deleteById(Long id) { repo.deleteById(id); }
}
