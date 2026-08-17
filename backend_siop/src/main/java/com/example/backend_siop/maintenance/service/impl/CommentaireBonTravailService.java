package com.example.backend_siop.maintenance.service.impl;

import com.example.backend_siop.maintenance.dto.CommentaireDTO;
import com.example.backend_siop.maintenance.entity.BonTravail;
import com.example.backend_siop.maintenance.entity.CommentaireBonTravail;
import com.example.backend_siop.maintenance.repository.BonTravailRepository;
import com.example.backend_siop.maintenance.repository.CommentaireBonTravailRepository;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import com.example.backend_siop.utilisateur.enums.TypeUtilisateur;
import com.example.backend_siop.utilisateur.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentaireBonTravailService {

    private final CommentaireBonTravailRepository commentaireRepository;
    private final BonTravailRepository bonTravailRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Transactional(readOnly = true)
    public List<CommentaireDTO> listerParBonTravail(Long btId) {
        return commentaireRepository.findByBonTravailIdOrderByCreatedAtAsc(btId)
                .stream()
                .map(this::versDTO)
                .toList();
    }

    @Transactional
    public CommentaireDTO ajouter(Long btId, String contenu, String emailAuteur) {
        BonTravail bt = bonTravailRepository.findById(btId)
                .orElseThrow(() -> new IllegalArgumentException("Bon de travail introuvable"));

        Utilisateur auteur = utilisateurRepository.findByEmail(emailAuteur)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        verifierAutorisation(bt, auteur);

        CommentaireBonTravail c = new CommentaireBonTravail();
        c.setBonTravail(bt);
        c.setAuteur(auteur);
        c.setContenu(contenu.trim());

        return versDTO(commentaireRepository.save(c));
    }
    @Transactional
    public void supprimer(Long btId, Long commentaireId, String emailAuteur) {
        CommentaireBonTravail c = commentaireRepository.findByIdAndBonTravailId(commentaireId, btId)
                .orElseThrow(() -> new IllegalArgumentException("Commentaire introuvable"));

        Utilisateur demandeur = utilisateurRepository.findByEmail(emailAuteur)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        if (!c.getAuteur().getId().equals(demandeur.getId())) {
            throw new SecurityException("Vous ne pouvez supprimer que vos propres messages.");
        }

        commentaireRepository.delete(c);
    }

    private void verifierAutorisation(BonTravail bt, Utilisateur auteur) {
        boolean estTechnicienDuBt = bt.getTechniciens().stream()
                .anyMatch(t -> t.getId().equals(auteur.getId()))
                || bt.getTechnicienResponsable().getId().equals(auteur.getId());

        boolean estResponsableMaintenance = auteur.getType() == TypeUtilisateur.RESPONSABLE_MAINTENANCE;

        if (!estTechnicienDuBt && !estResponsableMaintenance) {
            throw new SecurityException("Non autorisé à commenter ce bon de travail.");
        }
    }

    private CommentaireDTO versDTO(CommentaireBonTravail c) {
        Utilisateur a = c.getAuteur();
        return new CommentaireDTO(
                c.getId(),
                a.getId(),
                a.getPrenom() + " " + a.getNom(),
                a.getType().name(),
                c.getContenu(),
                c.getCreatedAt()
        );
    }
}