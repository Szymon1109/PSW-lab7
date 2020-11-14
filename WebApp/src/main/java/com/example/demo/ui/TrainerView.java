package com.example.demo.ui;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.vaadin.ui.*;

import java.util.*;
import java.util.stream.Collectors;

public class TrainerView extends VerticalLayout {

    private LessonSeriesRepository lessonSeriesRepository;
    private CourseRepository courseRepository;
    private MessageRepository messageRepository;
    private LessonRepository lessonRepository;
    private RequestRepository requestRepository;

    private VerticalLayout verticalLayout;
    private VerticalLayout lessonsLayout;
    private VerticalLayout messagesLayout;

    private User currentUser;

    public TrainerView(User currentUser, LessonSeriesRepository lessonSeriesRepository,
                       CourseRepository courseRepository, MessageRepository messageRepository,
                       LessonRepository lessonRepository, RequestRepository requestRepository) {
        this.currentUser = currentUser;
        this.lessonSeriesRepository = lessonSeriesRepository;
        this.courseRepository = courseRepository;
        this.messageRepository = messageRepository;
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

        initLessonsForTrainerLayout();
        initMessagesForTrainerLayout();

        Button lessons = new Button("Lista zajęć");
        lessons.addClickListener(event -> {
            verticalLayout.removeAllComponents();
            verticalLayout.addComponent(lessonsLayout);
        });

        Button messages = new Button("Powiadomienia");
        messages.addClickListener(event -> {
            verticalLayout.removeAllComponents();
            verticalLayout.addComponent(messagesLayout);
        });

        horizontalLayout.addComponents(lessons, messages);
        addComponents(welcomeLayout, horizontalWelcomeLayout, horizontalLayout, verticalLayout);
    }

    private void initMessagesForTrainerLayout() {
        messagesLayout = new VerticalLayout();

        List<Lesson> lessonList = lessonRepository.findAllByTrainer(currentUser);
        lessonList = lessonList.stream()
                .filter(z -> messageRepository.findAllByLesson(z) != null)
                .collect(Collectors.toList());

        ComboBox<Lesson> messagesComboBox = new ComboBox<>("Zajęcia");
        messagesComboBox.setItems(lessonList);
        messagesComboBox.setItemCaptionGenerator(Lesson::getSubject);
        messagesComboBox.setWidth("250");
        messagesComboBox.setEmptySelectionAllowed(false);

        Grid<Message> messagesGrid = new Grid<>();
        messagesGrid.addColumn(Message::getId).setCaption("ID").setWidth(70);
        messagesGrid.addColumn(Message::getSubject).setCaption("Temat").setWidth(200);
        messagesGrid.addColumn(Message::getText).setCaption("Treść").setWidth(330);
        messagesGrid.addColumn(p -> p.getStudent().getLogin()).setCaption("Uczestnik").setWidth(150);
        messagesGrid.setWidth("750");
        messagesComboBox.addValueChangeListener(event -> {
            List<Message> messageList = messageRepository.findAllByLesson(event.getValue());
            messagesGrid.setItems(messageList);
        });

        Button sendButton = new Button("Wyślij powiadomienie");
        sendButton.addClickListener(event -> initWindowForMessages());

        messagesLayout.addComponents(messagesComboBox, messagesGrid, sendButton);
    }

    private void initWindowForMessages() {
        Window windowAddNotification = new Window("Dodaj powiadomienie");
        windowAddNotification.setWidth(400.0f, Unit.PIXELS);
        windowAddNotification.setModal(true);
        windowAddNotification.setResizable(false);
        windowAddNotification.center();
        windowAddNotification.setDraggable(true);

        FormLayout formWindowNotification = new FormLayout();
        formWindowNotification.setMargin(true);

        ComboBox<Lesson> lessonComboBox = new ComboBox<>("Zajęcia");
        lessonComboBox.setItems(lessonRepository.findAllByTrainer(currentUser));
        lessonComboBox.setItemCaptionGenerator(Lesson::getSubject);
        lessonComboBox.setEmptySelectionAllowed(false);

        TextField subject = new TextField("Temat");
        TextArea text = new TextArea("Treść");
        ComboBox<User> userComboBox = new ComboBox<>("Uczestnik");
        userComboBox.setItemCaptionGenerator(User::getLogin);
        userComboBox.setEmptySelectionAllowed(false);

        lessonComboBox.addValueChangeListener(event -> {
            Course course = courseRepository.findAllByLessonSeriesListContains(
                    lessonSeriesRepository.findAllByLessonsContains(lessonComboBox.getValue()).get(0)).get(0);
            List<User> userList = requestRepository.findAllByCourse(course)
                    .stream()
                    .map(Request::getStudent)
                    .collect(Collectors.toList());
            userComboBox.setItems(userList);
        });

        Button sendButton = new Button("Wyślij");
        sendButton.addClickListener(event -> {
            if (subject.getValue().equals("")) {
                Notification.show("Wpisz temat!", Notification.Type.ERROR_MESSAGE);

            } else if (userComboBox.getValue() == null) {
                Notification.show("Wprowadź uczestnika!", Notification.Type.ERROR_MESSAGE);

            } else if (lessonComboBox.getValue() == null) {
                Notification.show("Wprowadź zajęcia!", Notification.Type.ERROR_MESSAGE);

            } else {
                Message message = new Message(0L, subject.getValue(), text.getValue(),
                        userComboBox.getValue(), lessonComboBox.getValue());
                messageRepository.save(message);
                Notification.show("Wysłano powiadomienie", "",
                        Notification.Type.HUMANIZED_MESSAGE);
                subject.clear();
                text.clear();
            }
        });
        formWindowNotification.addComponents(lessonComboBox, subject, text, userComboBox, sendButton);

        windowAddNotification.setContent(formWindowNotification);
        getUI().addWindow(windowAddNotification);
    }

    private void initLessonsForTrainerLayout() {
        lessonsLayout = new VerticalLayout();

        List<Lesson> lessonList = lessonRepository.findAllByTrainer(currentUser);
        Grid<Lesson> lessonGrid = new Grid<>();
        lessonGrid.setItems(lessonList);
        lessonGrid.addColumn(Lesson::getId).setCaption("ID");
        lessonGrid.addColumn(Lesson::getSubject).setCaption("Temat");
        lessonGrid.addColumn(Lesson::getDate).setCaption("Data");

        lessonsLayout.addComponent(lessonGrid);
    }

}
