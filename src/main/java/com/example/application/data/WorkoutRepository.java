package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long>, JpaSpecificationExecutor<Workout> {

}
