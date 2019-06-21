package com.example.demo.ui;

import com.example.demo.model.*;

import com.example.demo.repository.*;
import com.google.common.hash.Hashing;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private Button zarzadzanieProwadzacymi;

    private VerticalLayout utworzKursLayout;
    private VerticalLayout zgodyLayout;
    private VerticalLayout zarzadzanieBlokamiLayout;
    private VerticalLayout zarzadzanieZajeciamiLayout;
    private VerticalLayout zarzadzanieProwadzacymiLayout;

    private List<Kurs> listaKursow;
    private List<Uzytkownik> listaUzytkownikow;
    private List<Uzytkownik> listaProwadzacych;
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

            listaUzytkownikow = uzytkownikRepozytorium.findAll()
                    .stream()
                    .filter(u -> u.getTyp() == Typ.UCZESTNIK)
                    .collect(Collectors.toList());

            listaProwadzacych = uzytkownikRepozytorium.findAll()
                    .stream()
                    .filter(u -> u.getTyp() == Typ.PROWADZACY)
                    .collect(Collectors.toList());

            initKursyLayout();
            initZgodaLayout();
            initBlokiLayout();
            initZajeciaLayout();
            initProwadzacyLayout();

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

            zarzadzanieProwadzacymi = new Button("Prowadzący");
            zarzadzanieProwadzacymi.addClickListener(event -> {
                verticalLayout.removeAllComponents();
                verticalLayout.addComponent(zarzadzanieProwadzacymiLayout);
            });

            utworzKurs.click();
            horizontalLayout.addComponents(utworzKurs, zarzadzanieZgodami, zarzadzanieBlokami, zarzadzanieZajeciami, zarzadzanieProwadzacymi);
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

        zgloszenia = zgloszenieRepozytorium.findAllByUczestnik(uzytkownikComboBox.getValue())
                .stream()
                .filter(z -> z.getZgoda() == null)
                .collect(Collectors.toList());

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

        Button deleteButton = new Button("Usuń");
        deleteButton.addClickListener(event -> {
            if (!blokGrid.getSelectedItems().isEmpty()) {
                Blok blok = blokGrid.getSelectedItems().iterator().next();
                listaBlokow.remove(blok);
                provider.refreshAll();

                List<Blok> currentList = kursComboBox.getValue().getBlok();
                currentList.remove(blok);
                kursComboBox.getValue().setBlok(currentList);

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

        //TODO:
        //update bloki
    }

    private void initZajeciaLayout() {
        zarzadzanieZajeciamiLayout = new VerticalLayout();

        ComboBox<Blok> blokComboBox = new ComboBox<>("Wybierz blok");
        blokComboBox.setEmptySelectionAllowed(false);
        blokComboBox.setDataProvider(DataProvider.ofCollection(listaBlokow));
        blokComboBox.setItemCaptionGenerator(Blok::getNazwa);

        Grid<Zajecia> zajeciaGrid = new Grid<>();
        zajeciaGrid.addColumn(Zajecia::getId).setCaption("ID");
        zajeciaGrid.addColumn(Zajecia::getTemat).setCaption("Temat");
        zajeciaGrid.addColumn(Zajecia::getData).setCaption("Data");
        zajeciaGrid.addColumn(Zajecia::getProwadzacy).setCaption("Prowadzący");
        zajeciaGrid.setWidth("650");
        zajeciaGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        listaZajec = blokComboBox.getValue().getZajecia();
        ListDataProvider<Zajecia> provider = DataProvider.ofCollection(listaZajec);
        zajeciaGrid.setDataProvider(provider);

        blokComboBox.addValueChangeListener(event1 -> {
            listaZajec.clear();
            listaZajec.addAll(event1.getValue().getZajecia());
            provider.refreshAll();
        });

        Button deleteButton = new Button("Usuń");
        deleteButton.addClickListener(event -> {
            if (!zajeciaGrid.getSelectedItems().isEmpty()) {
                Zajecia zajecia = zajeciaGrid.getSelectedItems().iterator().next();
                listaZajec.remove(zajecia);
                provider.refreshAll();

                List<Zajecia> currentList = blokComboBox.getValue().getZajecia();
                currentList.remove(zajecia);
                blokComboBox.getValue().setZajecia(currentList);

                zajeciaRepozytorium.delete(zajecia);

                Notification.show("Usunięto zajęcia", "", Notification.Type.HUMANIZED_MESSAGE);
            }
        });

        Label label = new Label();
        TextField nazwaZajec = new TextField("Temat zajęć");
        DateField dataField = new DateField("Data zajęć");

        ComboBox<Uzytkownik> prowadzacyComboBox = new ComboBox<>("Wybierz prowadzącego");
        prowadzacyComboBox.setEmptySelectionAllowed(false);
        prowadzacyComboBox.setDataProvider(DataProvider.ofCollection(listaProwadzacych));
        prowadzacyComboBox.setItemCaptionGenerator(Uzytkownik::getLogin);

        Button addButton = new Button("Dodaj zajęcia");
        addButton.addClickListener(event -> {
            if (nazwaZajec.getValue() != null && dataField.getValue() != null && prowadzacyComboBox.getValue() != null) {
                if (zajeciaRepozytorium.findAllByNazwa(nazwaZajec.getValue()).isEmpty()) {

                    Zajecia zajecia = zajeciaRepozytorium.save(
                            new Zajecia(0L, nazwaZajec.getValue(), dataField.getValue(), prowadzacyComboBox.getValue()));

                    List<Zajecia> currentList = blokComboBox.getValue().getZajecia();
                    currentList.add(zajecia);
                    blokComboBox.getValue().setZajecia(currentList);

                    listaZajec.add(zajecia);
                    provider.refreshAll();
                    Notification.show("Utworzono zajęcia", "", Notification.Type.HUMANIZED_MESSAGE);

                } else
                    Notification.show("Zajęcia o podanej nazwie istnieją!", "", Notification.Type.ERROR_MESSAGE);
            } else
                Notification.show("Nie wybrano wszystkich danych!", "", Notification.Type.ERROR_MESSAGE);
        });

        zarzadzanieBlokamiLayout.addComponents(blokComboBox, zajeciaGrid, deleteButton, label, nazwaZajec, dataField, prowadzacyComboBox, addButton);

        //TODO:
        //update zajęcia
    }

    private void initProwadzacyLayout(){
        zarzadzanieProwadzacymiLayout = new VerticalLayout();

        TextField imieTextField = new TextField("Imię");
        TextField nazwiskoTextField = new TextField("Nazwisko");
        TextField loginTextField = new TextField("Login");
        PasswordField hasloField = new PasswordField("Hasło");

        Button addUzytkownik = new Button("Dodaj");

        addUzytkownik.addClickListener(event -> {
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
                                    Typ.PROWADZACY, imieText, nazwiskoText, 0);

                            uzytkownikRepozytorium.save(uzytkownik);

                            imieTextField.setValue("");
                            nazwiskoTextField.setValue("");
                            loginTextField.setValue("");
                            hasloField.setValue("");

                            Notification.show("Dodano prowadzącego!", "", Notification.Type.HUMANIZED_MESSAGE);
                        } else
                            Notification.show("Podany login już istnieje!", "", Notification.Type.ERROR_MESSAGE);
                    } else
                        Notification.show("Hasło musi składać się z conajmniej: 1 małej litery, 1 dużej litery i 1 cyfry!",
                                "", Notification.Type.ERROR_MESSAGE);
                } else
                    Notification.show("Hasło musi składać się z conajmniej 6 znaków!", "", Notification.Type.ERROR_MESSAGE);

            } else
                Notification.show("Dane muszą składać się z conajmniej 4 znaków!", "", Notification.Type.ERROR_MESSAGE);
        });

        zarzadzanieProwadzacymiLayout.addComponents(imieTextField, nazwiskoTextField, loginTextField, hasloField, addUzytkownik);

        Label emptyLabel = new Label();
        ComboBox<Uzytkownik> uzytkownikComboBox = new ComboBox<>("Wybierz użytkownika do usunięcia");
        uzytkownikComboBox.setEmptySelectionAllowed(false);
        uzytkownikComboBox.setDataProvider(DataProvider.ofCollection(listaProwadzacych));
        uzytkownikComboBox.setItemCaptionGenerator(Uzytkownik::getLogin);
        uzytkownikComboBox.setWidth("250");

        Button deleteButton = new Button("Usuń");
        deleteButton.addClickListener(event1 -> {
            if (uzytkownikComboBox.getValue() != null) {
                Uzytkownik uzytkownik = uzytkownikComboBox.getValue();
                listaProwadzacych.remove(uzytkownik);

                List<Zajecia> zajecia = zajeciaRepozytorium.findAllByProwadzacy(uzytkownik);
                zajecia.forEach(z -> z.setProwadzacy(null));

                uzytkownikRepozytorium.delete(uzytkownik);
                Notification.show("Prowadzący został usunięty!", "", Notification.Type.HUMANIZED_MESSAGE);

            } else
                Notification.show("Nie wybrano prowadzącego!", "", Notification.Type.ERROR_MESSAGE);
        });

        utworzKursLayout.addComponents(emptyLabel, uzytkownikComboBox, deleteButton);
    }

    private boolean sprawdzHaslo(String haslo) {
        final String patternString = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(haslo);

        return matcher.matches();
    }
}
