package com.example.backend_siop.maintenance.entity;

import com.example.backend_siop.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "modele_checklist")
@EqualsAndHashCode(callSuper = false, of = "id")
public class ModeleChecklist extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer ordre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String libelle;

    @ElementCollection
    @CollectionTable(
        name = "modele_checklist_mois",
        joinColumns = @JoinColumn(name = "modele_checklist_id")
    )
    @Column(name = "mois")
    private Set<Integer> moisApplicables = new HashSet<>();
}