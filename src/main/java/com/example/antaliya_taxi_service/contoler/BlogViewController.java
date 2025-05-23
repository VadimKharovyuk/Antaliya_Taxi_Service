package com.example.antaliya_taxi_service.contoler;

import com.example.antaliya_taxi_service.dto.blog.BlogCardTranslationDto;
import com.example.antaliya_taxi_service.dto.blog.BlogDetailDto;
import com.example.antaliya_taxi_service.dto.blog.BlogListDto;
import com.example.antaliya_taxi_service.dto.blog.BlogTranslationDto;
import com.example.antaliya_taxi_service.service.BlogService;
import com.example.antaliya_taxi_service.service.BlogTranslationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер для HTML-страниц блогов
 */
@Controller
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogViewController {

//    private final BlogService blogService;
//
//    private final BlogTranslationService translationService ;
//
//    /**
//     * Отображение списка блогов с пагинацией
//     */
//    @GetMapping
//    public String listBlogs(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "9") int size,
//            Model model) {
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by("uploadDate").descending());
//        BlogListDto blogs = blogService.getPublishedBlogs(pageable);
//
//        model.addAttribute("blogs", blogs);
//        model.addAttribute("currentPage", page);
//
//        return "blogs/list";
//    }
//
//    /**
//     * Отображение детальной страницы блога
//     */
//    @GetMapping("/{id}")
//    public String viewBlog(@PathVariable Long id, Model model, HttpServletRequest request) {
//        BlogDetailDto blog = blogService.getBlogDetailDto(id);
//        if (blog == null || Boolean.FALSE.equals(blog.getIsPublished())) {
//            return "error/404";
//        }
//        blogService.incrementViews(id, request);
//
//        model.addAttribute("blog", blog);
//
//        // Получаем популярные блоги для отображения в сайдбаре
//        Pageable pageable = PageRequest.of(0, 3, Sort.by("views").descending());
//        BlogListDto popularBlogs = blogService.getPublishedBlogs(pageable);
//        model.addAttribute("popularBlogs", popularBlogs.getBlogs());
//
//        return "blogs/detail";
//    }


    private final BlogService blogService;
    private final BlogTranslationService translationService;

    /**
     * Отображение списка блогов с пагинацией и поддержкой переводов
     */
    @GetMapping
    public String listBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String lang,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("uploadDate").descending());
        BlogListDto blogs = blogService.getPublishedBlogs(pageable);

        // Если указан язык перевода
        if (lang != null && isValidLanguage(lang)) {
            // Переводим карточки блогов
            List<BlogCardTranslationDto> translatedBlogs = blogs.getBlogs().stream()
                    .map(blog -> translationService.translateBlogCard(blog, lang))
                    .collect(Collectors.toList());

            model.addAttribute("translatedBlogs", translatedBlogs);
            model.addAttribute("isTranslated", true);
            model.addAttribute("currentLang", lang);
        } else {
            model.addAttribute("isTranslated", false);
        }

        model.addAttribute("blogs", blogs);
        model.addAttribute("currentPage", page);
        model.addAttribute("currentLang", lang != null ? lang : "original");

        return "blogs/list";
    }

    /**
     * Отображение конкретного блога с поддержкой переводов
     */
    @GetMapping("/{id}")
    public String viewBlog(
            @PathVariable Long id,
            @RequestParam(required = false) String lang,
            Model model) {

        BlogDetailDto blog = blogService.getBlogDetailDto(id);
        if (blog == null) {
            return "redirect:/blogs";
        }

        // Если указан язык перевода
        if (lang != null && isValidLanguage(lang)) {
            BlogTranslationDto translatedBlog = translationService.translateBlogDetail(blog, lang);
            model.addAttribute("translatedBlog", translatedBlog);
            model.addAttribute("isTranslated", true);
            model.addAttribute("currentLang", lang);
        } else {
            model.addAttribute("isTranslated", false);
        }

        model.addAttribute("blog", blog);
        model.addAttribute("currentLang", lang != null ? lang : "original");

        return "blogs/detail";
    }

    /**
     * REST API endpoint для получения переведенного списка блогов
     */
    @GetMapping("/api/translate")
    @ResponseBody
    public ResponseEntity<?> getBlogsTranslation(
            @RequestParam String lang,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {

        if (!isValidLanguage(lang)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Неподдерживаемый язык: " + lang));
        }

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("uploadDate").descending());
            BlogListDto blogs = blogService.getPublishedBlogs(pageable);

            List<BlogCardTranslationDto> translatedBlogs = blogs.getBlogs().stream()
                    .map(blog -> translationService.translateBlogCard(blog, lang))
                    .toList();

            Map<String, Object> response = Map.of(
                    "blogs", translatedBlogs,
                    "totalPages", blogs.getTotalPages(),
                    "currentPage", page,
                    "language", lang
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка перевода: " + e.getMessage()));
        }
    }

    /**
     * REST API endpoint для получения переведенного блога
     */
    @GetMapping("/{id}/api/translate")
    @ResponseBody
    public ResponseEntity<?> getBlogTranslation(
            @PathVariable Long id,
            @RequestParam String lang) {

        if (!isValidLanguage(lang)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Неподдерживаемый язык: " + lang));
        }

        try {
            BlogDetailDto blog = blogService.getBlogDetailDto(id);
            if (blog == null) {
                return ResponseEntity.notFound().build();
            }

            BlogTranslationDto translatedBlog = translationService.translateBlogDetail(blog, lang);
            return ResponseEntity.ok(translatedBlog);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка перевода: " + e.getMessage()));
        }
    }

    private boolean isValidLanguage(String lang) {
        return lang != null && (lang.equals("ru") || lang.equals("tr") || lang.equals("en"));
    }
}