package com.example.application.services;

import com.example.application.data.WorkoutType;
import com.example.application.data.WorkoutTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutTypeService {
    private final WorkoutTypeRepository workoutTypeRepository;

    // Konstruktoripohjainen injektio
    public WorkoutTypeService(WorkoutTypeRepository workoutTypeRepository) {
        this.workoutTypeRepository = workoutTypeRepository;
    }

    public WorkoutType save(WorkoutType workoutType) {
        return workoutTypeRepository.save(workoutType);
    }
    public List<WorkoutType> findAll() {
        return workoutTypeRepository.findAll();
    }
}
