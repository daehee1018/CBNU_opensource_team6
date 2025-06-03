package com.example.recommenddiet.repository;

import com.example.recommenddiet.entity.Nutrition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NutritionRepository extends JpaRepository<Nutrition, String> {
    List<Nutrition> findByCategory(String category);
    List<Nutrition> findBySubcategory(String subcategory);
} 