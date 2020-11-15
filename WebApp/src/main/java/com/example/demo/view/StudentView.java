package com.example.demo.view;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class StudentView extends VerticalLayout {

    private CourseRepository courseRepository;
    private MessageRepository messageRepository;
    private RequestRepository requestRepository;

    private VerticalLayout verticalLayout;
    private VerticalLayout requestsLayout;
    private VerticalLayout lessonSeriesLayout;
    private VerticalLayout messagesLayout;

    private User currentUser;

    public StudentView(User currentUser, CourseRepository courseRepository,
                       MessageRepository messageRepository, RequestRepository requestRepository) {
        this.currentUser = currentUser;
        this.courseRepository = courseRepository;
        this.messageRepository = messageRepository;
        this.requestRepository = requestRepository;

        HorizontalLayout welcomeLayout = new HorizontalLayout();
        String welcomeTitle = "Zalogowano jako: " + currentUser.getFirstName() + " "
                + currentUser.getLastName() + " | " + currentUser.getUserType().roleName;
        Label label = new Label(welcomeTitle);
        welcomeLayout.addComponents(label);

        HorizontalLayout horizontalWelcomeLayout = new HorizontalLayout();
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        verticalLayout = new VerticalLayout();

        initMessagesForStudentLayout();
        initLessonSeriesForStudentLayout();
        initRequestsForStudentLayout();

        Button messages = new Button("Powiadomienia");
        messages.addClickListener(event -> {
            verticalLayout.removeAllComponents();
            verticalLayout.addComponent(messagesLayout);
        });

        Button lessonSeries = new Button("Bloki");
        lessonSeries.addClickListener(event -> {
            verticalLayout.removeAllComponents();
            verticalLayout.addComponent(lessonSeriesLayout);
        });

        Button requests = new Button("Zgłoszenia");
        requests.addClickListener(event -> {
            verticalLayout.removeAllComponents();
            verticalLayout.addComponent(requestsLayout);
        });

        horizontalLayout.addComponents(messages, lessonSeries, requests);
        addComponents(welcomeLayout, horizontalWelcomeLayout, horizontalLayout, verticalLayout);
    }

    private void initMessagesForStudentLayout() {
        messagesLayout = new VerticalLayout();

        List<Message> messages = messageRepository.findAllByStudent(currentUser);
        Grid<Message> messageGrid = new Grid<>();
        messageGrid.addColumn(Message::getId).setCaption("ID").setWidth(70);
        messageGrid.addColumn(Message::getSubject).setCaption("Temat").setWidth(200);
        messageGrid.addColumn(Message::getText).setCaption("Treść").setWidth(330);
        messageGrid.addColumn(z -> z.getLesson().getSubject()).setCaption("Zajęcia").setWidth(300);
        messageGrid.setWidth("900");
        messageGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        messageGrid.setDataProvider(DataProvider.ofCollection(messages));

        messagesLayout.addComponents(messageGrid);
    }

    private void initLessonSeriesForStudentLayout() {
        lessonSeriesLayout = new VerticalLayout();

        Set<LessonSeries> lessonSeriesSet = new HashSet<>();
        requestRepository.findAllByStudent(currentUser)
                .stream()
                .filter(z -> z.getAccepted().equals("tak"))
                .map(z -> z.getCourse().getLessonSeriesList())
                .flatMap(List::stream)
                .forEach(lessonSeriesSet::add);

        ComboBox<LessonSeries> lessonSeriesComboBox = new ComboBox<>("Wybierz blok");
        lessonSeriesComboBox.setEmptySelectionAllowed(false);
        lessonSeriesComboBox.setDataProvider(DataProvider.ofCollection(lessonSeriesSet));
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

        lessonSeriesComboBox.addValueChangeListener(event -> {
            lessonList.clear();
            lessonList.addAll(event.getValue().getLessons());
            provider.refreshAll();
        });
        lessonSeriesLayout.addComponents(lessonSeriesComboBox, lessonGrid);
    }

    private void initRequestsForStudentLayout() {
        requestsLayout = new VerticalLayout();

        List<Course> courseList = getCoursesToRequest();
        ComboBox<Course> courseComboBox = new ComboBox<>("Wybierz kurs do zgłoszenia");
        courseComboBox.setEmptySelectionAllowed(false);
        courseComboBox.setItemCaptionGenerator(Course::getName);
        courseComboBox.setDataProvider(DataProvider.ofCollection(courseList));
        courseComboBox.setWidth("300");

        Button addButton = new Button("Wyślij zgłoszenie");
        addButton.addClickListener(event1 -> {
            if (courseComboBox.getValue() != null) {
                Course course = courseComboBox.getValue();
                Request request = new Request();
                request.setDate(LocalDate.now());
                request.setAccepted("");
                request.setStudent(currentUser);
                request.setCourse(course);
                requestRepository.save(request);

                List<Course> refreshedCourseList = getCoursesToRequest();
                courseComboBox.setDataProvider(DataProvider.ofCollection(refreshedCourseList));
                courseComboBox.setValue(null);

                Notification.show("Wysłano zgłoszenie na wybrany kurs!", "",
                        Notification.Type.HUMANIZED_MESSAGE);
            } else {
                Notification.show("Nie wybrano kursu do zgłoszenia!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponents(addButton);
        requestsLayout.addComponents(courseComboBox, horizontalLayout);
    }

    private List<Course> getCoursesToRequest() {
        Set<Long> requestedCourses = requestRepository.findAllByStudent(currentUser)
                .stream()
                .filter(z -> !z.getAccepted().equals("nie"))
                .map(z -> z.getCourse().getId())
                .collect(Collectors.toSet());

        return courseRepository.findAll()
                .stream()
                .filter(course -> !requestedCourses.contains(course.getId()))
                .collect(Collectors.toList());
    }

}
