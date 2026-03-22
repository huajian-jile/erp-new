package com.example.wmsnew.controller;

import com.example.wmsnew.entity.OrderAggregate;
import com.example.wmsnew.service.OrderAggregateService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/** Auto-generated CRUD API (aggregate). DO NOT EDIT. */
@RestController
@RequestMapping("/api/crud/order-aggregate")
public class OrderAggregateController {
    private final OrderAggregateService service;

    public OrderAggregateController(OrderAggregateService service) {
        this.service = service;
    }

    @GetMapping
    public Iterable<OrderAggregate> list() { return service.findAll(); }

    @GetMapping("/{id}")
    public Optional<OrderAggregate> get(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderAggregate create(@RequestBody OrderAggregate e) { return service.save(e); }

    @PutMapping("/{id}")
    public OrderAggregate update(@PathVariable Long id, @RequestBody OrderAggregate e) {
        e.setId(id);
        return service.save(e);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deleteById(id); }
}
