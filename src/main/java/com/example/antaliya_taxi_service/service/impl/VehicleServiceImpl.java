package com.example.antaliya_taxi_service.service.impl;

import com.example.antaliya_taxi_service.dto.vehicle.*;

import com.example.antaliya_taxi_service.maper.VehicleMapper;
import com.example.antaliya_taxi_service.model.Vehicle;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;
    private final StorageService storageService;
    private final PriceService priceService;

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

                // Загружаем новое изображение
                StorageResult storageResult = storageService.uploadImage(updateDTO.getImage());
                vehicle.setImageUrl(storageResult.getUrl());
                vehicle.setImageId(storageResult.getImageId());
                log.info("Новое изображение загружено: {}", storageResult.getImageId());
            }

            // Обновляем остальные поля
            vehicleMapper.updateEntity(vehicle, updateDTO);

            // Сохраняем изменения
            Vehicle savedVehicle = vehicleRepository.save(vehicle);

            log.info("Автомобиль успешно обновлен с ID: {}", savedVehicle.getId());
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
        log.info("Получение автомобиля по ID: {}", id);

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден с ID: " + id));

        return vehicleMapper.toResponseDTO(vehicle);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Удаление автомобиля с ID: {}", id);

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден с ID: " + id));

        // Удаляем изображение, если оно есть
        if (vehicle.getImageId() != null) {
            boolean deleted = storageService.deleteImage(vehicle.getImageId());
            if (deleted) {
                log.info("Изображение удалено: {}", vehicle.getImageId());
            } else {
                log.warn("Не удалось удалить изображение: {}", vehicle.getImageId());
            }
        }

        // Удаляем запись из базы данных
        vehicleRepository.delete(vehicle);
        log.info("Автомобиль успешно удален с ID: {}", id);
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
}