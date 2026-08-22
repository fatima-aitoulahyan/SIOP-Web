package com.example.backend_siop.ascenseur.entity;

import com.example.backend_siop.common.audit.Auditable;
import com.example.backend_siop.ascenseur.enums.TypeAscenseur;
import com.example.backend_siop.utilisateur.entity.Client;
import com.example.backend_siop.ascenseur.entity.Site;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ascenseur")
@EqualsAndHashCode(callSuper = false, of = "id")
public class Ascenseur extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable=false)
    private String marque;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cout_acquisition")
    private Double coutAcquisition;

    @Column(nullable = false)
    private String fabricant;

    @Column(name = "nombre_etages")
    private Integer nombreEtages;

    @Column(name = "capacite_personnes")
    private Integer capacitePersonnes;

    @Column(name = "charge_max_kg")
    private Double chargeMaxKg;

    private Double vitesse;

    private String modele;

    @Column(name = "numero_serie", unique = true)
    private String numeroSerie;

    @Column(name = "code_barre", unique = true)
    private String codeBarre;

    private String puissance;

    @Column(name = "date_mise_en_service")
    private LocalDate dateMiseEnService;

    @Column(name = "date_expiration_garantie")
    private LocalDate dateExpirationGarantie;

    @Enumerated(EnumType.STRING)
    private TypeAscenseur type;

    @Column(name = "informations_supplementaires", columnDefinition = "TEXT")
    private String informationsSupplementaires;

    private boolean actif = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site siteEntity;

    @OneToMany(mappedBy = "ascenseur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assemblage> assemblages = new ArrayList<>();

}