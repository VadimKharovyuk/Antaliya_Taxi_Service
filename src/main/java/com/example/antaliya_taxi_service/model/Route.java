package com.example.antaliya_taxi_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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

    private boolean active = true;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
