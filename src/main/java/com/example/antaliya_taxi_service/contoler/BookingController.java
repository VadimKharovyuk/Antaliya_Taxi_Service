package com.example.antaliya_taxi_service.contoler;

import com.example.antaliya_taxi_service.dto.Booking.BookingCreateDTO;
import com.example.antaliya_taxi_service.dto.Booking.BookingResponseDTO;
import com.example.antaliya_taxi_service.dto.Booking.TourBookingCreateDTO;
import com.example.antaliya_taxi_service.dto.RouteDto;
import com.example.antaliya_taxi_service.dto.tour.TourDto;
import com.example.antaliya_taxi_service.dto.vehicle.VehicleCardDto;
import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.enums.TripType;
import com.example.antaliya_taxi_service.exception.BookingValidationException;
import com.example.antaliya_taxi_service.exception.PassengerCapacityExceededException;
import com.example.antaliya_taxi_service.exception.VehicleNotAvailableException;
import com.example.antaliya_taxi_service.service.BookingService;
import com.example.antaliya_taxi_service.service.RouteService;
import com.example.antaliya_taxi_service.service.TourService;
import com.example.antaliya_taxi_service.service.VehicleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/booking")
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final TourService tourService;
    private final RouteService routeService;

    /**
     * ШАГ 1: Показываем страницу выбора автомобиля для тура
     */
    @GetMapping("/tour/{tourId}")
    public String selectVehicleForTour(@PathVariable("tourId") Long tourId,
                                       @RequestParam(value = "date", required = false) String dateParam,
                                       @RequestParam(value = "passengers", defaultValue = "1") Integer passengers,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        try {
            TourDto tour = tourService.findTourById(tourId);
            tourService.incrementViewsAsync(tourId);

            // Определяем дату для проверки доступности
            LocalDateTime selectedDate = parseDate(dateParam);

            // Получаем доступные автомобили на выбранную дату
            List<VehicleCardDto> availableVehicles = vehicleService.getAvailableVehicles(selectedDate, passengers);

            model.addAttribute("tour", tour);
            model.addAttribute("vehicles", availableVehicles);
            model.addAttribute("selectedDate", selectedDate);
            model.addAttribute("passengers", passengers);
            model.addAttribute("pageTitle", "Выбор автомобиля для тура: " + tour.getTitle());

            return "booking/vehicle-selection";

        } catch (EntityNotFoundException e) {
            log.error("Тур не найден с ID: {}", tourId);
            redirectAttributes.addFlashAttribute("error", "Тур не найден");
            return "redirect:/tours";
        } catch (Exception e) {
            log.error("Ошибка при загрузке выбора автомобиля для тура {}: {}", tourId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Не удалось загрузить список автомобилей");
            return "redirect:/tours";
        }
    }



    /**
     * AJAX: Получить доступные автомобили для даты/времени
     */
    @GetMapping("/tour/{tourId}/vehicles")
    @ResponseBody
    public ResponseEntity<List<VehicleCardDto>> getAvailableVehicles(
            @PathVariable("tourId") Long tourId,
            @RequestParam("date") String dateParam,
            @RequestParam(value = "passengers", defaultValue = "1") Integer passengers) {

        try {
            LocalDateTime selectedDate = parseDate(dateParam);
            List<VehicleCardDto> vehicles = vehicleService.getAvailableVehicles(selectedDate, passengers);
            return ResponseEntity.ok(vehicles);
        } catch (Exception e) {
            log.error("Ошибка при получении доступных автомобилей: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    /**
     * Метод для парсинга даты из строки
     */
    private LocalDateTime parseDate(String dateParam) {
        if (dateParam == null || dateParam.trim().isEmpty()) {
            log.warn("Передан пустой параметр даты, используем завтрашний день");
            return LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        }

        try {
            // Попытка парсинга в формате ISO (yyyy-MM-ddTHH:mm)
            return LocalDateTime.parse(dateParam.trim());
        } catch (DateTimeParseException e1) {
            try {
                // Попытка парсинга в формате yyyy-MM-dd HH:mm
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return LocalDateTime.parse(dateParam.trim(), formatter);
            } catch (DateTimeParseException e2) {
                try {
                    // Попытка парсинга только даты (добавляем время 10:00)
                    LocalDate date = LocalDate.parse(dateParam.trim());
                    return date.atTime(10, 0);
                } catch (DateTimeParseException e3) {
                    log.error("Не удалось распарсить дату: {}. Ошибки: {}, {}, {}",
                            dateParam, e1.getMessage(), e2.getMessage(), e3.getMessage());
                    throw new IllegalArgumentException("Некорректный формат даты: " + dateParam +
                            ". Ожидаемые форматы: yyyy-MM-ddTHH:mm, yyyy-MM-dd HH:mm, yyyy-MM-dd");
                }
            }
        }
    }
    @GetMapping("/tour/{tourId}/vehicle/{vehicleId}")
    public String bookTourWithVehicle(@PathVariable("tourId") Long tourId,
                                      @PathVariable("vehicleId") Long vehicleId,
                                      @RequestParam(value = "date", required = false) String dateParam,
                                      @RequestParam(value = "passengers", defaultValue = "1") Integer passengers,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        try {
            // Получаем данные тура и автомобиля
            TourDto tour = tourService.findTourById(tourId);
            VehicleCardDto vehicle = vehicleService.getVehicleById(vehicleId);

            // Проверяем доступность автомобиля
            LocalDateTime selectedDate = parseDate(dateParam);
            if (!vehicleService.isVehicleAvailable(vehicleId, selectedDate)) {
                redirectAttributes.addFlashAttribute("error", "Выбранный автомобиль больше не доступен на это время");
                return "redirect:/booking/tour/" + tourId;
            }

            // НОВОЕ: Создаем специализированное DTO для тура
            TourBookingCreateDTO tourBookingDTO = TourBookingCreateDTO.builder()
                    .tourId(tourId)
                    .vehicleId(vehicleId)
                    .tourDateTime(selectedDate)
                    .adultCount(passengers)
                    .childCount(0)
                    .needsChildSeat(false)
                    .needsNameGreeting(false)
                    .build();

            // Рассчитываем стоимость тура
            BigDecimal estimatedPrice = bookingService.calculateTourPrice(tourBookingDTO);

            model.addAttribute("tour", tour);
            model.addAttribute("selectedVehicle", vehicle);
            model.addAttribute("tourBookingDTO", tourBookingDTO);
            model.addAttribute("estimatedPrice", estimatedPrice);
            model.addAttribute("pageTitle", "Бронирование тура: " + tour.getTitle());

            return "booking/tour-form";

        } catch (Exception e) {
            log.error("Ошибка при загрузке формы бронирования тура: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Не удалось загрузить форму бронирования");
            return "redirect:/booking/tour/" + tourId;
        }
    }


    @PostMapping("/tour/create")
    public String createTourBooking(@Valid @ModelAttribute("tourBookingDTO") TourBookingCreateDTO tourBookingDTO,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {

        log.info("Создание бронирования тура для клиента: {} на дату: {}",
                tourBookingDTO.getCustomerName(), tourBookingDTO.getTourDateTime());

        try {
            if (bindingResult.hasErrors()) {
                log.warn("Ошибки валидации формы тура: {}", bindingResult.getAllErrors());
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.tourBookingDTO", bindingResult);
                redirectAttributes.addFlashAttribute("tourBookingDTO", tourBookingDTO);
                redirectAttributes.addFlashAttribute("error", "Пожалуйста, исправьте ошибки в форме");

                return String.format("redirect:/booking/tour/%d/vehicle/%d?date=%s&passengers=%d",
                        tourBookingDTO.getTourId(),
                        tourBookingDTO.getVehicleId(),
                        tourBookingDTO.getTourDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        tourBookingDTO.getAdultCount() + tourBookingDTO.getChildCount());
            }

            // Создаем бронирование тура
            BookingResponseDTO createdBooking = bookingService.createTourBooking(tourBookingDTO);

            log.info("Бронирование тура успешно создано: {}", createdBooking.getBookingReference());

            redirectAttributes.addFlashAttribute("success",
                    "Бронирование тура успешно создано! Номер: " + createdBooking.getBookingReference());

            return "redirect:/booking/success/" + createdBooking.getBookingReference();

        } catch (Exception e) {
            log.error("Ошибка при создании бронирования тура: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Не удалось создать бронирование тура");
            return redirectToTourForm(tourBookingDTO);
        }
    }

    private String redirectToTourForm(TourBookingCreateDTO tourBookingDTO) {
        return String.format("redirect:/booking/tour/%d/vehicle/%d?date=%s&passengers=%d",
                tourBookingDTO.getTourId(),
                tourBookingDTO.getVehicleId(),
                tourBookingDTO.getTourDateTime() != null ?
                        tourBookingDTO.getTourDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "",
                tourBookingDTO.getAdultCount() + tourBookingDTO.getChildCount());
    }



    /**
     * Страница успешного бронирования
     */
    @GetMapping("/success/{bookingReference}")
    public String bookingSuccess(@PathVariable("bookingReference") String bookingReference,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Получаем информацию о бронировании для отображения
            BookingResponseDTO booking = bookingService.findByReference(bookingReference);

            model.addAttribute("booking", booking);
            model.addAttribute("pageTitle", "Бронирование подтверждено - " + bookingReference);

            return "booking/success";

        } catch (EntityNotFoundException e) {
            log.error("Бронирование не найдено по номеру: {}", bookingReference);
            redirectAttributes.addFlashAttribute("error", "Бронирование не найдено");
            return "redirect:/";
        } catch (Exception e) {
            log.error("Ошибка при загрузке страницы успеха для {}: {}", bookingReference, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Ошибка при загрузке информации о бронировании");
            return "redirect:/";
        }
    }

    /**
     * AJAX: Проверка доступности автомобиля перед отправкой формы
     */
    @PostMapping("/validate-vehicle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validateVehicleAvailability(
            @RequestBody Map<String, Object> request) {

        try {
            Long vehicleId = Long.valueOf(request.get("vehicleId").toString());
            String dateStr = request.get("departureDateTime").toString();
            LocalDateTime departureDateTime = LocalDateTime.parse(dateStr);

            boolean isAvailable = vehicleService.isVehicleAvailable(vehicleId, departureDateTime);

            Map<String, Object> response = new HashMap<>();
            response.put("available", isAvailable);

            if (!isAvailable) {
                response.put("message", "К сожалению, выбранный автомобиль больше не доступен на это время");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Ошибка при проверке доступности автомобиля: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("available", false);
            response.put("message", "Ошибка при проверке доступности");
            return ResponseEntity.badRequest().body(response);
        }
    }


// === Дополнительные методы для обработки ошибок ===

    /**
     * Глобальный обработчик исключений для контроллера
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFound(EntityNotFoundException e,
                                       RedirectAttributes redirectAttributes) {
        log.error("Сущность не найдена: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("error", "Запрашиваемый объект не найден");
        return "redirect:/tours";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception e,
                                         RedirectAttributes redirectAttributes) {
        log.error("Неожиданная ошибка: {}", e.getMessage(), e);
        redirectAttributes.addFlashAttribute("error", "Произошла ошибка. Попробуйте позже.");
        return "redirect:/";
    }



    /**
     * Поиск бронирования по номеру
     */
    @GetMapping("/search")
    public String searchBooking() {
        return "booking/search";
    }

    @PostMapping("/search")
    public String findBooking(@RequestParam("bookingReference") String bookingReference,
                              @RequestParam("customerEmail") String customerEmail,
                              RedirectAttributes redirectAttributes) {
        try {
            BookingResponseDTO booking = bookingService.findByReferenceAndEmail(bookingReference, customerEmail);

            // Перенаправляем на страницу деталей бронирования
            return "redirect:/booking/details/" + booking.getBookingReference() + "?email=" + customerEmail;

        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Бронирование не найдено. Проверьте правильность номера и email адреса.");
            return "redirect:/booking/search";
        } catch (Exception e) {
            log.error("Ошибка при поиске бронирования: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Произошла ошибка при поиске. Попробуйте еще раз.");
            return "redirect:/booking/search";
        }
    }
    @GetMapping("/details/{bookingReference}")
    public String bookingDetails(@PathVariable("bookingReference") String bookingReference,
                                 @RequestParam(value = "email", required = false) String email,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        log.info("Запрос деталей бронирования: {} с email: {}",
                bookingReference,
                email != null ? email.replaceAll("@.*", "@***") : "null");

        try {
            BookingResponseDTO booking;

            if (email != null && !email.trim().isEmpty()) {
                // Безопасный поиск с проверкой email
                booking = bookingService.findByReferenceAndEmail(bookingReference, email);
                log.info("Найдено бронирование через безопасный поиск");
            } else {
                // Обычный поиск (для админов или внутренних ссылок)
                booking = bookingService.findByReference(bookingReference);
                log.info("Найдено бронирование через обычный поиск");
            }

            // Добавляем данные в модель
            model.addAttribute("booking", booking);
            model.addAttribute("pageTitle", "Бронирование " + bookingReference);

            log.info("Отображение деталей бронирования: ID={}, клиент={}, статус={}",
                    booking.getId(), booking.getCustomerName(), booking.getStatus());

            return "booking/details";

        } catch (EntityNotFoundException e) {
            log.warn("Бронирование не найдено: {} с email: {}", bookingReference, email);
            redirectAttributes.addFlashAttribute("error",
                    "Бронирование не найдено. Проверьте правильность номера бронирования" +
                            (email != null ? " и email адреса" : ""));
            return "redirect:/booking/search";

        } catch (IllegalArgumentException e) {
            log.warn("Неверные параметры запроса: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/booking/search";

        } catch (Exception e) {
            log.error("Неожиданная ошибка при загрузке деталей бронирования {}: {}",
                    bookingReference, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Произошла ошибка при загрузке информации о бронировании. Попробуйте еще раз.");
            return "redirect:/booking/search";
        }
    }



}