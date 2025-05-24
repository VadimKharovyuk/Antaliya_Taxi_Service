package com.example.antaliya_taxi_service.service.impl;
import com.example.antaliya_taxi_service.dto.Booking.BookingCreateDTO;
import com.example.antaliya_taxi_service.dto.Booking.BookingResponseDTO;
import com.example.antaliya_taxi_service.enums.BookingStatus;
import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.enums.TripType;
import com.example.antaliya_taxi_service.enums.VehicleClass;
import com.example.antaliya_taxi_service.exception.BookingValidationException;
import com.example.antaliya_taxi_service.exception.PassengerCapacityExceededException;
import com.example.antaliya_taxi_service.exception.VehicleNotAvailableException;
import com.example.antaliya_taxi_service.exception.VehicleNotFoundException;
import com.example.antaliya_taxi_service.maper.BookingMapper;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


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

        @Override
        @Transactional
        public BookingResponseDTO create(BookingCreateDTO createDTO) {
            log.info("Создание нового бронирования для клиента: {} на дату: {}",
                    createDTO.getCustomerName(), createDTO.getDepartureDateTime());

            try {
                // 1. Валидация входных данных
                validateBookingData(createDTO);

                // 2. Получение и проверка автомобиля
                Vehicle vehicle = getAndValidateVehicle(createDTO.getVehicleId(), createDTO);

                // 3. Получение маршрута или тура (если указаны)
                Route route = null;
                Tour tour = null;

                if (createDTO.getRouteId() != null) {
                    route = routeRepository.findById(createDTO.getRouteId())
                            .orElseThrow(() -> new RuntimeException("Маршрут не найден с ID: " + createDTO.getRouteId()));
                    log.info("Найден маршрут: {} -> {}", route.getPickupLocation(), route.getDropoffLocation());
                }

                if (createDTO.getTourId() != null) {
                    tour = tourRepository.findById(createDTO.getTourId())
                            .orElseThrow(() -> new RuntimeException("Тур не найден с ID: " + createDTO.getTourId()));
                    log.info("Найден тур: {}", tour.getTitle());
                }

                // 4. Создание entity из DTO
                Booking booking = bookingMapper.toEntity(createDTO);

                // 5. Установка связей
                booking.setVehicle(vehicle);
                booking.setRoute(route);
                booking.setTour(tour);

                // 6. Расчет стоимости
                calculateBookingPrice(booking, vehicle, route, tour);

                // 7. Установка дополнительных данных
                setBookingDefaults(booking);

                // 8. Генерация номера бронирования
                booking.setBookingReference(generateBookingReference());

                // 9. Сохранение в базу данных
                Booking savedBooking = bookingRepository.save(booking);

                // 10. Обновление статистики автомобиля (если нужно)
//                 updateVehicleStats(vehicle);

                log.info("Бронирование успешно создано с ID: {} и номером: {}",
                        savedBooking.getId(), savedBooking.getBookingReference());

                // 11. Преобразование в ответ DTO
                return bookingMapper.toResponseDTO(savedBooking);

            } catch (BookingValidationException |
                     VehicleNotAvailableException | PassengerCapacityExceededException e) {
                // Перебрасываем кастомные исключения как есть
                log.error("Ошибка валидации при создании бронирования: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Неожиданная ошибка при создании бронирования для {}: {}",
                        createDTO.getCustomerName(), e.getMessage(), e);
                throw new RuntimeException("Ошибка при создании бронирования: " + e.getMessage(), e);
            }
        }

    @Override
    public BigDecimal calculateEstimatedPrice(BookingCreateDTO bookingDTO) {
        try {
            log.debug("Расчет предварительной стоимости для бронирования: tour={}, vehicle={}",
                    bookingDTO.getTourId(), bookingDTO.getVehicleId());

            // Получаем базовую цену
            BigDecimal basePrice = getBasePrice(bookingDTO);

            // Получаем класс автомобиля
            VehicleClass vehicleClass = getVehicleClass(bookingDTO.getVehicleId());

            // Используем существующий метод расчета цены
            BigDecimal totalPrice = priceService.calculateTotalPrice(
                    basePrice,
                    vehicleClass,
                    bookingDTO.getTripType(),
                    bookingDTO.getDepartureDateTime(),
                    Boolean.TRUE.equals(bookingDTO.getNeedsChildSeat()),
                    Boolean.TRUE.equals(bookingDTO.getNeedsNameGreeting())
            );

            log.debug("Рассчитана предварительная стоимость: {} EUR", totalPrice);
            return totalPrice;

        } catch (Exception e) {
            log.error("Ошибка при расчете предварительной стоимости: {}", e.getMessage(), e);
            return new BigDecimal("0.00");
        }
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


    private BigDecimal getBasePrice(BookingCreateDTO bookingDTO) {
        BigDecimal basePrice = null;

        // Пытаемся получить цену из тура
        if (bookingDTO.getTourId() != null) {
            Tour tour = tourRepository.findById(bookingDTO.getTourId()).orElse(null);
            if (tour != null && tour.getPrice() != null) {
                basePrice = tour.getPrice();
                log.debug("Использована цена из тура: {} EUR", basePrice);
            }
        }

        // Если нет цены тура, пытаемся получить из маршрута
        if (basePrice == null && bookingDTO.getRouteId() != null) {
            Route route = routeRepository.findById(bookingDTO.getRouteId()).orElse(null);
            if (route != null && route.getBasePrice() != null) {
                basePrice = route.getBasePrice();
                log.debug("Использована цена из маршрута: {} EUR", basePrice);
            }
        }

        // Если цена все еще не найдена, используем значение по умолчанию
        if (basePrice == null) {
            basePrice = new BigDecimal("50.00");
            log.debug("Использована базовая цена по умолчанию: {} EUR", basePrice);
        }

        return basePrice;
    }
    /**
         * Валидация данных бронирования
         */
        private void validateBookingData(BookingCreateDTO createDTO) {
            List<String> errors = new ArrayList<>();

            // Проверка обязательных полей
            if (createDTO.getVehicleId() == null) {
                errors.add("Не выбран автомобиль");
            }

            if (createDTO.getDepartureDateTime() == null) {
                errors.add("Не указана дата и время отправления");
            } else if (createDTO.getDepartureDateTime().isBefore(LocalDateTime.now().plusHours(1))) {
                errors.add("Дата отправления должна быть минимум через 1 час от текущего времени");
            }

            // Проверка обратного трансфера
            if (Boolean.TRUE.equals(createDTO.getHasReturnTransfer())) {
                if (createDTO.getReturnDateTime() == null) {
                    errors.add("При обратном трансфере обязательно указать дату возврата");
                } else if (createDTO.getReturnDateTime().isBefore(createDTO.getDepartureDateTime().plusHours(1))) {
                    errors.add("Дата возврата должна быть после даты отправления");
                }
            }

            // Проверка количества пассажиров
            int totalPassengers = createDTO.getAdultCount() + (createDTO.getChildCount() != null ? createDTO.getChildCount() : 0);
            if (totalPassengers == 0) {
                errors.add("Общее количество пассажиров должно быть больше 0");
            }

            // Проверка логики маршрута/тура
            if (createDTO.getRouteId() != null && createDTO.getTourId() != null) {
                errors.add("Нельзя одновременно выбрать маршрут и тур");
            }

            if (!errors.isEmpty()) {
                throw new BookingValidationException(errors);
            }
        }


    /**
     * Получение и валидация автомобиля (ОБНОВЛЕННАЯ ВЕРСИЯ)
     */
    private Vehicle getAndValidateVehicle(Long vehicleId, BookingCreateDTO createDTO) {
        // Получаем автомобиль
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        String vehicleName = vehicle.getBrand() + " " + vehicle.getModel();

        // Проверяем активность
        if (!Boolean.TRUE.equals(vehicle.getActive())) {
            throw VehicleNotAvailableException.inactive(vehicleId, vehicleName);
        }

        // Проверяем вместимость
        int totalPassengers = createDTO.getAdultCount() + (createDTO.getChildCount() != null ? createDTO.getChildCount() : 0);
        if (totalPassengers > vehicle.getPassengerCapacity()) {
            throw new PassengerCapacityExceededException(vehicleId, vehicle.getPassengerCapacity(), totalPassengers);
        }

        // Проверяем доступность по времени
        if (isVehicleBusyAtDateTime(vehicleId, createDTO.getDepartureDateTime())) {
            throw VehicleNotAvailableException.alreadyBooked(vehicleId, vehicleName, createDTO.getDepartureDateTime());
        }

        // Проверяем обратный трансфер (если указан)
        if (Boolean.TRUE.equals(createDTO.getHasReturnTransfer()) && createDTO.getReturnDateTime() != null) {
            if (isVehicleBusyAtDateTime(vehicleId, createDTO.getReturnDateTime())) {
                throw VehicleNotAvailableException.alreadyBooked(vehicleId, vehicleName, createDTO.getReturnDateTime());
            }
        }

        log.info("Автомобиль '{}' прошел валидацию для {} пассажиров",
                vehicleName, totalPassengers);

        return vehicle;
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
     * Получает множитель времени (ночная доплата)
     */
    private BigDecimal getTimeMultiplier(LocalDateTime dateTime) {
        if (dateTime == null) {
            return BigDecimal.ONE;
        }

        int hour = dateTime.getHour();

        // Ночная доплата с 22:00 до 6:00
        if (hour >= 22 || hour < 6) {
            BigDecimal nightSurcharge = new BigDecimal("1.2");
            log.debug("Применена ночная доплата x{} для времени {}:00", nightSurcharge, hour);
            return nightSurcharge;
        }

        return BigDecimal.ONE;
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
         * Установка значений по умолчанию
         */
        private void setBookingDefaults(Booking booking) {
            booking.setStatus(BookingStatus.PENDING);
            booking.setCurrency(Currency.USD);

            // Если не указаны, устанавливаем значения по умолчанию
            if (booking.getAdultCount() == null) {
                booking.setAdultCount(1);
            }
            if (booking.getChildCount() == null) {
                booking.setChildCount(0);
            }
            if (booking.getHasReturnTransfer() == null) {
                booking.setHasReturnTransfer(false);
            }
            if (booking.getNeedsChildSeat() == null) {
                booking.setNeedsChildSeat(false);
            }
            if (booking.getNeedsNameGreeting() == null) {
                booking.setNeedsNameGreeting(false);
            }
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
