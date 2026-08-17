package com.example.backend_siop.maintenance.entity;

import com.example.backend_siop.common.audit.Auditable;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "commentaire_bon_travail")
@EqualsAndHashCode(callSuper = false, of = "id")
public class CommentaireBonTravail extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bon_travail_id", nullable = false)
    private BonTravail bonTravail;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auteur_id", nullable = false)
    private Utilisateur auteur;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;
}