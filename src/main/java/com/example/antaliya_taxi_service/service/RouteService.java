package com.example.antaliya_taxi_service.service;
import com.example.antaliya_taxi_service.dto.RouteDto;
import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.model.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface RouteService {

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
     * Создание нового маршрута с изображением
     */
    RouteDto.Response createRoute(RouteDto.Create routeDto, MultipartFile imageFile) throws IOException;

    /**
     * Обновление существующего маршрута
     */
    RouteDto.Response updateRoute(RouteDto.Update routeDto);

    /**
     * Обновление существующего маршрута с изображением
     */
    RouteDto.Response updateRoute(RouteDto.Update routeDto, MultipartFile imageFile) throws IOException;

    /**
     * Активация/деактивация маршрута
     */
    RouteDto.Response toggleRouteStatus(Long id, boolean active);

    /**
     * Удаление маршрута
     */
    void deleteRoute(Long id);

    /**
     * Удаление маршрута вместе с изображением
     */
    void deleteRouteWithImage(Long id) throws IOException;

    /**
     * Поиск маршрутов по месту отправления и назначения
     */
    List<RouteDto.SearchResult> searchRoutes(RouteDto.Search searchDto);

    /**
     * Поиск маршрутов по месту отправления и назначения с конвертацией цены
     */
    List<RouteDto.SearchResult> searchRoutes(RouteDto.Search searchDto, Currency displayCurrency);

    /**
     * Получение сущности маршрута по ID (для внутреннего использования)
     */
    Route getRouteEntityById(Long id);

    /**
     * Получение мест назначения для указанного места отправления
     */
    List<String> getDropoffLocationsForPickupLocation(String pickupLocation);

    /**
     * Получение всех уникальных мест отправления для активных маршрутов
     */
    List<String> getAllActivePickupLocations();

    /**
     * Получение всех уникальных мест назначения для активных маршрутов
     */
    List<String> getAllActiveDropoffLocations();

    /**
     * Конвертация цены маршрута в указанную валюту
     */
    RouteDto.PriceConversion convertRoutePrice(Long routeId, Currency targetCurrency);

    /**
     * Поиск маршрута по местам отправления и назначения
     */
    Optional<Route> findByPickupAndDropoffLocations(String pickupLocation, String dropoffLocation);

    /**
     * Загрузка изображения для существующего маршрута
     */
    RouteDto.Response uploadRouteImage(Long routeId, MultipartFile imageFile) throws IOException;

    /**
     * Удаление изображения маршрута без удаления самого маршрута
     */
    RouteDto.Response deleteRouteImage(Long routeId) throws IOException;

    /**
     * Для home Page карточки
     */
     List<RouteDto.DestinationCard> getPopularRoutes(Currency displayCurrency);

     /// для клинской части
    Page<RouteDto.Response> getAllRoutesWithPagination(Pageable pageable, Currency displayCurrency) ;


    RouteDto.Response findById(Long routeId);

    Long getActiveRout();


}