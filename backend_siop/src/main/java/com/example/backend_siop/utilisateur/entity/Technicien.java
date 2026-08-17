package com.example.backend_siop.utilisateur.entity;

import com.example.backend_siop.parc.entity.Parc;
import com.example.backend_siop.utilisateur.enums.TypeUtilisateur;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Technicien extends Utilisateur {

    private String specialite;

    private boolean disponible = true;

    @ManyToMany
    @JoinTable(
        name = "technicien_parc",
        joinColumns = @JoinColumn(name = "technicien_id"),
        inverseJoinColumns = @JoinColumn(name = "parc_id")
    )
    private List<Parc> parcs = new ArrayList<>();

    @Override
    public TypeUtilisateur getType() {
        return TypeUtilisateur.TECHNICIEN;
    }
}