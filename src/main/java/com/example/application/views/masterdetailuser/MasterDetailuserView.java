package com.example.application.views.masterdetailuser;

import com.example.application.data.Workout;
import com.example.application.services.WorkoutService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.application.views.masterdetailuser.Filters;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Master-Detail (user)")
@Route("master-detail/:workoutID?/:action?(edit)")
@Menu(order = 1, icon = LineAwesomeIconUrl.COLUMNS_SOLID)
@RolesAllowed("USER")
public class MasterDetailuserView extends Div implements BeforeEnterObserver {


    private final String WORKOUT_ID = "workoutID";
    private final String WORKOUT_EDIT_ROUTE_TEMPLATE = "master-detail/%s/edit";

    private final Grid<Workout> grid = new Grid<>(Workout.class, false);
    private final WorkoutService workoutService;


    private TextField name;
    private DateTimePicker date;
    private TextField duration;
    private TextField comment;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Workout> binder;
    private Workout workout;
    private Filters filters;


    public MasterDetailuserView(WorkoutService workoutService) {
        this.workoutService = workoutService;
        this.filters = new Filters(this::refreshGrid);

        addClassNames("master-detailuser-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(filters);
        add(splitLayout);

        // Configure Grid
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("date").setAutoWidth(true);
        grid.addColumn("duration").setAutoWidth(true);
        grid.addColumn("comment").setAutoWidth(true);

        // Filter and set items
        grid.setItems(query -> {
            List<Workout> allWorkouts = workoutService.list(
                    VaadinSpringDataHelpers.toSpringPageRequest(query)
            ).stream().collect(Collectors.toList());

            List<Workout> filteredWorkouts = filters.applyFilters(allWorkouts);

            return filteredWorkouts.stream();
        });
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);


        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(WORKOUT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(MasterDetailuserView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Workout.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(duration).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("duration");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.workout == null) {
                    this.workout = new Workout();
                }
                binder.writeBean(this.workout);
                workoutService.save(this.workout);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(MasterDetailuserView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> workoutId = event.getRouteParameters().get(WORKOUT_ID).map(Long::parseLong);
        if (workoutId.isPresent()) {
            Optional<Workout> workoutFromBackend = workoutService.get(workoutId.get());
            if (workoutFromBackend.isPresent()) {
                populateForm(workoutFromBackend.get());
            } else {
                Notification.show(String.format("The requested workout was not found, ID = %s", workoutId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(MasterDetailuserView.class);
            }
        }
    }


    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        name = new TextField("Name");
        date = new DateTimePicker("Date");
        date.setStep(Duration.ofSeconds(1));
        duration = new TextField("Duration");
        comment = new TextField("Comment");
        formLayout.add(name, date, duration, comment);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Workout value) {
        this.workout = value;
        binder.readBean(this.workout);

    }
}
