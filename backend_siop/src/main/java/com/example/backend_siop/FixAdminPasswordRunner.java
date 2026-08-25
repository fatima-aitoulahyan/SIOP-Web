package com.example.backend_siop;

import com.example.backend_siop.utilisateur.entity.*;
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
        String password = "123456789";
        String encodedPassword = passwordEncoder.encode(password);

        // 1. Administrateur Principal
        creerOuMettreAJour(
                "aitoulahyanfatima310@gmail.com",
                password,
                "aitoulahyan",
                "fatima",
                new Administrateur()
        );

        // 2. Client
        creerOuMettreAJour(
                "client1@gmail.com",
                password,
                "client1",
                "client1",
                new Client()
        );

        // 3. Responsable Maintenance
        creerOuMettreAJour(
                "rmaintenance@gmail.com",
                password,
                "Responsable",
                "Maintenance",
                new ResponsableMaintenance()
        );

        // 4. Technicien
        creerOuMettreAJour(
                "technicien1@gmail.com",
                password,
                "Technicien1",
                "Technicien1",
                new Technicien()
        );

        // 5. Hamza (Admin)
        creerOuMettreAJour(
                "hamza.elbarrak-etu@etu.univh2c.ma",
                password,
                "EL BARRAK",
                "HAMZA",
                new Administrateur()
        );

        System.out.println("══════════════════════════════════════");
        System.out.println("  Comptes initialisés avec succès !");
        System.out.println("  Mot de passe par défaut : " + password);
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
                    // On met à jour le nom et prénom au cas où ils changeraient
                    user.setNom(nom);
                    user.setPrenom(prenom);
                    utilisateurRepository.save(user);
                    System.out.println("[" + user.getType() + "] Mis à jour : " + email);
                },
                () -> {
                    template.setEmail(email);
                    template.setMotDePasse(passwordEncoder.encode(motDePasse));
                    template.setNom(nom);
                    template.setPrenom(prenom);
                    template.setActif(true);
                    utilisateurRepository.save(template);
                    System.out.println("[" + template.getType() + "] Créé : " + email);
                }
        );
    }
}