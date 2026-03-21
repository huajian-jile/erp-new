package com.example.wmsnew.controller;

import com.example.wmsnew.entity.liuEntity;
import com.example.wmsnew.service.liuEntityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/** Auto-generated CRUD API. DO NOT EDIT. */
@RestController
@RequestMapping("/api/crud/liu-entity")
public class liuEntityController {
    private final liuEntityService service;

    public liuEntityController(liuEntityService service) {
        this.service = service;
    }

    @GetMapping
    public Iterable<liuEntity> list() { return service.findAll(); }

    @GetMapping("/{id}")
    public Optional<liuEntity> get(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public liuEntity create(@RequestBody liuEntity e) { return service.save(e); }

    @PutMapping("/{id}")
    public liuEntity update(@PathVariable Long id, @RequestBody liuEntity e) {
        e.setId(id);
        return service.save(e);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deleteById(id); }
}
