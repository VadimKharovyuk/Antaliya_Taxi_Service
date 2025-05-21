package com.example.antaliya_taxi_service.dto.tour;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourListDto {

    private List<TourCardDto> tourCardDtos;
    private int totalPages;
    private int currentPage;
    private long totalItems;
    private int itemsPerPage;

    private boolean hasNext;
    private boolean hasPrevious;
    private boolean isFirst;
    private boolean isLast;

}
