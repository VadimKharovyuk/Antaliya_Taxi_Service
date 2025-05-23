package com.example.antaliya_taxi_service.service;



import com.example.antaliya_taxi_service.enums.VehicleClass;
import com.example.antaliya_taxi_service.model.Vehicle;
import org.springframework.stereotype.Service;

@Service
public class PriceService {

    /**
     * Форматирует цену для отображения на основе класса автомобиля
     */
    public String formatPrice(Vehicle vehicle) {
        if (vehicle == null || vehicle.getVehicleClass() == null) {
            return "Цена по запросу";
        }

        return formatPriceByClass(vehicle.getVehicleClass());
    }

    /**
     * Форматирует цену по классу автомобиля
     */
    public String formatPriceByClass(VehicleClass vehicleClass) {
        if (vehicleClass == null) {
            return "Цена по запросу";
        }

        switch (vehicleClass) {
            case STANDARD:
                return "от 30€/час";
            case LUXURY:
                return "от 45€/час";
            case PREMIUM:
                return "от 60€/час";
            default:
                return "Цена по запросу";
        }
    }

    /**
     * Получает базовую цену в числовом виде для расчетов
     */
    public double getBasePriceByClass(VehicleClass vehicleClass) {
        if (vehicleClass == null) {
            return 0.0;
        }

        switch (vehicleClass) {
            case STANDARD:
                return 30.0;
            case LUXURY:
                return 45.0;
            case PREMIUM:
                return 60.0;
            default:
                return 0.0;
        }
    }
}
