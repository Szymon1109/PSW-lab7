package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;

@Service
public class InitializationService {

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

    @PostConstruct
    public void init() {

        Uzytkownik u0 = new Uzytkownik(0L, "Szymon1104", Hashing.sha512().hashString("Pa$$word1", StandardCharsets.UTF_8).toString(), Typ.ADMIN, "Szymon", "Betlewski", 10);
        Uzytkownik u1 = new Uzytkownik(0L, "Ala123", Hashing.sha512().hashString("Ala123", StandardCharsets.UTF_8).toString(), Typ.UCZESTNIK, "Ala", "Kowalska", 8);
        Uzytkownik u2 = new Uzytkownik(0L, "Kasia123", Hashing.sha512().hashString("Kasia123", StandardCharsets.UTF_8).toString(), Typ.UCZESTNIK, "Kasia", "Nowak", 7);
        Uzytkownik u3 = new Uzytkownik(0L, "Piotr123", Hashing.sha512().hashString("Piotr123", StandardCharsets.UTF_8).toString(), Typ.UCZESTNIK, "Piotr", "Zieliński", 6);
        Uzytkownik u4 = new Uzytkownik(0L, "Pawel123", Hashing.sha512().hashString("Pawel123", StandardCharsets.UTF_8).toString(), Typ.UCZESTNIK, "Paweł", "Wiśniewski", 9);
        Uzytkownik u5 = new Uzytkownik(0L, "Ania123", Hashing.sha512().hashString("Ania123", StandardCharsets.UTF_8).toString(), Typ.PROWADZACY, "Ania", "Kowalczyk", 8);
        Uzytkownik u6 = new Uzytkownik(0L, "Michal123", Hashing.sha512().hashString("Michal123", StandardCharsets.UTF_8).toString(), Typ.PROWADZACY, "Michał", "Duda", 8);

        u0 = uzytkownikRepozytorium.save(u0);
        u1 = uzytkownikRepozytorium.save(u1);
        u2 = uzytkownikRepozytorium.save(u2);
        u3 = uzytkownikRepozytorium.save(u3);
        u4 = uzytkownikRepozytorium.save(u4);
        u5 = uzytkownikRepozytorium.save(u5);
        u6 = uzytkownikRepozytorium.save(u6);

        Zajecia z1 = new Zajecia(0L, "Speaking and listening in English", LocalDate.of(2019, 7, 1), u5);
        Zajecia z2 = new Zajecia(0L, "Reading and writing in English", LocalDate.of(2019, 7, 2), u5);
        Zajecia z3 = new Zajecia(0L, "Deutsch sprechen und zuhoren", LocalDate.of(2019, 7, 4), u5);
        Zajecia z4 = new Zajecia(0L, "Deutsch lesen und schreiben", LocalDate.of(2019, 7, 5), u5);
        Zajecia z5 = new Zajecia(0L, "Inżynieria oprogramowania", LocalDate.of(2019, 7, 8), u6);
        Zajecia z6 = new Zajecia(0L, "Podstawy programowania obiektowego", LocalDate.of(2019, 7, 8), u6);

        z1 = zajeciaRepozytorium.save(z1);
        z2 = zajeciaRepozytorium.save(z2);
        z3 = zajeciaRepozytorium.save(z3);
        z4 = zajeciaRepozytorium.save(z4);
        z5 = zajeciaRepozytorium.save(z5);
        z6 = zajeciaRepozytorium.save(z6);

        Blok b1 = new Blok(0L, "English classes", Arrays.asList(z1, z2));
        Blok b2 = new Blok(0L, "Deutsch Stunden", Arrays.asList(z3, z4));
        Blok b3 = new Blok(0L, "Zajęcia programistyczne", Arrays.asList(z5, z6));

        b1 = blokRepozytorium.save(b1);
        b2 = blokRepozytorium.save(b2);
        b3 = blokRepozytorium.save(b3);

        Kurs k1 = new Kurs(0L, "Kurs języków obcych", Arrays.asList(b1, b2));
        Kurs k2 = new Kurs(0L, "Kurs informatyczny", Arrays.asList(b1, b3));

        k1 = kursRepozytorium.save(k1);
        k2 = kursRepozytorium.save(k2);

        zgloszenieRepozytorium.save(new Zgloszenie(0L, LocalDate.of(2019, 6, 20), "tak", u1, k1));
        zgloszenieRepozytorium.save(new Zgloszenie(0L, LocalDate.of(2019, 6, 21), "", u1, k2));
        zgloszenieRepozytorium.save(new Zgloszenie(0L, LocalDate.of(2019, 6, 22), "", u2, k1));
        zgloszenieRepozytorium.save(new Zgloszenie(0L, LocalDate.of(2019, 6, 23), "", u3, k2));
        zgloszenieRepozytorium.save(new Zgloszenie(0L, LocalDate.of(2019, 6, 24), "", u4, k2));

        powiadomienieRepozytorium.save(new Powiadomienie(0L, "Powiadomienie", "Niedługo pierwsze zajęcia z kursu", u1, z1));
        powiadomienieRepozytorium.save(new Powiadomienie(0L, "Powiadomienie", "Niedługo pierwsze zajęcia z kursu", u1, z5));
        powiadomienieRepozytorium.save(new Powiadomienie(0L, "Powiadomienie", "Niedługo pierwsze zajęcia z kursu", u2, z1));
        powiadomienieRepozytorium.save(new Powiadomienie(0L, "Powiadomienie", "Niedługo pierwsze zajęcia z kursu", u3, z5));
        powiadomienieRepozytorium.save(new Powiadomienie(0L, "Powiadomienie", "Niedługo pierwsze zajęcia z kursu", u4, z5));
    }
}