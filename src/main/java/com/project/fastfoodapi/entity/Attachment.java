package com.project.fastfoodapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(nullable = false)
    private String name, type;
    @Column(nullable = false)
    private Long size;
    @Column(nullable = false)
    @JsonIgnore
    private byte[] bytes;
    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime lastUpdated;
}
