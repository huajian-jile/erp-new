package com.example.wmsnew.controller;

import com.example.wmsnew.entity.textEntity;
import com.example.wmsnew.service.textEntityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/** Auto-generated CRUD API. DO NOT EDIT. */
@RestController
@RequestMapping("/api/crud/text-entity")
public class textEntityController {
    private final textEntityService service;

    public textEntityController(textEntityService service) {
        this.service = service;
    }

    @GetMapping
    public Iterable<textEntity> list() { return service.findAll(); }

    @GetMapping("/{id}")
    public Optional<textEntity> get(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public textEntity create(@RequestBody textEntity e) { return service.save(e); }

    @PutMapping("/{id}")
    public textEntity update(@PathVariable Long id, @RequestBody textEntity e) {
        e.setId(id);
        return service.save(e);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deleteById(id); }
}
