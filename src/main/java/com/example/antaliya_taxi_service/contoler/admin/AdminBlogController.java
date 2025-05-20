package com.example.antaliya_taxi_service.contoler.admin;

import com.example.antaliya_taxi_service.dto.blog.BlogDto;
import com.example.antaliya_taxi_service.dto.blog.BlogListDto;
import com.example.antaliya_taxi_service.dto.blog.CreateBlogDto;
import com.example.antaliya_taxi_service.dto.blog.UpdateBlogDto;
import com.example.antaliya_taxi_service.service.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/blog")
public class AdminBlogController {

    private final BlogService blogService;

    @GetMapping()
    public String adminBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("uploadDate").descending());
        BlogListDto blogs = blogService.getAllBlogs(pageable);

        model.addAttribute("blogs", blogs);
        model.addAttribute("currentPage", page);

        return "admin/blogs/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("createBlogDto")) {
            model.addAttribute("createBlogDto", new CreateBlogDto());
        }
        return "admin/blogs/create";
    }

    /**
     * Обработка формы создания блога
     */
    @PostMapping("/create")
    public String createBlog(
            @Valid @ModelAttribute("createBlogDto") CreateBlogDto createBlogDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) throws IOException {

        // Проверка валидации
        if (bindingResult.hasErrors()) {
            // Если есть ошибки валидации, возвращаем на форму с сообщениями об ошибках
            return "admin/blogs/create";
        }

        BlogDto createdBlog = blogService.createBlog(createBlogDto);
        redirectAttributes.addFlashAttribute("success", "Блог успешно создан!");

        return "redirect:/admin/blog";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        BlogDto blog = blogService.findById(id);
        if (blog == null) {
            return "error/404";
        }

        if (!model.containsAttribute("updateBlogDto")) {
            // Создаем UpdateBlogDto на основе найденного блога
            UpdateBlogDto updateBlogDto = UpdateBlogDto.builder()
                    .id(blog.getId())
                    .title(blog.getTitle())
                    .description(blog.getDescription())
                    .shotDescription(blog.getShotDescription())
                    .isPublished(blog.getIsPublished())
                    .build();

            model.addAttribute("updateBlogDto", updateBlogDto);
        }

        // Добавляем оригинальный блог для отображения изображения и метаданных
        model.addAttribute("blog", blog);

        return "admin/blogs/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateBlog(
            @PathVariable Long id,
            @Valid @ModelAttribute("updateBlogDto") UpdateBlogDto updateBlogDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) throws IOException {

        if (bindingResult.hasErrors()) {
            // Если есть ошибки валидации, нам нужен оригинальный блог для отображения
            model.addAttribute("blog", blogService.findById(id));
            return "admin/blogs/edit";
        }

        BlogDto updatedBlog = blogService.updateBlog(updateBlogDto);
        redirectAttributes.addFlashAttribute("success", "Блог успешно обновлен!");

        return "redirect:/admin/blog";
    }

    /**
     * Удаление блога
     */
    @PostMapping("/delete/{id}")
    public String deleteBlog(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        blogService.deleteBlog(id);
        redirectAttributes.addFlashAttribute("success", "Блог успешно удален!");

        return "redirect:/admin/blogs";
    }

    /**
     * Изменение статуса публикации
     */
    @PostMapping("/toggle-publish/{id}")
    public String togglePublishStatus(
            @PathVariable Long id,
            @RequestParam Boolean publish,
            RedirectAttributes redirectAttributes) throws IOException {

        BlogDto blog = blogService.findById(id);
        if (blog == null) {
            return "error/404";
        }

        UpdateBlogDto updateBlogDto = UpdateBlogDto.builder()
                .id(id)
                .title(blog.getTitle())
                .description(blog.getDescription())
                .shotDescription(blog.getShotDescription())
                .updateImage(false)
                .isPublished(publish)
                .build();

        blogService.updateBlog(updateBlogDto);
        redirectAttributes.addFlashAttribute("success",
                publish ? "Блог опубликован!" : "Блог снят с публикации!");

        return "redirect:/admin/blogs";
    }
}
