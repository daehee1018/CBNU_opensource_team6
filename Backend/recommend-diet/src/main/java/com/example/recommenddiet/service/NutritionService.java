package com.example.recommenddiet.service;

import com.example.recommenddiet.entity.Nutrition;
import com.example.recommenddiet.repository.NutritionRepository;
import com.example.recommenddiet.dto.NutritionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NutritionService {
    
    private final NutritionRepository nutritionRepository;

    @Autowired
    public NutritionService(NutritionRepository nutritionRepository) {
        this.nutritionRepository = nutritionRepository;
    }

    public List<NutritionDTO> getAllNutritions() {
        return nutritionRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public NutritionDTO getNutritionByName(String name) {
        Optional<Nutrition> nutrition = nutritionRepository.findById(name);
        return nutrition.map(this::convertToDTO).orElse(null);
    }

    public List<NutritionDTO> getNutritionsByCategory(String category) {
        return nutritionRepository.findByCategory(category).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<NutritionDTO> getNutritionsBySubcategory(String subcategory) {
        return nutritionRepository.findBySubcategory(subcategory).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private NutritionDTO convertToDTO(Nutrition nutrition) {
        return new NutritionDTO(
            nutrition.getName(),
            nutrition.getCategory(),
            nutrition.getRepName(),
            nutrition.getSubcategory(),
            nutrition.getStandardAmount(),
            nutrition.getEnergy(),
            nutrition.getProtein(),
            nutrition.getFat(),
            nutrition.getCarbohydrate(),
            nutrition.getSugar(),
            nutrition.getSodium(),
            nutrition.getCholesterol(),
            nutrition.getSaturatedFat()
        );
    }
} 