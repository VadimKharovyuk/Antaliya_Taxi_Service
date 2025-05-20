package com.example.antaliya_taxi_service.dto.blog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogListDto {
    private List<BlogCardDto> blogs;
    private int totalPages;
    private int currentPage;
    private long totalItems;
    private int itemsPerPage;
}