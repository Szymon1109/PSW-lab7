package com.example.demo.repository;

import com.example.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ZgloszenieRepozytorium extends JpaRepository<Zgloszenie, Long> {

    List<Zgloszenie> findAllByUczestnik(Uzytkownik uzytkownik);

    @Transactional
    void deleteAllByKurs(Kurs kurs);
}
