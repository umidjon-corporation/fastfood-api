package com.project.fastfoodapi.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "branchs")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //intended - mo'ljal
    @Column(nullable = false)
    private String nameUz, nameRu, intended;

    @Column(columnDefinition = "text")
    private String address;

    @Column(nullable = false, columnDefinition = "time")
    private LocalTime start, finish;

    @Column(nullable = false)
    private Float latitude, longitude;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}
