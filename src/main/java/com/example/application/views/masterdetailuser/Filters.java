package com.example.application.views.masterdetailuser;

import com.example.application.data.Workout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Filters extends HorizontalLayout {
    private final TextField nameFilter;
    private final DatePicker startDateFilter;
    private final DatePicker endDateFilter;
    private final TextField durationFilter;
    private final Button searchButton;
    private final Button clearButton;
    private final Runnable onSearch;

    public Filters(Runnable onSearch) {
        this.onSearch = onSearch;

        // Initialize filter components with placeholders
        nameFilter = new TextField();
        nameFilter.setPlaceholder("Filter by name...");
        nameFilter.setClearButtonVisible(true);

        // Replace single date filter with start and end date filters
        startDateFilter = new DatePicker();
        startDateFilter.setPlaceholder("Start date...");

        endDateFilter = new DatePicker();
        endDateFilter.setPlaceholder("End date...");

        durationFilter = new TextField();
        durationFilter.setPlaceholder("Filter by minimum duration...");
        durationFilter.setClearButtonVisible(true);

        searchButton = new Button("Search");
        clearButton = new Button("Clear");

        // tehtävä 2, määritellään yksittäisille elementeille tyyli
        searchButton.getElement().getStyle().set("background-color", "#4CAF50");
        clearButton.getElement().getStyle().set("color", "red");

        // asetetaan kuuntelijat
        searchButton.addClickListener(e -> onSearch.run());
        clearButton.addClickListener(e -> clearFilters());

        setWidthFull();
        setSpacing(true);
        setPadding(true);

        // Add components to layout
        add(nameFilter, durationFilter,startDateFilter,endDateFilter, searchButton, clearButton);
    }

    private void clearFilters() {
        nameFilter.clear();
        startDateFilter.clear();
        endDateFilter.clear();
        durationFilter.clear();
        onSearch.run();
    }

    // Method to apply all filters to a list of workouts
    public List<Workout> applyFilters(List<Workout> workouts) {
        String nameFilterValue = nameFilter.getValue();
        String durationFilterValue = durationFilter.getValue();

        return workouts.stream()
                .filter(workout -> filterByName(workout, nameFilterValue))
                .filter(workout -> filterByDuration(workout, durationFilterValue))
                .filter(this::filterByDateRange)
                .collect(Collectors.toList());
    }

    // Filter predicates
    private boolean filterByName(Workout workout, String nameFilterValue) {
        if (nameFilterValue == null || nameFilterValue.isEmpty()) {
            return true;  // Jos hakusana on tyhjä, ei suodateta mitään
        }

        // Palautetaan true, jos workoutin nimi alkaa hakusanalla
        return workout.getName().toLowerCase().startsWith(nameFilterValue.toLowerCase());
    }

    private boolean filterByDuration(Workout workout, String filterValue) {
        if (filterValue == null || filterValue.isEmpty()) {
            return true; // No filter applied
        }
        try {
            int durationFilter = Integer.parseInt(filterValue);
            return workout.getDuration() >= durationFilter;
        } catch (NumberFormatException e) {
            return true; // Invalid number format, don't filter
        }
    }

    private boolean filterByDateRange(Workout workout) {
        LocalDate start = startDateFilter.getValue();
        LocalDate end = endDateFilter.getValue();

        // If neither date is set, don't filter
        if (start == null && end == null) {
            return true;
        }

        // If workout has no date, it can't be filtered by date
        if (workout.getDate() == null) {
            return false;
        }

        LocalDate workoutDate = workout.getDate().toLocalDate();

        // If only start date is set, filter all dates >= start
        if (start != null && end == null) {
            return !workoutDate.isBefore(start);
        }

        // If only end date is set, filter all dates <= end
        if (start == null && end != null) {
            return !workoutDate.isAfter(end);
        }

        // Assuming your Workout.date is a LocalDateTime or similar
        // You might need to adjust this logic based on your date field type

        return !workoutDate.isBefore(start) && !workoutDate.isAfter(end);
    }
}