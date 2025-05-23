package com.example.antaliya_taxi_service.service;
import com.example.antaliya_taxi_service.dto.blog.BlogCardDto;
import com.example.antaliya_taxi_service.dto.blog.BlogCardTranslationDto;
import com.example.antaliya_taxi_service.dto.blog.BlogDetailDto;
import com.example.antaliya_taxi_service.dto.blog.BlogTranslationDto;
import com.example.antaliya_taxi_service.model.Blog;
import com.example.antaliya_taxi_service.util.TranslationUsageMonitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class BlogTranslationService extends BaseTranslationService<Blog, BlogTranslationDto> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final TranslationUsageMonitor usageMonitor;
@Autowired
    public BlogTranslationService(TranslationService translationService, TranslationUsageMonitor usageMonitor) {
        super(translationService);
        this.usageMonitor = usageMonitor;
    }

    @Override
    public BlogTranslationDto translate(Blog blog, String targetLanguage, String sourceLanguage) {
        if (!isLanguageSupported(targetLanguage)) {
            log.warn("Неподдерживаемый язык: {}", targetLanguage);
            return createUntranslatedDto(blog, targetLanguage);
        }

        logTranslationStart(blog.getId(), targetLanguage);

        // Переводим текстовые поля и записываем статистику
        String translatedTitle = translateAndRecord(blog.getTitle(), targetLanguage, sourceLanguage);
        String translatedDescription = translateAndRecord(blog.getDescription(), targetLanguage, sourceLanguage);
        String translatedShortDesc = translateAndRecord(blog.getShotDescription(), targetLanguage, sourceLanguage);

        BlogTranslationDto result = BlogTranslationDto.builder()
                .id(blog.getId())
                .title(translatedTitle)
                .description(translatedDescription)
                .shotDescription(translatedShortDesc)
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

        logTranslationComplete(blog.getId(), targetLanguage);

        // Логируем текущее использование каждые 10 переводов
        if (usageMonitor.getRequestsCount() % 10 == 0) {
            usageMonitor.logCurrentUsage();
        }

        return result;
    }

    /**
     * Переводит BlogDetailDto на указанный язык
     */
    public BlogTranslationDto translateBlogDetail(BlogDetailDto blogDetail, String targetLanguage) {
        return translateBlogDetail(blogDetail, targetLanguage, null);
    }

    /**
     * Переводит BlogDetailDto с указанием исходного языка
     */
    public BlogTranslationDto translateBlogDetail(BlogDetailDto blogDetail, String targetLanguage, String sourceLanguage) {
        if (!isLanguageSupported(targetLanguage)) {
            log.warn("Неподдерживаемый язык: {}", targetLanguage);
            return createUntranslatedDtoFromDetail(blogDetail, targetLanguage);
        }

        logTranslationStart(blogDetail.getId(), targetLanguage);

        // Переводим текстовые поля и записываем статистику
        String translatedTitle = translateAndRecord(blogDetail.getTitle(), targetLanguage, sourceLanguage);
        String translatedDescription = translateAndRecord(blogDetail.getDescription(), targetLanguage, sourceLanguage);
        String translatedShortDesc = translateAndRecord(blogDetail.getShotDescription(), targetLanguage, sourceLanguage);

        BlogTranslationDto result = BlogTranslationDto.builder()
                .id(blogDetail.getId())
                .title(translatedTitle)
                .description(translatedDescription)
                .shotDescription(translatedShortDesc)
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

        logTranslationComplete(blogDetail.getId(), targetLanguage);
        return result;
    }

    /**
     * Переводит карточку блога на указанный язык
     */
    public BlogCardTranslationDto translateBlogCard(BlogCardDto blogCard, String targetLanguage) {
        return translateBlogCard(blogCard, targetLanguage, null);
    }

    /**
     * Переводит карточку блога с указанием исходного языка
     */
    public BlogCardTranslationDto translateBlogCard(BlogCardDto blogCard, String targetLanguage, String sourceLanguage) {
        if (!isLanguageSupported(targetLanguage)) {
            log.warn("Неподдерживаемый язык: {}", targetLanguage);
            return createUntranslatedCardDto(blogCard, targetLanguage);
        }

        log.debug("Переводим карточку блога {} на язык {}", blogCard.getId(), targetLanguage);

        // Переводим текстовые поля и записываем статистику
        String translatedTitle = translateAndRecord(blogCard.getTitle(), targetLanguage, sourceLanguage);
        String translatedShortDesc = translateAndRecord(blogCard.getShotDescription(), targetLanguage, sourceLanguage);

        return BlogCardTranslationDto.builder()
                .id(blogCard.getId())
                .title(translatedTitle)
                .shotDescription(translatedShortDesc)
                .url(blogCard.getUrl())
                .views(blogCard.getViews())
                .formattedDate(blogCard.getFormattedDate())
                .language(targetLanguage)
                .build();
    }

    /**
     * Переводит текст и записывает статистику использования
     */
    private String translateAndRecord(String text, String targetLanguage, String sourceLanguage) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        // Записываем статистику
        usageMonitor.recordTranslation(text);

        // Выполняем перевод
        return translationService.translateText(text, targetLanguage, sourceLanguage);
    }

    /**
     * Рассчитывает примерное время чтения (200 слов в минуту)
     */
    private Integer calculateReadingTime(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 1;
        }

        int wordCount = text.trim().split("\\s+").length;
        int minutes = Math.max(1, (int) Math.ceil(wordCount / 200.0));
        return minutes;
    }

    /**
     * Создает DTO без перевода (fallback)
     */
    private BlogTranslationDto createUntranslatedDto(Blog blog, String targetLanguage) {
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
     * Создает DTO без перевода из BlogDetailDto (fallback)
     */
    private BlogTranslationDto createUntranslatedDtoFromDetail(BlogDetailDto blogDetail, String targetLanguage) {
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
     * Создает CardDTO без перевода (fallback)
     */
    private BlogCardTranslationDto createUntranslatedCardDto(BlogCardDto blogCard, String targetLanguage) {
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
}