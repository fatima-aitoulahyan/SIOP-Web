package com.example.backend_siop.ascenseur.entity;

import com.example.backend_siop.common.audit.Auditable;
import com.example.backend_siop.parc.entity.Parc;
import com.example.backend_siop.referentiel.entity.Ville;
import com.example.backend_siop.utilisateur.entity.Client;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "site")
@EqualsAndHashCode(callSuper = false, of = "id")
public class Site extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ville_id", nullable = false)
    private Ville ville;

    @Column(nullable = false)
    private String adresse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parc_id", nullable = false)
    private Parc parc;

    @OneToMany(mappedBy = "siteEntity", cascade = CascadeType.ALL)
    private List<Ascenseur> ascenseurs = new ArrayList<>();
}