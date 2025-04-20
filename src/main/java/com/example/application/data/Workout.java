package com.example.application.data;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Workout extends AbstractEntity {

    private String name;
    private LocalDateTime date;
    private Integer duration;
    private String comment;

    @ManyToOne  // Suhde käyttäjään
    private User user;  // Käyttäjä, johon treeni kuuluu

    @ManyToOne
    @JoinColumn(name = "workout_type_id")
    private WorkoutType workoutType;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "details_id")
    private WorkoutDetails details;


    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



    // Getter WorkoutDetails-luokalle
    public WorkoutDetails getDetails() {
        return details;
    }

    // Setter WorkoutDetails-luokalle, jos tarvitaan
    public void setDetails(WorkoutDetails workoutDetails) {
        this.details = workoutDetails;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public Integer getDuration() {
        return duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }




}
