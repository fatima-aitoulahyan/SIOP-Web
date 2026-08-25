package com.example.backend_siop.config;

import com.example.backend_siop.parc.entity.Parc;
import com.example.backend_siop.parc.repository.ParcRepository;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.repository.TechnicienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IntegrationTechnicianInitializer implements ApplicationRunner {

    private final TechnicienRepository technicienRepository;
    private final ParcRepository parcRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${integration.fallback.technician-email}")
    private String fallbackEmail;

    @Value("${integration.fallback.technician-parc-name}")
    private String fallbackParcName;

    @Override
    public void run(ApplicationArguments args) {
        if (technicienRepository.findByEmail(fallbackEmail).isPresent()) {
            System.out.println("--> Technicien de secours déjà présent.");
            return;
        }

        // Créer un parc dédié si inexistant
        Parc parc = parcRepository.findByNom(fallbackParcName)
                .orElseGet(() -> {
                    Parc p = new Parc();
                    p.setNom(fallbackParcName);
                    return parcRepository.save(p);
                });

        Technicien tech = new Technicien();
        tech.setEmail(fallbackEmail);
        tech.setNom("Secours");
        tech.setPrenom("Intégration");
        tech.setMotDePasse(passwordEncoder.encode(UUID.randomUUID().toString()));
        tech.setActif(true);
        tech.setTelephone("N/A");
        tech.setSpecialite("Urgence");
        tech.setParcs(List.of(parc));

        technicienRepository.save(tech);
        System.out.println("--> Technicien de secours créé : " + fallbackEmail + " (parc: " + fallbackParcName + ")");
    }
}