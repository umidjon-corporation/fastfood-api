package com.project.fastfoodapi.repository;

import com.project.fastfoodapi.entity.Order;
import com.project.fastfoodapi.entity.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByTimeIsBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<Order> findByDelivery_Courier_IdAndTimeIsBetween(Long id, LocalDateTime timeStart, LocalDateTime timeEnd);

    List<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);

    List<Order> findByOrderStatusAndTimeIsBetween(OrderStatus orderStatus, LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<Order> findByOrderStatusAndDelivery_Courier_idIsNull(OrderStatus orderStatus, Pageable pageable);

    List<Order> findByOrderStatusAndDelivery_Courier_idIsNullAndTimeIsBetween(OrderStatus orderStatus, LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<Order> findByOrderStatusAndBranch_Id(OrderStatus orderStatus, Long branch_id, Pageable pageable);

    List<Order> findByOrderStatusAndBranch_IdAndTimeIsBetween(OrderStatus orderStatus, Long branch_id, LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<Order> findByOrderStatusAndBranch_IdAndDelivery_Courier_IdIsNull(OrderStatus orderStatus, Long branch_id, Pageable pageable);

    List<Order> findByOrderStatusAndBranch_IdAndDelivery_Courier_IdIsNullAndTimeIsBetween(OrderStatus orderStatus, Long branch_id, LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<Order> findByOrderStatusAndDelivery_Courier_idIsNotNull(OrderStatus orderStatus, Pageable pageable);

    List<Order> findByOrderStatusAndDelivery_Courier_idIsNotNullAndTimeIsBetween(OrderStatus orderStatus, LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<Order> findByOrderStatusAndBranch_IdAndDelivery_Courier_IdIsNotNull(OrderStatus orderStatus, Long branch, Pageable pageable);

    List<Order> findByOrderStatusAndBranch_IdAndDelivery_Courier_IdIsNotNullAndTimeIsBetween(OrderStatus orderStatus, Long branch, LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<Order> findByDelivery_Courier_Id(Long courier_id);
}