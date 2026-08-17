package com.example.backend_siop.maintenance.entity;

import com.example.backend_siop.common.audit.Auditable;
import com.example.backend_siop.maintenance.enums.GraviteAnomalie;
import com.example.backend_siop.maintenance.enums.StatutItem;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "item_check_list")
@EqualsAndHashCode(callSuper = false, of = "id")
public class ItemCheckList extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "checklist_maintenance_id", nullable = false)
    private ChecklistMaintenance checklistMaintenance;

    @Column(nullable = false)
    private Integer ordre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String libelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutItem statut = StatutItem.NON_VERIFIE;

    @Enumerated(EnumType.STRING)
    private GraviteAnomalie gravite;

    @Column(columnDefinition = "TEXT")
    private String remarque;
}