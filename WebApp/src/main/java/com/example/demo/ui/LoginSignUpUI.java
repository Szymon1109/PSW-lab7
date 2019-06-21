package com.example.demo.ui;

import com.example.demo.model.Typ;
import com.example.demo.model.Uzytkownik;
import com.example.demo.repository.*;
import com.google.common.hash.Hashing;
import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringUI
@Theme("mytheme")
public class LoginSignUpUI extends UI {

    @Autowired
    BlokRepozytorium blokRepozytorium;

    @Autowired
    KursRepozytorium kursRepozytorium;

    @Autowired
    PowiadomienieRepozytorium powiadomienieRepozytorium;

    @Autowired
    UzytkownikRepozytorium uzytkownikRepozytorium;

    @Autowired
    ZajeciaRepozytorium zajeciaRepozytorium;

    @Autowired
    ZgloszenieRepozytorium zgloszenieRepozytorium;

    private VerticalLayout root;
    private VerticalLayout verticalLayout;
    private Button logowanie;
    private Button rejestracja;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Page.getCurrent().setTitle("System szkoleń");
        root = new VerticalLayout();
        root.setSpacing(true);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        logowanie = new Button("Logowanie");
        rejestracja = new Button("Rejestracja");

        horizontalLayout.addComponents(logowanie, rejestracja);
        verticalLayout = new VerticalLayout();

        logowanie();
        rejestracja();

        root.addComponents(horizontalLayout, verticalLayout);
        setContent(root);
    }

    private void rejestracja() {
        rejestracja.addClickListener(clickEvent -> {
            verticalLayout.removeAllComponents();

            TextField imieTextField = new TextField("Podaj imię: ");
            TextField nazwiskoTextField = new TextField("Podaj nazwisko: ");
            TextField loginTextField = new TextField("Podaj login: ");
            PasswordField hasloField = new PasswordField("Podaj hasło: ");
            Label label = new Label();
            Button login = new Button("Zarejestruj się");

            verticalLayout.addComponents(imieTextField, nazwiskoTextField, loginTextField, hasloField, label, login);

            login.addClickListener(event -> {
                String imieText = imieTextField.getValue();
                String nazwiskoText = nazwiskoTextField.getValue();
                String loginText = loginTextField.getValue();
                String hasloText = hasloField.getValue();

                if (imieText.length() > 3 && nazwiskoText.length() > 3 && loginText.length() > 3) {
                    if (hasloText.length() > 5 && hasloText.length() < 20) {
                        if (sprawdzHaslo(hasloText)) {
                            if (!uzytkownikRepozytorium.findAllLogins().contains(loginText)) {

                                Uzytkownik uzytkownik = new Uzytkownik(0L, loginText,
                                        Hashing.sha512().hashString(hasloText, StandardCharsets.UTF_8).toString(),
                                        Typ.UCZESTNIK, imieText, nazwiskoText, 0);

                                uzytkownikRepozytorium.save(uzytkownik);

                                imieTextField.setValue("");
                                nazwiskoTextField.setValue("");
                                loginTextField.setValue("");
                                hasloField.setValue("");

                                Notification.show("Rejestracja udana!", "", Notification.Type.HUMANIZED_MESSAGE);
                            } else
                                Notification.show("Podany login już istnieje!", "", Notification.Type.HUMANIZED_MESSAGE);
                        } else
                            Notification.show("Hasło musi składać się z conajmniej: 1 małej litery, 1 dużej litery i 1 cyfry!",
                                    "", Notification.Type.HUMANIZED_MESSAGE);
                    } else
                        Notification.show("Hasło musi składać się z conajmniej 6 znaków!", "", Notification.Type.HUMANIZED_MESSAGE);

                } else
                    Notification.show("Dane muszą składać się z conajmniej 4 znaków!", "", Notification.Type.HUMANIZED_MESSAGE);
            });
        });
    }

    private void logowanie() {
        logowanie.addClickListener(clickEvent -> {
            verticalLayout.removeAllComponents();

            TextField loginTextField = new TextField("Podaj login: ");
            PasswordField passwordField = new PasswordField("Podaj hasło: ");
            Label label = new Label();
            Button login = new Button("Zaloguj się");

            verticalLayout.addComponents(loginTextField, passwordField, label, login);

            login.addClickListener(event -> {
                Optional<Uzytkownik> uzytkownik = uzytkownikRepozytorium.findByLogin(loginTextField.getValue());
                if (uzytkownik.isPresent()) {
                    if (uzytkownik.get().getHaslo().equals(Hashing.sha512().hashString(passwordField.getValue(), StandardCharsets.UTF_8).toString())) {
                        Notification.show("Logowanie udane!", "", Notification.Type.HUMANIZED_MESSAGE);

                        root.removeAllComponents();
                        root.addComponent(new LoggedUI(uzytkownik.get(), blokRepozytorium, kursRepozytorium,
                                powiadomienieRepozytorium, uzytkownikRepozytorium, zajeciaRepozytorium, zgloszenieRepozytorium));

                        Button wyloguj = new Button("Wyloguj");
                        wyloguj.addClickListener(event1 -> {
                            root.removeAllComponents();
                            init(null);
                            logowanie.click();
                        });

                        root.addComponent(wyloguj);
                    } else {
                        Notification.show("Nieprawidłowe hasło!", "", Notification.Type.HUMANIZED_MESSAGE);
                    }
                } else {
                    Notification.show("Podany login nie istnieje!", "", Notification.Type.HUMANIZED_MESSAGE);
                }
            });
        });
    }

    private boolean sprawdzHaslo(String haslo) {
        final String patternString = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(haslo);

        return matcher.matches();
    }
}