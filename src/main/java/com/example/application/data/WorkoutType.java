package com.example.application.data;

import com.vaadin.flow.component.template.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class WorkoutType extends AbstractEntity {

        private String name; // Kestävyysharjoittelu, Voimaharjoittelu, jne.

    @OneToMany(mappedBy = "workoutType")
    private List<Workout> workouts = new ArrayList<>();

    // konstruktorit
    public WorkoutType() {
    }

    // Konstruktori helpompaan testidatan luontiin
    public WorkoutType(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }


}
