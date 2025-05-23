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

    // Существующие методы
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

    public Blog createBlogFromDto(CreateBlogDto dto) {
        if (dto == null) {
            return null;
        }

        return Blog.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .shotDescription(dto.getShotDescription())
                .views(0)
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

    // НОВЫЕ МЕТОДЫ ДЛЯ ПЕРЕВОДА

    /**
     * Создает BlogTranslationDto из Blog с переведенными полями
     */
    public BlogTranslationDto toBlogTranslationDto(Blog blog,
                                                   String translatedTitle,
                                                   String translatedDescription,
                                                   String translatedShortDescription,
                                                   String targetLanguage) {
        if (blog == null) {
            return null;
        }

        return BlogTranslationDto.builder()
                .id(blog.getId())
                .title(translatedTitle != null ? translatedTitle : blog.getTitle())
                .description(translatedDescription != null ? translatedDescription : blog.getDescription())
                .shotDescription(translatedShortDescription != null ? translatedShortDescription : blog.getShotDescription())
                .url(blog.getUrl())
                .imageId(blog.getImageId())
                .views(blog.getViews())
                .uploadDate(blog.getUploadDate())
                .updateDate(blog.getUpdateDate())
                .isPublished(blog.getIsPublished())
                .language(targetLanguage)
                .formattedDate(blog.getUploadDate() != null ?
                        blog.getUploadDate().format(DATE_FORMATTER) : null)
                .readingTimeMinutes(calculateReadingTime(blog.getDescription()))
                .build();
    }

    /**
     * Создает BlogTranslationDto из BlogDetailDto с переведенными полями
     */
    public BlogTranslationDto toBlogTranslationDto(BlogDetailDto blogDetail,
                                                   String translatedTitle,
                                                   String translatedDescription,
                                                   String translatedShortDescription,
                                                   String targetLanguage) {
        if (blogDetail == null) {
            return null;
        }

        return BlogTranslationDto.builder()
                .id(blogDetail.getId())
                .title(translatedTitle != null ? translatedTitle : blogDetail.getTitle())
                .description(translatedDescription != null ? translatedDescription : blogDetail.getDescription())
                .shotDescription(translatedShortDescription != null ? translatedShortDescription : blogDetail.getShotDescription())
                .url(blogDetail.getUrl())
                .imageId(blogDetail.getImageId())
                .views(blogDetail.getViews())
                .uploadDate(blogDetail.getUploadDate())
                .updateDate(blogDetail.getUpdateDate())
                .isPublished(blogDetail.getIsPublished())
                .language(targetLanguage)
                .formattedDate(blogDetail.getFormattedDate())
                .readingTimeMinutes(blogDetail.getReadingTimeMinutes())
                .build();
    }

    /**
     * Создает BlogCardTranslationDto из BlogCardDto с переведенными полями
     */
    public BlogCardTranslationDto toBlogCardTranslationDto(BlogCardDto blogCard,
                                                           String translatedTitle,
                                                           String translatedShortDescription,
                                                           String targetLanguage) {
        if (blogCard == null) {
            return null;
        }

        return BlogCardTranslationDto.builder()
                .id(blogCard.getId())
                .title(translatedTitle != null ? translatedTitle : blogCard.getTitle())
                .shotDescription(translatedShortDescription != null ? translatedShortDescription : blogCard.getShotDescription())
                .url(blogCard.getUrl())
                .views(blogCard.getViews())
                .formattedDate(blogCard.getFormattedDate())
                .language(targetLanguage)
                .build();
    }

    /**
     * Создает непереведенную версию BlogTranslationDto из Blog
     */
    public BlogTranslationDto toUntranslatedBlogTranslationDto(Blog blog, String targetLanguage) {
        if (blog == null) {
            return null;
        }

        return BlogTranslationDto.builder()
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
                .language(targetLanguage)
                .formattedDate(blog.getUploadDate() != null ?
                        blog.getUploadDate().format(DATE_FORMATTER) : null)
                .readingTimeMinutes(calculateReadingTime(blog.getDescription()))
                .build();
    }

    /**
     * Создает непереведенную версию BlogTranslationDto из BlogDetailDto
     */
    public BlogTranslationDto toUntranslatedBlogTranslationDto(BlogDetailDto blogDetail, String targetLanguage) {
        if (blogDetail == null) {
            return null;
        }

        return BlogTranslationDto.builder()
                .id(blogDetail.getId())
                .title(blogDetail.getTitle())
                .description(blogDetail.getDescription())
                .shotDescription(blogDetail.getShotDescription())
                .url(blogDetail.getUrl())
                .imageId(blogDetail.getImageId())
                .views(blogDetail.getViews())
                .uploadDate(blogDetail.getUploadDate())
                .updateDate(blogDetail.getUpdateDate())
                .isPublished(blogDetail.getIsPublished())
                .language(targetLanguage)
                .formattedDate(blogDetail.getFormattedDate())
                .readingTimeMinutes(blogDetail.getReadingTimeMinutes())
                .build();
    }

    /**
     * Создает непереведенную версию BlogCardTranslationDto из BlogCardDto
     */
    public BlogCardTranslationDto toUntranslatedBlogCardTranslationDto(BlogCardDto blogCard, String targetLanguage) {
        if (blogCard == null) {
            return null;
        }

        return BlogCardTranslationDto.builder()
                .id(blogCard.getId())
                .title(blogCard.getTitle())
                .shotDescription(blogCard.getShotDescription())
                .url(blogCard.getUrl())
                .views(blogCard.getViews())
                .formattedDate(blogCard.getFormattedDate())
                .language(targetLanguage)
                .build();
    }

    /**
     * Вспомогательный метод для расчета времени чтения
     */
    private Integer calculateReadingTime(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 1;
        }
        int wordCount = text.trim().split("\\s+").length;
        return Math.max(1, (int) Math.ceil(wordCount / 200.0));
    }
}