package com.project.fastfoodapi.dto.front;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GroupedDataDto<T, R> {
    private String title;
    private T groupedBy;
    private List<R> content;
}
