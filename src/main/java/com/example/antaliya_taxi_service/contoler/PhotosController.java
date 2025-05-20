package com.example.antaliya_taxi_service.contoler;

import com.example.antaliya_taxi_service.dto.PhotoDto;
import com.example.antaliya_taxi_service.service.AlbumService;
import com.example.antaliya_taxi_service.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/photos")
@RequiredArgsConstructor
@Slf4j
public class PhotosController {

    private final PhotoService photoService;

    @GetMapping
    public String showAllPhotos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        // Создаем объект пагинации с сортировкой
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Получаем страницу с фотографиями
        Page<PhotoDto.Response> photosPage = photoService.getAllActivePhotos(pageable);
        List<PhotoDto.Response> photos = photosPage.getContent();

        // Добавляем данные на страницу
        model.addAttribute("photos", photos);
        model.addAttribute("size", size); // Изменено на size вместо photosSize
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", photosPage.getTotalPages());
        model.addAttribute("totalItems", photosPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        // Добавляем метаданные для SEO
        model.addAttribute("pageTitle", "Все фотографии | AntalyaTaxi");
        model.addAttribute("pageDescription",
                "Просмотрите все фотографии наших трансферов, экскурсий и достопримечательностей Анталии и Турции");

        return "photos/index";
    }

    /**
     * Отображение фотографий по тегу/категории
     * Можно добавить позже, если понадобится фильтрация
     */
    @GetMapping("/tag")
    public String showPhotosByTag(
            @RequestParam String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            Model model) {

        // Реализация будет добавлена, если потребуется
        // Можно использовать тот же шаблон, что и для всех фотографий,
        // но с добавлением параметра фильтрации

        return "photos/index";
    }
}