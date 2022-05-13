package com.project.fastfoodapi.entity;

import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String name;
    @ManyToOne()
    private Category parent;


    @Builder.Default
    @Column(nullable = false)
    private boolean active=true;
}
