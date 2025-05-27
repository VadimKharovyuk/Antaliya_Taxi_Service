package com.example.antaliya_taxi_service.dto.Album;

import com.example.antaliya_taxi_service.dto.PhotoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class AlbumDto {



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String title;
        private String description;
        private MultipartFile coverImage;
        private boolean active;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private String url;
        private String imageId;
        private LocalDateTime createdAt;
        private List<PhotoDto.Response> photos;
        private boolean active;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String title;
        private String description;
        private MultipartFile coverImage;
        private boolean active;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item {
        private Long id;
        private String title;
        private String description;
        private String url;
        private boolean active;
        private LocalDateTime createdAt;


    }

}

