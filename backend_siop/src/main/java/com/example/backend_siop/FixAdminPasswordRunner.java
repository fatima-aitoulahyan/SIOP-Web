package com.example.backend_siop;

import com.example.backend_siop.utilisateur.entity.Administrateur;
import com.example.backend_siop.utilisateur.entity.ResponsableMaintenance;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import com.example.backend_siop.utilisateur.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FixAdminPasswordRunner implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Administrateur
        creerOuMettreAJour(
                "aitoulahyanfatima310@gmail.com",
                "fatima",
                "aitoulahyan",
                "fatima",
                new Administrateur()
        );

        // 2. Responsable de maintenance (toi !)
        creerOuMettreAJour(
                "fatimazahraazzabi24@gmail.com",
                "resp123",
                "azzabi",
                "fatima zahra",
                new ResponsableMaintenance()
        );

        System.out.println("══════════════════════════════════════");
        System.out.println("  Comptes initialisés avec succès !");
        System.out.println("══════════════════════════════════════");
    }

    /**
     * Crée l'utilisateur s'il n'existe pas, sinon met à jour le mot de passe.
     */
    private void creerOuMettreAJour(
            String email,
            String motDePasse,
            String nom,
            String prenom,
            Utilisateur template
    ) {
        utilisateurRepository.findByEmail(email).ifPresentOrElse(
                user -> {
                    user.setMotDePasse(passwordEncoder.encode(motDePasse));
                    user.setActif(true);
                    utilisateurRepository.save(user);
                    System.out.println("[" + user.getType() + "] Mot de passe mis à jour : " + email);
                },
                () -> {
                    template.setEmail(email);
                    template.setMotDePasse(passwordEncoder.encode(motDePasse));
                    template.setNom(nom);
                    template.setPrenom(prenom);
                    template.setActif(true);
                    utilisateurRepository.save(template);
                    System.out.println("[" + template.getType() + "] Compte créé : " + email);
                }
        );
    }
}