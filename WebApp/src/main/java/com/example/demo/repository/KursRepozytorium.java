package com.example.demo.repository;

import com.example.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KursRepozytorium extends JpaRepository<Kurs, Long> {

    List<Kurs> findAllByNazwa(String nazwa);
    List<Kurs> findAllByBlok(Blok blok);
}
