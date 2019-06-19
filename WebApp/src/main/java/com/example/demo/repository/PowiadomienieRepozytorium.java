package com.example.demo.repository;

import com.example.demo.model.Powiadomienie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PowiadomienieRepozytorium extends JpaRepository<Powiadomienie, Long> {

}
