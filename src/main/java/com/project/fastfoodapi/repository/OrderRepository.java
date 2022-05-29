package com.project.fastfoodapi.repository;

import com.project.fastfoodapi.entity.Order;
import com.project.fastfoodapi.entity.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByDelivery_Courier_IdAndTimeIsBetween(Long id, LocalDateTime timeStart, LocalDateTime timeEnd);

    List<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);
    List<Order> findByOrderStatusAndDelivery_Courier_idIsNull(OrderStatus orderStatus, Pageable pageable);
    List<Order> findByOrderStatusAndFilial_Id(OrderStatus orderStatus, Long filial_id, Pageable pageable);
    List<Order> findByOrderStatusAndFilial_IdAndDelivery_Courier_IdIsNull(OrderStatus orderStatus, Long filial_id, Pageable pageable);
    List<Order> findByOrderStatusAndDelivery_Courier_idIsNotNull(OrderStatus orderStatus, Pageable pageable);
    List<Order> findByOrderStatusAndFilial_IdAndDelivery_Courier_IdIsNotNull(OrderStatus orderStatus, Long filial, Pageable pageable);
    List<Order> findByDelivery_Courier_Id(Long courier_id);
}