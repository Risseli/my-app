package com.example.application.views.login;

import com.example.application.data.Role;
import com.example.application.data.User;
import com.example.application.services.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RegisterComponent extends Div {

    private final Dialog dialog;

    public RegisterComponent(UserService userService, PasswordEncoder passwordEncoder) {
        dialog = new Dialog();
        User user = new User(); // luodaan user olio johon sidotaan formmin tiedot

        dialog.setHeaderTitle("Register");

        // luodaan formi tyylittelyä varten divin sisään
        FormLayout formLayout = new FormLayout();

        TextField username = new TextField("Username");
        TextField name = new TextField("Name");

        // profiilikuvan lisääminen
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload profilePicture = new Upload(buffer);
        profilePicture.setMaxFiles(1);
        profilePicture.setAcceptedFileTypes("image/*"); // hyväksyy kaikki kuvatiedostot
        profilePicture.addSucceededListener(event -> {
           String filename = event.getFileName();
            InputStream inputStream = buffer.getInputStream(filename);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int bytesRead;
                    while (true) {
                        try {
                            if (((bytesRead = inputStream.read(bytes)) == -1)) break;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        byteArrayOutputStream.write(bytes, 0, bytesRead);
                    }

            user.setProfilePicture(byteArrayOutputStream.toByteArray());
        });

        PasswordField password = new PasswordField("Password");
        PasswordField confirmpassword = new PasswordField("Confirm Password");

        // lisätään formille
        formLayout.add(username, name, password, confirmpassword, profilePicture);
        dialog.add(formLayout);

        Button save = new Button("Save");
        Button cancel = new Button("Cancel");

        dialog.getFooter().add(save, cancel);

        add(dialog);

        // sidotaan binderilla formin tiedot user luokkaan
        BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

        // tehdään validoinnit formille
        binder.forField(username).asRequired("Pakollinen kenttä")
                        .withValidator(userService::userNameAvailable, "Käyttäjänimi on vsarattu!")
                                .bind(User::getUsername, User::setUsername);

        binder.forField(name).asRequired("Pakolllinen kenttä!")
                .bind(User::getName, User::setName);

        binder.forField(password).asRequired("Pakollinen kenttä!")
                        .withValidator(pw -> pw.length() >= 8,
                                "Salasanan oltava vähintään 8 merkkiä")
                                .bind(User::getHashedPassword,
                                        (user1, pw) -> user1.setHashedPassword(passwordEncoder.encode(pw)));

        binder.forField(confirmpassword).asRequired("Pakollinen kenttä!")
                .withValidator(confirmed -> Objects.equals(confirmed, password.getValue()),"Salasanojen täytyy olla samat!")
                .bind(User::getHashedPassword,
                        (user1, pw) -> user1.setHashedPassword(passwordEncoder.encode(pw)));



        save.addClickListener(e -> {
            binder.validate();
            if (binder.isValid()) {
                try {
                    binder.writeBean(user);

                    // Asetetaan oletusrooli käyttäjälle
                    Set<Role> roles = new HashSet<>();
                    roles.add(Role.USER);
                    user.setRoles(roles);

                    // Tallennetaan käyttäjä tietokantaan
                    userService.save(user);

                    // Kirjautuminen heti rekisteröinnin jälkeen
                    UI.getCurrent().navigate("login");

                    // Navigoidaan oikeaan näkymään
                    UI.getCurrent().navigate("user");

                    dialog.close();
                } catch (ValidationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        cancel.addClickListener(e -> {
            name.clear();
            username.clear();
            password.clear();
            confirmpassword.clear();
            profilePicture.clearFileList();
            dialog.close();
        });
    }

    public void openRegisterComponent() {
        dialog.open(); // Tässä avataan dialogi
    }
}
