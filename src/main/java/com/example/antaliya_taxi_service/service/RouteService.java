package com.example.antaliya_taxi_service.service;


import com.example.antaliya_taxi_service.dto.RouteDto;
import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.model.Route;

import java.util.List;
import java.util.Optional;

public interface RouteService {

    /**
     * Получение всех маршрутов
     */
    List<RouteDto.Response> getAllRoutes();

    /**
     * Получение всех маршрутов с конвертацией цены в указанную валюту
     */
    List<RouteDto.Response> getAllRoutes(Currency displayCurrency);

    /**
     * Получение маршрута по ID
     */
    RouteDto.Response getRouteById(Long id);

    /**
     * Получение маршрута по ID с конвертацией цены в указанную валюту
     */
    RouteDto.Response getRouteById(Long id, Currency displayCurrency);

    /**
     * Создание нового маршрута
     */
    RouteDto.Response createRoute(RouteDto.Create routeDto);

    /**
     * Обновление существующего маршрута
     */
    RouteDto.Response updateRoute(RouteDto.Update routeDto);

    /**
     * Активация/деактивация маршрута
     */
    RouteDto.Response toggleRouteStatus(Long id, boolean active);

    /**
     * Удаление маршрута
     */
    void deleteRoute(Long id);

    /**
     * Поиск маршрутов по месту отправления и назначения
     */
    List<RouteDto.SearchResult> searchRoutes(RouteDto.Search searchDto);

    /**
     * Поиск маршрутов по месту отправления и назначения с конвертацией цены
     */
    List<RouteDto.SearchResult> searchRoutes(RouteDto.Search searchDto, Currency displayCurrency);

    /**
     * Конвертация цены маршрута в указанную валюту
     */
    RouteDto.PriceConversion convertRoutePrice(Long routeId, Currency targetCurrency);

    /**
     * Получение сущности маршрута по ID (для внутреннего использования)
     */
    Route getRouteEntityById(Long id);

    /**
     * Поиск маршрута по месту отправления и назначения
     */
    Optional<Route> findByPickupAndDropoffLocations(String pickupLocation, String dropoffLocation);
}
