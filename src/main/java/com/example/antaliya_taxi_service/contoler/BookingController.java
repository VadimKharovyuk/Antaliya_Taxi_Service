package com.example.antaliya_taxi_service.contoler;

import com.example.antaliya_taxi_service.dto.Booking.BookingCreateDTO;
import com.example.antaliya_taxi_service.dto.Booking.BookingResponseDTO;
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
import java.time.LocalDateTime;
import java.util.List;

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
     * ШАГ 2: Форма бронирования с уже выбранным автомобилем
     */
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

            // Предзаполняем форму бронирования
            BookingCreateDTO bookingDTO = BookingCreateDTO.builder()
                    .tourId(tourId)
                    .vehicleId(vehicleId) // Автомобиль уже выбран!
                    .tripType(TripType.TOUR)
                    .departureDateTime(selectedDate)
                    .adultCount(passengers)
                    .childCount(0)
                    .hasReturnTransfer(false)
                    .needsChildSeat(false)
                    .needsNameGreeting(false)
                    .build();

            // Рассчитываем предварительную стоимость
            BigDecimal estimatedPrice = bookingService.calculateEstimatedPrice(bookingDTO);

            model.addAttribute("tour", tour);
            model.addAttribute("selectedVehicle", vehicle);
            model.addAttribute("bookingDTO", bookingDTO);
            model.addAttribute("estimatedPrice", estimatedPrice);
            model.addAttribute("pageTitle", "Бронирование тура: " + tour.getTitle());

            return "booking/tour-form";

        } catch (EntityNotFoundException e) {
            log.error("Тур или автомобиль не найден. Tour ID: {}, Vehicle ID: {}", tourId, vehicleId);
            redirectAttributes.addFlashAttribute("error", "Тур или автомобиль не найден");
            return "redirect:/tours";
        } catch (Exception e) {
            log.error("Ошибка при загрузке формы бронирования: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Не удалось загрузить форму бронирования");
            return "redirect:/booking/tour/" + tourId;
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
     * Обработка создания бронирования (без изменений)
     */
//    @PostMapping("/create")
//    public String createBooking(@Valid @ModelAttribute("bookingDTO") BookingCreateDTO bookingDTO,
//                                BindingResult bindingResult,
//                                RedirectAttributes redirectAttributes,
//                                Model model) {
//        bookingService.create(bookingDTO);
//
//        return "redirect:/booking/success/" + bookingDTO.getBookingReference();
//    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===

    private LocalDateTime parseDate(String dateParam) {
        if (dateParam != null && !dateParam.trim().isEmpty()) {
            try {
                return LocalDateTime.parse(dateParam);
            } catch (Exception e) {
                log.warn("Не удалось распарсить дату: {}", dateParam);
            }
        }
        return LocalDateTime.now().plusHours(24); // По умолчанию завтра
    }

//    private final BookingService bookingService;
//    private final VehicleService vehicleService;
//    private final TourService tourService;
//    private final RouteService routeService;
//
//    @GetMapping("/tour/{tourId}")
//    public String bookTour(@PathVariable("tourId") Long tourId,
//                           Model model,
//                           RedirectAttributes redirectAttributes) {
//        try {
//            TourDto tour = tourService.findTourById(tourId);
//
//            // Увеличиваем счетчик просмотров (используем обычный метод, не async)
//            tourService.incrementViewsAsync(tourId);
//
//            // ИСПРАВЛЕНО: используем правильный метод
//            List<VehicleCardDto> availableVehicles = vehicleService.getActiveVehicles();
//
//            BookingCreateDTO bookingDTO = BookingCreateDTO.builder()
//                    .tourId(tourId)
//                    .tripType(TripType.TOUR)
//                    .departureDateTime(LocalDateTime.now().plusHours(24))
//                    .adultCount(1)
//                    .childCount(0)
//                    .hasReturnTransfer(false)
//                    .needsChildSeat(false)
//                    .needsNameGreeting(false)
//                    .build();
//
//            model.addAttribute("tour", tour);
//            model.addAttribute("vehicles", availableVehicles);
//            model.addAttribute("bookingDTO", bookingDTO);
//            model.addAttribute("tripTypes", TripType.values());
//            model.addAttribute("pageTitle", "Бронирование тура: " + tour.getTitle());
//
//            return "booking/tour-form";
//
//        } catch (EntityNotFoundException e) {
//            log.error("Тур не найден с ID: {}", tourId);
//            redirectAttributes.addFlashAttribute("error", "Тур не найден");
//            return "redirect:/tours";
//
//        } catch (Exception e) {
//            log.error("Ошибка при загрузке формы бронирования тура {}: {}", tourId, e.getMessage(), e);
//            redirectAttributes.addFlashAttribute("error", "Не удалось загрузить форму бронирования. Попробуйте позже.");
//            return "redirect:/tours";
//        }
//    }
//
//    @GetMapping("/transfer")
//    public String bookTransfer(@RequestParam(value = "routeId", required = false) Long routeId,
//                               @RequestParam(required = false) Currency displayCurrency ,
//                               Model model,
//                               RedirectAttributes redirectAttributes) {
//        try {
//            List<VehicleCardDto> availableVehicles = vehicleService.getActiveVehicles();
//            List<RouteDto.DestinationCard> popularRoutes = routeService.getPopularRoutes(displayCurrency);
//
//            BookingCreateDTO bookingDTO = BookingCreateDTO.builder()
//                    .routeId(routeId)
//                    .tripType(TripType.TRANSFER)
//                    .departureDateTime(LocalDateTime.now().plusHours(2))
//                    .adultCount(1)
//                    .childCount(0)
//                    .hasReturnTransfer(false)
//                    .needsChildSeat(false)
//                    .needsNameGreeting(false)
//                    .build();
//
//            // Если выбран конкретный маршрут, заполняем данные
//            if (routeId != null) {
//                try {
//                    RouteDto.Response route = routeService.findById(routeId);
//                    bookingDTO.setPickupLocation(route.getPickupLocation());
//                    bookingDTO.setDropoffLocation(route.getDropoffLocation());
//                    model.addAttribute("selectedRoute", route);
//                } catch (EntityNotFoundException e) {
//                    log.warn("Маршрут не найден с ID: {}", routeId);
//                    // Продолжаем без предзаполнения маршрута
//                }
//            }
//
//            model.addAttribute("vehicles", availableVehicles);
//            model.addAttribute("routes", popularRoutes);
//            model.addAttribute("bookingDTO", bookingDTO);
//            model.addAttribute("tripTypes", TripType.values());
//            model.addAttribute("pageTitle", "Бронирование трансфера");
//
//            return "booking/transfer-form";
//
//        } catch (Exception e) {
//            log.error("Ошибка при загрузке формы бронирования трансфера: {}", e.getMessage(), e);
//            redirectAttributes.addFlashAttribute("error", "Не удалось загрузить форму бронирования");
//            return "redirect:/";
//        }
//    }

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
     * Страница успешного бронирования
     */
    @GetMapping("/success/{bookingReference}")
    public String bookingSuccess(@PathVariable("bookingReference") String bookingReference, Model model) {
        model.addAttribute("bookingReference", bookingReference);
        model.addAttribute("pageTitle", "Бронирование подтверждено");
        return "booking/success";
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
            // Здесь будет логика поиска бронирования
            // BookingResponseDTO booking = bookingService.findByReferenceAndEmail(bookingReference, customerEmail);

            redirectAttributes.addFlashAttribute("message",
                    "Функция поиска бронирования в разработке");
            return "redirect:/booking/search";

        } catch (Exception e) {
            log.error("Ошибка при поиске бронирования: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Бронирование не найдено");
            return "redirect:/booking/search";
        }
    }


    private String getFormViewName(BookingCreateDTO bookingDTO) {
        if (bookingDTO.getTourId() != null) {
            return "booking/tour-form";
        }
        return "booking/transfer-form";
    }
}