package com.example.wmsnew.controller;

import com.example.wmsnew.entity.DemoEntity;
import com.example.wmsnew.service.DemoEntityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/** Auto-generated CRUD API. DO NOT EDIT. */
@RestController
@RequestMapping("/api/crud/demo-entity")
public class DemoEntityController {
    private final DemoEntityService service;

    public DemoEntityController(DemoEntityService service) {
        this.service = service;
    }

    @GetMapping
    public Iterable<DemoEntity> list() { return service.findAll(); }

    @GetMapping("/{id}")
    public Optional<DemoEntity> get(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DemoEntity create(@RequestBody DemoEntity e) { return service.save(e); }

    @PutMapping("/{id}")
    public DemoEntity update(@PathVariable Long id, @RequestBody DemoEntity e) {
        e.setId(id);
        return service.save(e);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deleteById(id); }
}
