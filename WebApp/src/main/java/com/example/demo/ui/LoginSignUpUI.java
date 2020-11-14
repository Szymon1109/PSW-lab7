package com.example.demo.ui;

import com.example.demo.util.UserType;
import com.example.demo.model.User;
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

import static com.example.demo.util.DataUtils.checkPassword;

@SpringUI
@Theme("mytheme")
public class LoginSignUpUI extends UI {

    private LessonSeriesRepository lessonSeriesRepository;
    private CourseRepository courseRepository;
    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private LessonRepository lessonRepository;
    private RequestRepository requestRepository;

    @Autowired
    public LoginSignUpUI(LessonSeriesRepository lessonSeriesRepository,
                         CourseRepository courseRepository,
                         MessageRepository messageRepository,
                         UserRepository userRepository,
                         LessonRepository lessonRepository,
                         RequestRepository requestRepository) {
        this.lessonSeriesRepository = lessonSeriesRepository;
        this.courseRepository = courseRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.requestRepository = requestRepository;
    }

    private VerticalLayout root;
    private VerticalLayout verticalLayout;
    private Button loginButton;
    private Button registrationButton;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Page.getCurrent().setTitle("System szkoleń");
        root = new VerticalLayout();
        root.setSpacing(true);

        HorizontalLayout welcomeLayout = new HorizontalLayout();
        Label label = new Label("System szkolenia pracowników. Prosimy o zalogowanie!");
        welcomeLayout.addComponents(label);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        loginButton = new Button("Logowanie");
        registrationButton = new Button("Rejestracja");
        horizontalLayout.addComponents(loginButton, registrationButton);

        HorizontalLayout horizontalWelcomeLayout = new HorizontalLayout();
        verticalLayout = new VerticalLayout();

        login();
        registration();

        root.addComponents(welcomeLayout, horizontalWelcomeLayout, horizontalLayout, verticalLayout);
        setContent(root);
    }

    private void login() {
        loginButton.addClickListener(clickEvent -> {
            verticalLayout.removeAllComponents();

            TextField loginTextField = new TextField("Podaj login: ");
            PasswordField passwordField = new PasswordField("Podaj hasło: ");
            Label label = new Label();
            Button login = new Button("Zaloguj się");
            verticalLayout.addComponents(loginTextField, passwordField, label, login);

            login.addClickListener(event -> {
                Optional<User> optionalUser = userRepository.findByLogin(loginTextField.getValue());
                if (optionalUser.isPresent()) {
                    String givenPassword = Hashing.sha512().hashString(passwordField.getValue(), StandardCharsets.UTF_8).toString();
                    if (optionalUser.get().getPassword().equals(givenPassword)) {
                        Notification.show("Logowanie udane!", "", Notification.Type.HUMANIZED_MESSAGE);
                        root.removeAllComponents();
                        root.addComponent(
                                new LoggedUI(optionalUser.get(), lessonSeriesRepository, courseRepository,
                                        messageRepository, userRepository, lessonRepository, requestRepository));

                        Button logout = new Button("Wyloguj");
                        logout.addClickListener(event1 -> {
                            root.removeAllComponents();
                            init(null);
                        });
                        root.addComponent(logout);
                    } else {
                        Notification.show("Nieprawidłowe hasło!",
                                "", Notification.Type.ERROR_MESSAGE);
                    }
                } else {
                    Notification.show("Podany login nie istnieje!",
                            "", Notification.Type.ERROR_MESSAGE);
                }
            });
        });
    }

    private void registration() {
        registrationButton.addClickListener(clickEvent -> {
            verticalLayout.removeAllComponents();

            TextField firstNameTextField = new TextField("Podaj imię: ");
            TextField lastNameTextField = new TextField("Podaj nazwisko: ");
            TextField loginTextField = new TextField("Podaj login: ");
            PasswordField passwordField = new PasswordField("Podaj hasło: ");
            Label label = new Label();
            Button register = new Button("Zarejestruj się");
            verticalLayout.addComponents(firstNameTextField, lastNameTextField, loginTextField, passwordField, label, register);

            register.addClickListener(event -> {
                String firstNameText = firstNameTextField.getValue();
                String lastNameText = lastNameTextField.getValue();
                String loginText = loginTextField.getValue();
                String passwordText = passwordField.getValue();

                if (firstNameText.length() > 2 && lastNameText.length() > 2 && loginText.length() > 2) {
                    if (passwordText.length() > 5 && passwordText.length() < 20) {
                        if (checkPassword(passwordText)) {
                            if (!userRepository.findAllLogin().contains(loginText)) {
                                User user = new User(0L, loginText,
                                        Hashing.sha512().hashString(passwordText, StandardCharsets.UTF_8).toString(),
                                        UserType.STUDENT, firstNameText, lastNameText);
                                userRepository.save(user);

                                firstNameTextField.setValue("");
                                lastNameTextField.setValue("");
                                loginTextField.setValue("");
                                passwordField.setValue("");
                                Notification.show("Rejestracja udana!",
                                        "", Notification.Type.HUMANIZED_MESSAGE);
                            } else {
                                Notification.show("Podany login już istnieje!",
                                        "", Notification.Type.ERROR_MESSAGE);
                            }
                        } else {
                            Notification.show("Hasło musi składać się z conajmniej: 1 małej litery, 1 dużej litery i 1 cyfry!",
                                    "", Notification.Type.ERROR_MESSAGE);
                        }
                    } else {
                        Notification.show("Hasło musi składać się z conajmniej 6 znaków!",
                                "", Notification.Type.ERROR_MESSAGE);
                    }
                } else {
                    Notification.show("Dane muszą składać się z conajmniej 3 znaków!",
                            "", Notification.Type.ERROR_MESSAGE);
                }
            });
        });
    }

}