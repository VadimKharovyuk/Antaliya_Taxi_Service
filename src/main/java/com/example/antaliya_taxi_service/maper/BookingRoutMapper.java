package com.example.antaliya_taxi_service.maper;

import com.example.antaliya_taxi_service.dto.Booking.BookingResponseDTO;
import com.example.antaliya_taxi_service.dto.Booking.RouteBookingCreateDTO;
import com.example.antaliya_taxi_service.enums.BookingStatus;
import com.example.antaliya_taxi_service.model.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingRoutMapper {

    public Booking toEntity(RouteBookingCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return Booking.builder()
                // Личная информация клиента
                .customerName(dto.getCustomerName())
                .customerEmail(dto.getCustomerEmail())
                .customerPhone(dto.getCustomerPhone())

                // Тип поездки
                .tripType(dto.getTripType())

                // Основной трансфер
                .pickupLocation(dto.getPickupLocation())
                .dropoffLocation(dto.getDropoffLocation())
                .departureDateTime(dto.getDepartureDateTime())
                .flightNumber(dto.getFlightNumber())
                .hotelAddress(dto.getHotelAddress())

                // Обратный трансфер
                .hasReturnTransfer(dto.getHasReturnTransfer() != null ? dto.getHasReturnTransfer() : false)
                .returnDateTime(dto.getReturnDateTime())
                .returnFlightNumber(dto.getReturnFlightNumber())
                .returnMeetingTime(dto.getReturnMeetingTime())
                .returnPickupLocation(dto.getReturnPickupLocation())
                .returnDropoffLocation(dto.getReturnDropoffLocation())

                // Пассажиры
                .adultCount(dto.getAdultCount() != null ? dto.getAdultCount() : 1)
                .childCount(dto.getChildCount() != null ? dto.getChildCount() : 0)

                // Дополнительные услуги
                .needsChildSeat(dto.getNeedsChildSeat() != null ? dto.getNeedsChildSeat() : false)
                .needsNameGreeting(dto.getNeedsNameGreeting() != null ? dto.getNeedsNameGreeting() : false)
                .specialRequests(dto.getSpecialRequests())

                // Статус по умолчанию
                .status(BookingStatus.PENDING)

                // Остальные поля устанавливаются в сервисе:
                // - vehicle, route (по ID)
                // - ценовые поля (рассчитываются)
                // - bookingReference (генерируется)
                // - аудит поля (JPA)
                .build();
    }

    /**
     * Обратный маппинг Booking Entity -> RouteBookingCreateDTO
     * Может понадобиться для редактирования бронирования
     */
    public RouteBookingCreateDTO toRouteCreateDTO(Booking booking) {
        if (booking == null) {
            return null;
        }

        return RouteBookingCreateDTO.builder()
                // ID связанных сущностей
                .routeId(booking.getRoute() != null ? booking.getRoute().getId() : null)
                .vehicleId(booking.getVehicle() != null ? booking.getVehicle().getId() : null)

                // Личная информация клиента
                .customerName(booking.getCustomerName())
                .customerEmail(booking.getCustomerEmail())
                .customerPhone(booking.getCustomerPhone())

                // Тип поездки
                .tripType(booking.getTripType())

                // Основной трансфер
                .pickupLocation(booking.getPickupLocation())
                .dropoffLocation(booking.getDropoffLocation())
                .departureDateTime(booking.getDepartureDateTime())
                .flightNumber(booking.getFlightNumber())
                .hotelAddress(booking.getHotelAddress())

                // Обратный трансфер
                .hasReturnTransfer(booking.getHasReturnTransfer())
                .returnDateTime(booking.getReturnDateTime())
                .returnFlightNumber(booking.getReturnFlightNumber())
                .returnMeetingTime(booking.getReturnMeetingTime())
                .returnPickupLocation(booking.getReturnPickupLocation())
                .returnDropoffLocation(booking.getReturnDropoffLocation())

                // Пассажиры
                .adultCount(booking.getAdultCount())
                .childCount(booking.getChildCount())

                // Дополнительные услуги
                .needsChildSeat(booking.getNeedsChildSeat())
                .needsNameGreeting(booking.getNeedsNameGreeting())
                .specialRequests(booking.getSpecialRequests())
                .build();
    }

    /**
     * Маппинг Booking Entity -> BookingResponseDTO
     * Для возврата данных клиенту
     */
    public BookingResponseDTO toResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingResponseDTO.builder()
                .id(booking.getId())

                // Личная информация клиента
                .customerName(booking.getCustomerName())
                .customerEmail(booking.getCustomerEmail())
                .customerPhone(booking.getCustomerPhone())

                // Связи (упрощенные данные)
                .vehicleId(booking.getVehicle() != null ? booking.getVehicle().getId() : null)
                .routeId(booking.getRoute() != null ? booking.getRoute().getId() : null)
                .tourId(booking.getTour() != null ? booking.getTour().getId() : null)

                // Названия (составляются из данных)
                .routeName(buildRouteName(booking))
                .tourName(booking.getTour() != null ? booking.getTour().getTitle() : null)

                // Тип поездки
                .tripType(booking.getTripType())

                // Основной трансфер
                .pickupLocation(booking.getPickupLocation())
                .dropoffLocation(booking.getDropoffLocation())
                .departureDateTime(booking.getDepartureDateTime())
                .flightNumber(booking.getFlightNumber())
                .hotelAddress(booking.getHotelAddress())

                // Обратный трансфер
                .hasReturnTransfer(booking.getHasReturnTransfer())
                .returnDateTime(booking.getReturnDateTime())
                .returnFlightNumber(booking.getReturnFlightNumber())
                .returnMeetingTime(booking.getReturnMeetingTime())
                .returnPickupLocation(booking.getReturnPickupLocation())
                .returnDropoffLocation(booking.getReturnDropoffLocation())

                // Пассажиры
                .adultCount(booking.getAdultCount())
                .childCount(booking.getChildCount())
                .totalPassengers(calculateTotalPassengers(booking))

                // Дополнительные услуги
                .needsChildSeat(booking.getNeedsChildSeat())
                .needsNameGreeting(booking.getNeedsNameGreeting())
                .specialRequests(booking.getSpecialRequests())

                // Ценообразование
                .basePrice(booking.getBasePrice())
                .vehicleMultiplier(booking.getVehicleMultiplier())
                .tripMultiplier(booking.getTripMultiplier())
                .totalPrice(booking.getTotalPrice())
                .currency(booking.getCurrency())
                .formattedPrice(formatPrice(booking))

                // Статус и управление
                .status(booking.getStatus())
                .bookingReference(booking.getBookingReference())
                .adminNotes(booking.getAdminNotes())

                // Аудит
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .confirmedAt(booking.getConfirmedAt())
                .completedAt(booking.getCompletedAt())

                // Вычисляемые поля
                .statusDisplayName(getStatusDisplayName(booking.getStatus()))
                .canBeCancelled(canBeCancelled(booking))
                .canBeModified(canBeModified(booking))
                .build();
    }

    /**
     * Вспомогательные методы
     */

    private String buildRouteName(Booking booking) {
        if (booking.getRoute() == null) {
            // Составляем из полей бронирования
            String pickup = booking.getPickupLocation();
            String dropoff = booking.getDropoffLocation();

            if (pickup != null && dropoff != null) {
                return pickup + " → " + dropoff;
            }
        }
        return "Маршрут";
    }

    private Integer calculateTotalPassengers(Booking booking) {
        int adults = booking.getAdultCount() != null ? booking.getAdultCount() : 0;
        int children = booking.getChildCount() != null ? booking.getChildCount() : 0;
        return adults + children;
    }

    private String formatPrice(Booking booking) {
        if (booking.getTotalPrice() != null && booking.getCurrency() != null) {
            return String.format("%.2f %s",
                    booking.getTotalPrice(),
                    booking.getCurrency().name());
        }
        return "Цена не указана";
    }

    private String getStatusDisplayName(BookingStatus status) {
        if (status == null) return "Неизвестно";

        return switch (status) {
            case PENDING -> "Ожидает подтверждения";
            case CONFIRMED -> "Подтверждено";
            case IN_PROGRESS -> "В процессе";
            case COMPLETED -> "Завершено";
            case CANCELLED -> "Отменено";
            case REJECTED -> "Не удалось";
        };
    }

    private Boolean canBeCancelled(Booking booking) {
        if (booking.getStatus() == null) return false;

        return booking.getStatus() == BookingStatus.PENDING ||
                booking.getStatus() == BookingStatus.CONFIRMED;
    }

    private Boolean canBeModified(Booking booking) {
        return booking.getStatus() == BookingStatus.PENDING;
    }
}
