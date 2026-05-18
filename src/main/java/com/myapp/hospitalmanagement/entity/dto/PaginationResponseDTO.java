package com.myapp.hospitalmanagement.entity.dto;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationResponseDTO<T> {
    private List<T> data;
    private int totalPages;
    private long totalElements;
    private int pageNumber;
    private int size;

    public static <T> PaginationResponseDTO<T> from(Page<T> page) {
            return PaginationResponseDTO.<T>builder()
                    .data(page.getContent())
                    .totalPages(page.getTotalPages())
                    .pageNumber(page.getNumber())
                    .totalElements(page.getTotalElements())
                    .size(page.getSize())
                    .build();
    }
}
