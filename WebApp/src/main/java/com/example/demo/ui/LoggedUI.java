package com.example.demo.ui;

import com.example.demo.model.*;

import com.example.demo.repository.*;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoggedUI extends VerticalLayout {

    private BlokRepozytorium blokRepozytorium;
    private KursRepozytorium kursRepozytorium;
    private PowiadomienieRepozytorium powiadomienieRepozytorium;
    private UzytkownikRepozytorium uzytkownikRepozytorium;
    private ZajeciaRepozytorium zajeciaRepozytorium;
    private ZgloszenieRepozytorium zgloszenieRepozytorium;

    private Uzytkownik uzytkownik;
    private HorizontalLayout horizontalLayout;
    private VerticalLayout verticalLayout;
    private Button utworzKurs;
    private Button zarzadzanieZgodami;
    private Button zarzadzanieBlokami;
    private Button zarzadzanieZajeciami;

    private VerticalLayout utworzKursLayout;
    private VerticalLayout zgodyLayout;
    private VerticalLayout zarzadzanieBlokamiLayout;
    private VerticalLayout zarzadzanieZajeciamiLayout;

    private List<Kurs> listaKursow;
    private List<Uzytkownik> listaUzytkownikow;
    private List<Zgloszenie> zgloszenia;
    private List<Blok> listaBlokow;
    private List<Zajecia> listaZajec;

    public LoggedUI(Uzytkownik uzytkownik, BlokRepozytorium blokRepozytorium, KursRepozytorium kursRepozytorium,
                    PowiadomienieRepozytorium powiadomienieRepozytorium, UzytkownikRepozytorium uzytkownikRepozytorium,
                    ZajeciaRepozytorium zajeciaRepozytorium, ZgloszenieRepozytorium zgloszenieRepozytorium) {

        this.uzytkownik = uzytkownik;
        this.blokRepozytorium = blokRepozytorium;
        this.kursRepozytorium = kursRepozytorium;
        this.powiadomienieRepozytorium = powiadomienieRepozytorium;
        this.uzytkownikRepozytorium = uzytkownikRepozytorium;
        this.zajeciaRepozytorium = zajeciaRepozytorium;
        this.zgloszenieRepozytorium = zgloszenieRepozytorium;

        horizontalLayout = new HorizontalLayout();
        verticalLayout = new VerticalLayout();

        if(uzytkownik.getTyp().equals(Typ.ADMIN)) {
            listaKursow = kursRepozytorium.findAll();
            listaUzytkownikow = uzytkownikRepozytorium.findAll();

            /*projectsUserParticipationProvider = DataProvider.ofCollection(listaKursow);
            projectsUserParticipationProvider.setFilter(project -> projectParticipationRepository
                    .findAllByUserAndProject(user, project)
                    .size() > 0
            );
            projectsUserParticipationProvider.setSortComparator((o1, o2) -> o1.getName().compareTo(o2.getName()));*/

            initKursyLayout();
            initZgodaLayout();
            initBlokiLayout();
            initZajeciaLayout();

            utworzKurs = new Button("Kursy");
            utworzKurs.addClickListener(event -> {
                verticalLayout.removeAllComponents();
                verticalLayout.addComponent(utworzKursLayout);
            });

            zarzadzanieZgodami = new Button("Zgody");
            zarzadzanieZgodami.addClickListener(event -> {
                verticalLayout.removeAllComponents();
                verticalLayout.addComponent(zgodyLayout);
            });

            zarzadzanieBlokami = new Button("Bloki");
            zarzadzanieBlokami.addClickListener(event -> {
                verticalLayout.removeAllComponents();
                verticalLayout.addComponent(zarzadzanieBlokamiLayout);
            });

            zarzadzanieZajeciami = new Button("Zajęcia");
            zarzadzanieZajeciami.addClickListener(event -> {
                verticalLayout.removeAllComponents();
                verticalLayout.addComponent(zarzadzanieZajeciamiLayout);
            });

            utworzKurs.click();
            horizontalLayout.addComponents(utworzKurs, zarzadzanieZgodami, zarzadzanieBlokami, zarzadzanieZajeciami);
            addComponents(horizontalLayout, verticalLayout);
        }
    }

    private void initKursyLayout() {
        utworzKursLayout = new VerticalLayout();
        TextField nameProject = new TextField("Nazwa");
        Button createButton = new Button("Utwórz");

        createButton.addClickListener(event1 -> {
            if (nameProject.getValue() != null) {
                if (kursRepozytorium.findAllByNazwa(nameProject.getValue()).isEmpty()) {
                    Kurs kurs = kursRepozytorium.save(
                            new Kurs(0L, nameProject.getValue(), null));

                    listaKursow.add(kurs);
                    Notification.show("Utworzono kurs", "", Notification.Type.HUMANIZED_MESSAGE);

                } else
                    Notification.show("Kurs o podanej nazwie istnieje!", "", Notification.Type.ERROR_MESSAGE);
            }
        });

        utworzKursLayout.addComponents(nameProject, createButton);

        Label emptyLabel = new Label();
        ComboBox<Kurs> kursComboBox = new ComboBox<>("Wybierz kurs do usunięcia");
        kursComboBox.setEmptySelectionAllowed(false);
        kursComboBox.setDataProvider(DataProvider.ofCollection(listaKursow));
        kursComboBox.setItemCaptionGenerator(Kurs::getNazwa);
        kursComboBox.setWidth("250");

        Button deleteKursButton = new Button("Usuń");
        deleteKursButton.addClickListener(event1 -> {
            if (kursComboBox.getValue() != null) {
                kursRepozytorium.delete(kursComboBox.getValue());
                Notification.show("Kurs został usunięty!", "", Notification.Type.HUMANIZED_MESSAGE);

            } else
                Notification.show("Nie wybrano kursu!", "", Notification.Type.ERROR_MESSAGE);
        });

        utworzKursLayout.addComponents(emptyLabel, kursComboBox, deleteKursButton);

        //TODO:
        //update kursy
    }

    private void initZgodaLayout() {
        zgodyLayout = new VerticalLayout();

        ComboBox<Uzytkownik> uzytkownikComboBox = new ComboBox<>("Wybierz użytkownika");
        uzytkownikComboBox.setEmptySelectionAllowed(false);
        uzytkownikComboBox.setDataProvider(DataProvider.ofCollection(listaUzytkownikow));
        uzytkownikComboBox.setItemCaptionGenerator(Uzytkownik::getLogin);
        uzytkownikComboBox.setWidth("250");

        zgloszenia = zgloszenieRepozytorium.findAllByUczestnik(uzytkownikComboBox.getValue());
        zgloszenia.stream().filter(z -> z.getZgoda() == null);

        ComboBox<Zgloszenie> zgloszenieComboBox = new ComboBox<>("Wybierz zgłoszenie");
        zgloszenieComboBox.setEmptySelectionAllowed(false);
        zgloszenieComboBox.setItemCaptionGenerator(Zgloszenie::toString);
        zgloszenieComboBox.setWidth("250");
        uzytkownikComboBox.addValueChangeListener(event -> zgloszenieComboBox.setItems(zgloszenia));

        Button potwierdz = new Button("Potwierdź");
        potwierdz.addClickListener(event1 -> {
            if (zgloszenieComboBox.getValue() != null) {
                zgloszenieComboBox.getValue().setZgoda(true);
                Notification.show("Potwierdzono zgłoszenie!", "", Notification.Type.HUMANIZED_MESSAGE);
            } else
                Notification.show("Nie wybrano zgłoszenia!", "", Notification.Type.ERROR_MESSAGE);
        });

        Button odrzuc = new Button("Odrzuć");
        potwierdz.addClickListener(event1 -> {
            if (zgloszenieComboBox.getValue() != null) {
                zgloszenieComboBox.getValue().setZgoda(false);
                Notification.show("Odrzucono zgłoszenie!", "", Notification.Type.HUMANIZED_MESSAGE);

            } else
                Notification.show("Nie wybrano zgłoszenia!", "", Notification.Type.ERROR_MESSAGE);
        });

        horizontalLayout.addComponents(potwierdz, odrzuc);
        zgodyLayout.addComponents(uzytkownikComboBox, zgloszenieComboBox, horizontalLayout);
    }

    private void initBlokiLayout() {
        zarzadzanieBlokamiLayout = new VerticalLayout();

        ComboBox<Kurs> kursComboBox = new ComboBox<>("Wybierz kurs");
        kursComboBox.setEmptySelectionAllowed(false);
        kursComboBox.setDataProvider(DataProvider.ofCollection(listaKursow));
        kursComboBox.setItemCaptionGenerator(Kurs::getNazwa);

        Grid<Blok> blokGrid = new Grid<>();
        blokGrid.addColumn(Blok::getId).setCaption("ID");
        blokGrid.addColumn(Blok::getNazwa).setCaption("Nazwa");
        blokGrid.setWidth("650");
        blokGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        listaBlokow = kursComboBox.getValue().getBlok();
        ListDataProvider<Blok> provider = DataProvider.ofCollection(listaBlokow);
        blokGrid.setDataProvider(provider);

        kursComboBox.addValueChangeListener(event1 -> {
            listaBlokow.clear();
            listaBlokow.addAll(event1.getValue().getBlok());
            provider.refreshAll();
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        Button deleteButton = new Button("Usuń");
        deleteButton.addClickListener(event -> {
            if (!blokGrid.getSelectedItems().isEmpty()) {
                Blok blok = blokGrid.getSelectedItems().iterator().next();
                listaBlokow.remove(blok);
                provider.refreshAll();
                kursRepozytorium.deleteAllByBlok(blok);
                blokRepozytorium.delete(blok);
                Notification.show("Usunięto blok", "", Notification.Type.HUMANIZED_MESSAGE);
            }
        });

        Label label = new Label();
        TextField nazwaBloku = new TextField("Nazwa bloku");

        Button addButton = new Button("Dodaj blok");
        addButton.addClickListener(event -> {
            if (nazwaBloku.getValue() != null) {
                if (blokRepozytorium.findAllByNazwa(nazwaBloku.getValue()).isEmpty()) {

                    Blok blok = blokRepozytorium.save(
                            new Blok(0L, nazwaBloku.getValue(), null));

                    List<Blok> currentList = kursComboBox.getValue().getBlok();
                    currentList.add(blok);
                    kursComboBox.getValue().setBlok(currentList);

                    listaBlokow.add(blok);
                    provider.refreshAll();
                    Notification.show("Utworzono kurs", "", Notification.Type.HUMANIZED_MESSAGE);

                } else
                    Notification.show("Kurs o podanej nazwie istnieje!", "", Notification.Type.ERROR_MESSAGE);
            } else
                Notification.show("Nie podano nazwy kursu!", "", Notification.Type.ERROR_MESSAGE);
        });

        zarzadzanieBlokamiLayout.addComponents(kursComboBox, blokGrid, deleteButton, label, nazwaBloku, addButton);
    }

    private void initZajeciaLayout() {
        zarzadzanieZajeciamiLayout = new VerticalLayout();
        ComboBox<Project> projectComboBox = new ComboBox<>("Select project");
        projectComboBox.setEmptySelectionAllowed(true);
        projectComboBox.setDataProvider(projectsUserParticipationProvider);
        projectComboBox.setItemCaptionGenerator(Project::getName);

        ComboBox<Sprint> sprintComboBox = new ComboBox<>("Select sprint");
        sprintComboBox.setEmptySelectionAllowed(true);
        sprintComboBox.setItemCaptionGenerator(item -> item.getFromLocalDate().toString() +
                " - " + item.getToLocalDate().toString());
        sprintComboBox.setWidth("250");
//        if (!projects.isEmpty()) {
//            projectComboBox.setValue(projects.get(0));
//            sprintComboBox.setValue(sprintRepository.findAllByProject(projects.get(0))
//                    .stream()
//                    .filter(sprint -> !sprint.getFromLocalDate().isBefore(LocalDate.now()))
//                    .min(Comparator.comparing(Sprint::getFromLocalDate)).orElse(new Sprint()));
//        }
        TextField findByNameTextField = new TextField("Find task by name");
        Button chooseToAddTaskButton = new Button("Add task");
        VerticalLayout verticalLayout = new VerticalLayout();
        TextField nameTextField = new TextField("Name");
        TextField descriptionTextField = new TextField("Description");
        TextField wageTextField = new TextField("Wage");
        TextField storyPointsTextField = new TextField("Story points");
        Button saveButton = new Button("Save");

        Button chooseToEditProgressButton = new Button("Edit progress");
        ComboBox<Progress> progressComboBox = new ComboBox<>("Select progress");
        progressComboBox.setEmptySelectionAllowed(false);
        progressComboBox.setItems(Progress.values());
        Button saveProgress = new Button("Save");

        chooseToAddTaskButton.addClickListener(event -> {
            verticalLayout.removeAllComponents();
            verticalLayout.addComponents(nameTextField, descriptionTextField, wageTextField,
                    storyPointsTextField, saveButton);
        });

        chooseToEditProgressButton.addClickListener(event -> {
            verticalLayout.removeAllComponents();
            verticalLayout.addComponents(progressComboBox, saveProgress);
        });

        Grid<Task> taskGrid = new Grid<>();
        taskGrid.addColumn(Task::getName).setCaption("Name");
        taskGrid.addColumn(Task::getDescription).setCaption("Description");
        taskGrid.addColumn(task -> task.getUser().getName()).setCaption("User");
        taskGrid.addColumn(Task::getProgress).setCaption("Progress");
        taskGrid.setWidth("750");
        taskGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        List<Task> taskList = new ArrayList<>();
        ListDataProvider<Task> provider = DataProvider.ofCollection(taskList);
        provider.setSortComparator((o1, o2) -> o1.getProgress().getValue() - o2.getProgress().getValue());
        taskGrid.setDataProvider(provider);

        projectComboBox.addValueChangeListener(event ->
                sprintComboBox.setItems(sprintRepository.findAllByProject(event.getValue()))
        );
        sprintComboBox.addValueChangeListener(event -> {
            taskList.clear();
            taskList.addAll(taskRepository.findAllBySprint(event.getValue()));
            provider.refreshAll();
        });
        findByNameTextField.addValueChangeListener(event -> {
            taskList.clear();
            taskList.addAll(
                    taskRepository.findAllByNameStartingWithIgnoreCase(event.getValue()));
            provider.refreshAll();
        });

        saveButton.addClickListener(event -> {
            if (!sprintComboBox.isEmpty()) {
                if (!nameTextField.isEmpty() && !descriptionTextField.isEmpty() &&
                        !wageTextField.isEmpty() && !storyPointsTextField.isEmpty()) {
                    try {
                        Task task = taskRepository.save(
                                new Task(0L, nameTextField.getValue(), descriptionTextField.getValue(),
                                        sprintComboBox.getValue(), Integer.valueOf(wageTextField.getValue()),
                                        Integer.valueOf(storyPointsTextField.getValue()), Progress.TODO, user));
                        Sprint sprint = task.getSprint();
                        sprint.setStoryPointsPlanned(sprint.getStoryPointsPlanned() + Integer.valueOf(storyPointsTextField.getValue()));
                        sprintRepository.save(sprint);
                        taskList.add(task);
                        provider.refreshAll();
                    } catch (NumberFormatException e) {
                        Notification.show("Wrong number!", "",
                                Notification.Type.ERROR_MESSAGE);
                    }
                } else {
                    Notification.show("Empty field!", "",
                            Notification.Type.ERROR_MESSAGE);
                }
            } else {
                Notification.show("Select sprint from ComboBox!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });

        saveProgress.addClickListener(event -> {
            if (taskGrid.getSelectedItems().size() == 1) {
                if (!progressComboBox.isEmpty()) {
                    Task task = taskGrid.getSelectedItems().iterator().next();
                    int index = taskList.indexOf(task);
                    task.setProgress(progressComboBox.getValue());
                    task = taskRepository.save(task);
                    taskList.set(index, task);
                    provider.refreshAll();
                } else {
                    Notification.show("Select progress from ComboBox!", "",
                            Notification.Type.ERROR_MESSAGE);
                }
            } else {
                Notification.show("Select one task from grid!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });

        HorizontalLayout findTasksHorizontalLayout = new HorizontalLayout();
        findTasksHorizontalLayout.addComponents(projectComboBox, sprintComboBox, findByNameTextField);
        HorizontalLayout addTasksHorizontalLayout = new HorizontalLayout();
        addTasksHorizontalLayout.addComponents(chooseToAddTaskButton, chooseToEditProgressButton);
        zarzadzanieZajeciamiLayout.addComponents(findTasksHorizontalLayout, taskGrid, addTasksHorizontalLayout, verticalLayout);

        DateField fromDateField = new DateField("Select start of sprint");
        fromDateField.setValue(LocalDate.now());
        fromDateField.setTextFieldEnabled(false);
        DateField toDateField = new DateField("Select end of sprint");
        toDateField.setValue(LocalDate.now().plusDays(7L));
        toDateField.setTextFieldEnabled(false);
        TextField storyPointsPlannedTextField = new TextField("Planned story points");

        /*Button addButton = new Button("Dodaj zajecia");
        addButton.addClickListener(event -> {
            try {
                if (toDateField.getValue().isAfter(fromDateField.getValue())) {
                    if (sprintRepository.findAllByProject(projectComboBox.getValue()).stream().allMatch(sprint ->
                            fromDateField.getValue().isAfter(sprint.getToLocalDate()) || toDateField.getValue().isBefore(sprint.getFromLocalDate()))) {
                        Sprint sprint = sprintRepository.save(new Sprint(0L, fromDateField.getValue(),
                                toDateField.getValue(), Integer.valueOf(storyPointsPlannedTextField.getValue()), projectComboBox.getValue()));
                        sprintList.add(sprint);
                        provider.refreshAll();
                    } else {
                        Notification.show("Sprint will overlap with other!", "",
                                Notification.Type.ERROR_MESSAGE);
                    }
                } else {
                    Notification.show("Wrong dates!", "",
                            Notification.Type.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                Notification.show("Wrong number of planned story points", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });*/
    }

    private void initInformationProjectLayout() {
        informacjeOKursieLayout = new VerticalLayout();
        ComboBox<Project> projectComboBox = new ComboBox<>("Projects");
        projectComboBox.setEmptySelectionAllowed(true);
        projectComboBox.setDataProvider(allProjectsProvider);
        projectComboBox.setItemCaptionGenerator(Project::getName);

        ComboBox<Sprint> sprintComboBox = new ComboBox<>("Sprint");
        sprintComboBox.setEmptySelectionAllowed(true);
        sprintComboBox.setItemCaptionGenerator(item -> item.getFromLocalDate().toString() +
                " - " + item.getToLocalDate().toString());
        sprintComboBox.setWidth("250");

        ProgressBar progressBar = new ProgressBar(0.0F);
        progressBar.setCaption("Story points");
        progressBar.setWidth("150px");

        ComboBox<User> userComboBox = new ComboBox<>("Users");
        userComboBox.setEmptySelectionAllowed(true);
        userComboBox.setItemCaptionGenerator(User::getName);

        Grid<Task> taskGrid = new Grid<>();
        taskGrid.addColumn(Task::getName).setCaption("Name");
        taskGrid.addColumn(Task::getDescription).setCaption("Description").setWidth(200.0);
        taskGrid.addColumn(Task::getWage).setCaption("Wage");
        taskGrid.addColumn(Task::getStoryPoints).setCaption("Story points");
        taskGrid.addColumn(Task::getProgress).setCaption("Progress");
        taskGrid.setWidth("750");
        taskGrid.setHeight("300");
        taskGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        projectComboBox.addValueChangeListener(event -> {
            sprintComboBox.setItems(sprintRepository.findAllByProject(event.getValue()));
            userComboBox.setItems(
                    projectParticipationRepository.findAllByProject(event.getValue())
                            .stream()
                            .map(ProjectParticipation::getUser)
                            .distinct()
            );
        });

        sprintComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                float doneStoryPoints = taskRepository.findAllBySprintAndAndProgress(sprintComboBox.getValue(), Progress.DONE).stream().mapToInt(Task::getStoryPoints).sum();
                float plannedStoryPoints = sprintComboBox.getValue().getStoryPointsPlanned();
                progressBar.setValue(doneStoryPoints / plannedStoryPoints);
            } else {
                progressBar.setValue(0.0F);
            }
        });

        userComboBox.addValueChangeListener(event -> taskGrid.setItems(
                taskRepository.findAllByUser(event.getValue())
                        .stream()
                        .filter(task -> task.getSprint()
                                .getProject().equals(projectComboBox.getValue()))
        ));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponents(projectComboBox, sprintComboBox, progressBar);
        informacjeOKursieLayout.addComponents(horizontalLayout, userComboBox, taskGrid);
    }

}
