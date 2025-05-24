package com.example.antaliya_taxi_service.dto.Booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingStatsDTO {

    // === ОБЩАЯ СТАТИСТИКА ===

    private Long totalBookings;
    private Long pendingBookings;
    private Long confirmedBookings;
    private Long completedBookings;
    private Long cancelledBookings;

    // === ФИНАНСОВАЯ СТАТИСТИКА ===

    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal averageBookingValue;

    // === СТАТИСТИКА ПО ПЕРИОДАМ ===

    private Long todayBookings;
    private Long weekBookings;
    private Long monthBookings;

    // === ПОПУЛЯРНЫЕ НАПРАВЛЕНИЯ ===

    private Map<String, Long> popularRoutes; // "Аэропорт - Отель" -> количество
    private Map<String, Long> popularVehicles; // "Mercedes Vito" -> количество

    // === СТАТИСТИКА ПО СТАТУСАМ ===

    private Map<String, Long> statusDistribution;
    private Map<String, BigDecimal> revenueByStatus;
}
