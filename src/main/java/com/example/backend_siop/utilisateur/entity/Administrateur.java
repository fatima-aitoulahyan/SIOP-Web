package com.example.backend_siop.utilisateur.entity;

import com.example.backend_siop.utilisateur.enums.TypeUtilisateur;
import jakarta.persistence.Entity;

@Entity
public class Administrateur extends Utilisateur {

    @Override
    public TypeUtilisateur getType() {
        return TypeUtilisateur.ADMINISTRATEUR;
    }
}