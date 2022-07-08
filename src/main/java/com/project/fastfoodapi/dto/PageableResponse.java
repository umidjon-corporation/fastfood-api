package com.project.fastfoodapi.dto;


import com.project.fastfoodapi.mapper.HumanMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

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

    public static <T>PageableResponse<T> parsePage(Page<T> page){
        return PageableResponse.<T>builder()
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .content(page.getContent())
                .totalItems(page.getTotalElements())
                .build();
    }
    public static <T>PageableResponse<T> parsePage(Page<?> page, Collection<T> content){
        return PageableResponse.<T>builder()
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .content(content)
                .totalItems(page.getTotalElements())
                .build();
    }
}
