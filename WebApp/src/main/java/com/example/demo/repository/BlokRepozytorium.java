package com.example.demo.repository;

import com.example.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlokRepozytorium extends JpaRepository<Blok, Long> {

    List<Blok> findAllByNazwa(String nazwa);
    List<Blok> findAllByZajecia(Zajecia zajecia);
}
