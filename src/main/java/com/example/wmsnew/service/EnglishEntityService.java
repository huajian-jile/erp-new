package com.example.wmsnew.service;

import com.example.wmsnew.entity.EnglishEntity;
import com.example.wmsnew.repository.EnglishEntityRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Auto-generated. DO NOT EDIT. */
@Service
public class EnglishEntityService {
    private final EnglishEntityRepository repo;

    public EnglishEntityService(EnglishEntityRepository repo) {
        this.repo = repo;
    }

    public EnglishEntity save(EnglishEntity e) { return repo.save(e); }
    public Optional<EnglishEntity> findById(Long id) { return repo.findById(id); }
    public Iterable<EnglishEntity> findAll() { return repo.findAll(); }
    public void deleteById(Long id) { repo.deleteById(id); }
}
