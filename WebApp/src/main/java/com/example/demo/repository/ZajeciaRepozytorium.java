package com.example.demo.repository;

import com.example.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZajeciaRepozytorium extends JpaRepository<Zajecia, Long> {

    List<Zajecia> findAllByTemat(String temat);
    List<Zajecia> findAllByProwadzacy(Uzytkownik uzytkownik);
}
