package com.example.wmsnew.repository;

import org.springframework.data.repository.CrudRepository;
import com.example.wmsnew.entity.OrderAggregate;

import java.util.List;

/** Auto-generated. DO NOT EDIT. */
public interface OrderAggregateOrderItemLineRepository extends CrudRepository<OrderAggregate.OrderItemLine, Long> {
    List<OrderAggregate.OrderItemLine> findByOrderId(Long orderId);
}
