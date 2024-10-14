package com.ticketing.performance.application.dto;

import lombok.*;
import org.springframework.data.domain.Page;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class PageInfo {

    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;

    public static  PageInfo of(long totalElements, int totalPages, int currentPage, int pageSize) {
        return PageInfo.builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(currentPage + 1)
                .pageSize(pageSize)
                .build();
    }
}
