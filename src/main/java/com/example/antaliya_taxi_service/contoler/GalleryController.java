package com.example.antaliya_taxi_service.contoler;

import com.example.antaliya_taxi_service.dto.AlbumDto;
import com.example.antaliya_taxi_service.exception.ResourceNotFoundException;
import com.example.antaliya_taxi_service.service.AlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/gallery")
@RequiredArgsConstructor
@Slf4j
public class GalleryController {

    private final AlbumService albumService;

    /**
     * Главная страница галереи - показывает все активные альбомы
     */
    @GetMapping
    public String showGallery(Model model) {
        List<AlbumDto.Item> albums = albumService.getAllActiveAlbums();
        model.addAttribute("albums", albums);

        // Добавляем метаданные для SEO
        model.addAttribute("pageTitle", "Фотогалерея | AntalyaTaxi");
        model.addAttribute("pageDescription", "Просмотрите нашу фотогалерею с изображениями трансферов и экскурсий по Анталии и Турции");

        return "gallery/index";
    }

    /**
     * Страница просмотра отдельного альбома
     */
    @GetMapping("/albums/{id}")
    public String viewAlbum(@PathVariable Long id, Model model) {
        try {
            AlbumDto.Response album = albumService.getAlbumById(id);

            // Проверяем, что альбом активен
            if (!album.isActive()) {
                return "redirect:/gallery?error=albumNotActive";
            }

            model.addAttribute("album", album);

            // Добавляем метаданные для SEO
            model.addAttribute("pageTitle", album.getTitle() + " | Фотогалерея | AntalyaTaxi");
            model.addAttribute("pageDescription", album.getDescription());

            return "gallery/album";
        } catch (ResourceNotFoundException e) {
            log.warn("Попытка просмотра несуществующего альбома с id: {}", id);
            return "redirect:/gallery?error=albumNotFound";
        }
    }

    /**
     * Страница для просмотра отдельной фотографии в полном размере
     */
    @GetMapping("/photos/{id}")
    public String viewPhoto(@PathVariable Long id, Model model) {
        // Здесь можно реализовать просмотр фотографии на отдельной странице
        // Но поскольку в сервисе нет метода получения фотографии по ID,
        // это будет заглушка для будущей реализации

        model.addAttribute("photoId", id);
        model.addAttribute("pageTitle", "Просмотр фотографии | AntalyaTaxi");

        return "gallery/photo";
    }

    /**
     * Страница с информацией о галерее
     */
    @GetMapping("/about")
    public String aboutGallery(Model model) {
        model.addAttribute("pageTitle", "О нашей галерее | AntalyaTaxi");
        model.addAttribute("pageDescription", "Информация о нашей фотогалерее, процессе создания фотографий и использовании");

        return "gallery/about";
    }
}