package com.project.fastfoodapi.entity;

import com.project.fastfoodapi.entity.enums.OrderStatus;
import com.project.fastfoodapi.entity.enums.PayType;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime time = LocalDateTime.now();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn
    private List<OrderProduct> products;

    @ManyToOne
    private Human operator;

    @Enumerated(EnumType.STRING)
    private PayType payType;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Delivery delivery;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Filial filial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.NEW;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Human client;

    @Column(nullable = false, scale = 2)
    private BigDecimal amount;
}
