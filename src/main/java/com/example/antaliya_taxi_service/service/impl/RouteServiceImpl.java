package com.example.antaliya_taxi_service.service.impl;

import com.example.antaliya_taxi_service.dto.RouteDto;
import com.example.antaliya_taxi_service.enums.Currency;

import com.example.antaliya_taxi_service.exception.ResourceNotFoundException;
import com.example.antaliya_taxi_service.maper.RouteMapper;
import com.example.antaliya_taxi_service.model.Route;
import com.example.antaliya_taxi_service.repository.RouteRepository;
import com.example.antaliya_taxi_service.service.CurrencyService;
import com.example.antaliya_taxi_service.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Override
    public RouteDto.Response createRoute(RouteDto.Create routeDto) {
        Route route = routeMapper.toEntity(routeDto);
        Route savedRoute = routeRepository.save(route);
        return routeMapper.toResponseDto(savedRoute);
    }

    @Override
    public RouteDto.Response updateRoute(RouteDto.Update routeDto) {
        Route existingRoute = getRouteEntityById(routeDto.getId());
        routeMapper.updateRouteFromDto(routeDto, existingRoute);
        Route updatedRoute = routeRepository.save(existingRoute);
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
     * Вспомогательный метод для расчета схожести двух строк
     * Используется алгоритм расстояния Левенштейна
     */
    private double calculateStringSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return 0.0;
        }

        // Если строки идентичны
        if (s1.equals(s2)) {
            return 1.0;
        }

        // Расчет расстояния Левенштейна
        int distance = levenshteinDistance(s1, s2);

        // Нормализованное сходство (1.0 означает полное совпадение, 0.0 - полное различие)
        return 1.0 - ((double) distance / Math.max(s1.length(), s2.length()));
    }

    /**
     * Расчет расстояния Левенштейна между двумя строками
     */
    private int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();

        // Создаем матрицу расстояний
        int[][] dp = new int[len1 + 1][len2 + 1];

        // Инициализация
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        // Заполнение матрицы
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[len1][len2];
    }

}
