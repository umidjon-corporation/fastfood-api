package com.project.fastfoodapi.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(scale = 2, nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Float longitude, latitude;

    @ManyToOne()
    @JoinColumn()
    private Human courier;

    @Column(nullable = false)
    private String address;
}
