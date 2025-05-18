package com.example.antaliya_taxi_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;


import com.example.antaliya_taxi_service.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pickupLocation;
    private String dropoffLocation;

    private Integer distance;
    private Integer estimatedTime;

    @Column(precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(length = 3)
    private Currency currency = Currency.TRY; // Значение по умолчанию - Турецкая лира

    private boolean active = true;


    private String url;
    private String imageId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    @PrePersist
    @PreUpdate
    public void normalizeData() {
        if (pickupLocation != null) pickupLocation = pickupLocation.trim();
        if (dropoffLocation != null) dropoffLocation = dropoffLocation.trim();

    }
}
