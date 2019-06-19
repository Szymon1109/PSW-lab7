package com.example.demo.repository;

import com.example.demo.model.Uzytkownik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UzytkownikRepozytorium extends JpaRepository<Uzytkownik, Long> {

    Optional<Uzytkownik> findByLogin(String login);
    List<Uzytkownik> findAllUzytkownik();

    @Query(value = "SELECT login FROM uzytkownik", nativeQuery = true)
    List<Object> findAllLogins();
}
