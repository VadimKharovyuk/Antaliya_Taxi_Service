package com.example.antaliya_taxi_service.service.impl;

import com.example.antaliya_taxi_service.dto.Booking.BookingCreateDTO;
import com.example.antaliya_taxi_service.dto.Booking.BookingResponseDTO;
import com.example.antaliya_taxi_service.dto.Booking.RouteBookingCreateDTO;
import com.example.antaliya_taxi_service.dto.Booking.TourBookingCreateDTO;
import com.example.antaliya_taxi_service.dto.RouteDto;
import com.example.antaliya_taxi_service.dto.vehicle.VehicleResponseDTO;
import com.example.antaliya_taxi_service.enums.BookingStatus;
import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.enums.TripType;
import com.example.antaliya_taxi_service.enums.VehicleClass;
import com.example.antaliya_taxi_service.exception.BookingValidationException;
import com.example.antaliya_taxi_service.exception.PassengerCapacityExceededException;
import com.example.antaliya_taxi_service.exception.VehicleNotAvailableException;
import com.example.antaliya_taxi_service.exception.VehicleNotFoundException;
import com.example.antaliya_taxi_service.maper.BookingMapper;
import com.example.antaliya_taxi_service.maper.BookingRoutMapper;
import com.example.antaliya_taxi_service.model.Booking;
import com.example.antaliya_taxi_service.model.Route;
import com.example.antaliya_taxi_service.model.Tour;
import com.example.antaliya_taxi_service.model.Vehicle;
import com.example.antaliya_taxi_service.repository.BookingRepository;
import com.example.antaliya_taxi_service.repository.RouteRepository;
import com.example.antaliya_taxi_service.repository.TourRepository;
import com.example.antaliya_taxi_service.repository.VehicleRepository;
import com.example.antaliya_taxi_service.service.BookingService;
import com.example.antaliya_taxi_service.service.PriceService;
import com.example.antaliya_taxi_service.service.RouteService;
import com.example.antaliya_taxi_service.service.VehicleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final RouteRepository routeRepository;
    private final TourRepository tourRepository;
    private final BookingMapper bookingMapper;
    private final PriceService priceService;
    private final RouteService routeService;
    private final VehicleService vehicleService;
    private final BookingRoutMapper bookingRoutMapper;

    @Override
    @Transactional
    public BookingResponseDTO createTourBooking(TourBookingCreateDTO tourBookingDTO) {
        log.info("Создание бронирования тура для клиента: {} на дату: {}",
                tourBookingDTO.getCustomerName(), tourBookingDTO.getTourDateTime());

        try {
            // 1. Валидация данных тура
            validateTourBookingData(tourBookingDTO);

            // 2. Получение и проверка тура
            Tour tour = getTourById(tourBookingDTO.getTourId());

            // 3. Получение и проверка автомобиля
            Vehicle vehicle = getAndValidateVehicleForTour(tourBookingDTO, tour);

            // 4. Создание entity для тура
            Booking booking = createTourBookingEntity(tourBookingDTO, tour, vehicle);

            // 5. Расчет стоимости
            calculateTourBookingPrice(booking, vehicle, tour);

            // 6. Установка дополнительных данных
            setTourBookingDefaults(booking);

            // 7. Генерация номера бронирования
            booking.setBookingReference(generateBookingReference());

            // 8. Сохранение
            Booking savedBooking = bookingRepository.save(booking);

            log.info("Бронирование тура успешно создано: ID={}, номер={}",
                    savedBooking.getId(), savedBooking.getBookingReference());

            return bookingMapper.toResponseDTO(savedBooking);

        } catch (Exception e) {
            log.error("Ошибка при создании бронирования тура: {}", e.getMessage(), e);
            throw e;
        }
    }


    private void calculateTourBookingPrice(Booking booking, Vehicle vehicle, Tour tour) {
        BigDecimal basePrice = tour.getPrice() != null ? tour.getPrice() : new BigDecimal("80.00");

        BigDecimal totalPrice = priceService.calculateTotalPrice(
                basePrice,
                vehicle.getVehicleClass(),
                TripType.TOUR,
                booking.getDepartureDateTime(),
                Boolean.TRUE.equals(booking.getNeedsChildSeat()),
                Boolean.TRUE.equals(booking.getNeedsNameGreeting())
        );

        // Сохраняем множители
        booking.setBasePrice(basePrice);
        booking.setVehicleMultiplier(priceService.getVehicleClassMultiplier(vehicle.getVehicleClass()));
        booking.setTripMultiplier(priceService.getTripTypeMultiplier(TripType.TOUR));
        booking.setTimeMultiplier(priceService.getTimeMultiplier(booking.getDepartureDateTime()));
        booking.setTotalPrice(totalPrice);
        booking.setCurrency(Currency.EUR);

        log.info("Рассчитана стоимость тура: {} EUR", totalPrice);
    }

    private void setTourBookingDefaults(Booking booking) {
        booking.setStatus(BookingStatus.PENDING);
        booking.setCurrency(Currency.EUR); // Для туров EUR по умолчанию

        // Устанавливаем текущее время
        LocalDateTime now = LocalDateTime.now();
        booking.setCreatedAt(now);
        booking.setUpdatedAt(now);
    }


    private Booking createTourBookingEntity(TourBookingCreateDTO tourBookingDTO, Tour tour, Vehicle vehicle) {
        Booking booking = new Booking();

        // Личная информация
        booking.setCustomerName(tourBookingDTO.getCustomerName());
        booking.setCustomerEmail(tourBookingDTO.getCustomerEmail());
        booking.setCustomerPhone(tourBookingDTO.getCustomerPhone());

        // Связи
        booking.setTour(tour);
        booking.setVehicle(vehicle);

        // Данные поездки
        booking.setTripType(TripType.TOUR);
        booking.setDepartureDateTime(tourBookingDTO.getTourDateTime());

        // Пассажиры
        booking.setAdultCount(tourBookingDTO.getAdultCount());
        booking.setChildCount(tourBookingDTO.getChildCount() != null ? tourBookingDTO.getChildCount() : 0);

        // Дополнительные услуги
        booking.setNeedsChildSeat(Boolean.TRUE.equals(tourBookingDTO.getNeedsChildSeat()));
        booking.setNeedsNameGreeting(Boolean.TRUE.equals(tourBookingDTO.getNeedsNameGreeting()));
        booking.setSpecialRequests(tourBookingDTO.getSpecialRequests());

        // Контактная информация
        booking.setHotelAddress(tourBookingDTO.getHotelAddress());


        // ===== ИСПРАВЛЕНИЕ: ОБЯЗАТЕЛЬНО УСТАНАВЛИВАЕМ МЕСТА =====
        String pickupLocation;
        String dropoffLocation;

        // Пытаемся получить места из тура
        if (tour.getPickupLocation() != null && !tour.getPickupLocation().trim().isEmpty()) {
            pickupLocation = tour.getPickupLocation();
        } else {
            pickupLocation = "Место встречи согласно программе тура \"" + tour.getTitle() + "\"";
        }

        if (tour.getDropoffLocation() != null && !tour.getDropoffLocation().trim().isEmpty()) {
            dropoffLocation = tour.getDropoffLocation();
        } else {
            dropoffLocation = "Место завершения согласно программе тура \"" + tour.getTitle() + "\"";
        }

        booking.setPickupLocation(pickupLocation);
        booking.setDropoffLocation(dropoffLocation);

        log.info("Установлены места для тура: {} -> {}", pickupLocation, dropoffLocation);

        // Туры не имеют обратного трансфера по умолчанию
        booking.setHasReturnTransfer(false);

        return booking;
    }


    private Vehicle getAndValidateVehicleForTour(TourBookingCreateDTO tourBookingDTO, Tour tour) {
        Vehicle vehicle = vehicleRepository.findById(tourBookingDTO.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException(tourBookingDTO.getVehicleId()));

        String vehicleName = vehicle.getBrand() + " " + vehicle.getModel();

        // Проверяем активность
        if (!Boolean.TRUE.equals(vehicle.getActive())) {
            throw VehicleNotAvailableException.inactive(tourBookingDTO.getVehicleId(), vehicleName);
        }

        // Проверяем вместимость
        int totalPassengers = tourBookingDTO.getAdultCount() +
                (tourBookingDTO.getChildCount() != null ? tourBookingDTO.getChildCount() : 0);
        if (totalPassengers > vehicle.getPassengerCapacity()) {
            throw new PassengerCapacityExceededException(
                    tourBookingDTO.getVehicleId(),
                    vehicle.getPassengerCapacity(),
                    totalPassengers);
        }

        // Проверяем доступность по времени
        if (isVehicleBusyAtDateTime(tourBookingDTO.getVehicleId(), tourBookingDTO.getTourDateTime())) {
            throw VehicleNotAvailableException.alreadyBooked(
                    tourBookingDTO.getVehicleId(),
                    vehicleName,
                    tourBookingDTO.getTourDateTime());
        }

        log.info("Автомобиль '{}' доступен для тура на {} для {} пассажиров",
                vehicleName, tourBookingDTO.getTourDateTime(), totalPassengers);

        return vehicle;
    }


    private Tour getTourById(Long tourId) {
        return tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Тур не найден с ID: " + tourId));
    }

    private void validateTourBookingData(TourBookingCreateDTO tourBookingDTO) {
        List<String> errors = new ArrayList<>();

        // Проверка времени тура
        if (tourBookingDTO.getTourDateTime().isBefore(LocalDateTime.now().plusHours(2))) {
            errors.add("Тур должен быть забронирован минимум за 2 часа до начала");
        }

        // Проверка количества пассажиров
        int totalPassengers = tourBookingDTO.getAdultCount() +
                (tourBookingDTO.getChildCount() != null ? tourBookingDTO.getChildCount() : 0);
        if (totalPassengers == 0) {
            errors.add("Общее количество пассажиров должно быть больше 0");
        }

        if (!errors.isEmpty()) {
            throw new BookingValidationException(errors);
        }
    }


    @Override
    public BookingResponseDTO findByReference(String bookingReference) {
        log.info("Поиск бронирования по номеру: {}", bookingReference);

        // Валидация входного параметра
        if (bookingReference == null || bookingReference.trim().isEmpty()) {
            log.warn("Передан пустой или null номер бронирования");
            throw new IllegalArgumentException("Номер бронирования не может быть пустым");
        }

        String normalizedReference = bookingReference.trim().toUpperCase();

        try {
            // Более лаконичный способ с orElseThrow()
            Booking booking = bookingRepository.findByBookingReference(normalizedReference)
                    .orElseThrow(() -> {
                        log.warn("Бронирование не найдено по номеру: {}", normalizedReference);
                        return new EntityNotFoundException("Бронирование с номером " + normalizedReference + " не найдено");
                    });

            log.info("Найдено бронирование: ID={}, клиент={}, дата={}",
                    booking.getId(),
                    booking.getCustomerName(),
                    booking.getDepartureDateTime());

            return bookingMapper.toResponseDTO(booking);

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при поиске бронирования по номеру {}: {}",
                    normalizedReference, e.getMessage(), e);
            throw new RuntimeException("Ошибка при поиске бронирования: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal calculateTourPrice(TourBookingCreateDTO tourBookingDTO) {
        try {
            log.debug("Расчет стоимости тура: tourId={}, vehicleId={}",
                    tourBookingDTO.getTourId(), tourBookingDTO.getVehicleId());

            // Получаем тур
            Tour tour = getTourById(tourBookingDTO.getTourId());

            // Получаем базовую цену из тура
            BigDecimal basePrice = tour.getPrice() != null ? tour.getPrice() : new BigDecimal("80.00");

            // Получаем класс автомобиля
            VehicleClass vehicleClass = getVehicleClass(tourBookingDTO.getVehicleId());

            // Рассчитываем общую стоимость
            BigDecimal totalPrice = priceService.calculateTotalPrice(
                    basePrice,
                    vehicleClass,
                    TripType.TOUR,
                    tourBookingDTO.getTourDateTime(),
                    Boolean.TRUE.equals(tourBookingDTO.getNeedsChildSeat()),
                    Boolean.TRUE.equals(tourBookingDTO.getNeedsNameGreeting())
            );

            log.debug("Рассчитана стоимость тура: {} EUR", totalPrice);
            return totalPrice;

        } catch (Exception e) {
            log.error("Ошибка при расчете стоимости тура: {}", e.getMessage(), e);
            return new BigDecimal("0.00");
        }
    }

    @Override
    public BookingResponseDTO findByReferenceAndEmail(String bookingReference, String customerEmail) {
        log.info("Поиск бронирования по номеру {} и email {}", bookingReference,
                customerEmail != null ? customerEmail.replaceAll("@.*", "@***") : "null");

        // Валидация входных параметров
        if (bookingReference == null || bookingReference.trim().isEmpty()) {
            log.warn("Передан пустой номер бронирования");
            throw new IllegalArgumentException("Номер бронирования не может быть пустым");
        }

        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            log.warn("Передан пустой email клиента");
            throw new IllegalArgumentException("Email клиента не может быть пустым");
        }

        // Нормализация данных
        String normalizedReference = bookingReference.trim().toUpperCase();
        String normalizedEmail = customerEmail.trim().toLowerCase();

        try {
            // Поиск бронирования с дополнительной проверкой по email
            Optional<Booking> bookingOptional = bookingRepository
                    .findByBookingReferenceAndCustomerEmailIgnoreCase(normalizedReference, normalizedEmail);

            if (bookingOptional.isEmpty()) {
                log.warn("Бронирование не найдено по номеру {} и email {}",
                        normalizedReference, normalizedEmail.replaceAll("@.*", "@***"));
                throw new EntityNotFoundException(
                        "Бронирование с номером " + normalizedReference + " и указанным email не найдено. " +
                                "Проверьте правильность номера бронирования и email адреса."
                );
            }

            Booking booking = bookingOptional.get();

            log.info("Найдено бронирование: ID={}, клиент={}, дата={}, статус={}",
                    booking.getId(),
                    booking.getCustomerName(),
                    booking.getDepartureDateTime(),
                    booking.getStatus());

            return bookingMapper.toResponseDTO(booking);

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при поиске бронирования по номеру {} и email: {}",
                    normalizedReference, e.getMessage(), e);
            throw new RuntimeException("Ошибка при поиске бронирования: " + e.getMessage(), e);
        }
    }

    @Override
    public Long getNewBookingsCount() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        return bookingRepository.getNewBookingsCount(oneWeekAgo);
    }

    @Override
    public BigDecimal calculateRoutePrice(RouteBookingCreateDTO routeBookingDTO) {
        // Получаем Route из базы
        RouteDto.Response route= routeService.findById(routeBookingDTO.getRouteId());
        // Получаем Vehicle из базы
        VehicleResponseDTO  vehicle = vehicleService.getById(routeBookingDTO.getVehicleId());
        return priceService.calculateTotalPrice(
                route.getBasePrice(),                    // Базовая цена из маршрута
                vehicle.getVehicleClass(),               // Класс авто
                routeBookingDTO.getTripType(),           // ONE_WAY или ROUND_TRIP
                routeBookingDTO.getDepartureDateTime(),  // Время для ночной доплаты
                routeBookingDTO.getNeedsChildSeat(),     // Детское кресло
                routeBookingDTO.getNeedsNameGreeting()   // Табличка с именем
        );
    }

    @Override
    public BookingResponseDTO createRouteBooking(RouteBookingCreateDTO routeBookingDTO) {
        // 1. Валидация
        RouteDto.Response route = routeService.findById(routeBookingDTO.getRouteId());
        VehicleResponseDTO vehicle = vehicleService.getById(routeBookingDTO.getVehicleId());

        // 2. Расчет цены
        BigDecimal totalPrice = calculateRoutePrice(routeBookingDTO);

        // 3. Маппинг в Entity
        Booking booking = bookingRoutMapper.toEntity(routeBookingDTO);

        // 3.1. Установить связанные Entity (нужно получить из базы)
        Route routeEntity = routeRepository.findById(routeBookingDTO.getRouteId()).orElseThrow();
        Vehicle vehicleEntity = vehicleRepository.findById(routeBookingDTO.getVehicleId()).orElseThrow();

        booking.setRoute(routeEntity);
        booking.setVehicle(vehicleEntity);

        // 3.2. Установить рассчитанные цены
        booking.setBasePrice(route.getBasePrice());
        booking.setTotalPrice(totalPrice);
        booking.setCurrency(route.getCurrency());

        // 3.3. Генерировать номер бронирования
        booking.setBookingReference(generateBookingReference());

        // 4. Сохранение
        Booking saved = bookingRepository.save(booking);

        // 5. Возврат DTO
        return bookingRoutMapper.toResponseDto(saved);
    }

    /**
     * Получить класс автомобиля по ID
     */
    private VehicleClass getVehicleClass(Long vehicleId) throws EntityNotFoundException {
        if (vehicleId == null) {
            return VehicleClass.getDefault();
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Автомобиль не найден с ID: " + vehicleId));

        return vehicle.getVehicleClass() != null ? vehicle.getVehicleClass() : VehicleClass.getDefault();
    }


    /**
     * Проверка занятости автомобиля в указанное время (ОБНОВЛЕННАЯ ВЕРСИЯ)
     */
    private boolean isVehicleBusyAtDateTime(Long vehicleId, LocalDateTime dateTime) {
        LocalDateTime startCheck = dateTime.minusHours(2);
        LocalDateTime endCheck = dateTime.plusHours(2);

        // Используем статические методы enum для получения активных статусов
        long conflictingBookings = bookingRepository.countByVehicleIdAndDateTimeRange(
                vehicleId, startCheck, endCheck, BookingStatus.getActiveStatuses());

        if (conflictingBookings > 0) {
            log.debug("Найдено {} конфликтующих бронирований для автомобиля ID: {} на время: {}",
                    conflictingBookings, vehicleId, dateTime);
            return true;
        }

        // Дополнительная проверка: не слишком ли рано/поздно для работы (опционально)
        int hour = dateTime.getHour();
        if (hour < 5 || hour > 23) {
            log.debug("Запрошено время вне рабочих часов для автомобиля ID: {} в {}", vehicleId, dateTime);
            // Можно вернуть true, если не работаем ночью
            // return true;
        }

        return false;
    }


    /**
     * Исправленный метод расчета стоимости бронирования
     */
    private void calculateBookingPrice(Booking booking, Vehicle vehicle, Route route, Tour tour) {
        BigDecimal basePrice = getBasePrice(route, tour);

        // Правильный вызов с учетом времени отправления
        BigDecimal totalPrice = priceService.calculateTotalPrice(
                basePrice,
                vehicle.getVehicleClass(),
                booking.getTripType(),
                booking.getDepartureDateTime(), // Возвращаем эту строку!
                Boolean.TRUE.equals(booking.getNeedsChildSeat()),
                Boolean.TRUE.equals(booking.getNeedsNameGreeting())
        );

        // Получаем множители для сохранения в booking
        BigDecimal vehicleMultiplier = priceService.getVehicleClassMultiplier(vehicle.getVehicleClass());
        BigDecimal tripMultiplier = priceService.getTripTypeMultiplier(booking.getTripType());
        BigDecimal timeMultiplier = priceService.getTimeMultiplier(booking.getDepartureDateTime());

        // Сохраняем все данные в booking
        booking.setBasePrice(basePrice);
        booking.setVehicleMultiplier(vehicleMultiplier);
        booking.setTripMultiplier(tripMultiplier);
        booking.setTimeMultiplier(timeMultiplier); // Добавьте это поле в класс Booking если его нет
        booking.setTotalPrice(totalPrice);
        booking.setCurrency(Currency.EUR);

        log.info("💰 Рассчитана стоимость бронирования: {} EUR", totalPrice);
    }


    /**
     * Получение базовой цены из маршрута, тура или по умолчанию
     */
    private BigDecimal getBasePrice(Route route, Tour tour) {
        BigDecimal basePrice;

        if (route != null && route.getBasePrice() != null) {
            basePrice = route.getBasePrice();
            log.debug("📍 Использована базовая цена из маршрута: {} EUR", basePrice);
        } else if (tour != null && tour.getPrice() != null) {
            basePrice = tour.getPrice();
            log.debug("🗺️ Использована базовая цена из тура: {} EUR", basePrice);
        } else {
            // Используем значение по умолчанию
            basePrice = new BigDecimal("50.00");
            log.debug("⚙️ Использована базовая цена по умолчанию: {} EUR", basePrice);
        }

        return basePrice;
    }


    /**
     * Генерация уникального номера бронирования
     */
    private String generateBookingReference() {
        // Формат: ANT-YYYYMMDD-XXXX (ANT = Antalya Taxi)
        String prefix = "ANT";
        String datePart = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        String reference = prefix + "-" + datePart + "-" + randomPart;

        // Проверяем уникальность (на всякий случай)
        while (bookingRepository.existsByBookingReference(reference)) {
            randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            reference = prefix + "-" + datePart + "-" + randomPart;
        }

        return reference;
    }
}
