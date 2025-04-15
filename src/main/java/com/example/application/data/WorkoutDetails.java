package com.example.application.data;

import jakarta.persistence.*;

@Entity
public class WorkoutDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int caloriesBurned;
    private String notes;
    private int averageHeartRate;

    @OneToOne
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;
}
