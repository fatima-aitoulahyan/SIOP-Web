package com.example.backend_siop.config;

import com.example.backend_siop.utilisateur.entity.ResponsableMaintenance;
import com.example.backend_siop.utilisateur.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IntegrationUserInitializer implements ApplicationRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${integration.system.email}")
    private String systemUserEmail;

    @Override
    public void run(ApplicationArguments args) {
        if (utilisateurRepository.findByEmail(systemUserEmail).isEmpty()) {
            ResponsableMaintenance systemUser = new ResponsableMaintenance();
            systemUser.setEmail(systemUserEmail);
            systemUser.setNom("Système");
            systemUser.setPrenom("Intégration");
            // Mot de passe aléatoire et invalide pour la connexion humaine
            systemUser.setMotDePasse(passwordEncoder.encode(UUID.randomUUID().toString()));
            systemUser.setActif(true); // Toujours actif pour les appels machine
            systemUser.setTelephone("N/A");

            utilisateurRepository.save(systemUser);
            System.out.println("--> Utilisateur système d'intégration créé : " + systemUserEmail);
        } else {
            System.out.println("--> Utilisateur système d'intégration déjà présent.");
        }
    }
}