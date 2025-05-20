package com.example.antaliya_taxi_service.contoler.admin;

import com.example.antaliya_taxi_service.dto.AlbumDto;
import com.example.antaliya_taxi_service.exception.ResourceNotFoundException;
import com.example.antaliya_taxi_service.service.AlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin/albums")
@RequiredArgsConstructor
@Slf4j
public class AdminAlbumController {

    private final AlbumService albumService;

    /**
     * Страница со списком всех альбомов
     */
    @GetMapping
    public String albumsList(Model model) {
        model.addAttribute("albums", albumService.getAllAlbums());
        return "admin/albums/list";
    }

    /**
     * Страница создания нового альбома
     */
    @GetMapping("/create")
    public String createAlbumForm(Model model) {
        model.addAttribute("album", new AlbumDto.Request());
        return "admin/albums/create";
    }

    /**
     * Обработка формы создания альбома
     */
    @PostMapping("/create")
    public String createAlbum(@ModelAttribute AlbumDto.Request request,
                              RedirectAttributes redirectAttributes) {
        try {
            AlbumDto.Response album = albumService.createAlbum(request);
            redirectAttributes.addFlashAttribute("successMessage", "Альбом успешно создан");
            return "redirect:/admin/albums/" + album.getId();
        } catch (IOException e) {
            log.error("Ошибка при загрузке изображения для альбома", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при загрузке изображения: " + e.getMessage());
            return "redirect:/admin/albums/create";
        }
    }

    /**
     * Страница просмотра альбома
     */
    @GetMapping("/{id}")
    public String viewAlbum(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("album", albumService.getAlbumById(id));
            return "admin/albums/view";
        } catch (ResourceNotFoundException e) {
            return "redirect:/admin/albums?error=albumNotFound";
        }
    }

    /**
     * Страница редактирования альбома
     */
    @GetMapping("/{id}/edit")
    public String editAlbumForm(@PathVariable Long id, Model model) {
        try {
            // Получаем данные альбома для заполнения формы
            AlbumDto.Response album = albumService.getAlbumById(id);
            model.addAttribute("album", album);
            model.addAttribute("albumId", id);
            return "admin/albums/edit";
        } catch (ResourceNotFoundException e) {
            return "redirect:/admin/albums?error=albumNotFound";
        }
    }

    /**
     * Обработка формы редактирования альбома
     */
    @PostMapping("/{id}/edit")
    public String updateAlbum(@PathVariable Long id,
                              @RequestParam(required = false) String title,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false, name = "coverImage") MultipartFile coverImage,
                              @RequestParam(required = false, defaultValue = "false") boolean active,
                              RedirectAttributes redirectAttributes) {
        try {
            // Создаем объект запроса обновления
            AlbumDto.UpdateRequest request = AlbumDto.UpdateRequest.builder()
                    .title(title)
                    .description(description)
                    .coverImage(coverImage)
                    .active(active)
                    .build();

            // Обновляем альбом
            albumService.updateAlbum(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Альбом успешно обновлен");
            return "redirect:/admin/albums/" + id;
        } catch (ResourceNotFoundException e) {
            return "redirect:/admin/albums?error=albumNotFound";
        } catch (IOException e) {
            log.error("Ошибка при загрузке изображения для альбома", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при загрузке изображения: " + e.getMessage());
            return "redirect:/admin/albums/" + id + "/edit";
        }
    }

    /**
     * Удаление альбома
     */
    @PostMapping("/{id}/delete")
    public String deleteAlbum(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            albumService.deleteAlbum(id);
            redirectAttributes.addFlashAttribute("successMessage", "Альбом успешно удален");
            return "redirect:/admin/albums";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Альбом не найден");
            return "redirect:/admin/albums";
        }
    }

    /**
     * Изменение статуса активности альбома
     */
    @PostMapping("/{id}/toggle-active")
    public String toggleAlbumActivity(@PathVariable Long id,
                                      @RequestParam boolean active,
                                      RedirectAttributes redirectAttributes) {
        try {
            albumService.updateAlbumActivity(id, active);
            String status = active ? "активирован" : "деактивирован";
            redirectAttributes.addFlashAttribute("successMessage", "Альбом успешно " + status);
            return "redirect:/admin/albums/" + id;
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Альбом не найден");
            return "redirect:/admin/albums";
        }
    }

    /**
     * Страница добавления фотографии в альбом
     */
    @GetMapping("/{id}/add-photo")
    public String addPhotoForm(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("album", albumService.getAlbumById(id));
            return "admin/albums/add-photo";
        } catch (ResourceNotFoundException e) {
            return "redirect:/admin/albums?error=albumNotFound";
        }
    }

    /**
     * Обработка формы добавления фотографии
     */
    @PostMapping("/{id}/add-photo")
    public String addPhoto(@PathVariable Long id,
                           @RequestParam("image") MultipartFile image,
                           @RequestParam String title,
                           @RequestParam(required = false) String description,
                           RedirectAttributes redirectAttributes) {
        try {
            albumService.addPhotoToAlbum(id, image, title, description);
            redirectAttributes.addFlashAttribute("successMessage", "Фотография успешно добавлена");
            return "redirect:/admin/albums/" + id;
        } catch (ResourceNotFoundException e) {
            return "redirect:/admin/albums?error=albumNotFound";
        } catch (IOException e) {
            log.error("Ошибка при загрузке фотографии", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при загрузке фотографии: " + e.getMessage());
            return "redirect:/admin/albums/" + id + "/add-photo";
        }
    }

    /**
     * Удаление фотографии из альбома
     */
    @PostMapping("/{albumId}/photos/{photoId}/delete")
    public String removePhoto(@PathVariable Long albumId,
                              @PathVariable Long photoId,
                              RedirectAttributes redirectAttributes) {
        try {
            albumService.removePhotoFromAlbum(albumId, photoId);
            redirectAttributes.addFlashAttribute("successMessage", "Фотография успешно удалена");
            return "redirect:/admin/albums/" + albumId;
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/albums/" + albumId;
        }
    }
}