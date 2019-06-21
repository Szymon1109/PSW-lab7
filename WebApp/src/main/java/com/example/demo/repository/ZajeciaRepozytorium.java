package com.example.demo.repository;

import com.example.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ZajeciaRepozytorium extends JpaRepository<Zajecia, Long> {

    List<Zajecia> findAllByProwadzacy(Uzytkownik uzytkownik);
    List<Zajecia> findAllByTematStartingWithIgnoreCase(String name);

    /*@Transactional
    void deleteAllByBlok(Blok blok);*/
}
