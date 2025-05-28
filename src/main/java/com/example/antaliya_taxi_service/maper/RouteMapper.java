package com.example.antaliya_taxi_service.maper;

import com.example.antaliya_taxi_service.dto.RouteDto;
import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.model.Route;
import com.example.antaliya_taxi_service.service.CurrencyService;
import com.example.antaliya_taxi_service.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Component
@RequiredArgsConstructor
public class RouteMapper {
    private final CurrencyService currencyService ;

    /**
     * Преобразует сущность Route в DTO ответа RouteDto.Response
     */
    public RouteDto.Response toResponseDto(Route route) {
        if (route == null) {
            return null;
        }

        RouteDto.Response responseDto = new RouteDto.Response();
        responseDto.setId(route.getId());
        responseDto.setPickupLocation(route.getPickupLocation());
        responseDto.setDropoffLocation(route.getDropoffLocation());
        responseDto.setDistance(route.getDistance());
        responseDto.setEstimatedTime(route.getEstimatedTime());
        responseDto.setBasePrice(route.getBasePrice());
        responseDto.setCurrency(route.getCurrency());
        responseDto.setActive(route.isActive());
        // Добавляем поля изображений
        responseDto.setUrl(route.getUrl());
        responseDto.setImageId(route.getImageId());
        responseDto.setCreatedAt(route.getCreatedAt());
        responseDto.setUpdatedAt(route.getUpdatedAt());

        // По умолчанию конвертированная цена совпадает с базовой
        responseDto.setConvertedPrice(route.getBasePrice());
        responseDto.setDisplayCurrency(route.getCurrency());

        return responseDto;
    }

    /**
     * Преобразует список сущностей Route в список DTO ответов
     */
    public List<RouteDto.Response> toResponseDtoList(List<Route> routes) {
        if (routes == null) {
            return null;
        }

        return routes.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public Route toEntity(RouteDto.Create createDto) {
        if (createDto == null) {
            return null;
        }

        Route route = new Route();
        route.setPickupLocation(createDto.getPickupLocation());
        route.setDropoffLocation(createDto.getDropoffLocation());
        route.setDistance(createDto.getDistance());
        route.setEstimatedTime(createDto.getEstimatedTime());
        route.setBasePrice(createDto.getBasePrice());
        route.setCurrency(createDto.getCurrency() != null ? createDto.getCurrency() : Currency.TRY);
        route.setActive(createDto.isActive());
        route.setUrl(createDto.getUrl());
        route.setImageId(createDto.getImageId());

        // Добавьте логирование
        log.info("Converting DTO to Entity: " +
                        "pickupLocation={}, dropoffLocation={}, " +
                        "distance={}, estimatedTime={}, " +
                        "basePrice={}, currency={}, active={}",
                route.getPickupLocation(), route.getDropoffLocation(),
                route.getDistance(), route.getEstimatedTime(),
                route.getBasePrice(), route.getCurrency(), route.isActive());

        return route;
    }

    /**
     * Обновляет существующую сущность Route данными из DTO обновления
     */
    public void updateRouteFromDto(RouteDto.Update updateDto, Route route) {
        if (updateDto == null || route == null) {
            return;
        }

        if (updateDto.getPickupLocation() != null) {
            route.setPickupLocation(updateDto.getPickupLocation());
        }

        if (updateDto.getDropoffLocation() != null) {
            route.setDropoffLocation(updateDto.getDropoffLocation());
        }

        if (updateDto.getDistance() != null) {
            route.setDistance(updateDto.getDistance());
        }

        if (updateDto.getEstimatedTime() != null) {
            route.setEstimatedTime(updateDto.getEstimatedTime());
        }

        if (updateDto.getBasePrice() != null) {
            route.setBasePrice(updateDto.getBasePrice());
        }

        if (updateDto.getCurrency() != null) {
            route.setCurrency(updateDto.getCurrency());
        }

        // Обновляем поля изображений только если они предоставлены
        if (updateDto.getUrl() != null) {
            route.setUrl(updateDto.getUrl());
        }

        if (updateDto.getImageId() != null) {
            route.setImageId(updateDto.getImageId());
        }

        route.setActive(updateDto.isActive());
    }

    /**
     * Преобразует сущность Route в DTO результата поиска RouteDto.SearchResult
     */
    public RouteDto.SearchResult toSearchResultDto(Route route) {
        if (route == null) {
            return null;
        }

        RouteDto.SearchResult resultDto = new RouteDto.SearchResult();
        resultDto.setId(route.getId());
        resultDto.setPickupLocation(route.getPickupLocation());
        resultDto.setDropoffLocation(route.getDropoffLocation());
        resultDto.setDistance(route.getDistance());
        resultDto.setEstimatedTime(route.getEstimatedTime());
        resultDto.setBasePrice(route.getBasePrice());
        resultDto.setCurrency(route.getCurrency());
        // Добавляем поля изображений
        resultDto.setUrl(route.getUrl());
        resultDto.setImageId(route.getImageId());

        // По умолчанию конвертированная цена совпадает с базовой
        resultDto.setConvertedPrice(route.getBasePrice());
        resultDto.setDisplayCurrency(route.getCurrency());

        return resultDto;
    }

    /**
     * Преобразует список сущностей Route в список DTO результатов поиска
     */
    public List<RouteDto.SearchResult> toSearchResultDtoList(List<Route> routes) {
        if (routes == null) {
            return null;
        }

        return routes.stream()
                .map(this::toSearchResultDto)
                .collect(Collectors.toList());
    }

    /**
     * Устанавливает сконвертированную цену в DTO ответа
     */
    public void setConvertedPrice(
            RouteDto.Response responseDto,
            BigDecimal convertedPrice,
            Currency displayCurrency) {

        if (responseDto != null) {
            responseDto.setConvertedPrice(convertedPrice);
            responseDto.setDisplayCurrency(displayCurrency);
        }
    }

    /**
     * Устанавливает сконвертированную цену в DTO результата поиска
     */
    public void setConvertedPrice(
            RouteDto.SearchResult searchResultDto,
            BigDecimal convertedPrice,
            Currency displayCurrency) {

        if (searchResultDto != null) {
            searchResultDto.setConvertedPrice(convertedPrice);
            searchResultDto.setDisplayCurrency(displayCurrency);
        }
    }

    /**
     * Создает DTO конвертации цены
     */
    public RouteDto.PriceConversion toPriceConversionDto(
            Long routeId,
            BigDecimal originalPrice,
            Currency originalCurrency,
            BigDecimal convertedPrice,
            Currency targetCurrency,
            BigDecimal exchangeRate) {

        RouteDto.PriceConversion conversionDto = new RouteDto.PriceConversion();
        conversionDto.setRouteId(routeId);
        conversionDto.setOriginalPrice(originalPrice);
        conversionDto.setOriginalCurrency(originalCurrency);
        conversionDto.setConvertedPrice(convertedPrice);
        conversionDto.setTargetCurrency(targetCurrency);
        conversionDto.setExchangeRate(exchangeRate);
        conversionDto.setConversionTime(java.time.LocalDateTime.now());

        return conversionDto;
    }

    /**
     * Обновляет поля изображения в Route из результата загрузки StorageService
     */
    public void updateRouteWithStorageResult(Route route, StorageService.StorageResult result) {
        if (route != null && result != null) {
            route.setUrl(result.getUrl());
            route.setImageId(result.getImageId());
        }
    }

    /**
     * Преобразует список сущностей Route в список DestinationCard DTO
     */
    public List<RouteDto.DestinationCard> toDestinationCardList(List<Route> routes, Currency displayCurrency) {
        if (routes == null) {
            return Collections.emptyList();
        }

        return routes.stream()
                .map(route -> {
                    RouteDto.DestinationCard card = new RouteDto.DestinationCard();
                    card.setId(route.getId());
                    card.setPickupLocation(route.getPickupLocation());
                    card.setDropoffLocation(route.getDropoffLocation());
                    card.setDistance(route.getDistance());
                    card.setEstimatedTime(route.getEstimatedTime());
                    card.setUrl(route.getUrl());


                    // Определяем цену и валюту
                    if (displayCurrency != null && !displayCurrency.equals(route.getCurrency())) {
                        // Конвертируем цену
                        BigDecimal convertedPrice = currencyService.convert(
                                route.getBasePrice(),
                                route.getCurrency(),
                                displayCurrency);
                        card.setPrice(convertedPrice);
                        card.setCurrency(displayCurrency);
                    } else {
                        // Используем базовую цену
                        card.setPrice(route.getBasePrice());
                        card.setCurrency(route.getCurrency());
                    }

                    return card;
                })
                .collect(Collectors.toList());
    }

    public RouteDto.Response toTranslatedResponse(RouteDto.Response original,
                                                  String translatedPickup,
                                                  String translatedDropoff,
                                                  String targetLanguage) {
        return new RouteDto.Response(
                original.getId(),
                translatedPickup,
                translatedDropoff,
                original.getDistance(),
                original.getEstimatedTime(),
                original.getBasePrice(),
                original.getCurrency(),
                original.isActive(),
                original.getUrl(),
                original.getImageId(),
                original.getCreatedAt(),
                original.getUpdatedAt(),
                original.getConvertedPrice(),
                original.getDisplayCurrency(),
                targetLanguage
        );
    }
}