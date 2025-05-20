package com.example.antaliya_taxi_service.repository;

import com.example.antaliya_taxi_service.dto.RouteDto;
import com.example.antaliya_taxi_service.model.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByPickupLocationAndDropoffLocation(String pickupLocation, String dropoffLocation);

    List<Route> findByActive(boolean active);

    Optional<Route> findByPickupLocationAndDropoffLocationAndActiveTrue(String pickupLocation, String dropoffLocation);

    /**
     * Поиск активного маршрута по месту отправления и назначения (игнорируя регистр)
     */
    @Query("SELECT r FROM Route r WHERE LOWER(r.pickupLocation) = LOWER(:pickupLocation) " +
            "AND LOWER(r.dropoffLocation) = LOWER(:dropoffLocation) AND r.active = true")
    Optional<Route> findByPickupLocationAndDropoffLocationIgnoreCaseAndActiveTrue(
            String pickupLocation, String dropoffLocation);

    /**
     * Поиск всех маршрутов по месту отправления и назначения (игнорируя регистр)
     */
    @Query("SELECT r FROM Route r WHERE LOWER(r.pickupLocation) = LOWER(:pickupLocation) " +
            "AND LOWER(r.dropoffLocation) = LOWER(:dropoffLocation)")
    List<Route> findByPickupLocationAndDropoffLocationIgnoreCase(
            String pickupLocation, String dropoffLocation);

    @Query("SELECT r FROM Route r WHERE TRIM(r.pickupLocation) = :pickup AND TRIM(r.dropoffLocation) = :dropoff AND r.active = true")
    Optional<Route> findByTrimmedLocationsAndActiveTrue(@Param("pickup") String pickupLocation, @Param("dropoff") String dropoffLocation);



    Page<Route> findByActiveTrue(Pageable pageable);
}