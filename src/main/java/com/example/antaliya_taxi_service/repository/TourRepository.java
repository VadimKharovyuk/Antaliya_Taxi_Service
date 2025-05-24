package com.example.antaliya_taxi_service.repository;

import com.example.antaliya_taxi_service.model.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    Page<Tour> findByIsBestsellerTrueOrderByViewsDesc(Pageable pageable);

    @Modifying
    @Query("UPDATE Tour t SET t.views = COALESCE(t.views, 0) + 1 WHERE t.id = :tourId")
    void incrementViews(@Param("tourId") Long tourId);
}
