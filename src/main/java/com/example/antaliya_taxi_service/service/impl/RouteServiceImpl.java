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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return routeRepository.findByPickupLocationAndDropoffLocation(pickupLocation, dropoffLocation)
                .stream()
                .findFirst();
    }
}