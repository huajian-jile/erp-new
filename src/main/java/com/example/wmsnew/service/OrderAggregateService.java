package com.example.wmsnew.service;

import com.example.wmsnew.entity.OrderAggregate;
import com.example.wmsnew.repository.OrderAggregateRepository;
import com.example.wmsnew.repository.OrderAggregateOrderItemLineRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

/** Auto-generated aggregate Service. DO NOT EDIT. */
@Service
public class OrderAggregateService {
    private final OrderAggregateRepository parentRepo;
    private final OrderAggregateOrderItemLineRepository childRepo;

    public OrderAggregateService(OrderAggregateRepository parentRepo, OrderAggregateOrderItemLineRepository childRepo) {
        this.parentRepo = parentRepo;
        this.childRepo = childRepo;
    }

    public OrderAggregate save(OrderAggregate e) {
        OrderAggregate saved = parentRepo.save(e);
        List<?> existing = childRepo.findByOrderId(saved.getId());
        if (existing != null) existing.forEach(it -> childRepo.deleteById((( OrderAggregate.OrderItemLine)it).getId()));
        for (var item : e.getItems()) {
            item.setId(null);
            item.setOrderId(saved.getId());
            childRepo.save(item);
        }
        return findById(saved.getId()).orElseThrow();
    }

    public Optional<OrderAggregate> findById(Long id) {
        return parentRepo.findById(id).map(parent -> {
            var items = childRepo.findByOrderId(id);
            parent.setItems(items != null ? items : new ArrayList<>());
            return parent;
        });
    }

    public Iterable<OrderAggregate> findAll() {
        var list = new ArrayList<OrderAggregate>();
        parentRepo.findAll().forEach(p -> findById(p.getId()).ifPresent(list::add));
        return list;
    }

    public void deleteById(Long id) {
        var items = childRepo.findByOrderId(id);
        if (items != null) items.forEach(it -> childRepo.deleteById(it.getId()));
        parentRepo.deleteById(id);
    }
}
