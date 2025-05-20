package com.example.antaliya_taxi_service.maper;



import com.example.antaliya_taxi_service.dto.*;
import com.example.antaliya_taxi_service.dto.blog.*;
import com.example.antaliya_taxi_service.model.Blog;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BlogMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Преобразование Entity в базовый DTO
    public BlogDto toDto(Blog blog) {
        if (blog == null) {
            return null;
        }

        return BlogDto.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .description(blog.getDescription())
                .shotDescription(blog.getShotDescription())
                .url(blog.getUrl())
                .imageId(blog.getImageId())
                .views(blog.getViews())
                .uploadDate(blog.getUploadDate())
                .updateDate(blog.getUpdateDate())
                .isPublished(blog.getIsPublished())
                .build();
    }

    // Преобразование DTO в Entity
    public Blog toEntity(BlogDto dto) {
        if (dto == null) {
            return null;
        }

        return Blog.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .shotDescription(dto.getShotDescription())
                .url(dto.getUrl())
                .imageId(dto.getImageId())
                .views(dto.getViews())
                .uploadDate(dto.getUploadDate())
                .updateDate(dto.getUpdateDate())
                .isPublished(dto.getIsPublished())
                .build();
    }

    // Преобразование в детальное представление
    public BlogDetailDto toDetailDto(Blog blog, String nextBlogId, String prevBlogId) {
        if (blog == null) {
            return null;
        }

        // Расчет примерного времени чтения (1 минута на 1000 символов)
        int readingTime = 1;
        if (blog.getDescription() != null) {
            readingTime = Math.max(1, blog.getDescription().length() / 1000);
        }

        return BlogDetailDto.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .description(blog.getDescription())
                .shotDescription(blog.getShotDescription())
                .url(blog.getUrl())
                .imageId(blog.getImageId())
                .views(blog.getViews())
                .uploadDate(blog.getUploadDate())
                .updateDate(blog.getUpdateDate())
                .isPublished(blog.getIsPublished())
                .formattedDate(blog.getUploadDate().format(DATE_FORMATTER))
                .readingTimeMinutes(readingTime)
                .nextBlogId(nextBlogId)
                .prevBlogId(prevBlogId)
                .build();
    }

    // Преобразование в карточку для списка
    public BlogCardDto toCardDto(Blog blog) {
        if (blog == null) {
            return null;
        }

        return BlogCardDto.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .shotDescription(blog.getShotDescription())
                .url(blog.getUrl())
                .views(blog.getViews())
                .formattedDate(blog.getUploadDate().format(DATE_FORMATTER))
                .build();
    }

    // Преобразование страницы блогов в ListDto
    public BlogListDto toListDto(Page<Blog> blogPage) {
        List<BlogCardDto> blogCards = blogPage.getContent().stream()
                .map(this::toCardDto)
                .collect(Collectors.toList());

        return BlogListDto.builder()
                .blogs(blogCards)
                .totalPages(blogPage.getTotalPages())
                .currentPage(blogPage.getNumber())
                .totalItems(blogPage.getTotalElements())
                .itemsPerPage(blogPage.getSize())
                .build();
    }

    public Blog createBlogFromDto(CreateBlogDto dto) {
        if (dto == null) {
            return null;
        }

        return Blog.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .shotDescription(dto.getShotDescription())
                // url и imageId будут установлены после загрузки изображения
                .views(0) // Начальное количество просмотров
                .uploadDate(java.time.LocalDateTime.now())
                .updateDate(java.time.LocalDateTime.now())
                .isPublished(dto.getIsPublished())
                .build();
    }

    public void updateBlogFromDto(Blog blog, UpdateBlogDto dto) {
        if (blog == null || dto == null) {
            return;
        }

        blog.setTitle(dto.getTitle());
        blog.setDescription(dto.getDescription());
        blog.setShotDescription(dto.getShotDescription());
        blog.setIsPublished(dto.getIsPublished());
        blog.setUpdateDate(java.time.LocalDateTime.now());


    }
}
