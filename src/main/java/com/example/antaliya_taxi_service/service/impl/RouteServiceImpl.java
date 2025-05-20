//package com.example.antaliya_taxi_service.service.impl;
//
//import com.example.antaliya_taxi_service.dto.RouteDto;
//import com.example.antaliya_taxi_service.enums.Currency;
//
//import com.example.antaliya_taxi_service.exception.ResourceNotFoundException;
//import com.example.antaliya_taxi_service.maper.RouteMapper;
//import com.example.antaliya_taxi_service.model.Route;
//import com.example.antaliya_taxi_service.repository.RouteRepository;
//import com.example.antaliya_taxi_service.service.CurrencyService;
//import com.example.antaliya_taxi_service.service.RouteService;
//import com.example.antaliya_taxi_service.service.StorageService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.stream.Collectors;
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class RouteServiceImpl implements RouteService {
//
//    private final RouteRepository routeRepository;
//    private final RouteMapper routeMapper;
//    private final CurrencyService currencyService;
//    private final StorageService storageService;
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<RouteDto.Response> getAllRoutes() {
//        List<Route> routes = routeRepository.findAll();
//        return routeMapper.toResponseDtoList(routes);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<RouteDto.Response> getAllRoutes(Currency displayCurrency) {
//        List<RouteDto.Response> routeDtos = getAllRoutes();
//
//        if (displayCurrency != null) {
//            routeDtos.forEach(routeDto -> {
//                if (!routeDto.getCurrency().equals(displayCurrency)) {
//                    BigDecimal convertedPrice = currencyService.convert(
//                            routeDto.getBasePrice(), routeDto.getCurrency(), displayCurrency);
//                    routeMapper.setConvertedPrice(routeDto, convertedPrice, displayCurrency);
//                }
//            });
//        }
//
//        return routeDtos;
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public RouteDto.Response getRouteById(Long id) {
//        Route route = getRouteEntityById(id);
//        return routeMapper.toResponseDto(route);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public RouteDto.Response getRouteById(Long id, Currency displayCurrency) {
//        RouteDto.Response routeDto = getRouteById(id);
//
//        if (displayCurrency != null && !routeDto.getCurrency().equals(displayCurrency)) {
//            BigDecimal convertedPrice = currencyService.convert(
//                    routeDto.getBasePrice(), routeDto.getCurrency(), displayCurrency);
//            routeMapper.setConvertedPrice(routeDto, convertedPrice, displayCurrency);
//        }
//
//        return routeDto;
//    }
//
//    @Override
//    public RouteDto.Response createRoute(RouteDto.Create routeDto) {
//        Route route = routeMapper.toEntity(routeDto);
//        Route savedRoute = routeRepository.save(route);
//        return routeMapper.toResponseDto(savedRoute);
//    }
//
//    @Override
//    public RouteDto.Response updateRoute(RouteDto.Update routeDto) {
//        Route existingRoute = getRouteEntityById(routeDto.getId());
//        routeMapper.updateRouteFromDto(routeDto, existingRoute);
//        Route updatedRoute = routeRepository.save(existingRoute);
//        return routeMapper.toResponseDto(updatedRoute);
//    }
//
//    @Override
//    public RouteDto.Response toggleRouteStatus(Long id, boolean active) {
//        Route route = getRouteEntityById(id);
//        route.setActive(active);
//        Route savedRoute = routeRepository.save(route);
//        return routeMapper.toResponseDto(savedRoute);
//    }
//
//    @Override
//    public void deleteRoute(Long id) {
//        if (!routeRepository.existsById(id)) {
//            throw new ResourceNotFoundException("Route not found with id: " + id);
//        }
//        routeRepository.deleteById(id);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<RouteDto.SearchResult> searchRoutes(RouteDto.Search searchDto) {
//        List<Route> routes = routeRepository.findByPickupLocationAndDropoffLocation(
//                searchDto.getPickupLocation(), searchDto.getDropoffLocation());
//
//        return routeMapper.toSearchResultDtoList(routes);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<RouteDto.SearchResult> searchRoutes(RouteDto.Search searchDto, Currency displayCurrency) {
//        List<RouteDto.SearchResult> results = searchRoutes(searchDto);
//
//        if (displayCurrency != null) {
//            results.forEach(result -> {
//                if (!result.getCurrency().equals(displayCurrency)) {
//                    BigDecimal convertedPrice = currencyService.convert(
//                            result.getBasePrice(), result.getCurrency(), displayCurrency);
//                    routeMapper.setConvertedPrice(result, convertedPrice, displayCurrency);
//                }
//            });
//        }
//
//        return results;
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public RouteDto.PriceConversion convertRoutePrice(Long routeId, Currency targetCurrency) {
//        Route route = getRouteEntityById(routeId);
//
//        if (route.getCurrency().equals(targetCurrency)) {
//            return routeMapper.toPriceConversionDto(
//                    routeId, route.getBasePrice(), route.getCurrency(),
//                    route.getBasePrice(), targetCurrency, BigDecimal.ONE);
//        }
//
//        BigDecimal exchangeRate = currencyService.getExchangeRate(route.getCurrency(), targetCurrency);
//        BigDecimal convertedPrice = currencyService.convert(
//                route.getBasePrice(), route.getCurrency(), targetCurrency);
//
//        return routeMapper.toPriceConversionDto(
//                routeId, route.getBasePrice(), route.getCurrency(),
//                convertedPrice, targetCurrency, exchangeRate);
//    }
//
//
//
//    @Override
//    @Transactional(readOnly = true)
//    public Route getRouteEntityById(Long id) {
//        return routeRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public Optional<Route> findByPickupAndDropoffLocations(String pickupLocation, String dropoffLocation) {
//        // Проверяем входные параметры
//        if (pickupLocation == null || dropoffLocation == null ||
//                pickupLocation.trim().isEmpty() || dropoffLocation.trim().isEmpty()) {
//            return Optional.empty();
//        }
//
//        // Нормализуем входные строки, удаляя лишние пробелы
//        String normalizedPickup = pickupLocation.trim();
//        String normalizedDropoff = dropoffLocation.trim();
//
//        // Добавляем логирование для отладки
//        log.debug("Ищем маршрут: '{}' -> '{}'", normalizedPickup, normalizedDropoff);
//
//        // Получаем все маршруты для проверки и отладки
//        List<Route> allRoutes = routeRepository.findAll();
//        log.debug("Все маршруты в базе данных:");
//        for (Route route : allRoutes) {
//            log.debug("ID: {}, Pickup: '{}', Dropoff: '{}', Active: {}",
//                    route.getId(), route.getPickupLocation(), route.getDropoffLocation(), route.isActive());
//        }
//
//        // Сначала ищем точное совпадение активного маршрута
//        Optional<Route> exactActiveRoute = routeRepository
//                .findByPickupLocationAndDropoffLocationAndActiveTrue(normalizedPickup, normalizedDropoff);
//
//        if (exactActiveRoute.isPresent()) {
//            log.debug("Найдено точное совпадение активного маршрута");
//            return exactActiveRoute;
//        }
//
//        // Если нет, ищем с помощью custom SQL запроса с trim()
//        // Добавьте новый метод в ваш RouteRepository
//        Optional<Route> trimmedActiveRoute = routeRepository
//                .findByTrimmedLocationsAndActiveTrue(normalizedPickup, normalizedDropoff);
//
//        if (trimmedActiveRoute.isPresent()) {
//            log.debug("Найдено совпадение с применением trim() к данным в БД");
//            return trimmedActiveRoute;
//        }
//
//        // Если нет, ищем активный маршрут без учета регистра
//        Optional<Route> caseInsensitiveActiveRoute = routeRepository
//                .findByPickupLocationAndDropoffLocationIgnoreCaseAndActiveTrue(normalizedPickup, normalizedDropoff);
//
//        if (caseInsensitiveActiveRoute.isPresent()) {
//            log.debug("Найдено совпадение без учета регистра (активный маршрут)");
//            return caseInsensitiveActiveRoute;
//        }
//
//        // Если нет, ищем точное совпадение любого маршрута (активного или нет)
//        Optional<Route> exactRoute = routeRepository
//                .findByPickupLocationAndDropoffLocation(normalizedPickup, normalizedDropoff)
//                .stream()
//                .findFirst();
//
//        if (exactRoute.isPresent()) {
//            log.debug("Найдено точное совпадение маршрута (возможно неактивного)");
//            return exactRoute;
//        }
//
//        // В последнюю очередь, ищем любой маршрут без учета регистра
//        Optional<Route> anyRoute = routeRepository
//                .findByPickupLocationAndDropoffLocationIgnoreCase(normalizedPickup, normalizedDropoff)
//                .stream()
//                .findFirst();
//
//        if (anyRoute.isPresent()) {
//            log.debug("Найдено совпадение без учета регистра");
//        } else {
//            log.debug("Маршрут не найден");
//        }
//
//        return anyRoute;
//    }
//
//
//    /**
//     * Получение всех уникальных мест отправления для активных маршрутов
//     */
//    @Override
//    @Transactional(readOnly = true)
//    public List<String> getAllActivePickupLocations() {
//        // Получаем все активные маршруты
//        List<Route> activeRoutes = routeRepository.findByActive(true);
//
//        // Извлекаем уникальные места отправления, сортируем их и возвращаем
//        return activeRoutes.stream()
//                .map(Route::getPickupLocation)
//                .distinct()
//                .sorted()
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Получение всех уникальных мест назначения для активных маршрутов
//     */
//    @Override
//    @Transactional(readOnly = true)
//    public List<String> getAllActiveDropoffLocations() {
//        // Получаем все активные маршруты
//        List<Route> activeRoutes = routeRepository.findByActive(true);
//
//        // Извлекаем уникальные места назначения, сортируем их и возвращаем
//        return activeRoutes.stream()
//                .map(Route::getDropoffLocation)
//                .distinct()
//                .sorted()
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Получение всех уникальных мест назначения для заданного места отправления
//     */
//    @Override
//    @Transactional(readOnly = true)
//    public List<String> getDropoffLocationsForPickupLocation(String pickupLocation) {
//        // Проверяем, что место отправления не пустое
//        if (pickupLocation == null || pickupLocation.trim().isEmpty()) {
//            return getAllActiveDropoffLocations();
//        }
//
//        // Нормализуем строку
//        String normalizedPickup = pickupLocation.trim();
//
//        // Получаем все активные маршруты для данного места отправления
//        List<Route> routes = routeRepository.findByActive(true);
//
//        // Фильтруем по месту отправления, извлекаем уникальные места назначения и сортируем
//        return routes.stream()
//                .filter(route -> route.getPickupLocation().equals(normalizedPickup))
//                .map(Route::getDropoffLocation)
//                .distinct()
//                .sorted()
//                .collect(Collectors.toList());
//    }
//
//}

package com.example.antaliya_taxi_service.service.impl;

import com.example.antaliya_taxi_service.dto.RouteDto;
import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.exception.ResourceNotFoundException;
import com.example.antaliya_taxi_service.maper.RouteMapper;
import com.example.antaliya_taxi_service.model.Route;
import com.example.antaliya_taxi_service.repository.RouteRepository;
import com.example.antaliya_taxi_service.service.CurrencyService;
import com.example.antaliya_taxi_service.service.RouteService;
import com.example.antaliya_taxi_service.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final CurrencyService currencyService;
    private final StorageService storageService;

    @Override
    @Transactional(readOnly = true)
    public List<RouteDto.Response> getAllRoutes() {
        List<Route> routes = routeRepository.findAll();
        return routeMapper.toResponseDtoList(routes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteDto.Response> getAllRoutes(Currency displayCurrency) {
        List<RouteDto.Response> routeDtos = getAllRoutes();

        if (displayCurrency != null) {
            routeDtos.forEach(routeDto -> {
                if (!routeDto.getCurrency().equals(displayCurrency)) {
                    BigDecimal convertedPrice = currencyService.convert(
                            routeDto.getBasePrice(), routeDto.getCurrency(), displayCurrency);
                    routeMapper.setConvertedPrice(routeDto, convertedPrice, displayCurrency);
                }
            });
        }

        return routeDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public RouteDto.Response getRouteById(Long id) {
        Route route = getRouteEntityById(id);
        return routeMapper.toResponseDto(route);
    }

    @Override
    @Transactional(readOnly = true)
    public RouteDto.Response getRouteById(Long id, Currency displayCurrency) {
        RouteDto.Response routeDto = getRouteById(id);

        if (displayCurrency != null && !routeDto.getCurrency().equals(displayCurrency)) {
            BigDecimal convertedPrice = currencyService.convert(
                    routeDto.getBasePrice(), routeDto.getCurrency(), displayCurrency);
            routeMapper.setConvertedPrice(routeDto, convertedPrice, displayCurrency);
        }

        return routeDto;
    }
@Transactional(readOnly = true)
    @Override
    public RouteDto.Response createRoute(RouteDto.Create routeDto) {
        Route route = routeMapper.toEntity(routeDto);
        Route savedRoute = routeRepository.save(route);
        return routeMapper.toResponseDto(savedRoute);
    }

    @Transactional
    @Override
    public RouteDto.Response createRoute(RouteDto.Create routeDto, MultipartFile imageFile) throws IOException {
        log.info("Creating new route with details: {}", routeDto);

        Route route = routeMapper.toEntity(routeDto);

        log.info("Route entity before save: {}", route);

        // Обработка загрузки изображения
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                StorageService.StorageResult uploadResult = storageService.uploadImage(imageFile);
                route.setUrl(uploadResult.getUrl());
                route.setImageId(uploadResult.getImageId());
            } catch (IOException e) {
                log.error("Failed to upload image: {}", e.getMessage(), e);
                throw new IOException("Failed to upload image: " + e.getMessage(), e);
            }
        }

        try {
            Route savedRoute = routeRepository.save(route);
            log.info("Route saved successfully. Saved entity: {}", savedRoute);
            return routeMapper.toResponseDto(savedRoute);
        } catch (Exception e) {
            log.error("Error saving route: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public RouteDto.Response updateRoute(RouteDto.Update routeDto) {
        Route existingRoute = getRouteEntityById(routeDto.getId());
        routeMapper.updateRouteFromDto(routeDto, existingRoute);
        Route updatedRoute = routeRepository.save(existingRoute);
        return routeMapper.toResponseDto(updatedRoute);
    }

    @Override
    public RouteDto.Response updateRoute(RouteDto.Update routeDto, MultipartFile imageFile) throws IOException {
        log.info("Updating route ID {} with image", routeDto.getId());
        Route existingRoute = getRouteEntityById(routeDto.getId());
        routeMapper.updateRouteFromDto(routeDto, existingRoute);

        // Обработка изображения, если оно предоставлено
        if (imageFile != null && !imageFile.isEmpty()) {
            log.info("New image provided for route ID {}. Size: {} bytes", routeDto.getId(), imageFile.getSize());

            // Если у маршрута уже есть изображение, удаляем его
            if (existingRoute.getImageId() != null && !existingRoute.getImageId().isEmpty()) {
                log.info("Deleting previous image with ID: {}", existingRoute.getImageId());
                try {
                    boolean deleted = storageService.deleteImage(existingRoute.getImageId());
                    if (deleted) {
                        log.info("Previous image deleted successfully");
                    } else {
                        log.warn("Failed to delete previous image with ID: {}", existingRoute.getImageId());
                    }
                } catch (Exception e) {
                    log.warn("Error when trying to delete previous image: {}", e.getMessage());
                    // Продолжаем выполнение, даже если удаление старого изображения не удалось
                }
            }

            // Загружаем новое изображение
            try {
                StorageService.StorageResult uploadResult = storageService.uploadImage(imageFile);
                existingRoute.setUrl(uploadResult.getUrl());
                existingRoute.setImageId(uploadResult.getImageId());
                log.info("New image uploaded successfully. URL: {}, ID: {}", uploadResult.getUrl(), uploadResult.getImageId());
            } catch (IOException e) {
                log.error("Failed to upload new image: {}", e.getMessage(), e);
                throw new IOException("Failed to upload new image: " + e.getMessage(), e);
            }
        }

        Route updatedRoute = routeRepository.save(existingRoute);
        log.info("Route updated successfully");

        return routeMapper.toResponseDto(updatedRoute);
    }

    @Override
    public RouteDto.Response toggleRouteStatus(Long id, boolean active) {
        Route route = getRouteEntityById(id);
        route.setActive(active);
        Route savedRoute = routeRepository.save(route);
        return routeMapper.toResponseDto(savedRoute);
    }

    @Override
    public void deleteRoute(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Route not found with id: " + id);
        }
        routeRepository.deleteById(id);
    }

    @Override
    public void deleteRouteWithImage(Long id) throws IOException {
        log.info("Deleting route with ID {} including image", id);
        Route route = getRouteEntityById(id);

        // Если у маршрута есть изображение, удаляем его
        if (route.getImageId() != null && !route.getImageId().isEmpty()) {
            log.info("Deleting associated image with ID: {}", route.getImageId());
            try {
                boolean deleted = storageService.deleteImage(route.getImageId());
                if (deleted) {
                    log.info("Image deleted successfully");
                } else {
                    log.warn("Failed to delete image with ID: {}", route.getImageId());
                }
            } catch (Exception e) {
                log.error("Error when deleting image: {}", e.getMessage(), e);
                // Продолжаем удаление маршрута даже при ошибке удаления изображения
            }
        }

        routeRepository.delete(route);
        log.info("Route deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteDto.SearchResult> searchRoutes(RouteDto.Search searchDto) {
        List<Route> routes = routeRepository.findByPickupLocationAndDropoffLocation(
                searchDto.getPickupLocation(), searchDto.getDropoffLocation());

        return routeMapper.toSearchResultDtoList(routes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteDto.SearchResult> searchRoutes(RouteDto.Search searchDto, Currency displayCurrency) {
        List<RouteDto.SearchResult> results = searchRoutes(searchDto);

        if (displayCurrency != null) {
            results.forEach(result -> {
                if (!result.getCurrency().equals(displayCurrency)) {
                    BigDecimal convertedPrice = currencyService.convert(
                            result.getBasePrice(), result.getCurrency(), displayCurrency);
                    routeMapper.setConvertedPrice(result, convertedPrice, displayCurrency);
                }
            });
        }

        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public RouteDto.PriceConversion convertRoutePrice(Long routeId, Currency targetCurrency) {
        Route route = getRouteEntityById(routeId);

        if (route.getCurrency().equals(targetCurrency)) {
            return routeMapper.toPriceConversionDto(
                    routeId, route.getBasePrice(), route.getCurrency(),
                    route.getBasePrice(), targetCurrency, BigDecimal.ONE);
        }

        BigDecimal exchangeRate = currencyService.getExchangeRate(route.getCurrency(), targetCurrency);
        BigDecimal convertedPrice = currencyService.convert(
                route.getBasePrice(), route.getCurrency(), targetCurrency);

        return routeMapper.toPriceConversionDto(
                routeId, route.getBasePrice(), route.getCurrency(),
                convertedPrice, targetCurrency, exchangeRate);
    }

    @Override
    @Transactional(readOnly = true)
    public Route getRouteEntityById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Route> findByPickupAndDropoffLocations(String pickupLocation, String dropoffLocation) {
        // Проверяем входные параметры
        if (pickupLocation == null || dropoffLocation == null ||
                pickupLocation.trim().isEmpty() || dropoffLocation.trim().isEmpty()) {
            return Optional.empty();
        }

        // Нормализуем входные строки, удаляя лишние пробелы
        String normalizedPickup = pickupLocation.trim();
        String normalizedDropoff = dropoffLocation.trim();

        // Добавляем логирование для отладки
        log.debug("Ищем маршрут: '{}' -> '{}'", normalizedPickup, normalizedDropoff);

        // Получаем все маршруты для проверки и отладки
        List<Route> allRoutes = routeRepository.findAll();
        log.debug("Все маршруты в базе данных:");
        for (Route route : allRoutes) {
            log.debug("ID: {}, Pickup: '{}', Dropoff: '{}', Active: {}",
                    route.getId(), route.getPickupLocation(), route.getDropoffLocation(), route.isActive());
        }

        // Сначала ищем точное совпадение активного маршрута
        Optional<Route> exactActiveRoute = routeRepository
                .findByPickupLocationAndDropoffLocationAndActiveTrue(normalizedPickup, normalizedDropoff);

        if (exactActiveRoute.isPresent()) {
            log.debug("Найдено точное совпадение активного маршрута");
            return exactActiveRoute;
        }

        // Если нет, ищем с помощью custom SQL запроса с trim()
        // Добавьте новый метод в ваш RouteRepository
        Optional<Route> trimmedActiveRoute = routeRepository
                .findByTrimmedLocationsAndActiveTrue(normalizedPickup, normalizedDropoff);

        if (trimmedActiveRoute.isPresent()) {
            log.debug("Найдено совпадение с применением trim() к данным в БД");
            return trimmedActiveRoute;
        }

        // Если нет, ищем активный маршрут без учета регистра
        Optional<Route> caseInsensitiveActiveRoute = routeRepository
                .findByPickupLocationAndDropoffLocationIgnoreCaseAndActiveTrue(normalizedPickup, normalizedDropoff);

        if (caseInsensitiveActiveRoute.isPresent()) {
            log.debug("Найдено совпадение без учета регистра (активный маршрут)");
            return caseInsensitiveActiveRoute;
        }

        // Если нет, ищем точное совпадение любого маршрута (активного или нет)
        Optional<Route> exactRoute = routeRepository
                .findByPickupLocationAndDropoffLocation(normalizedPickup, normalizedDropoff)
                .stream()
                .findFirst();

        if (exactRoute.isPresent()) {
            log.debug("Найдено точное совпадение маршрута (возможно неактивного)");
            return exactRoute;
        }

        // В последнюю очередь, ищем любой маршрут без учета регистра
        Optional<Route> anyRoute = routeRepository
                .findByPickupLocationAndDropoffLocationIgnoreCase(normalizedPickup, normalizedDropoff)
                .stream()
                .findFirst();

        if (anyRoute.isPresent()) {
            log.debug("Найдено совпадение без учета регистра");
        } else {
            log.debug("Маршрут не найден");
        }

        return anyRoute;
    }

    /**
     * Получение всех уникальных мест отправления для активных маршрутов
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllActivePickupLocations() {
        // Получаем все активные маршруты
        List<Route> activeRoutes = routeRepository.findByActive(true);

        // Извлекаем уникальные места отправления, сортируем их и возвращаем
        return activeRoutes.stream()
                .map(Route::getPickupLocation)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Получение всех уникальных мест назначения для активных маршрутов
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllActiveDropoffLocations() {
        // Получаем все активные маршруты
        List<Route> activeRoutes = routeRepository.findByActive(true);

        // Извлекаем уникальные места назначения, сортируем их и возвращаем
        return activeRoutes.stream()
                .map(Route::getDropoffLocation)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Получение всех уникальных мест назначения для заданного места отправления
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getDropoffLocationsForPickupLocation(String pickupLocation) {
        // Проверяем, что место отправления не пустое
        if (pickupLocation == null || pickupLocation.trim().isEmpty()) {
            return getAllActiveDropoffLocations();
        }

        // Нормализуем строку
        String normalizedPickup = pickupLocation.trim();

        // Получаем все активные маршруты для данного места отправления
        List<Route> routes = routeRepository.findByActive(true);

        // Фильтруем по месту отправления, извлекаем уникальные места назначения и сортируем
        return routes.stream()
                .filter(route -> route.getPickupLocation().equals(normalizedPickup))
                .map(Route::getDropoffLocation)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Загрузка изображения для существующего маршрута
     */
    @Override
    public RouteDto.Response uploadRouteImage(Long routeId, MultipartFile imageFile) throws IOException {
        log.info("Uploading image for route ID: {}", routeId);

        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        Route route = getRouteEntityById(routeId);

        // Если у маршрута уже есть изображение, удаляем его
        if (route.getImageId() != null && !route.getImageId().isEmpty()) {
            log.info("Deleting previous image with ID: {}", route.getImageId());
            try {
                boolean deleted = storageService.deleteImage(route.getImageId());
                if (!deleted) {
                    log.warn("Failed to delete previous image with ID: {}", route.getImageId());
                }
            } catch (Exception e) {
                log.warn("Error when trying to delete previous image: {}", e.getMessage());
                // Продолжаем выполнение даже при ошибке удаления
            }
        }

        // Загружаем новое изображение
        try {
            StorageService.StorageResult uploadResult = storageService.uploadImage(imageFile);
            route.setUrl(uploadResult.getUrl());
            route.setImageId(uploadResult.getImageId());
            log.info("Image uploaded successfully. URL: {}, ID: {}", uploadResult.getUrl(), uploadResult.getImageId());
        } catch (IOException e) {
            log.error("Failed to upload image: {}", e.getMessage(), e);
            throw new IOException("Failed to upload image: " + e.getMessage(), e);
        }

        Route updatedRoute = routeRepository.save(route);
        log.info("Route updated with new image");

        return routeMapper.toResponseDto(updatedRoute);
    }

    /**
     * Удаление изображения маршрута без удаления самого маршрута
     */
    @Override
    public RouteDto.Response deleteRouteImage(Long routeId) throws IOException {
        log.info("Deleting image for route ID: {}", routeId);

        Route route = getRouteEntityById(routeId);

        // Проверяем, есть ли у маршрута изображение
        if (route.getImageId() == null || route.getImageId().isEmpty()) {
            log.info("Route ID {} doesn't have an image to delete", routeId);
            return routeMapper.toResponseDto(route);
        }

        // Удаляем изображение
        try {
            boolean deleted = storageService.deleteImage(route.getImageId());
            if (deleted) {
                log.info("Image deleted successfully");

                // Очищаем поля изображения в сущности
                route.setUrl(null);
                route.setImageId(null);

                Route updatedRoute = routeRepository.save(route);
                log.info("Route updated after image deletion");

                return routeMapper.toResponseDto(updatedRoute);
            } else {
                log.warn("Failed to delete image with ID: {}", route.getImageId());
                throw new IOException("Failed to delete image");
            }
        } catch (Exception e) {
            log.error("Error when deleting image: {}", e.getMessage(), e);
            throw new IOException("Error when deleting image: " + e.getMessage(), e);
        }
    }


/**
 * Получить маршруты для отображения на главной странице
 */
    @Transactional(readOnly = true)
    public List<RouteDto.DestinationCard> getPopularRoutes(Currency displayCurrency) {
        Pageable pageable = PageRequest.of(0, 6);
        Page<Route> popularRoutesPage = routeRepository.findByActiveTrue(pageable);
        List<Route> popularRoutes = popularRoutesPage.getContent();
        return routeMapper.toDestinationCardList(popularRoutes, displayCurrency);
    }
}