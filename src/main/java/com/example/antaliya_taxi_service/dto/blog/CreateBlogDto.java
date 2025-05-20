package com.example.antaliya_taxi_service.dto.blog;



import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBlogDto {
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String shotDescription;
    private MultipartFile image;
    private Boolean isPublished;
}
