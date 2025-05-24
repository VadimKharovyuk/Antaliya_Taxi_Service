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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
     * Форма бронирования тура
     */
    @GetMapping("/tour/{tourId}")
    public String bookTour(@PathVariable("tourId") Long tourId, Model model) {
        try {
            TourDto tour = tourService.findTourById(tourId);

            // Увеличиваем счетчик просмотров
//            tourService.incrementViews(tourId);

            List<VehicleCardDto> availableVehicles = vehicleService.getActiveVehicles();

            BookingCreateDTO bookingDTO = BookingCreateDTO.builder()
                    .tourId(tourId)
//                    .tripType(TripType.TOUR)
                    .departureDateTime(LocalDateTime.now().plusHours(24)) // По умолчанию завтра
                    .adultCount(1)
                    .childCount(0)
                    .hasReturnTransfer(false)
                    .needsChildSeat(false)
                    .needsNameGreeting(false)
                    .build();

            model.addAttribute("tour", tour);
            model.addAttribute("vehicles", availableVehicles);
            model.addAttribute("bookingDTO", bookingDTO);
            model.addAttribute("tripTypes", TripType.values());
            model.addAttribute("pageTitle", "Бронирование тура: " + tour.getTitle());

            return "booking/tour-form";

        } catch (Exception e) {
            log.error("Ошибка при загрузке формы бронирования тура {}: {}", tourId, e.getMessage());
            model.addAttribute("error", "Не удалось загрузить форму бронирования");
            return "error/general";
        }
    }

    /**
     * Форма бронирования трансфера/такси
     */
    @GetMapping("/transfer")
    public String bookTransfer(@RequestParam(value = "routeId", required = false)
                                   Long routeId, Model model,
                               @RequestParam(required = false) Currency displayCurrency) {
        try {
            List<VehicleCardDto> availableVehicles = vehicleService.getActiveVehicles();
            List<RouteDto.DestinationCard> popularRoutes = routeService.getPopularRoutes(displayCurrency);

            BookingCreateDTO bookingDTO = BookingCreateDTO.builder()
                    .routeId(routeId)
//                    .tripType(TripType.TRANSFER)
                    .departureDateTime(LocalDateTime.now().plusHours(2)) // По умолчанию через 2 часа
                    .adultCount(1)
                    .childCount(0)
                    .hasReturnTransfer(false)
                    .needsChildSeat(false)
                    .needsNameGreeting(false)
                    .build();

            // Если выбран конкретный маршрут, заполняем данные
            if (routeId != null) {
//                RouteDto route = routeService.findById(routeId);
//                bookingDTO.setPickupLocation(route.getPickupLocation());
//                bookingDTO.setDropoffLocation(route.getDropoffLocation());
//                model.addAttribute("selectedRoute", route);
            }

            model.addAttribute("vehicles", availableVehicles);
            model.addAttribute("routes", popularRoutes);
            model.addAttribute("bookingDTO", bookingDTO);
            model.addAttribute("tripTypes", TripType.values());
            model.addAttribute("pageTitle", "Бронирование трансфера");

            return "booking/transfer-form";

        } catch (Exception e) {
            log.error("Ошибка при загрузке формы бронирования трансфера: {}", e.getMessage());
            model.addAttribute("error", "Не удалось загрузить форму бронирования");
            return "error/general";
        }
    }

    /**
     * Обработка создания бронирования
     */
    @PostMapping("/create")
    public String createBooking(@Valid @ModelAttribute("bookingDTO") BookingCreateDTO bookingDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            // Если есть ошибки валидации
            if (bindingResult.hasErrors()) {
                log.warn("Ошибки валидации при создании бронирования: {}", bindingResult.getAllErrors());

                // Перезагружаем необходимые данные для формы
//                reloadFormData(bookingDTO, model);
                return getFormViewName(bookingDTO);
            }

            // Создаем бронирование
            BookingResponseDTO booking = bookingService.create(bookingDTO);

            log.info("Успешно создано бронирование {} для клиента {}",
                    booking.getBookingReference(), booking.getCustomerName());

            redirectAttributes.addFlashAttribute("success",
                    "Бронирование успешно создано! Номер бронирования: " + booking.getBookingReference());
            redirectAttributes.addFlashAttribute("booking", booking);

            return "redirect:/booking/success/" + booking.getBookingReference();

        } catch (BookingValidationException e) {
            log.error("Ошибка валидации бронирования: {}", e.getMessage());
//            model.addAttribute("validationErrors", e.getErrors());
//            reloadFormData(bookingDTO, model);
            return getFormViewName(bookingDTO);

        } catch (VehicleNotAvailableException e) {
            log.error("Автомобиль недоступен: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
//            reloadFormData(bookingDTO, model);
            return getFormViewName(bookingDTO);

        } catch (PassengerCapacityExceededException e) {
            log.error("Превышена вместимость: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
//            reloadFormData(bookingDTO, model);
            return getFormViewName(bookingDTO);

        } catch (Exception e) {
            log.error("Неожиданная ошибка при создании бронирования: {}", e.getMessage(), e);
            model.addAttribute("error", "Произошла ошибка при создании бронирования. Попробуйте позже.");
//            reloadFormData(bookingDTO, model);
            return getFormViewName(bookingDTO);
        }
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

//    /**
//     * Вспомогательные методы
//     */
//    private void reloadFormData(BookingCreateDTO bookingDTO, Model model) {
//        try {
//            List<VehicleCardDto> availableVehicles = vehicleService.getAvailableVehicles();
//            model.addAttribute("vehicles", availableVehicles);
//            model.addAttribute("tripTypes", TripType.values());
//
//            if (bookingDTO.getTourId() != null) {
//                TourDto tour = tourService.findTourById(bookingDTO.getTourId());
//                model.addAttribute("tour", tour);
//            }
//
//            if (bookingDTO.getRouteId() != null) {
//                RouteDto route = routeService.findById(bookingDTO.getRouteId());
//                model.addAttribute("selectedRoute", route);
//            }
//
//            if (TripType.TRANSFER.equals(bookingDTO.getTripType())) {
//                List<RouteDto> popularRoutes = routeService.getPopularRoutes();
//                model.addAttribute("routes", popularRoutes);
//            }
//
//        } catch (Exception e) {
//            log.error("Ошибка при перезагрузке данных формы: {}", e.getMessage());
//        }
//    }

    private String getFormViewName(BookingCreateDTO bookingDTO) {
        if (bookingDTO.getTourId() != null) {
            return "booking/tour-form";
        }
        return "booking/transfer-form";
    }
}