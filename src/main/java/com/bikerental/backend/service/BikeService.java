package com.bikerental.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bikerental.backend.dto.BikeDTO;
import com.bikerental.backend.entity.BikeEntity;
import com.bikerental.backend.entity.BikeStatus;
import com.bikerental.backend.repository.BikeRepository;

@Service
@Slf4j
@Transactional
public class BikeService {

    @Autowired
    private BikeRepository bikeRepository;

    public Page<BikeDTO> getAvailableBikes(Pageable pageable) {
        return bikeRepository.findByStatus(BikeStatus.READY, pageable)
            .map(this::mapBikeToDTO);
    }

    public Page<BikeDTO> searchBikes(String searchTerm, Pageable pageable) {
        return bikeRepository.findByModelContainingIgnoreCaseOrBrandContainingIgnoreCase(
            searchTerm, searchTerm, pageable)
            .map(this::mapBikeToDTO);
    }

    public Page<BikeDTO> filterByBrand(String brand, Pageable pageable) {
        return bikeRepository.findByBrand(brand, pageable)
            .map(this::mapBikeToDTO);
    }

    public BikeDTO getBikeById(String bikeId) {
        BikeEntity bike = bikeRepository.findById(bikeId)
            .orElseThrow(() -> new RuntimeException("Bike not found"));
        return mapBikeToDTO(bike);
    }

    public BikeDTO createBike(BikeDTO bikeDTO) {
        BikeEntity bike = BikeEntity.builder()
            .model(bikeDTO.getModel())
            .brand(bikeDTO.getBrand())
            .registrationNumber(bikeDTO.getRegistrationNumber())
            .pricePerHour(bikeDTO.getPricePerHour())
            .status(BikeStatus.READY)
            .currentLocation(bikeDTO.getCurrentLocation())
            .mileage(bikeDTO.getMileage())
            .build();

        bike = bikeRepository.save(bike);
        return mapBikeToDTO(bike);
    }

    public BikeDTO updateBike(String bikeId, BikeDTO bikeDTO) {
        BikeEntity bike = bikeRepository.findById(bikeId)
            .orElseThrow(() -> new RuntimeException("Bike not found"));

        if (bikeDTO.getModel() != null) bike.setModel(bikeDTO.getModel());
        if (bikeDTO.getBrand() != null) bike.setBrand(bikeDTO.getBrand());
        if (bikeDTO.getPricePerHour() != null) bike.setPricePerHour(bikeDTO.getPricePerHour());
        if (bikeDTO.getStatus() != null) bike.setStatus(bikeDTO.getStatus());
        if (bikeDTO.getCurrentLocation() != null) bike.setCurrentLocation(bikeDTO.getCurrentLocation());
        if (bikeDTO.getMileage() != null) bike.setMileage(bikeDTO.getMileage());

        bike = bikeRepository.save(bike);
        return mapBikeToDTO(bike);
    }

    public void deleteBike(String bikeId) {
        BikeEntity bike = bikeRepository.findById(bikeId)
            .orElseThrow(() -> new RuntimeException("Bike not found"));
        bikeRepository.delete(bike);
    }

    private BikeDTO mapBikeToDTO(BikeEntity bike) {
        return BikeDTO.builder()
            .id(bike.getId())
            .model(bike.getModel())
            .brand(bike.getBrand())
            .registrationNumber(bike.getRegistrationNumber())
            .pricePerHour(bike.getPricePerHour())
            .status(bike.getStatus())
            .currentLocation(bike.getCurrentLocation())
            .mileage(bike.getMileage())
            .bikePhotoUrl(bike.getBikePhotoUrl())
            .description(bike.getDescription())
            .build();
    }
}
