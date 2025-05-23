//package com.example.antaliya_taxi_service.maper;
//
//import com.example.antaliya_taxi_service.dto.vehicle.*;
//import com.example.antaliya_taxi_service.model.Vehicle;
//import org.springframework.stereotype.Component;
//
//@Component
//public class VehicleMapper {
//
//    /**
//     * Преобразует VehicleCreateDTO в Vehicle entity
//     */
//    public Vehicle toEntity(VehicleCreateDTO dto) {
//        if (dto == null) {
//            return null;
//        }
//
//        return Vehicle.builder()
//                .brand(dto.getBrand())
//                .model(dto.getModel())
//                .year(dto.getYear())
//                .licensePlate(dto.getLicensePlate())
//                .vehicleClass(dto.getVehicleClass())
//                .passengerCapacity(dto.getPassengerCapacity())
//                .luggageCapacity(dto.getLuggageCapacity())
//                .hasWifi(dto.getHasWifi())
//                .hasAirConditioning(dto.getHasAirConditioning())
//                .hasLeatherSeats(dto.getHasLeatherSeats())
//                .hasChildSeat(dto.getHasChildSeat())
//                .additionalFeatures(dto.getAdditionalFeatures())
//                .description(dto.getDescription())
//                .fuelType(dto.getFuelType())
//                .engineCapacity(dto.getEngineCapacity())
//                .active(dto.getActive())
//                .build();
//    }
//
//    /**
//     * Преобразует Vehicle entity в VehicleResponseDTO
//     */
//    public VehicleResponseDTO toResponseDTO(Vehicle vehicle) {
//        if (vehicle == null) {
//            return null;
//        }
//
//        return VehicleResponseDTO.builder()
//                .id(vehicle.getId())
//                .brand(vehicle.getBrand())
//                .model(vehicle.getModel())
//                .year(vehicle.getYear())
//                .licensePlate(vehicle.getLicensePlate())
//                .vehicleClass(vehicle.getVehicleClass())
//                .passengerCapacity(vehicle.getPassengerCapacity())
//                .luggageCapacity(vehicle.getLuggageCapacity())
//                .hasWifi(vehicle.getHasWifi())
//                .hasAirConditioning(vehicle.getHasAirConditioning())
//                .hasLeatherSeats(vehicle.getHasLeatherSeats())
//                .hasChildSeat(vehicle.getHasChildSeat())
//                .additionalFeatures(vehicle.getAdditionalFeatures())
//                .active(vehicle.getActive())
//                .completedTrips(vehicle.getCompletedTrips())
//                .imageUrl(vehicle.getImageUrl())
//                .imageId(vehicle.getImageId())
//                .description(vehicle.getDescription())
//                .fuelType(vehicle.getFuelType())
//                .engineCapacity(vehicle.getEngineCapacity())
//                .createdAt(vehicle.getCreatedAt())
//                .updatedAt(vehicle.getUpdatedAt())
//                .build();
//    }
//
//    /**
//     * Преобразует Vehicle в VehicleCardDto (без формattedPrice)
//     */
//    public VehicleCardDto toCardDto(Vehicle vehicle) {
//        if (vehicle == null) {
//            return null;
//        }
//
//        return VehicleCardDto.builder()
//                .id(vehicle.getId())
//                .brand(vehicle.getBrand())
//                .model(vehicle.getModel())
//                .year(vehicle.getYear())
//                .vehicleClass(vehicle.getVehicleClass())
//                .passengerCapacity(vehicle.getPassengerCapacity())
//                .luggageCapacity(vehicle.getLuggageCapacity())
//                .imageUrl(vehicle.getImageUrl())
//                .active(vehicle.getActive())
//                .completedTrips(vehicle.getCompletedTrips())
//                // formattedPrice будет установлен в сервисе
//                .build();
//    }
//
//    /**
//     * Обновляет существующий Vehicle entity данными из VehicleUpdateDTO
//     */
//    public void updateEntity(Vehicle vehicle, VehicleUpdateDTO dto) {
//        if (vehicle == null || dto == null) {
//            return;
//        }
//
//        if (dto.getBrand() != null) {
//            vehicle.setBrand(dto.getBrand());
//        }
//        if (dto.getModel() != null) {
//            vehicle.setModel(dto.getModel());
//        }
//        if (dto.getYear() != null) {
//            vehicle.setYear(dto.getYear());
//        }
//        if (dto.getLicensePlate() != null) {
//            vehicle.setLicensePlate(dto.getLicensePlate());
//        }
//        if (dto.getVehicleClass() != null) {
//            vehicle.setVehicleClass(dto.getVehicleClass());
//        }
//        if (dto.getPassengerCapacity() != null) {
//            vehicle.setPassengerCapacity(dto.getPassengerCapacity());
//        }
//        if (dto.getLuggageCapacity() != null) {
//            vehicle.setLuggageCapacity(dto.getLuggageCapacity());
//        }
//        if (dto.getHasWifi() != null) {
//            vehicle.setHasWifi(dto.getHasWifi());
//        }
//        if (dto.getHasAirConditioning() != null) {
//            vehicle.setHasAirConditioning(dto.getHasAirConditioning());
//        }
//        if (dto.getHasLeatherSeats() != null) {
//            vehicle.setHasLeatherSeats(dto.getHasLeatherSeats());
//        }
//        if (dto.getHasChildSeat() != null) {
//            vehicle.setHasChildSeat(dto.getHasChildSeat());
//        }
//        if (dto.getAdditionalFeatures() != null) {
//            vehicle.setAdditionalFeatures(dto.getAdditionalFeatures());
//        }
//        if (dto.getDescription() != null) {
//            vehicle.setDescription(dto.getDescription());
//        }
//        if (dto.getFuelType() != null) {
//            vehicle.setFuelType(dto.getFuelType());
//        }
//        if (dto.getEngineCapacity() != null) {
//            vehicle.setEngineCapacity(dto.getEngineCapacity());
//        }
//        if (dto.getActive() != null) {
//            vehicle.setActive(dto.getActive());
//        }
//    }
//
//}

package com.example.antaliya_taxi_service.maper;

import com.example.antaliya_taxi_service.dto.vehicle.*;
import com.example.antaliya_taxi_service.model.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    /**
     * Преобразует VehicleCreateDTO в Vehicle entity
     */
    public Vehicle toEntity(VehicleCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return Vehicle.builder()
                .brand(dto.getBrand())
                .model(dto.getModel())
                .year(dto.getYear())
                .licensePlate(dto.getLicensePlate())
                .vehicleClass(dto.getVehicleClass())
                .passengerCapacity(dto.getPassengerCapacity())
                .luggageCapacity(dto.getLuggageCapacity())
                .hasWifi(dto.getHasWifi())
                .hasAirConditioning(dto.getHasAirConditioning())
                .hasLeatherSeats(dto.getHasLeatherSeats())
                .hasChildSeat(dto.getHasChildSeat())
                .additionalFeatures(dto.getAdditionalFeatures())
                .description(dto.getDescription())
                .fuelType(dto.getFuelType())
                .engineCapacity(dto.getEngineCapacity())
                .active(dto.getActive())
                .build();
    }

    /**
     * Преобразует Vehicle entity в VehicleResponseDTO
     */
    public VehicleResponseDTO toResponseDTO(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        return VehicleResponseDTO.builder()
                .id(vehicle.getId())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .licensePlate(vehicle.getLicensePlate())
                .vehicleClass(vehicle.getVehicleClass())
                .passengerCapacity(vehicle.getPassengerCapacity())
                .luggageCapacity(vehicle.getLuggageCapacity())
                .hasWifi(vehicle.getHasWifi())
                .hasAirConditioning(vehicle.getHasAirConditioning())
                .hasLeatherSeats(vehicle.getHasLeatherSeats())
                .hasChildSeat(vehicle.getHasChildSeat())
                .additionalFeatures(vehicle.getAdditionalFeatures())
                .active(vehicle.getActive())
                .completedTrips(vehicle.getCompletedTrips())
                .imageUrl(vehicle.getImageUrl())
                .imageId(vehicle.getImageId())
                .description(vehicle.getDescription())
                .fuelType(vehicle.getFuelType())
                .engineCapacity(vehicle.getEngineCapacity())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }

    /**
     * НОВЫЙ МЕТОД: Преобразует VehicleResponseDTO в VehicleUpdateDTO
     * Решает проблему дублирования кода в контроллере
     */
    public VehicleUpdateDTO toUpdateDTO(VehicleResponseDTO responseDTO) {
        if (responseDTO == null) {
            return null;
        }

        return VehicleUpdateDTO.builder()
                .id(responseDTO.getId())
                .brand(responseDTO.getBrand())
                .model(responseDTO.getModel())
                .year(responseDTO.getYear())
                .licensePlate(responseDTO.getLicensePlate())
                .vehicleClass(responseDTO.getVehicleClass())
                .passengerCapacity(responseDTO.getPassengerCapacity())
                .luggageCapacity(responseDTO.getLuggageCapacity())
                .hasWifi(responseDTO.getHasWifi())
                .hasAirConditioning(responseDTO.getHasAirConditioning())
                .hasLeatherSeats(responseDTO.getHasLeatherSeats())
                .hasChildSeat(responseDTO.getHasChildSeat())
                .additionalFeatures(responseDTO.getAdditionalFeatures())
                .description(responseDTO.getDescription())
                .fuelType(responseDTO.getFuelType())
                .engineCapacity(responseDTO.getEngineCapacity())
                .active(responseDTO.getActive())
                .build();
    }

    /**
     * Преобразует Vehicle в VehicleCardDto (без формattedPrice)
     */
    public VehicleCardDto toCardDto(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        return VehicleCardDto.builder()
                .id(vehicle.getId())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .vehicleClass(vehicle.getVehicleClass())
                .passengerCapacity(vehicle.getPassengerCapacity())
                .luggageCapacity(vehicle.getLuggageCapacity())
                .imageUrl(vehicle.getImageUrl())
                .active(vehicle.getActive())
                .completedTrips(vehicle.getCompletedTrips())

                .hasWifi(vehicle.getHasWifi())
                .hasAirConditioning(vehicle.getHasAirConditioning())
                .hasLeatherSeats(vehicle.getHasLeatherSeats())
                .hasChildSeat(vehicle.getHasChildSeat())

                // formattedPrice будет установлен в сервисе
                .build();
    }

    /**
     * Обновляет существующий Vehicle entity данными из VehicleUpdateDTO
     */
    public void updateEntity(Vehicle vehicle, VehicleUpdateDTO dto) {
        if (vehicle == null || dto == null) {
            return;
        }

        if (dto.getBrand() != null) {
            vehicle.setBrand(dto.getBrand());
        }
        if (dto.getModel() != null) {
            vehicle.setModel(dto.getModel());
        }
        if (dto.getYear() != null) {
            vehicle.setYear(dto.getYear());
        }
        if (dto.getLicensePlate() != null) {
            vehicle.setLicensePlate(dto.getLicensePlate());
        }
        if (dto.getVehicleClass() != null) {
            vehicle.setVehicleClass(dto.getVehicleClass());
        }
        if (dto.getPassengerCapacity() != null) {
            vehicle.setPassengerCapacity(dto.getPassengerCapacity());
        }
        if (dto.getLuggageCapacity() != null) {
            vehicle.setLuggageCapacity(dto.getLuggageCapacity());
        }
        if (dto.getHasWifi() != null) {
            vehicle.setHasWifi(dto.getHasWifi());
        }
        if (dto.getHasAirConditioning() != null) {
            vehicle.setHasAirConditioning(dto.getHasAirConditioning());
        }
        if (dto.getHasLeatherSeats() != null) {
            vehicle.setHasLeatherSeats(dto.getHasLeatherSeats());
        }
        if (dto.getHasChildSeat() != null) {
            vehicle.setHasChildSeat(dto.getHasChildSeat());
        }
        if (dto.getAdditionalFeatures() != null) {
            vehicle.setAdditionalFeatures(dto.getAdditionalFeatures());
        }
        if (dto.getDescription() != null) {
            vehicle.setDescription(dto.getDescription());
        }
        if (dto.getFuelType() != null) {
            vehicle.setFuelType(dto.getFuelType());
        }
        if (dto.getEngineCapacity() != null) {
            vehicle.setEngineCapacity(dto.getEngineCapacity());
        }
        if (dto.getActive() != null) {
            vehicle.setActive(dto.getActive());
        }
    }
}