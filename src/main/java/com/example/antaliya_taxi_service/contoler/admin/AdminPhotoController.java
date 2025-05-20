package com.example.antaliya_taxi_service.contoler.admin;

import com.example.antaliya_taxi_service.dto.PhotoDto;
import com.example.antaliya_taxi_service.exception.ResourceNotFoundException;
import com.example.antaliya_taxi_service.service.AlbumService;
import com.example.antaliya_taxi_service.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin/photos")
@RequiredArgsConstructor
@Slf4j
public class AdminPhotoController {

    private final PhotoService photoService;
    private final AlbumService albumService;

    /**
     * Страница просмотра отдельной фотографии
     */
    @GetMapping("/{id}")
    public String viewPhoto(@PathVariable Long id, Model model) {
        try {
            PhotoDto.Response photo = photoService.getPhotoById(id);
            model.addAttribute("photo", photo);

            // Получаем информацию об альбоме, к которому принадлежит фотография
            // В DTO можно добавить albumId для упрощения
            Long albumId = getAlbumIdFromPhoto(photo);
            model.addAttribute("albumId", albumId);

            return "admin/photos/view";
        } catch (ResourceNotFoundException e) {
            return "redirect:/admin/albums?error=photoNotFound";
        }
    }

    /**
     * Страница редактирования фотографии
     */
    @GetMapping("/{id}/edit")
    public String editPhotoForm(@PathVariable Long id, Model model) {
        try {
            PhotoDto.Response photo = photoService.getPhotoById(id);
            model.addAttribute("photo", photo);

            // Получаем информацию об альбоме, к которому принадлежит фотография
            Long albumId = getAlbumIdFromPhoto(photo);
            model.addAttribute("albumId", albumId);

            return "admin/photos/edit";
        } catch (ResourceNotFoundException e) {
            return "redirect:/admin/albums?error=photoNotFound";
        }
    }

    /**
     * Обработка формы редактирования фотографии
     */
    @PostMapping("/{id}/edit")
    public String updatePhoto(@PathVariable Long id,
                              @RequestParam(required = false) String title,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false, name = "image") MultipartFile image,
                              RedirectAttributes redirectAttributes) {
        try {
            // Получаем текущие данные фотографии для определения albumId
            PhotoDto.Response currentPhoto = photoService.getPhotoById(id);
            Long albumId = getAlbumIdFromPhoto(currentPhoto);

            // Создаем объект запроса обновления
            PhotoDto.UpdateRequest request = PhotoDto.UpdateRequest.builder()
                    .title(title)
                    .description(description)
                    .image(image)
                    .build();

            // Обновляем фотографию
            photoService.updatePhoto(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Фотография успешно обновлена");
            return "redirect:/admin/photos/" + id;
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Фотография не найдена");
            return "redirect:/admin/albums";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при загрузке изображения: " + e.getMessage());
            return "redirect:/admin/photos/" + id + "/edit";
        }
    }

    /**
     * Удаление фотографии
     */
    @PostMapping("/{id}/delete")
    public String deletePhoto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Получаем информацию о фотографии для определения albumId перед удалением
            PhotoDto.Response photo = photoService.getPhotoById(id);
            Long albumId = getAlbumIdFromPhoto(photo);

            // Удаляем фотографию
            photoService.deletePhoto(id);
            redirectAttributes.addFlashAttribute("successMessage", "Фотография успешно удалена");
            return "redirect:/admin/albums/" + albumId;
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Фотография не найдена");
            return "redirect:/admin/albums";
        }
    }

    /**
     * Вспомогательный метод для получения ID альбома из фото
     * Примечание: лучше добавить albumId в PhotoDto.Response
     */
    private Long getAlbumIdFromPhoto(PhotoDto.Response photo) {

        try {
          albumService.getAlbumById(photo.getAlbumId());
            return photo.getAlbumId();
        } catch (Exception e) {
            log.error("Не удалось определить albumId для фотографии с id: {}", photo.getId(), e);
            return null;
        }
    }
}