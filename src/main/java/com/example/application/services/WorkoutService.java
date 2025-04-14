package com.example.application.services;

import com.example.application.data.Workout;
import com.example.application.data.WorkoutRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class WorkoutService {

    private final WorkoutRepository repository;

    public WorkoutService(WorkoutRepository repository) {
        this.repository = repository;
    }

    public Optional<Workout> get(Long id) {
        return repository.findById(id);
    }

    public Workout save(Workout entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Workout> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Workout> list(Pageable pageable, Specification<Workout> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
