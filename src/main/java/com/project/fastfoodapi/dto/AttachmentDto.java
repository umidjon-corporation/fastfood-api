package com.project.fastfoodapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AttachmentDto {
    private String name, type, url;
    private Long size;
    private LocalDateTime lastUpdated;
}
