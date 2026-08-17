package com.example.backend_siop.referentiel.entity;

import com.example.backend_siop.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ville")
@EqualsAndHashCode(callSuper = false, of = "id")
public class Ville extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    private String region;

    @Column(name = "code_postal")
    private String codePostal;

    private boolean active = true;
}