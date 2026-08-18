package com.example.backend_siop;

import com.example.backend_siop.utilisateur.entity.*;
import com.example.backend_siop.utilisateur.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class FixAdminPasswordRunner implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String password = passwordEncoder.encode("123456789");

        // 1. Administrateur Principal
        upsertUser("aitoulahyanfatima310@gmail.com", "aitoulahyan", "fatima", password, Administrateur::new);

        // 2. Client
        upsertUser("client1@gmail.com", "client1", "client1", password, Client::new);

        // 3. Responsable Maintenance
        upsertUser("rmaintenance@gmail.com", "Responsable", "Maintenance", password, ResponsableMaintenance::new);

        // 4. Technicien
        upsertUser("technicien1@gmail.com", "Technicien1", "Technicien1", password, Technicien::new);

        // 5. Hamza (Admin ou autre selon votre choix)
        upsertUser("hamza.elbarrak-etu@etu.univh2c.ma", "EL BARRAK", "HAMZA", password, Administrateur::new);

        System.out.println("--> Initialisation des utilisateurs terminée avec le mot de passe: 123456789");
    }

    /**
     * Méthode utilitaire pour créer ou mettre à jour un utilisateur
     */
    private void upsertUser(String email, String nom, String prenom, String encodedPassword, Supplier<Utilisateur> userSupplier) {
        utilisateurRepository.findByEmail(email).ifPresentOrElse(
                user -> {
                    user.setMotDePasse(encodedPassword);
                    user.setActif(true);
                    user.setNom(nom);
                    user.setPrenom(prenom);
                    utilisateurRepository.save(user);
                    System.out.println("--> Mis à jour : " + email);
                },
                () -> {
                    Utilisateur newUser = userSupplier.get();
                    newUser.setEmail(email);
                    newUser.setMotDePasse(encodedPassword);
                    newUser.setNom(nom);
                    newUser.setPrenom(prenom);
                    newUser.setActif(true);
                    utilisateurRepository.save(newUser);
                    System.out.println("--> Créé (" + newUser.getClass().getSimpleName() + ") : " + email);
                }
        );
    }
}