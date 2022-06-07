package com.project.fastfoodapi.repository;

import com.project.fastfoodapi.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    @Query(nativeQuery = true,
            value = "select op.product_id from order_product op group by op.product_id order by count(op.product_id)*sum(op.count) desc limit ?")
    List<Long> findTopProducts(Integer limit);

}