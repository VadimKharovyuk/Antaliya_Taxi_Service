package com.example.antaliya_taxi_service.repository;

import com.example.antaliya_taxi_service.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByPickupLocationAndDropoffLocation(String pickupLocation, String dropoffLocation);

    List<Route> findByActive(boolean active);

    @Query("SELECT DISTINCT r.pickupLocation FROM Route r WHERE r.active = true ORDER BY r.pickupLocation")
    List<String> findAllActivePickupLocations();

    @Query("SELECT DISTINCT r.dropoffLocation FROM Route r WHERE r.active = true ORDER BY r.dropoffLocation")
    List<String> findAllActiveDropoffLocations();

    @Query("SELECT DISTINCT r.dropoffLocation FROM Route r WHERE r.active = true AND r.pickupLocation = :pickupLocation ORDER BY r.dropoffLocation")
    List<String> findActiveDropoffLocationsByPickupLocation(String pickupLocation);


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
}