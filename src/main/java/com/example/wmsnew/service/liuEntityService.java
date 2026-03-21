package com.example.wmsnew.service;

import com.example.wmsnew.entity.liuEntity;
import com.example.wmsnew.repository.liuEntityRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Auto-generated. DO NOT EDIT. */
@Service
public class liuEntityService {
    private final liuEntityRepository repo;

    public liuEntityService(liuEntityRepository repo) {
        this.repo = repo;
    }

    public liuEntity save(liuEntity e) { return repo.save(e); }
    public Optional<liuEntity> findById(Long id) { return repo.findById(id); }
    public Iterable<liuEntity> findAll() { return repo.findAll(); }
    public void deleteById(Long id) { repo.deleteById(id); }
}
