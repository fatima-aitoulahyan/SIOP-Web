package com.example.backend_siop.dashboard.service.impl;

import com.example.backend_siop.ascenseur.repository.AscenseurRepository;
import com.example.backend_siop.ascenseur.repository.SiteRepository;
import com.example.backend_siop.dashboard.dto.ActiviteParParcDTO;
import com.example.backend_siop.dashboard.dto.DashboardAdminDTO;
import com.example.backend_siop.dashboard.dto.RepartitionUtilisateursDTO;
import com.example.backend_siop.maintenance.enums.StatutBonTravail;
import com.example.backend_siop.maintenance.enums.StatutDemande;
import com.example.backend_siop.maintenance.repository.BonTravailRepository;
import com.example.backend_siop.maintenance.repository.DemandeMaintenanceRepository;
import com.example.backend_siop.dashboard.service.DashboardAdminService;
import com.example.backend_siop.dashboard.service.DashboardResponsableService;
import com.example.backend_siop.parc.entity.Parc;
import com.example.backend_siop.parc.repository.ParcRepository;
import com.example.backend_siop.utilisateur.repository.AdministrateurRepository;
import com.example.backend_siop.utilisateur.repository.ClientRepository;
import com.example.backend_siop.utilisateur.repository.ResponsableMaintenanceRepository;
import com.example.backend_siop.utilisateur.repository.TechnicienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardAdminServiceImpl implements DashboardAdminService {

    private final ParcRepository parcRepository;
    private final SiteRepository siteRepository;
    private final AscenseurRepository ascenseurRepository;
    private final DemandeMaintenanceRepository demandeRepository;
    private final BonTravailRepository bonTravailRepository;
    private final ClientRepository clientRepository;
    private final TechnicienRepository technicienRepository;
    private final ResponsableMaintenanceRepository responsableRepository;
    private final AdministrateurRepository administrateurRepository;
    private final DashboardResponsableService dashboardService;

    @Override
    @Transactional(readOnly = true)
    public DashboardAdminDTO getStats() {
        DashboardAdminDTO dto = new DashboardAdminDTO();

        // Section 1 — Structure
        dto.setNombreParcs(parcRepository.count());
        dto.setNombreSites(siteRepository.count());
        dto.setNombreAscenseurs(ascenseurRepository.count());
        dto.setNombreClients(clientRepository.count());

        // Section 3 — Santé opérationnelle
        long total = demandeRepository.count();
        long resolues = demandeRepository.countByStatut(StatutDemande.RESOLUE);
        long annulees = demandeRepository.countByStatut(StatutDemande.ANNULEE);

        dto.setDemandesTotales(total);
        dto.setDemandesEnAttente(demandeRepository.countByStatut(StatutDemande.EN_ATTENTE));
        dto.setInterventionsEnCours(bonTravailRepository.countByStatut(StatutBonTravail.EN_COURS));

        long rejetees = demandeRepository.countByStatut(StatutDemande.REJETEE);
        long base = total - annulees - rejetees;
        double taux = (base > 0) ? ((double) resolues / base) * 100 : 0;
        dto.setTauxResolution(Math.round(taux * 10.0) / 10.0);

        dto.setAnomaliesCritiques(dashboardService.getAnomaliesCritiques().size());

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public RepartitionUtilisateursDTO getRepartitionUtilisateurs() {
        RepartitionUtilisateursDTO dto = new RepartitionUtilisateursDTO();

        long clients = clientRepository.count();
        long techniciens = technicienRepository.count();
        long responsables = responsableRepository.count();
        long admins = administrateurRepository.count();

        dto.setClients(clients);
        dto.setTechniciens(techniciens);
        dto.setResponsables(responsables);
        dto.setAdministrateurs(admins);
        dto.setTotal(clients + techniciens + responsables + admins);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActiviteParParcDTO> getActiviteParParc() {
        List<Object[]> comptages = demandeRepository
                .countEnAttenteGroupeParParc(StatutDemande.EN_ATTENTE);

        Map<Long, Long> demandesParParc = new HashMap<>();
        for (Object[] ligne : comptages) {
            Long parcId = (Long) ligne[0];
            Long nombre = (Long) ligne[1];
            demandesParParc.put(parcId, nombre);
        }

        List<Parc> parcs = parcRepository.findAllByOrderByNomAsc();

        return parcs.stream()
                .map(parc -> new ActiviteParParcDTO(
                        parc.getId(),
                        parc.getNom(),
                        siteRepository.countByParcId(parc.getId()),
                        technicienRepository.countByParcs_Id(parc.getId()),
                        demandesParParc.getOrDefault(parc.getId(), 0L)
                ))
                .toList();
    }
}
