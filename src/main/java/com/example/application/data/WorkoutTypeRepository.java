package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutTypeRepository extends JpaRepository<WorkoutType, Long> {
    // Voit lisätä tarvittavia custom-haut tänne, jos niitä tarvitset
}