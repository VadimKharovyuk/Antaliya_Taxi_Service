package com.example.antaliya_taxi_service.dto.Booking;


import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingListDto {
    private List<BookingCardDto> bookings;
    private int totalPages;
    private int currentPage;
    private long totalItems;
    private int itemsPerPage;
}
