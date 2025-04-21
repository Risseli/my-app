package com.example.application.views.loggedUser;

import com.example.application.data.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TagService;
import com.example.application.services.UserService;
import com.example.application.services.WorkoutService;
import com.example.application.services.WorkoutTypeService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Route(value = "user", layout = MainLayout.class)
@Menu(order = 2, icon = LineAwesomeIconUrl.USER_SOLID)
@RolesAllowed("USER")
@PageTitle("Omat reenit")
public class UserView extends VerticalLayout implements BeforeEnterObserver {

    private final WorkoutService workoutService;
    private final UserService userService;
    private GridListDataView<Workout> dataView;
    private List<Workout> workouts = new ArrayList<>();
    private Grid<Workout> grid;
    private WorkoutFilter workoutFilter;
    private WorkoutTypeService workoutTypeService;
    private final TagService tagService;


    public UserView(WorkoutService workoutService, UserService userService, WorkoutTypeService workoutTypeService, TagService tagService) {
        this.workoutService = workoutService;
        this.userService = userService;
        this.workoutTypeService = workoutTypeService;
        this.tagService = tagService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        add(new H2("User Workouts"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOpt = userService.getByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            workouts = new ArrayList<>(workoutService.getWorkoutsByUsername(user.getName()));

            if (workouts.isEmpty()) {
                WorkoutType testType = new WorkoutType("Test workout");
                workoutTypeService.save(testType);

                for (int i = 1; i <= 20; i++) {
                    Workout workout = new Workout();
                    workout.setName("Testworkout " + i);
                    workout.setComment("Comment " + i);
                    workout.setDuration(20 + i); // esim. 21-30 min
                    workout.setUser(user);
                    workout.setWorkoutType(testType);

                    WorkoutDetails details = new WorkoutDetails();
                    details.setCaloriesBurned(200 + i * 10); // esim. 210–300 kcal
                    details.setAverageHeartRate(110 + i);    // esim. 111–120 bpm
                    workout.setDetails(details);

                    Tag tag = new Tag();
                    tag.setName("Tag " + i);
                    tagService.save(tag);

                    workout.getTags().add(tag);

                    workoutService.save(workout);
                    workouts.add(workout);
                }
            }

            createForm(user);
            createWorkoutGrid();
        }


    }



    private void createForm(User user) {
        TextField nameField = new TextField("Name");
        TextField commentField = new TextField("Comment");
        IntegerField durationField = new IntegerField("Duration (min)");
        IntegerField caloriesField = new IntegerField("Calories");
        IntegerField avgHeartRateField = new IntegerField("Avg HR");
        TextField tagsField = new TextField("Tags");

        ComboBox<WorkoutType> workoutTypeComboBox = new ComboBox<>("Choose Workout Type");

        List<WorkoutType> workoutTypes = List.of(
                new WorkoutType("Endurance training"),
                new WorkoutType("Strength training"),
                new WorkoutType("Weight training"),
                new WorkoutType("Sport related training")
        );

        workoutTypeComboBox.setItems(workoutTypes);
        workoutTypeComboBox.setItemLabelGenerator(WorkoutType::getName);

        Button clearButton = new Button("Clear");
        clearButton.addClickListener(e -> {
            nameField.clear();
            commentField.clear();
            durationField.clear();
            caloriesField.clear();
            avgHeartRateField.clear();
            tagsField.clear();
            workoutTypeComboBox.setValue(null);
        });

        Button addButton = new Button("Add Workout");
        addButton.addClickListener(e -> {
            String name = nameField.getValue();
            String comment = commentField.getValue();
            Integer duration = durationField.getValue();
            Integer calories = caloriesField.getValue();
            String tags = tagsField.getValue();
            Integer avgHeartRate = avgHeartRateField.getValue();

            if (name != null && !name.isEmpty() && duration != null) {
                Workout workout = new Workout();
                workout.setName(name);
                workout.setComment(comment);
                workout.setDuration(duration);
                workout.setUser(user);

                // Luo Set<Tag>, jos sitä ei ole vielä
                Set<Tag> tagSet = new HashSet<>();

                // Oletetaan, että käyttäjä voi syöttää useita tageja, erotettuina pilkulla
                if (tags != null && !tags.isEmpty()) {
                    // Erotellaan tagit pilkulla ja lisätään ne Set<Tag>:iin
                    String[] tagNames = tags.split(",");
                    for (String tagName : tagNames) {
                        Tag tag = new Tag();
                        tag.setName(tagName.trim());  // Poistetaan mahdolliset ylimääräiset välilyönnit
                        tagSet.add(tag);  // Lisää tagi Set:iin
                    }
                }

                workout.setTags(tagSet);  // Aseta tags Set Workout-objektiin

                WorkoutType selectedType = workoutTypeComboBox.getValue();

                // Tarkista, että se ei ole null ja että WorkoutType on tallennettu

                    // Jos WorkoutType ei ole vielä tietokannassa, tallenna se
                    if (selectedType.getId() == null) {
                        workoutTypeService.save(selectedType); // Ei staattinen, ei virhettä                    }
                    }
                    // Liitä WorkoutType Workout-objektiin
                    workout.setWorkoutType(selectedType);

                    // Tallenna Workout
                    workoutService.save(workout); //

                for (Tag tag : tagSet) {
                    tagService.save(tag);  // Tallenna jokainen tagi
                }


                WorkoutDetails details = new WorkoutDetails();
                details.setCaloriesBurned(calories != null ? calories : 0);
                details.setAverageHeartRate(avgHeartRate != null ? avgHeartRate : 0);
                workout.setDetails(details);

                workoutService.save(workout);
                workouts.add(workout);
                dataView.refreshAll();

                nameField.clear();
                commentField.clear();
                durationField.clear();
                caloriesField.clear();
                avgHeartRateField.clear();
                tagsField.clear();
                workoutTypeComboBox.setValue(null);
            }
        });

        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        formLayout.add(nameField, commentField, durationField, caloriesField, avgHeartRateField, workoutTypeComboBox, tagsField, addButton, clearButton);

        add(formLayout);
    }


    private void createWorkoutGrid() {
        grid = new Grid<>(Workout.class, false);
        grid.setWidthFull();

        Grid.Column<Workout> nameColumn = grid.addColumn(Workout::getName).setHeader("Name").setSortable(true);
        Grid.Column<Workout> commentColumn = grid.addColumn(Workout::getComment).setHeader("Comment");
        Grid.Column<Workout> durationColumn = grid.addColumn(Workout::getDuration).setHeader("Duration");
        Grid.Column<Workout> caloriesColumn = grid.addColumn(w -> w.getDetails() != null ? w.getDetails().getCaloriesBurned() : null).setHeader("Calories");
        Grid.Column<Workout> heartRateColumn = grid.addColumn(w -> w.getDetails() != null ? w.getDetails().getAverageHeartRate() : null).setHeader("Avg HR");
        Grid.Column<Workout> tagsColumn = grid.addColumn(workout -> {
            Set<Tag> tags = workout.getTags();
            if (tags == null || tags.isEmpty()) {
                return "";
            }
            return tags.stream()
                    .map(Tag::getName)
                    .collect(Collectors.joining(", "));
        }).setHeader("Tags");



        Grid.Column<Workout> workoutTypeColumn = grid.addColumn(workout -> { WorkoutType type = workout.getWorkoutType();
            return type != null ? type.getName() : "";
        }).setHeader("Workout Type");
        dataView = grid.setItems(workouts);
        workoutFilter = new WorkoutFilter(dataView);

        HeaderRow filterRow = grid.appendHeaderRow();
        filterRow.getCell(nameColumn).setComponent(createFilterField("Filter name", workoutFilter::setName));
        filterRow.getCell(commentColumn).setComponent(createFilterField("Filter comment", workoutFilter::setComment));
        filterRow.getCell(durationColumn).setComponent(createFilterField("Filter duration", value -> {
            try {
                workoutFilter.setDuration(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                workoutFilter.setDuration(null);
            }
        }));

        // Add filter field for type
        TextField typeFilterField = createFilterField("Filter workout type", value -> workoutFilter.setType(value));
        filterRow.getCell(workoutTypeColumn).setComponent(typeFilterField);

        add(grid);

        if (workouts.isEmpty()) {
            WorkoutType testType = new WorkoutType("Testiharjoittelu");
            workoutTypeService.save(testType);
        }
    }

    private TextField createFilterField(String placeholder, Consumer<String> filterChangeListener) {
        TextField field = new TextField();
        field.setPlaceholder(placeholder);
        field.setClearButtonVisible(true);
        field.setWidthFull();
        field.addValueChangeListener(e -> filterChangeListener.accept(e.getValue()));
        return field;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOpt = userService.getByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Tarkistetaan, onko käyttäjä kirjautunut ja ei ole "user" käyttäjä
            if ("user".equals(user.getUsername())) {
                // Jos käyttäjä on "user", ohjataan käyttäjä virhesivulle
                event.rerouteTo("forbidden");
            } else {
                // Lähetetään tervetuloviesti käyttäjälle server pushin avulla
                getUI().ifPresent(ui -> {
                    ui.access(() -> {
                        new Notification("Tervetuloa, " + user.getName() + "!", 3000)
                                .open(); // Tervetuloviesti, joka näytetään 3 sekunnin ajan
                    });
                });
            }
        } else {
            // Jos käyttäjä ei ole kirjautunut sisään, ohjataan kirjautumissivulle
            event.rerouteTo("login");
        }
    }


    private static class WorkoutFilter {
        private final GridListDataView<Workout> dataView;

        private String name = "";
        private String comment = "";
        private Integer duration = null;
        private String type = "";

        public WorkoutFilter(GridListDataView<Workout> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::filterWorkout);
        }

        public void setName(String name) {
            this.name = name.toLowerCase();
            dataView.refreshAll();
        }

        public void setComment(String comment) {
            this.comment = comment.toLowerCase();
            dataView.refreshAll();
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
            dataView.refreshAll();
        }

        public void setType(String type) {
            this.type = type.toLowerCase();
            dataView.refreshAll();
        }

        private boolean filterWorkout(Workout workout) {
            boolean matchesName = workout.getName() != null && workout.getName().toLowerCase().contains(name);
            boolean matchesComment = workout.getComment() != null && workout.getComment().toLowerCase().contains(comment);
            boolean matchesDuration = duration == null || (workout.getDuration() != null && workout.getDuration().equals(duration));
            boolean matchesType = type.isEmpty() || (workout.getWorkoutType() != null && workout.getWorkoutType().getName().toLowerCase().contains(type));

            return matchesName && matchesComment && matchesDuration && matchesType;
        }
    }
}

