package com.example.demo.repository;

import com.example.demo.model.Blok;
import com.example.demo.model.Kurs;
import com.example.demo.model.Project;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KursRepozytorium extends JpaRepository<Kurs, Long> {
    List<Kurs> findAllByNazwa(String nazwa);
    //void deleteAllByBlok(Blok blok);
}
