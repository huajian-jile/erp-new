package com.example.wmsnew.controller;

import com.example.wmsnew.entity.EnglishEntity;
import com.example.wmsnew.service.EnglishEntityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/** Auto-generated CRUD API. DO NOT EDIT. */
@RestController
@RequestMapping("/api/crud/english-entity")
public class EnglishEntityController {
    private final EnglishEntityService service;

    public EnglishEntityController(EnglishEntityService service) {
        this.service = service;
    }

    @GetMapping
    public Iterable<EnglishEntity> list() { return service.findAll(); }

    @GetMapping("/{id}")
    public Optional<EnglishEntity> get(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnglishEntity create(@RequestBody EnglishEntity e) { return service.save(e); }

    @PutMapping("/{id}")
    public EnglishEntity update(@PathVariable Long id, @RequestBody EnglishEntity e) {
        e.setId(id);
        return service.save(e);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deleteById(id); }
}
