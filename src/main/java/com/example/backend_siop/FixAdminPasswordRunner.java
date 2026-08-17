 
package com.example.backend_siop;

import com.example.backend_siop.utilisateur.entity.Administrateur;
import com.example.backend_siop.utilisateur.repository.UtilisateurRepository;
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
        utilisateurRepository.findByEmail("aitoulahyanfatima310@gmail.com").ifPresentOrElse(
                user -> {
                    user.setMotDePasse(passwordEncoder.encode("fatima"));
                    user.setActif(true);
                    utilisateurRepository.save(user);
                    System.out.println("--> Mot de passe administrateur mis à jour avec succès !");
                },
                () -> {
                    Administrateur admin = new Administrateur();
                    admin.setEmail("aitoulahyanfatima310@gmail.com");
                    admin.setMotDePasse(passwordEncoder.encode("fatima"));
                    admin.setNom("aitoulahyan");
                    admin.setPrenom("fatima");
                    admin.setActif(true);
                    utilisateurRepository.save(admin);
                    System.out.println("--> Administrateur créé avec succès !");
                }
        );
    }
}
