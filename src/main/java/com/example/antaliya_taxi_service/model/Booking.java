package com.example.antaliya_taxi_service.model;

import com.example.antaliya_taxi_service.enums.BookingStatus;
import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.enums.TripType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === ЛИЧНАЯ ИНФОРМАЦИЯ КЛИЕНТА ===

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName; // Имя и фамилия

    @Column(name = "customer_email", nullable = false, length = 100)
    private String customerEmail; // Электронная почта

    @Column(name = "customer_phone", nullable = false, length = 20)
    private String customerPhone; // Номер телефона


    // === СВЯЗИ С ДРУГИМИ СУЩНОСТЯМИ ===

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle; // Выбранный автомобиль

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route; // Для такси (nullable для туров)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    private Tour tour; // Для туров (nullable для такси)

    // === ТИП ПОЕЗДКИ ===

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_type", nullable = false)
    private TripType tripType; // ONE_WAY или ROUND_TRIP

    // === ОСНОВНОЙ ТРАНСФЕР ===

    @Column(name = "pickup_location", nullable = false, length = 200)
    private String pickupLocation; // Место получения

    @Column(name = "dropoff_location", nullable = false, length = 200)
    private String dropoffLocation; // Место назначения

    @Column(name = "departure_date", nullable = false)
    private LocalDateTime departureDateTime; // Дата и время отправления

    @Column(name = "flight_number", length = 20)
    private String flightNumber; // Номер рейса

    @Column(name = "hotel_address", length = 300)
    private String hotelAddress; // Название отеля / Адрес назначения

    // === ОБРАТНЫЙ ТРАНСФЕР (опционально) ===

    @Builder.Default
    @Column(name = "has_return_transfer")
    private Boolean hasReturnTransfer = false; // Нужен ли обратный трансфер

    @Column(name = "return_date")
    private LocalDateTime returnDateTime; // Дата обратного рейса

    @Column(name = "return_flight_number", length = 20)
    private String returnFlightNumber; // Номер обратного рейса

    @Column(name = "return_meeting_time", length = 10)
    private String returnMeetingTime; // Время обратной встречи

    @Column(name = "return_pickup_location", length = 200)
    private String returnPickupLocation; // Место получения (обратно)

    @Column(name = "return_dropoff_location", length = 200)
    private String returnDropoffLocation; // Место назначения (обратно)

    // === ПАССАЖИРЫ ===

    @Builder.Default
    @Column(name = "adult_count")
    private Integer adultCount = 2; // Количество взрослых

    @Builder.Default
    @Column(name = "child_count")
    private Integer childCount = 0; // Количество детей

    // === ДОПОЛНИТЕЛЬНЫЕ УСЛУГИ ===

    @Builder.Default
    @Column(name = "needs_child_seat")
    private Boolean needsChildSeat = false; // Детское сиденье

    @Builder.Default
    @Column(name = "needs_name_greeting")
    private Boolean needsNameGreeting = false; // Приветствие по имени

    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests; // Особые пожелания / Заметки

    // === ЦЕНООБРАЗОВАНИЕ ===

    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice; // Базовая цена из Route/Tour

    @Column(name = "vehicle_multiplier", precision = 3, scale = 2)
    private BigDecimal vehicleMultiplier; // Множитель класса авто

    @Column(name = "trip_multiplier", precision = 3, scale = 2)
    private BigDecimal tripMultiplier; // Множитель типа поездки

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice; // Итоговая цена

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "currency", length = 3)
    private Currency currency = Currency.USD; // Валюта

    // === СТАТУС И УПРАВЛЕНИЕ ===

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", nullable = false)
    private BookingStatus status = BookingStatus.PENDING; // Статус бронирования

    @Column(name = "booking_reference", unique = true, length = 20)
    private String bookingReference; // Уникальный номер бронирования

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes; // Заметки администратора

    // === АУДИТ ===

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt; // Время подтверждения

    @Column(name = "completed_at")
    private LocalDateTime completedAt; // Время завершения

    private BigDecimal timeMultiplier ;
}