package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Powiadomienie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String temat;
    private String tresc;

    @OneToOne
    private Uzytkownik uzytkownik;

    @OneToOne
    private Zajecia zajecia;
}
