package com.example.antaliya_taxi_service.dto.vehicle;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleListDto {
    private List<VehicleCardDto> vehicles;
    private int totalPages;
    private int currentPage;
    private long totalItems;
    private int itemsPerPage;
}
