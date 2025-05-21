package com.example.antaliya_taxi_service.contoler.admin;

import com.example.antaliya_taxi_service.dto.blog.BlogDetailDto;
import com.example.antaliya_taxi_service.dto.blog.BlogListDto;
import com.example.antaliya_taxi_service.service.BlogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Контроллер для HTML-страниц блогов
 */
@Controller
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogViewController {

    private final BlogService blogService;

    /**
     * Отображение списка блогов с пагинацией
     */
    @GetMapping
    public String listBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("uploadDate").descending());
        BlogListDto blogs = blogService.getPublishedBlogs(pageable);

        model.addAttribute("blogs", blogs);
        model.addAttribute("currentPage", page);

        return "blogs/list";
    }

    /**
     * Отображение детальной страницы блога
     */
    @GetMapping("/{id}")
    public String viewBlog(@PathVariable Long id, Model model, HttpServletRequest request) {
        BlogDetailDto blog = blogService.getBlogDetailDto(id);
        if (blog == null || Boolean.FALSE.equals(blog.getIsPublished())) {
            return "error/404";
        }
        blogService.incrementViews(id, request);

        model.addAttribute("blog", blog);

        // Получаем популярные блоги для отображения в сайдбаре
        Pageable pageable = PageRequest.of(0, 3, Sort.by("views").descending());
        BlogListDto popularBlogs = blogService.getPublishedBlogs(pageable);
        model.addAttribute("popularBlogs", popularBlogs.getBlogs());

        return "blogs/detail";
    }
}