package com.example.backend_siop.utilisateur.entity;

import com.example.backend_siop.utilisateur.enums.TypeUtilisateur;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Client extends Utilisateur {

    private String adresse;

    @Override
    public TypeUtilisateur getType() {
        return TypeUtilisateur.CLIENT;
    }
}