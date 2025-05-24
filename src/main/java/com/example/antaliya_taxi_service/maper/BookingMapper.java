package com.example.antaliya_taxi_service.maper;

import com.example.antaliya_taxi_service.dto.Booking.BookingCardDto;
import com.example.antaliya_taxi_service.dto.Booking.BookingCreateDTO;
import com.example.antaliya_taxi_service.dto.Booking.BookingResponseDTO;
import com.example.antaliya_taxi_service.dto.Booking.BookingUpdateDTO;
import com.example.antaliya_taxi_service.dto.vehicle.VehicleCardDto;
import com.example.antaliya_taxi_service.enums.BookingStatus;
import com.example.antaliya_taxi_service.model.Booking;
import com.example.antaliya_taxi_service.model.Vehicle;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class BookingMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", new Locale("ru"));

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("ru"));

    /**
     * Преобразует BookingCreateDTO в Booking entity
     */
    public Booking toEntity(BookingCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return Booking.builder()
                .customerName(dto.getCustomerName())
                .customerEmail(dto.getCustomerEmail())
                .customerPhone(dto.getCustomerPhone())
                .tripType(dto.getTripType())
                .pickupLocation(dto.getPickupLocation())
                .dropoffLocation(dto.getDropoffLocation())
                .departureDateTime(dto.getDepartureDateTime())
                .flightNumber(dto.getFlightNumber())
                .hotelAddress(dto.getHotelAddress())
                .hasReturnTransfer(dto.getHasReturnTransfer())
                .returnDateTime(dto.getReturnDateTime())
                .returnFlightNumber(dto.getReturnFlightNumber())
                .returnMeetingTime(dto.getReturnMeetingTime())
                .returnPickupLocation(dto.getReturnPickupLocation())
                .returnDropoffLocation(dto.getReturnDropoffLocation())
                .adultCount(dto.getAdultCount())
                .childCount(dto.getChildCount())
                .needsChildSeat(dto.getNeedsChildSeat())
                .needsNameGreeting(dto.getNeedsNameGreeting())
                .specialRequests(dto.getSpecialRequests())
                .build();
    }

    /**
     * Преобразует Booking entity в BookingResponseDTO
     */
    public BookingResponseDTO toResponseDTO(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingResponseDTO.builder()
                .id(booking.getId())
                .customerName(booking.getCustomerName())
                .customerEmail(booking.getCustomerEmail())
                .customerPhone(booking.getCustomerPhone())
                .vehicleId(booking.getVehicle() != null ? booking.getVehicle().getId() : null)
                .routeId(booking.getRoute() != null ? booking.getRoute().getId() : null)
                .routeName(booking.getRoute() != null ?
                        booking.getRoute().getPickupLocation() + " - " + booking.getRoute().getDropoffLocation() : null)
                .tourId(booking.getTour() != null ? booking.getTour().getId() : null)
                .tourName(booking.getTour() != null ? booking.getTour().getTitle() : null)
                .tripType(booking.getTripType())
                .pickupLocation(booking.getPickupLocation())
                .dropoffLocation(booking.getDropoffLocation())
                .departureDateTime(booking.getDepartureDateTime())
                .flightNumber(booking.getFlightNumber())
                .hotelAddress(booking.getHotelAddress())
                .hasReturnTransfer(booking.getHasReturnTransfer())
                .returnDateTime(booking.getReturnDateTime())
                .returnFlightNumber(booking.getReturnFlightNumber())
                .returnMeetingTime(booking.getReturnMeetingTime())
                .returnPickupLocation(booking.getReturnPickupLocation())
                .returnDropoffLocation(booking.getReturnDropoffLocation())
                .adultCount(booking.getAdultCount())
                .childCount(booking.getChildCount())
                .totalPassengers(calculateTotalPassengers(booking.getAdultCount(), booking.getChildCount()))
                .needsChildSeat(booking.getNeedsChildSeat())
                .needsNameGreeting(booking.getNeedsNameGreeting())
                .specialRequests(booking.getSpecialRequests())
                .basePrice(booking.getBasePrice())
                .vehicleMultiplier(booking.getVehicleMultiplier())
                .tripMultiplier(booking.getTripMultiplier())
                .totalPrice(booking.getTotalPrice())
                .currency(booking.getCurrency())
                .formattedPrice(formatPrice(booking.getTotalPrice(), booking.getCurrency()))
                .status(booking.getStatus())
                .bookingReference(booking.getBookingReference())
                .adminNotes(booking.getAdminNotes())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .confirmedAt(booking.getConfirmedAt())
                .completedAt(booking.getCompletedAt())
                .statusDisplayName(getStatusDisplayName(booking.getStatus()))
                .formattedDepartureDate(booking.getDepartureDateTime() != null ?
                        booking.getDepartureDateTime().format(DATE_TIME_FORMATTER) : null)
                .formattedReturnDate(booking.getReturnDateTime() != null ?
                        booking.getReturnDateTime().format(DATE_TIME_FORMATTER) : null)
                .canBeCancelled(canBeCancelled(booking.getStatus()))
                .canBeModified(canBeModified(booking.getStatus()))
                .build();
    }

    /**
     * Преобразует Booking в BookingCardDto для списков
     */
    public BookingCardDto toCardDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingCardDto.builder()
                .id(booking.getId())
                .bookingReference(booking.getBookingReference())
                .customerName(booking.getCustomerName())
                .customerPhone(booking.getCustomerPhone())
                .pickupLocation(booking.getPickupLocation())
                .dropoffLocation(booking.getDropoffLocation())
                .departureDateTime(booking.getDepartureDateTime())
                .formattedDepartureDate(booking.getDepartureDateTime() != null ?
                        booking.getDepartureDateTime().format(DATE_TIME_FORMATTER) : null)
                .vehicleName(formatVehicleName(booking.getVehicle()))
                .vehicleClass(booking.getVehicle() != null ?
                        booking.getVehicle().getVehicleClass().toString() : null)
                .totalPassengers(calculateTotalPassengers(booking.getAdultCount(), booking.getChildCount()))
                .totalPrice(booking.getTotalPrice())
                .formattedPrice(formatPrice(booking.getTotalPrice(), booking.getCurrency()))
                .status(booking.getStatus())
                .statusDisplayName(getStatusDisplayName(booking.getStatus()))
                .statusColor(getStatusColor(booking.getStatus()))
                .tripType(booking.getTripType())
                .hasReturnTransfer(booking.getHasReturnTransfer())
                .createdAt(booking.getCreatedAt())
                .formattedCreatedDate(booking.getCreatedAt() != null ?
                        booking.getCreatedAt().format(DATE_FORMATTER) : null)
                .build();
    }

    /**
     * Преобразует BookingResponseDTO в BookingUpdateDTO
     */
    public BookingUpdateDTO toUpdateDTO(BookingResponseDTO responseDTO) {
        if (responseDTO == null) {
            return null;
        }

        return BookingUpdateDTO.builder()
                .id(responseDTO.getId())
                .customerName(responseDTO.getCustomerName())
                .customerEmail(responseDTO.getCustomerEmail())
                .customerPhone(responseDTO.getCustomerPhone())
                .vehicleId(responseDTO.getVehicleId())
                .routeId(responseDTO.getRouteId())
                .tourId(responseDTO.getTourId())
                .tripType(responseDTO.getTripType())
                .pickupLocation(responseDTO.getPickupLocation())
                .dropoffLocation(responseDTO.getDropoffLocation())
                .departureDateTime(responseDTO.getDepartureDateTime())
                .flightNumber(responseDTO.getFlightNumber())
                .hotelAddress(responseDTO.getHotelAddress())
                .hasReturnTransfer(responseDTO.getHasReturnTransfer())
                .returnDateTime(responseDTO.getReturnDateTime())
                .returnFlightNumber(responseDTO.getReturnFlightNumber())
                .returnMeetingTime(responseDTO.getReturnMeetingTime())
                .returnPickupLocation(responseDTO.getReturnPickupLocation())
                .returnDropoffLocation(responseDTO.getReturnDropoffLocation())
                .adultCount(responseDTO.getAdultCount())
                .childCount(responseDTO.getChildCount())
                .needsChildSeat(responseDTO.getNeedsChildSeat())
                .needsNameGreeting(responseDTO.getNeedsNameGreeting())
                .specialRequests(responseDTO.getSpecialRequests())
                .status(responseDTO.getStatus())
                .adminNotes(responseDTO.getAdminNotes())
                .totalPrice(responseDTO.getTotalPrice())
                .build();
    }

    /**
     * Обновляет существующий Booking entity данными из BookingUpdateDTO
     */
    public void updateEntity(Booking booking, BookingUpdateDTO dto) {
        if (booking == null || dto == null) {
            return;
        }

        if (dto.getCustomerName() != null) {
            booking.setCustomerName(dto.getCustomerName());
        }
        if (dto.getCustomerEmail() != null) {
            booking.setCustomerEmail(dto.getCustomerEmail());
        }
        if (dto.getCustomerPhone() != null) {
            booking.setCustomerPhone(dto.getCustomerPhone());
        }
        if (dto.getTripType() != null) {
            booking.setTripType(dto.getTripType());
        }
        if (dto.getPickupLocation() != null) {
            booking.setPickupLocation(dto.getPickupLocation());
        }
        if (dto.getDropoffLocation() != null) {
            booking.setDropoffLocation(dto.getDropoffLocation());
        }
        if (dto.getDepartureDateTime() != null) {
            booking.setDepartureDateTime(dto.getDepartureDateTime());
        }
        if (dto.getFlightNumber() != null) {
            booking.setFlightNumber(dto.getFlightNumber());
        }
        if (dto.getHotelAddress() != null) {
            booking.setHotelAddress(dto.getHotelAddress());
        }
        if (dto.getHasReturnTransfer() != null) {
            booking.setHasReturnTransfer(dto.getHasReturnTransfer());
        }
        if (dto.getReturnDateTime() != null) {
            booking.setReturnDateTime(dto.getReturnDateTime());
        }
        if (dto.getReturnFlightNumber() != null) {
            booking.setReturnFlightNumber(dto.getReturnFlightNumber());
        }
        if (dto.getReturnMeetingTime() != null) {
            booking.setReturnMeetingTime(dto.getReturnMeetingTime());
        }
        if (dto.getReturnPickupLocation() != null) {
            booking.setReturnPickupLocation(dto.getReturnPickupLocation());
        }
        if (dto.getReturnDropoffLocation() != null) {
            booking.setReturnDropoffLocation(dto.getReturnDropoffLocation());
        }
        if (dto.getAdultCount() != null) {
            booking.setAdultCount(dto.getAdultCount());
        }
        if (dto.getChildCount() != null) {
            booking.setChildCount(dto.getChildCount());
        }
        if (dto.getNeedsChildSeat() != null) {
            booking.setNeedsChildSeat(dto.getNeedsChildSeat());
        }
        if (dto.getNeedsNameGreeting() != null) {
            booking.setNeedsNameGreeting(dto.getNeedsNameGreeting());
        }
        if (dto.getSpecialRequests() != null) {
            booking.setSpecialRequests(dto.getSpecialRequests());
        }
        if (dto.getStatus() != null) {
            booking.setStatus(dto.getStatus());
        }
        if (dto.getAdminNotes() != null) {
            booking.setAdminNotes(dto.getAdminNotes());
        }
        if (dto.getTotalPrice() != null) {
            booking.setTotalPrice(dto.getTotalPrice());
        }
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===

    private Integer calculateTotalPassengers(Integer adults, Integer children) {
        if (adults == null) adults = 0;
        if (children == null) children = 0;
        return adults + children;
    }

    private String formatPrice(java.math.BigDecimal price, com.example.antaliya_taxi_service.enums.Currency currency) {
        if (price == null) return null;
        if (currency == null) return price.toString();
        return String.format("%.2f %s", price, currency.toString());
    }

    private String formatVehicleName(Vehicle vehicle) {
        if (vehicle == null) return null;
        StringBuilder name = new StringBuilder();
        name.append(vehicle.getBrand()).append(" ").append(vehicle.getModel());
        if (vehicle.getYear() != null) {
            name.append(" ").append(vehicle.getYear());
        }
        return name.toString();
    }

    private String getStatusDisplayName(BookingStatus status) {
        if (status == null) return null;

        switch (status) {
            case PENDING:
                return "Ожидает подтверждения";
            case CONFIRMED:
                return "Подтверждено";
            case IN_PROGRESS:
                return "В процессе";
            case COMPLETED:
                return "Завершено";
            case CANCELLED:
                return "Отменено";
            default:
                return status.toString();
        }
    }

    private String getStatusColor(BookingStatus status) {
        if (status == null) return "secondary";

        switch (status) {
            case PENDING:
                return "warning"; // Желтый
            case CONFIRMED:
                return "info"; // Синий
            case IN_PROGRESS:
                return "primary"; // Основной цвет
            case COMPLETED:
                return "success"; // Зеленый
            case CANCELLED:
                return "danger"; // Красный
            default:
                return "secondary"; // Серый
        }
    }

    private Boolean canBeCancelled(BookingStatus status) {
        if (status == null) return false;
        return status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED;
    }

    private Boolean canBeModified(BookingStatus status) {
        if (status == null) return false;
        return status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED;
    }
}