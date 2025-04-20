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

    @OneToOne(mappedBy = "details")
    private Workout workout;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public int setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
        return caloriesBurned;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getAverageHeartRate() {
        return averageHeartRate;
    }


    public void setAverageHeartRate(int averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }
}
