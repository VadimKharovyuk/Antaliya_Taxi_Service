package com.example.antaliya_taxi_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tours")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer duration; // в часах

    @Column(name = "is_bestseller")
    private Boolean isBestseller;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(length = 50)
    private String language;

    private String url;

    @Column(name = "image_id")
    private String imageId;

    @Builder.Default
    private Integer views = 0;

    @CreationTimestamp
    @Column(name = "upload_date", updatable = false)
    private LocalDateTime uploadDate;

    @UpdateTimestamp
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
