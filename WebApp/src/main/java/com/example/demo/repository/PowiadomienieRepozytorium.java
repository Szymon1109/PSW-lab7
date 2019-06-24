package com.example.demo.repository;

import com.example.demo.model.Powiadomienie;
import com.example.demo.model.Uzytkownik;
import com.example.demo.model.Zajecia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PowiadomienieRepozytorium extends JpaRepository<Powiadomienie, Long> {

    List<Powiadomienie> findAllByUzytkownik(Uzytkownik uzytkownik);
    List<Powiadomienie> findAllByZajecia(Zajecia zajecia);
}
