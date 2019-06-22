package com.example.demo.ui;

import com.example.demo.model.*;

import com.example.demo.repository.*;
import com.google.common.hash.Hashing;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    private List<Kurs> listaKursow = new ArrayList<>();
    private List<Uzytkownik> listaUzytkownikow = new ArrayList<>();
    private List<Uzytkownik> listaProwadzacych = new ArrayList<>();
    private List<Zgloszenie> zgloszenia = new ArrayList<>();
    private List<Zajecia> listaZajec = new ArrayList<>();

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

            zarzadzanieZgodami = new Button("Zgłoszenia");
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

            horizontalLayout.addComponents(zarzadzanieZgodami, utworzKurs, zarzadzanieBlokami, zarzadzanieZajeciami, zarzadzanieProwadzacymi);
            addComponents(horizontalLayout, verticalLayout);
        }
    }

    private void initKursyLayout() {
        utworzKursLayout = new VerticalLayout();
        TextField nameProject = new TextField("Nazwa");
        Button createButton = new Button("Utwórz");

        createButton.addClickListener(event1 -> {
            if (!nameProject.getValue().equals("")) {
                if (kursRepozytorium.findAllByNazwa(nameProject.getValue()).isEmpty()) {
                    Kurs kurs = kursRepozytorium.save(
                            new Kurs(0L, nameProject.getValue(), null));

                    listaKursow.add(kurs);
                    nameProject.setValue("");
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

        kursComboBox.addValueChangeListener(event ->
                kursComboBox.setDataProvider(DataProvider.ofCollection(listaKursow)));

        Button deleteKursButton = new Button("Usuń");
        deleteKursButton.addClickListener(event1 -> {
            if (kursComboBox.getValue() != null) {
                zgloszenieRepozytorium.deleteAllByKurs(kursComboBox.getValue());
                kursRepozytorium.delete(kursComboBox.getValue());
                listaKursow.remove(kursComboBox.getValue());

                kursComboBox.setDataProvider(DataProvider.ofCollection(listaKursow));
                kursComboBox.setValue(null);

                Notification.show("Kurs został usunięty!", "", Notification.Type.HUMANIZED_MESSAGE);

            } else
                Notification.show("Nie wybrano kursu!", "", Notification.Type.ERROR_MESSAGE);
        });

        utworzKursLayout.addComponents(emptyLabel, kursComboBox, deleteKursButton);

        Label empty = new Label();
        TextField nameKurs = new TextField("Nazwa");
        nameKurs.setWidth("250");

        ComboBox<Kurs> kursUpdate = new ComboBox<>("Wybierz kurs do aktualizacji");
        kursUpdate.setEmptySelectionAllowed(false);
        kursUpdate.setDataProvider(DataProvider.ofCollection(listaKursow));
        kursUpdate.setItemCaptionGenerator(z -> z.getId().toString());
        kursUpdate.setWidth("100");

        kursUpdate.addValueChangeListener(event -> {
            if(kursUpdate.getValue() != null) {
                kursUpdate.setDataProvider(DataProvider.ofCollection(listaKursow));
                nameKurs.setValue(kursUpdate.getValue().getNazwa());
            }
        });

        Button updateKursButton = new Button("Aktualizuj");
        updateKursButton.addClickListener(event1 -> {
            if (kursUpdate.getValue() != null && !nameKurs.getValue().equals("")) {
                Kurs kurs = new Kurs(kursUpdate.getValue().getId(), nameKurs.getValue(), kursUpdate.getValue().getBlok());

                kursRepozytorium.save(kurs);
                listaKursow = kursRepozytorium.findAll();

                nameKurs.setValue("");
                kursUpdate.setValue(null);

                kursUpdate.setDataProvider(DataProvider.ofCollection(listaKursow));
                kursComboBox.setDataProvider(DataProvider.ofCollection(listaKursow));

                Notification.show("Kurs został zaktualizowany!", "", Notification.Type.HUMANIZED_MESSAGE);

            } else
                Notification.show("Nie wybrano danych!", "", Notification.Type.ERROR_MESSAGE);
        });

        utworzKursLayout.addComponents(empty, kursUpdate, nameKurs, updateKursButton);
    }

    private void initZgodaLayout() {
        zgodyLayout = new VerticalLayout();

        ComboBox<Uzytkownik> uzytkownikComboBox = new ComboBox<>("Wybierz uczestnika");
        uzytkownikComboBox.setEmptySelectionAllowed(false);
        uzytkownikComboBox.setDataProvider(DataProvider.ofCollection(listaUzytkownikow));
        uzytkownikComboBox.setItemCaptionGenerator(Uzytkownik::getLogin);

        ComboBox<Zgloszenie> zgloszenieComboBox = new ComboBox<>("Wybierz zgłoszenie");
        zgloszenieComboBox.setEmptySelectionAllowed(false);
        zgloszenieComboBox.setItemCaptionGenerator(z -> z.getKurs().getNazwa() + ": " + z.getData().toString());
        zgloszenieComboBox.setWidth("300");
        uzytkownikComboBox.addValueChangeListener(event -> zgloszenieComboBox.setItems(
                zgloszenieRepozytorium.findAllByUczestnik(uzytkownikComboBox.getValue())
                        .stream()
                        .filter(z -> z.getZgoda() == null)
                        .collect(Collectors.toList())));

        Button potwierdz = new Button("Potwierdź");
        potwierdz.addClickListener(event1 -> {
            if (zgloszenieComboBox.getValue() != null) {
                Zgloszenie zgloszenie = zgloszenieComboBox.getValue();
                zgloszenie.setZgoda(true);
                zgloszenieRepozytorium.save(zgloszenie);

                uzytkownikComboBox.setValue(null);
                zgloszenieComboBox.setValue(null);

                Notification.show("Potwierdzono zgłoszenie!", "", Notification.Type.HUMANIZED_MESSAGE);
            } else
                Notification.show("Nie wybrano zgłoszenia!", "", Notification.Type.ERROR_MESSAGE);
        });

        Button odrzuc = new Button("Odrzuć");
        odrzuc.addClickListener(event1 -> {
            if (zgloszenieComboBox.getValue() != null) {
                Zgloszenie zgloszenie = zgloszenieComboBox.getValue();
                zgloszenie.setZgoda(false);
                zgloszenieRepozytorium.save(zgloszenie);

                uzytkownikComboBox.setValue(null);
                zgloszenieComboBox.setValue(null);

                Notification.show("Odrzucono zgłoszenie!", "", Notification.Type.HUMANIZED_MESSAGE);
            } else
                Notification.show("Nie wybrano zgłoszenia!", "", Notification.Type.ERROR_MESSAGE);
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponents(potwierdz, odrzuc);
        zgodyLayout.addComponents(uzytkownikComboBox, zgloszenieComboBox, horizontalLayout);
    }

    private void initBlokiLayout() {
        zarzadzanieBlokamiLayout = new VerticalLayout();

        List<Kurs> listaKursow = kursRepozytorium.findAll();

        ComboBox<Kurs> kursComboBox = new ComboBox<>("Wybierz kurs");
        kursComboBox.setEmptySelectionAllowed(false);
        kursComboBox.setDataProvider(DataProvider.ofCollection(listaKursow));
        kursComboBox.setItemCaptionGenerator(Kurs::getNazwa);
        kursComboBox.setWidth("250");

        Grid<Blok> blokGrid = new Grid<>();
        blokGrid.addColumn(Blok::getId).setCaption("ID").setWidth(100);
        blokGrid.addColumn(Blok::getNazwa).setCaption("Nazwa").setWidth(550);
        blokGrid.setWidth("650");
        blokGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        List<Blok> listaBlokow = new ArrayList<>();
        ListDataProvider<Blok> provider = DataProvider.ofCollection(listaBlokow);
        blokGrid.setDataProvider(provider);

        kursComboBox.addValueChangeListener(event -> {
            List<Kurs> lista = kursRepozytorium.findAll();
            kursComboBox.setDataProvider(DataProvider.ofCollection(lista));

            listaBlokow.clear();
            if(event.getValue().getBlok() != null) {
                listaBlokow.addAll(event.getValue().getBlok());
            }
            provider.refreshAll();
        });

        Button deleteButton = new Button("Usuń");
        deleteButton.addClickListener(event -> {
            if (!blokGrid.getSelectedItems().isEmpty()) {
                Blok blok = blokGrid.getSelectedItems().iterator().next();
                listaBlokow.remove(blok);
                provider.refreshAll();

                Kurs kurs = kursComboBox.getValue();
                List<Blok> currentList = kurs.getBlok();
                currentList.remove(blok);
                kurs.setBlok(currentList);
                kursRepozytorium.save(kurs);

                Notification.show("Usunięto blok", "", Notification.Type.HUMANIZED_MESSAGE);
            }
        });

        Label label = new Label();
        TextField nazwaBloku = new TextField("Nazwa bloku");

        Button addButton = new Button("Dodaj blok");
        addButton.addClickListener(event -> {
            if (!nazwaBloku.getValue().equals("")) {
                if (blokRepozytorium.findAllByNazwa(nazwaBloku.getValue()).isEmpty()) {

                    Blok blok = blokRepozytorium.save(
                            new Blok(0L, nazwaBloku.getValue(), null));

                    Kurs kurs = kursComboBox.getValue();
                    List<Blok> currentList = kurs.getBlok() != null ? kurs.getBlok() : new ArrayList<>();
                    currentList.add(blok);
                    kurs.setBlok(currentList);
                    kursRepozytorium.save(kurs);

                    listaBlokow.add(blok);
                    provider.refreshAll();

                    nazwaBloku.setValue("");
                    Notification.show("Utworzono blok", "", Notification.Type.HUMANIZED_MESSAGE);

                } else
                    Notification.show("Blok o podanej nazwie istnieje!", "", Notification.Type.ERROR_MESSAGE);
            } else
                Notification.show("Nie podano nazwy bloku!", "", Notification.Type.ERROR_MESSAGE);
        });

        zarzadzanieBlokamiLayout.addComponents(kursComboBox, blokGrid, deleteButton, label, nazwaBloku, addButton);

        Label empty = new Label();
        TextField nameBlok = new TextField("Nazwa");
        nameBlok.setWidth("250");

        ComboBox<Blok> blokUpdate = new ComboBox<>("Wybierz blok do aktualizacji");
        blokUpdate.setEmptySelectionAllowed(false);
        blokUpdate.setDataProvider(provider);
        blokUpdate.setItemCaptionGenerator(b -> b.getId().toString());
        blokUpdate.setWidth("100");

        blokUpdate.addValueChangeListener(event -> {
            if(blokUpdate.getValue() != null) {
                nameBlok.setValue(blokUpdate.getValue().getNazwa());
            }
        });

        Button updateBlokButton = new Button("Aktualizuj");
        updateBlokButton.addClickListener(event1 -> {
            if (blokUpdate.getValue() != null && !nameBlok.getValue().equals("")) {
                Blok blok = new Blok(blokUpdate.getValue().getId(), nameBlok.getValue(), blokUpdate.getValue().getZajecia());

                blokRepozytorium.save(blok);

                nameBlok.setValue("");
                blokUpdate.setValue(null);

                listaBlokow.clear();
                Set<Blok> zbior = new HashSet<>(kursRepozytorium.findById(kursComboBox.getValue().getId()).get().getBlok());
                listaBlokow.addAll(zbior);
                provider.refreshAll();

                Notification.show("Blok został zaktualizowany!", "", Notification.Type.HUMANIZED_MESSAGE);

            } else
                Notification.show("Nie wybrano danych!", "", Notification.Type.ERROR_MESSAGE);
        });

        zarzadzanieBlokamiLayout.addComponents(empty, blokUpdate, nameBlok, updateBlokButton);
    }

    private void initZajeciaLayout() {
        zarzadzanieZajeciamiLayout = new VerticalLayout();

        List<Blok> listaBlokow = blokRepozytorium.findAll();

        ComboBox<Blok> blokComboBox = new ComboBox<>("Wybierz blok");
        blokComboBox.setEmptySelectionAllowed(false);
        blokComboBox.setDataProvider(DataProvider.ofCollection(listaBlokow));
        blokComboBox.setItemCaptionGenerator(Blok::getNazwa);
        blokComboBox.setWidth("250");

        Grid<Zajecia> zajeciaGrid = new Grid<>();
        zajeciaGrid.addColumn(Zajecia::getId).setCaption("ID").setWidth(70);
        zajeciaGrid.addColumn(Zajecia::getTemat).setCaption("Temat").setWidth(300);
        zajeciaGrid.addColumn(Zajecia::getData).setCaption("Data").setWidth(130);
        zajeciaGrid.addColumn(z -> z.getProwadzacy() != null ? z.getProwadzacy().getLogin() : "").setCaption("Prowadzący").setWidth(150);
        zajeciaGrid.setWidth("650");
        zajeciaGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        List<Zajecia> listaZajec = new ArrayList<>();
        ListDataProvider<Zajecia> provider = DataProvider.ofCollection(listaZajec);
        zajeciaGrid.setDataProvider(provider);

        blokComboBox.addValueChangeListener(event1 -> {
            List<Blok> lista = blokRepozytorium.findAll();
            blokComboBox.setDataProvider(DataProvider.ofCollection(lista));

            listaZajec.clear();
            if(event1.getValue().getZajecia() != null) {
                listaZajec.addAll(event1.getValue().getZajecia());
            }
            provider.refreshAll();
        });

        Button deleteButton = new Button("Usuń");
        deleteButton.addClickListener(event -> {
            if (!zajeciaGrid.getSelectedItems().isEmpty()) {
                Zajecia zajecia = zajeciaGrid.getSelectedItems().iterator().next();
                listaZajec.remove(zajecia);
                provider.refreshAll();

                Blok blok = blokComboBox.getValue();
                List<Zajecia> currentList = blok.getZajecia();
                currentList.remove(zajecia);
                blok.setZajecia(currentList);
                blokRepozytorium.save(blok);

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
            if (!nazwaZajec.getValue().equals("") && dataField.getValue() != null && prowadzacyComboBox.getValue() != null) {
                if (zajeciaRepozytorium.findAllByTemat(nazwaZajec.getValue()).isEmpty()) {

                    Zajecia zajecia = zajeciaRepozytorium.save(
                            new Zajecia(0L, nazwaZajec.getValue(), dataField.getValue(), prowadzacyComboBox.getValue()));

                    Blok blok = blokComboBox.getValue();
                    List<Zajecia> currentList = blok.getZajecia() != null ? blok.getZajecia() : new ArrayList<>();
                    currentList.add(zajecia);
                    blok.setZajecia(currentList);
                    blokRepozytorium.save(blok);

                    listaZajec.add(zajecia);
                    provider.refreshAll();

                    nazwaZajec.setValue("");
                    dataField.setValue(null);
                    prowadzacyComboBox.setValue(null);

                    Notification.show("Utworzono zajęcia", "", Notification.Type.HUMANIZED_MESSAGE);

                } else
                    Notification.show("Zajęcia o podanej nazwie istnieją!", "", Notification.Type.ERROR_MESSAGE);
            } else
                Notification.show("Nie podano wszystkich danych!", "", Notification.Type.ERROR_MESSAGE);
        });

        zarzadzanieZajeciamiLayout.addComponents(blokComboBox, zajeciaGrid, deleteButton, label, nazwaZajec, dataField, prowadzacyComboBox, addButton);

        Label empty = new Label();

        TextField nameZajecia = new TextField("Temat zajęć");
        nameZajecia.setWidth("300");
        DateField dataZajecia = new DateField("Data zajęć");

        ComboBox<Uzytkownik> prowadzacyZajecia = new ComboBox<>("Wybierz prowadzącego");
        prowadzacyZajecia.setEmptySelectionAllowed(false);
        prowadzacyZajecia.setDataProvider(DataProvider.ofCollection(listaProwadzacych));
        prowadzacyZajecia.setItemCaptionGenerator(Uzytkownik::getLogin);

        ComboBox<Zajecia> zajeciaUpdate = new ComboBox<>("Wybierz zajęcia do aktualizacji");
        zajeciaUpdate.setEmptySelectionAllowed(false);
        zajeciaUpdate.setDataProvider(provider);
        zajeciaUpdate.setItemCaptionGenerator(b -> b.getId().toString());
        zajeciaUpdate.setWidth("100");

        zajeciaUpdate.addValueChangeListener(event -> {
            if(zajeciaUpdate.getValue() != null) {

                nameZajecia.setValue(zajeciaUpdate.getValue().getTemat());
                dataZajecia.setValue(zajeciaUpdate.getValue().getData());
                prowadzacyZajecia.setValue(zajeciaUpdate.getValue().getProwadzacy());
            }
        });

        Button updateZajecia = new Button("Zaktualizuj zajęcia");
        updateZajecia.addClickListener(event -> {
            if (!nameZajecia.getValue().equals("") && dataZajecia.getValue() != null && prowadzacyZajecia.getValue() != null) {

                Zajecia zajecia = new Zajecia(zajeciaUpdate.getValue().getId(), nameZajecia.getValue(),
                        dataZajecia.getValue(), prowadzacyZajecia.getValue());

                zajeciaRepozytorium.save(zajecia);

                zajeciaUpdate.setValue(null);
                nameZajecia.setValue("");
                dataZajecia.setValue(null);
                prowadzacyZajecia.setValue(null);

                listaZajec.clear();
                listaZajec.addAll(blokRepozytorium.findById(blokComboBox.getValue().getId()).get().getZajecia());
                provider.refreshAll();

                Notification.show("Zaktualizowano zajęcia", "", Notification.Type.HUMANIZED_MESSAGE);

            } else
                Notification.show("Nie podano wszystkich danych!", "", Notification.Type.ERROR_MESSAGE);
        });

        zarzadzanieZajeciamiLayout.addComponents(empty, zajeciaUpdate, nameZajecia, dataZajecia, prowadzacyZajecia, updateZajecia);
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
        ComboBox<Uzytkownik> uzytkownikComboBox = new ComboBox<>("Wybierz prowadzącego do usunięcia");
        uzytkownikComboBox.setEmptySelectionAllowed(false);
        uzytkownikComboBox.setDataProvider(DataProvider.ofCollection(listaProwadzacych));
        uzytkownikComboBox.setItemCaptionGenerator(Uzytkownik::getLogin);
        uzytkownikComboBox.addValueChangeListener(event -> {

                listaProwadzacych = uzytkownikRepozytorium.findAll()
                        .stream()
                        .filter(u -> u.getTyp() == Typ.PROWADZACY)
                        .collect(Collectors.toList());

                uzytkownikComboBox.setDataProvider(DataProvider.ofCollection(listaProwadzacych));
        });

        Button deleteButton = new Button("Usuń");
        deleteButton.addClickListener(event1 -> {
            if (uzytkownikComboBox.getValue() != null) {
                Uzytkownik uzytkownik = uzytkownikComboBox.getValue();
                listaProwadzacych.remove(uzytkownik);

                List<Zajecia> zajecia = zajeciaRepozytorium.findAllByProwadzacy(uzytkownik);
                zajecia.forEach(z -> z.setProwadzacy(null));

                zajeciaRepozytorium.saveAll(zajecia);
                uzytkownikRepozytorium.delete(uzytkownik);

                uzytkownikComboBox.setDataProvider(DataProvider.ofCollection(listaProwadzacych));
                uzytkownikComboBox.setValue(null);

                Notification.show("Prowadzący został usunięty!", "", Notification.Type.HUMANIZED_MESSAGE);

            } else
                Notification.show("Nie wybrano prowadzącego!", "", Notification.Type.ERROR_MESSAGE);
        });

        zarzadzanieProwadzacymiLayout.addComponents(emptyLabel, uzytkownikComboBox, deleteButton);
    }

    private boolean sprawdzHaslo(String haslo) {
        final String patternString = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(haslo);

        return matcher.matches();
    }
}
