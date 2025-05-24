package com.example.antaliya_taxi_service.service.impl;

import com.example.antaliya_taxi_service.dto.vehicle.*;

import com.example.antaliya_taxi_service.enums.BookingStatus;
import com.example.antaliya_taxi_service.enums.VehicleClass;
import com.example.antaliya_taxi_service.maper.VehicleMapper;
import com.example.antaliya_taxi_service.model.Vehicle;
import com.example.antaliya_taxi_service.repository.BookingRepository;
import com.example.antaliya_taxi_service.repository.VehicleRepository;
import com.example.antaliya_taxi_service.service.PriceService;
import com.example.antaliya_taxi_service.service.StorageService;
import com.example.antaliya_taxi_service.service.StorageService.StorageResult;
import com.example.antaliya_taxi_service.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;
    private final StorageService storageService;
    private final PriceService priceService;
    private final BookingRepository bookingRepository;

    @Override
    public VehicleListDto getVehicles(Pageable pageable) {
        Page<Vehicle> vehiclePage = vehicleRepository.findAll(pageable);

        List<VehicleCardDto> vehicleCards = vehiclePage.getContent()
                .stream()
                .map(vehicle -> {
                    VehicleCardDto cardDto = vehicleMapper.toCardDto(vehicle);
                    cardDto.setFormattedPrice(priceService.formatPrice(vehicle));
                    return cardDto;
                })
                .collect(Collectors.toList());

        return VehicleListDto.builder()
                .vehicles(vehicleCards)
                .totalPages(vehiclePage.getTotalPages())
                .currentPage(vehiclePage.getNumber())
                .totalItems(vehiclePage.getTotalElements())
                .itemsPerPage(vehiclePage.getSize())
                .build();
    }

    @Override
    @Transactional
    public VehicleResponseDTO create(VehicleCreateDTO createDTO) {
        log.info("Создание нового автомобиля: {} {}", createDTO.getBrand(), createDTO.getModel());

        try {
            // Преобразуем DTO в entity
            Vehicle vehicle = vehicleMapper.toEntity(createDTO);

            // Обрабатываем загрузку изображения, если оно есть
            if (createDTO.getImage() != null && !createDTO.getImage().isEmpty()) {
                StorageResult storageResult = storageService.uploadImage(createDTO.getImage());
                vehicle.setImageUrl(storageResult.getUrl());
                vehicle.setImageId(storageResult.getImageId());
                log.info("Изображение успешно загружено: {}", storageResult.getImageId());
            }

            // Сохраняем в базу данных
            Vehicle savedVehicle = vehicleRepository.save(vehicle);

            log.info("Автомобиль успешно создан с ID: {}", savedVehicle.getId());
            return vehicleMapper.toResponseDTO(savedVehicle);

        } catch (IOException e) {
            log.error("Ошибка при загрузке изображения для автомобиля {} {}: {}",
                    createDTO.getBrand(), createDTO.getModel(), e.getMessage());
            throw new RuntimeException("Ошибка при загрузке изображения: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Ошибка при создании автомобиля {} {}: {}",
                    createDTO.getBrand(), createDTO.getModel(), e.getMessage());
            throw new RuntimeException("Ошибка при создании автомобиля: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public VehicleResponseDTO update(VehicleUpdateDTO updateDTO) {
        log.info("Обновление автомобиля с ID: {}", updateDTO.getId());

        Vehicle vehicle = vehicleRepository.findById(updateDTO.getId())
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден с ID: " + updateDTO.getId()));

        try {
            // Обрабатываем обновление изображения, если новое изображение предоставлено
            if (updateDTO.getImage() != null && !updateDTO.getImage().isEmpty()) {
                // Удаляем старое изображение, если оно есть
                if (vehicle.getImageId() != null) {
                    boolean deleted = storageService.deleteImage(vehicle.getImageId());
                    if (deleted) {
                        log.info("Старое изображение удалено: {}", vehicle.getImageId());
                    }
                }

                StorageResult storageResult = storageService.uploadImage(updateDTO.getImage());
                vehicle.setImageUrl(storageResult.getUrl());
                vehicle.setImageId(storageResult.getImageId());
            }
            vehicleMapper.updateEntity(vehicle, updateDTO);

            Vehicle savedVehicle = vehicleRepository.save(vehicle);
            return vehicleMapper.toResponseDTO(savedVehicle);

        } catch (IOException e) {
            log.error("Ошибка при обновлении изображения для автомобиля с ID {}: {}",
                    updateDTO.getId(), e.getMessage());
            throw new RuntimeException("Ошибка при обновлении изображения: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Ошибка при обновлении автомобиля с ID {}: {}",
                    updateDTO.getId(), e.getMessage());
            throw new RuntimeException("Ошибка при обновлении автомобиля: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDTO getById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден с ID: " + id));
        return vehicleMapper.toResponseDTO(vehicle);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден с ID: " + id));
        if (vehicle.getImageId() != null) {
            boolean deleted = storageService.deleteImage(vehicle.getImageId());
            if (deleted) {
                log.info("Изображение удалено: {}", vehicle.getImageId());
            } else {
                log.warn("Не удалось удалить изображение: {}", vehicle.getImageId());
            }
        }
        vehicleRepository.delete(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleCardDto> getActiveVehicles() {
        log.info("Получение списка активных автомобилей");

        List<Vehicle> vehicles = vehicleRepository.findByActiveTrue();

        return vehicles.stream()
                .map(vehicle -> {
                    VehicleCardDto cardDto = vehicleMapper.toCardDto(vehicle);
                    cardDto.setFormattedPrice(priceService.formatPrice(vehicle));
                    return cardDto;
                })
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public VehicleUpdateDTO getForEdit(Long id) {
        log.info("Получение автомобиля для редактирования с ID: {}", id);

        VehicleResponseDTO vehicle = this.getById(id); // Используем существующий метод
        return vehicleMapper.toUpdateDTO(vehicle);     // Маппер остается в сервисе
    }

    @Override
    public List<VehicleCardDto> getAvailableVehicles(LocalDateTime selectedDate, Integer passengers) {
        try {
            log.debug("Поиск доступных автомобилей на {} для {} пассажиров", selectedDate, passengers);

            // 1. Получаем все активные автомобили с нужной вместимостью
            List<Vehicle> suitableVehicles = vehicleRepository.findByActiveTrueAndPassengerCapacityGreaterThanEqualOrderByVehicleClassAsc(passengers);

            // 2. Фильтруем по доступности во времени
            List<Vehicle> availableVehicles = suitableVehicles.stream()
                    .filter(vehicle -> isVehicleAvailableAtTime(vehicle.getId(), selectedDate))
                    .toList();

            // 3. Преобразуем в DTO и сортируем по цене
            List<VehicleCardDto> result = availableVehicles.stream()
                    .map(vehicleMapper::toCardDto)
                    .sorted(Comparator.comparing(v -> v.getVehicleClass().getPriceMultiplier()))
                    .collect(Collectors.toList());

            log.info("Найдено {} доступных автомобилей из {} подходящих", result.size(), suitableVehicles.size());
            return result;

        } catch (Exception e) {
            log.error("Ошибка при поиске доступных автомобилей: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isVehicleAvailable(Long vehicleId, LocalDateTime dateTime) {
        try {
            // Проверяем существование и активность автомобиля
            Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
            if (vehicle == null || !Boolean.TRUE.equals(vehicle.getActive())) {
                log.debug("Автомобиль ID {} не найден или неактивен", vehicleId);
                return false;
            }

            return isVehicleAvailableAtTime(vehicleId, dateTime);

        } catch (Exception e) {
            log.error("Ошибка при проверке доступности автомобиля {}: {}", vehicleId, e.getMessage());
            return false;
        }
    }

    @Override
    public VehicleCardDto getVehicleById(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
        return vehicleMapper.toCardDto(vehicle);
    }

    private boolean isVehicleAvailableAtTime(Long vehicleId, LocalDateTime dateTime) {
        // Создаем временное окно для проверки конфликтов (±2 часа)
        LocalDateTime startCheck = dateTime.minusHours(2);
        LocalDateTime endCheck = dateTime.plusHours(2);

        // Получаем активные статусы бронирований
        List<BookingStatus> activeStatuses = BookingStatus.getActiveStatuses();

        // Проверяем конфликтующие бронирования
        long conflictingBookings = bookingRepository.countByVehicleIdAndDateTimeRange(
                vehicleId, startCheck, endCheck, activeStatuses);

        boolean isAvailable = conflictingBookings == 0;

        if (!isAvailable) {
            log.debug("Автомобиль ID {} занят на время {}, найдено {} конфликтующих бронирований",
                    vehicleId, dateTime, conflictingBookings);
        }

        return isAvailable;
    }


}