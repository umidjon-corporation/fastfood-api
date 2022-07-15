package com.project.fastfoodapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T>{
    private String message;
    @Builder.Default
    private boolean success=false;
    private T data;

    public boolean isFailed(){
        return !success;
    }
}
