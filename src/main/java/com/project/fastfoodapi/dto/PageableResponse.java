package com.project.fastfoodapi.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageableResponse<T> {
    private int currentPage;
    private long totalItems;
    private int totalPages;
    private Collection<T> content;
}
