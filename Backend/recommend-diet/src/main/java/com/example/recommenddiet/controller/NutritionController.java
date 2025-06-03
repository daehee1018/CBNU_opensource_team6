package com.example.recommenddiet.controller;

import com.example.recommenddiet.dto.NutritionDTO;
import com.example.recommenddiet.service.NutritionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/nutrition")
public class NutritionController {

    private final NutritionService nutritionService;

    @Autowired
    public NutritionController(NutritionService nutritionService) {
        this.nutritionService = nutritionService;
    }

    @GetMapping
    public ResponseEntity<List<NutritionDTO>> getAllNutritions() {
        List<NutritionDTO> nutritions = nutritionService.getAllNutritions();
        return ResponseEntity.ok(nutritions);
    }

    @GetMapping("/{name}")
    public ResponseEntity<NutritionDTO> getNutritionByName(@PathVariable String name) {
        NutritionDTO nutrition = nutritionService.getNutritionByName(name);
        if (nutrition != null) {
            return ResponseEntity.ok(nutrition);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<NutritionDTO>> getNutritionsByCategory(@PathVariable String category) {
        List<NutritionDTO> nutritions = nutritionService.getNutritionsByCategory(category);
        return ResponseEntity.ok(nutritions);
    }

    @GetMapping("/subcategory/{subcategory}")
    public ResponseEntity<List<NutritionDTO>> getNutritionsBySubcategory(@PathVariable String subcategory) {
        List<NutritionDTO> nutritions = nutritionService.getNutritionsBySubcategory(subcategory);
        return ResponseEntity.ok(nutritions);
    }
} 