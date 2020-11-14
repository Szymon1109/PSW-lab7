package com.example.demo.ui;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.util.UserType;
import com.google.common.hash.Hashing;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.util.DataUtils.checkPassword;

public class AdminView extends VerticalLayout {

    private LessonSeriesRepository lessonSeriesRepository;
    private CourseRepository courseRepository;
    private UserRepository userRepository;
    private LessonRepository lessonRepository;
    private RequestRepository requestRepository;

    private VerticalLayout verticalLayout;
    private VerticalLayout coursesLayout;
    private VerticalLayout requestsLayout;
    private VerticalLayout lessonSeriesLayout;
    private VerticalLayout lessonsLayout;
    private VerticalLayout trainersLayout;

    private List<Course> courseList;
    private List<User> studentList;
    private List<User> trainerList;

    public AdminView(User currentUser, LessonSeriesRepository lessonSeriesRepository,
                     CourseRepository courseRepository, UserRepository userRepository,
                     LessonRepository lessonRepository, RequestRepository requestRepository) {
        this.lessonSeriesRepository = lessonSeriesRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.requestRepository = requestRepository;

        HorizontalLayout welcomeLayout = new HorizontalLayout();
        String welcomeTitle = "Zalogowano jako: " + currentUser.getFirstName() + " "
                + currentUser.getLastName() + " | " + currentUser.getUserType().roleName;
        Label label = new Label(welcomeTitle);
        welcomeLayout.addComponents(label);

        HorizontalLayout horizontalWelcomeLayout = new HorizontalLayout();
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        verticalLayout = new VerticalLayout();

        courseList = courseRepository.findAll();
        studentList = userRepository.findAll()
                .stream()
                .filter(u -> u.getUserType().equals(UserType.STUDENT))
                .collect(Collectors.toList());
        trainerList = userRepository.findAll()
                .stream()
                .filter(u -> u.getUserType().equals(UserType.TRAINER))
                .collect(Collectors.toList());

        initCoursesLayout();
        initRequestsLayout();
        initLessonSeriesLayout();
        initLessonsLayout();
        initTrainersLayout();

        Button courses = new Button("Kursy");
        courses.addClickListener(event -> {
            verticalLayout.removeAllComponents();
            verticalLayout.addComponent(coursesLayout);
        });

        Button requests = new Button("Zgłoszenia");
        requests.addClickListener(event -> {
            verticalLayout.removeAllComponents();
            verticalLayout.addComponent(requestsLayout);
        });

        Button lessonSeries = new Button("Bloki");
        lessonSeries.addClickListener(event -> {
            verticalLayout.removeAllComponents();
            verticalLayout.addComponent(lessonSeriesLayout);
        });

        Button lessons = new Button("Zajęcia");
        lessons.addClickListener(event -> {
            verticalLayout.removeAllComponents();
            verticalLayout.addComponent(lessonsLayout);
        });

        Button trainers = new Button("Prowadzący");
        trainers.addClickListener(event -> {
            verticalLayout.removeAllComponents();
            verticalLayout.addComponent(trainersLayout);
        });

        horizontalLayout.addComponents(requests, courses, lessonSeries, lessons, trainers);
        addComponents(welcomeLayout, horizontalWelcomeLayout, horizontalLayout, verticalLayout);
    }

    private void initCoursesLayout() {
        coursesLayout = new VerticalLayout();
        TextField courseName = new TextField("Nazwa");
        Button addButton = new Button("Utwórz");

        addButton.addClickListener(event1 -> {
            if (!courseName.getValue().equals("")) {
                if (courseRepository.findAllByName(courseName.getValue()).isEmpty()) {
                    Course course = courseRepository.save(
                            new Course(0L, courseName.getValue(), null));
                    courseList.add(course);
                    courseName.setValue("");

                    Notification.show("Utworzono kurs", "",
                            Notification.Type.HUMANIZED_MESSAGE);
                } else {
                    Notification.show("Kurs o podanej nazwie istnieje!", "",
                            Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        coursesLayout.addComponents(courseName, addButton);

        Label emptyLabel = new Label();
        ComboBox<Course> courseComboBox = new ComboBox<>("Wybierz kurs do usunięcia");
        courseComboBox.setEmptySelectionAllowed(false);
        courseComboBox.setDataProvider(DataProvider.ofCollection(courseList));
        courseComboBox.setItemCaptionGenerator(Course::getName);
        courseComboBox.setWidth("250");
        courseComboBox.addValueChangeListener(event ->
                courseComboBox.setDataProvider(DataProvider.ofCollection(courseList)));

        Button deleteButton = new Button("Usuń");
        deleteButton.addClickListener(event1 -> {
            if (courseComboBox.getValue() != null) {
                requestRepository.deleteAllByCourse(courseComboBox.getValue());
                courseRepository.delete(courseComboBox.getValue());

                courseList.remove(courseComboBox.getValue());
                courseComboBox.setDataProvider(DataProvider.ofCollection(courseList));
                courseComboBox.setValue(null);

                Notification.show("Kurs został usunięty!", "",
                        Notification.Type.HUMANIZED_MESSAGE);
            } else {
                Notification.show("Nie wybrano kursu!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        coursesLayout.addComponents(emptyLabel, courseComboBox, deleteButton);

        Label empty = new Label();
        TextField courseNameToUpdate = new TextField("Nazwa");
        courseNameToUpdate.setWidth("250");

        ComboBox<Course> courseToUpdate = new ComboBox<>("Wybierz kurs do aktualizacji");
        courseToUpdate.setEmptySelectionAllowed(false);
        courseToUpdate.setDataProvider(DataProvider.ofCollection(courseList));
        courseToUpdate.setItemCaptionGenerator(z -> z.getId().toString());
        courseToUpdate.setWidth("100");
        courseToUpdate.addValueChangeListener(event -> {
            if (courseToUpdate.getValue() != null) {
                courseToUpdate.setDataProvider(DataProvider.ofCollection(courseList));
                courseNameToUpdate.setValue(courseToUpdate.getValue().getName());
            }
        });

        Button updateButton = new Button("Aktualizuj");
        updateButton.addClickListener(event1 -> {
            if (courseToUpdate.getValue() != null && !courseNameToUpdate.getValue().equals("")) {
                Course course = new Course(courseToUpdate.getValue().getId(), courseNameToUpdate.getValue(),
                        courseToUpdate.getValue().getLessonSeriesList());
                courseRepository.save(course);

                courseList = courseRepository.findAll();
                courseNameToUpdate.setValue("");
                courseToUpdate.setValue(null);
                courseToUpdate.setDataProvider(DataProvider.ofCollection(courseList));
                courseComboBox.setDataProvider(DataProvider.ofCollection(courseList));

                Notification.show("Kurs został zaktualizowany!", "",
                        Notification.Type.HUMANIZED_MESSAGE);
            } else {
                Notification.show("Nie wybrano danych!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        coursesLayout.addComponents(empty, courseToUpdate, courseNameToUpdate, updateButton);
    }

    private void initRequestsLayout() {
        requestsLayout = new VerticalLayout();

        ComboBox<User> userComboBox = new ComboBox<>("Wybierz uczestnika");
        userComboBox.setEmptySelectionAllowed(false);
        userComboBox.setDataProvider(DataProvider.ofCollection(studentList));
        userComboBox.setItemCaptionGenerator(User::getLogin);

        ComboBox<Request> requestComboBox = new ComboBox<>("Wybierz zgłoszenie");
        requestComboBox.setEmptySelectionAllowed(false);
        requestComboBox.setItemCaptionGenerator(z -> z.getCourse().getName() + ": " + z.getDate().toString());
        requestComboBox.setWidth("300");
        userComboBox.addValueChangeListener(event -> requestComboBox.setItems(
                requestRepository.findAllByStudent(userComboBox.getValue())
                        .stream()
                        .filter(z -> z.getAccepted().equals(""))
                        .collect(Collectors.toList())));

        Button acceptButton = new Button("Potwierdź");
        acceptButton.addClickListener(event1 -> {
            if (requestComboBox.getValue() != null) {
                Request request = requestComboBox.getValue();
                request.setAccepted("tak");
                requestRepository.save(request);

                userComboBox.setValue(null);
                requestComboBox.setValue(null);

                Notification.show("Potwierdzono zgłoszenie!", "",
                        Notification.Type.HUMANIZED_MESSAGE);
            } else {
                Notification.show("Nie wybrano zgłoszenia!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });

        Button rejectButton = new Button("Odrzuć");
        rejectButton.addClickListener(event1 -> {
            if (requestComboBox.getValue() != null) {
                Request request = requestComboBox.getValue();
                request.setAccepted("nie");
                requestRepository.save(request);

                userComboBox.setValue(null);
                requestComboBox.setValue(null);

                Notification.show("Odrzucono zgłoszenie!", "",
                        Notification.Type.HUMANIZED_MESSAGE);
            } else {
                Notification.show("Nie wybrano zgłoszenia!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponents(acceptButton, rejectButton);
        requestsLayout.addComponents(userComboBox, requestComboBox, horizontalLayout);
    }

    private void initLessonSeriesLayout() {
        lessonSeriesLayout = new VerticalLayout();

        List<Course> courseList = courseRepository.findAll();
        ComboBox<Course> courseComboBox = new ComboBox<>("Wybierz kurs");
        courseComboBox.setEmptySelectionAllowed(false);
        courseComboBox.setDataProvider(DataProvider.ofCollection(courseList));
        courseComboBox.setItemCaptionGenerator(Course::getName);
        courseComboBox.setWidth("250");

        Grid<LessonSeries> lessonSeriesGrid = new Grid<>();
        lessonSeriesGrid.addColumn(LessonSeries::getId).setCaption("ID").setWidth(100);
        lessonSeriesGrid.addColumn(LessonSeries::getName).setCaption("Nazwa").setWidth(550);
        lessonSeriesGrid.setWidth("650");
        lessonSeriesGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        List<LessonSeries> lessonSeriesList = new ArrayList<>();
        ListDataProvider<LessonSeries> provider = DataProvider.ofCollection(lessonSeriesList);
        lessonSeriesGrid.setDataProvider(provider);

        courseComboBox.addValueChangeListener(event -> {
            List<Course> list = courseRepository.findAll();
            courseComboBox.setDataProvider(DataProvider.ofCollection(list));
            lessonSeriesList.clear();
            if (event.getValue().getLessonSeriesList() != null) {
                lessonSeriesList.addAll(event.getValue().getLessonSeriesList());
            }
            provider.refreshAll();
        });

        Button deleteButton = new Button("Usuń");
        deleteButton.addClickListener(event -> {
            if (!lessonSeriesGrid.getSelectedItems().isEmpty()) {
                LessonSeries lessonSeries = lessonSeriesGrid.getSelectedItems().iterator().next();
                lessonSeriesList.remove(lessonSeries);
                provider.refreshAll();

                Course course = courseComboBox.getValue();
                List<LessonSeries> currentList = course.getLessonSeriesList();
                currentList.remove(lessonSeries);
                course.setLessonSeriesList(currentList);
                courseRepository.save(course);

                Notification.show("Usunięto blok", "",
                        Notification.Type.HUMANIZED_MESSAGE);
            }
        });

        Label label = new Label();
        TextField lessonSeriesName = new TextField("Nazwa bloku");

        Button addButton = new Button("Dodaj blok");
        addButton.addClickListener(event -> {
            if (!lessonSeriesName.getValue().equals("")) {
                if (lessonSeriesRepository.findAllByName(lessonSeriesName.getValue()).isEmpty()) {
                    LessonSeries lessonSeries = lessonSeriesRepository.save(
                            new LessonSeries(0L, lessonSeriesName.getValue(), null));
                    Course course = courseComboBox.getValue();
                    List<LessonSeries> currentList = course.getLessonSeriesList() != null ?
                            course.getLessonSeriesList() : new ArrayList<>();
                    currentList.add(lessonSeries);
                    course.setLessonSeriesList(currentList);
                    courseRepository.save(course);

                    lessonSeriesList.add(lessonSeries);
                    provider.refreshAll();
                    lessonSeriesName.setValue("");

                    Notification.show("Utworzono blok", "",
                            Notification.Type.HUMANIZED_MESSAGE);
                } else {
                    Notification.show("Blok o podanej nazwie istnieje!", "",
                            Notification.Type.ERROR_MESSAGE);
                }
            } else {
                Notification.show("Nie podano nazwy bloku!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        lessonSeriesLayout.addComponents(courseComboBox, lessonSeriesGrid,
                deleteButton, label, lessonSeriesName, addButton);

        Label empty = new Label();
        TextField lessonSeriesNameToUpdate = new TextField("Nazwa");
        lessonSeriesNameToUpdate.setWidth("250");

        ComboBox<LessonSeries> lessonSeriesComboBox = new ComboBox<>("Wybierz blok do aktualizacji");
        lessonSeriesComboBox.setEmptySelectionAllowed(false);
        lessonSeriesComboBox.setDataProvider(provider);
        lessonSeriesComboBox.setItemCaptionGenerator(b -> b.getId().toString());
        lessonSeriesComboBox.setWidth("100");
        lessonSeriesComboBox.addValueChangeListener(event -> {
            if (lessonSeriesComboBox.getValue() != null) {
                lessonSeriesNameToUpdate.setValue(lessonSeriesComboBox.getValue().getName());
            }
        });

        Button updateButton = new Button("Aktualizuj");
        updateButton.addClickListener(event1 -> {
            if (lessonSeriesComboBox.getValue() != null && !lessonSeriesNameToUpdate.getValue().equals("")) {
                LessonSeries lessonSeries = new LessonSeries(lessonSeriesComboBox.getValue().getId(),
                        lessonSeriesNameToUpdate.getValue(), lessonSeriesComboBox.getValue().getLessons());
                lessonSeriesRepository.save(lessonSeries);

                lessonSeriesNameToUpdate.setValue("");
                lessonSeriesComboBox.setValue(null);
                lessonSeriesList.clear();
                Set<LessonSeries> set = new HashSet<>(Objects.requireNonNull(
                        courseRepository.findById(courseComboBox.getValue().getId())
                                .map(Course::getLessonSeriesList)
                                .orElse(null)));
                lessonSeriesList.addAll(set);
                provider.refreshAll();

                Notification.show("Blok został zaktualizowany!", "",
                        Notification.Type.HUMANIZED_MESSAGE);
            } else {
                Notification.show("Nie wybrano danych!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        lessonSeriesLayout.addComponents(empty, lessonSeriesComboBox, lessonSeriesNameToUpdate, updateButton);
    }

    private void initLessonsLayout() {
        lessonsLayout = new VerticalLayout();

        List<LessonSeries> lessonSeriesList = lessonSeriesRepository.findAll();
        ComboBox<LessonSeries> lessonSeriesComboBox = new ComboBox<>("Wybierz blok");
        lessonSeriesComboBox.setEmptySelectionAllowed(false);
        lessonSeriesComboBox.setDataProvider(DataProvider.ofCollection(lessonSeriesList));
        lessonSeriesComboBox.setItemCaptionGenerator(LessonSeries::getName);
        lessonSeriesComboBox.setWidth("250");

        Grid<Lesson> lessonGrid = new Grid<>();
        lessonGrid.addColumn(Lesson::getId).setCaption("ID").setWidth(70);
        lessonGrid.addColumn(Lesson::getSubject).setCaption("Temat").setWidth(300);
        lessonGrid.addColumn(Lesson::getDate).setCaption("Data").setWidth(130);
        lessonGrid.addColumn(z -> z.getTrainer() != null ? z.getTrainer().getLogin() : "")
                .setCaption("Prowadzący").setWidth(150);
        lessonGrid.setWidth("650");
        lessonGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        List<Lesson> lessonList = new ArrayList<>();
        ListDataProvider<Lesson> provider = DataProvider.ofCollection(lessonList);
        lessonGrid.setDataProvider(provider);

        lessonSeriesComboBox.addValueChangeListener(event1 -> {
            List<LessonSeries> list = lessonSeriesRepository.findAll();
            lessonSeriesComboBox.setDataProvider(DataProvider.ofCollection(list));
            lessonList.clear();
            if (event1.getValue().getLessons() != null) {
                lessonList.addAll(event1.getValue().getLessons());
            }
            provider.refreshAll();
        });

        Button deleteButton = new Button("Usuń");
        deleteButton.addClickListener(event -> {
            if (!lessonGrid.getSelectedItems().isEmpty()) {
                Lesson lesson = lessonGrid.getSelectedItems().iterator().next();
                lessonList.remove(lesson);
                provider.refreshAll();
                LessonSeries lessonSeries = lessonSeriesComboBox.getValue();
                List<Lesson> currentList = lessonSeries.getLessons();
                currentList.remove(lesson);
                lessonSeries.setLessons(currentList);
                lessonSeriesRepository.save(lessonSeries);

                Notification.show("Usunięto zajęcia", "",
                        Notification.Type.HUMANIZED_MESSAGE);
            }
        });

        Label label = new Label();
        TextField lessonSubject = new TextField("Temat zajęć");
        DateField dataField = new DateField("Data zajęć");

        ComboBox<User> trainerComboBox = new ComboBox<>("Wybierz prowadzącego");
        trainerComboBox.setEmptySelectionAllowed(false);
        trainerComboBox.setDataProvider(DataProvider.ofCollection(trainerList));
        trainerComboBox.setItemCaptionGenerator(User::getLogin);
        trainerComboBox.addValueChangeListener(event -> trainerComboBox.setDataProvider(
                DataProvider.ofCollection(trainerList = userRepository.findAll()
                        .stream()
                        .filter(u -> u.getUserType().equals(UserType.TRAINER))
                        .collect(Collectors.toList()))
        ));

        Button addButton = new Button("Dodaj zajęcia");
        addButton.addClickListener(event -> {
            if (!lessonSubject.getValue().equals("") &&
                    dataField.getValue() != null && trainerComboBox.getValue() != null) {
                if (lessonRepository.findAllBySubject(lessonSubject.getValue()).isEmpty()) {
                    Lesson lesson = lessonRepository.save(
                            new Lesson(0L, lessonSubject.getValue(),
                                    dataField.getValue(), trainerComboBox.getValue()));
                    LessonSeries lessonSeries = lessonSeriesComboBox.getValue();
                    List<Lesson> currentList = lessonSeries.getLessons() != null ?
                            lessonSeries.getLessons() : new ArrayList<>();
                    currentList.add(lesson);
                    lessonSeries.setLessons(currentList);
                    lessonSeriesRepository.save(lessonSeries);

                    lessonList.add(lesson);
                    provider.refreshAll();
                    lessonSubject.setValue("");
                    dataField.setValue(null);
                    trainerComboBox.setValue(null);

                    Notification.show("Utworzono zajęcia", "",
                            Notification.Type.HUMANIZED_MESSAGE);
                } else {
                    Notification.show("Zajęcia o podanej nazwie istnieją!", "",
                            Notification.Type.ERROR_MESSAGE);
                }
            } else {
                Notification.show("Nie podano wszystkich danych!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        lessonsLayout.addComponents(lessonSeriesComboBox, lessonGrid, deleteButton,
                label, lessonSubject, dataField, trainerComboBox, addButton);

        Label empty = new Label();

        TextField lessonSubjectToUpdate = new TextField("Temat zajęć");
        lessonSubjectToUpdate.setWidth("300");
        DateField lessonDateToUpdate = new DateField("Data zajęć");

        ComboBox<User> trainerComboBoxToUpdate = new ComboBox<>("Wybierz prowadzącego");
        trainerComboBoxToUpdate.setEmptySelectionAllowed(false);
        trainerComboBoxToUpdate.setDataProvider(DataProvider.ofCollection(trainerList));
        trainerComboBoxToUpdate.setItemCaptionGenerator(User::getLogin);
        trainerComboBoxToUpdate.addValueChangeListener(event -> trainerComboBoxToUpdate.setDataProvider(
                DataProvider.ofCollection(
                        trainerList = userRepository.findAll()
                                .stream()
                                .filter(u -> u.getUserType().equals(UserType.TRAINER))
                                .collect(Collectors.toList()))
        ));

        ComboBox<Lesson> lessonComboBox = new ComboBox<>("Wybierz zajęcia do aktualizacji");
        lessonComboBox.setEmptySelectionAllowed(false);
        lessonComboBox.setDataProvider(provider);
        lessonComboBox.setItemCaptionGenerator(b -> b.getId().toString());
        lessonComboBox.setWidth("100");
        lessonComboBox.addValueChangeListener(event -> {
            if (lessonComboBox.getValue() != null) {
                lessonSubjectToUpdate.setValue(lessonComboBox.getValue().getSubject());
                lessonDateToUpdate.setValue(lessonComboBox.getValue().getDate());
                trainerComboBoxToUpdate.setValue(lessonComboBox.getValue().getTrainer());
            }
        });

        Button updateButton = new Button("Zaktualizuj zajęcia");
        updateButton.addClickListener(event -> {
            if (!lessonSubjectToUpdate.getValue().equals("") &&
                    lessonDateToUpdate.getValue() != null && trainerComboBoxToUpdate.getValue() != null) {
                Lesson lesson = new Lesson(lessonComboBox.getValue().getId(), lessonSubjectToUpdate.getValue(),
                        lessonDateToUpdate.getValue(), trainerComboBoxToUpdate.getValue());
                lessonRepository.save(lesson);

                lessonComboBox.setValue(null);
                lessonSubjectToUpdate.setValue("");
                lessonDateToUpdate.setValue(null);
                trainerComboBoxToUpdate.setValue(null);
                lessonList.clear();
                lessonList.addAll(Objects.requireNonNull(
                        lessonSeriesRepository.findById(lessonSeriesComboBox.getValue().getId())
                                .map(LessonSeries::getLessons)
                                .orElse(null)));
                provider.refreshAll();

                Notification.show("Zaktualizowano zajęcia", "",
                        Notification.Type.HUMANIZED_MESSAGE);
            } else {
                Notification.show("Nie podano wszystkich danych!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        lessonsLayout.addComponents(empty, lessonComboBox, lessonSubjectToUpdate,
                lessonDateToUpdate, trainerComboBoxToUpdate, updateButton);
    }

    private void initTrainersLayout() {
        trainersLayout = new VerticalLayout();

        TextField firstNameTextField = new TextField("Imię");
        TextField lastNameTextField = new TextField("Nazwisko");
        TextField loginTextField = new TextField("Login");
        PasswordField passwordField = new PasswordField("Hasło");

        Button addButton = new Button("Dodaj");
        addButton.addClickListener(event -> {
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
                                    UserType.TRAINER, firstNameText, lastNameText);
                            userRepository.save(user);

                            firstNameTextField.setValue("");
                            lastNameTextField.setValue("");
                            loginTextField.setValue("");
                            passwordField.setValue("");

                            Notification.show("Dodano prowadzącego!", "",
                                    Notification.Type.HUMANIZED_MESSAGE);
                        } else {
                            Notification.show("Podany login już istnieje!", "",
                                    Notification.Type.ERROR_MESSAGE);
                        }
                    } else {
                        Notification.show("Hasło musi składać się z conajmniej: 1 małej litery, 1 dużej litery i 1 cyfry!",
                                "", Notification.Type.ERROR_MESSAGE);
                    }
                } else {
                    Notification.show("Hasło musi składać się z conajmniej 6 znaków!", "",
                            Notification.Type.ERROR_MESSAGE);
                }
            } else {
                Notification.show("Dane muszą składać się z conajmniej 3 znaków!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        trainersLayout.addComponents(firstNameTextField, lastNameTextField,
                loginTextField, passwordField, addButton);

        Label emptyLabel = new Label();

        ComboBox<User> trainersComboBox = new ComboBox<>("Wybierz prowadzącego do usunięcia");
        trainersComboBox.setEmptySelectionAllowed(false);
        trainersComboBox.setDataProvider(DataProvider.ofCollection(trainerList));
        trainersComboBox.setItemCaptionGenerator(User::getLogin);
        trainersComboBox.addValueChangeListener(event -> {
            trainerList = userRepository.findAll()
                    .stream()
                    .filter(u -> u.getUserType().equals(UserType.TRAINER))
                    .collect(Collectors.toList());
            trainersComboBox.setDataProvider(DataProvider.ofCollection(trainerList));
        });

        Button deleteButton = new Button("Usuń");
        deleteButton.addClickListener(event1 -> {
            if (trainersComboBox.getValue() != null) {
                User user = trainersComboBox.getValue();
                trainerList.remove(user);

                List<Lesson> lesson = lessonRepository.findAllByTrainer(user);
                lesson.forEach(z -> z.setTrainer(null));
                lessonRepository.saveAll(lesson);
                userRepository.delete(user);

                trainersComboBox.setDataProvider(DataProvider.ofCollection(trainerList));
                trainersComboBox.setValue(null);

                Notification.show("Prowadzący został usunięty!", "",
                        Notification.Type.HUMANIZED_MESSAGE);
            } else {
                Notification.show("Nie wybrano prowadzącego!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        trainersLayout.addComponents(emptyLabel, trainersComboBox, deleteButton);
    }

}
