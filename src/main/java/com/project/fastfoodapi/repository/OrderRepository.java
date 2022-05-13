package com.project.fastfoodapi.repository;

import com.project.fastfoodapi.entity.Order;
import com.project.fastfoodapi.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByDelivery_Courier_IdAndTimeIsBetween(Long id, LocalDateTime timeStart, LocalDateTime timeEnd);
    List<Order> findByOrderStatus(OrderStatus orderStatus);
    List<Order> findByOrderStatusAndDelivery_Courier_idIsNull(OrderStatus orderStatus);
    List<Order> findByOrderStatusAndFilial_Id(OrderStatus orderStatus, Long filial_id);
    List<Order> findByOrderStatusAndFilial_IdAndDelivery_Courier_IdIsNull(OrderStatus orderStatus, Long filial_id);
    List<Order> findByOrderStatusAndDelivery_Courier_idIsNotNull(OrderStatus orderStatus);
    List<Order> findByOrderStatusAndFilial_IdAndDelivery_Courier_IdIsNotNull(OrderStatus orderStatus, Long filial);
    List<Order> findByDelivery_Courier_Id(Long courier_id);
}