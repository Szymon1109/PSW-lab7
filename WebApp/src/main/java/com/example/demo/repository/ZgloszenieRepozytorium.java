package com.example.demo.repository;

import com.example.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZgloszenieRepozytorium extends JpaRepository<Zgloszenie, Long> {
    //List<Zgloszenie> findAllByUczestnikAndKurs(Uzytkownik uzytkownik, Kurs kurs);

    List<Zgloszenie> findAllByUczestnik(Uzytkownik uzytkownik);

    //List<Zgloszenie> findAllByKurs(Kurs kurs);
}
