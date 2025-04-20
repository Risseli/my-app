package com.example.application.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

@Route("forbidden")
@PermitAll
public class ForbiddenView extends VerticalLayout {
    public ForbiddenView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H2 forbiddenMessage = new H2("Pääsy estetty! Sinulla ei ole oikeuksia nähdä tätä sivua.");
        add(forbiddenMessage);
    }
}
