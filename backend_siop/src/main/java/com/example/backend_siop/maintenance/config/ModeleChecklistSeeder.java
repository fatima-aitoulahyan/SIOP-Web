package com.example.backend_siop.maintenance.config;

import com.example.backend_siop.maintenance.entity.ModeleChecklist;
import com.example.backend_siop.maintenance.repository.ModeleChecklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ModeleChecklistSeeder implements CommandLineRunner {

    private final ModeleChecklistRepository modeleChecklistRepository;

    private static final Set<Integer> TOUS_LES_MOIS = Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
    private static final Set<Integer> MOIS_IMPAIRS = Set.of(1, 3, 5, 7, 9, 11);
    private static final Set<Integer> MOIS_TRIMESTRIELS = Set.of(1, 4, 7, 10);
    private static final Set<Integer> MOIS_SEMESTRIELS = Set.of(1, 7);
    private static final Set<Integer> MOIS_JUIN = Set.of(6);

    private record Item(String libelle, Set<Integer> mois) {}

    private static final List<Item> ITEMS = List.of(
            new Item("Prise de contact avec le client", TOUS_LES_MOIS),
            new Item("Vérification du fonctionnement des portes", TOUS_LES_MOIS),
            new Item("Vérification du fonctionnement des indicateurs de niveaux", TOUS_LES_MOIS),
            new Item("Contrôle d'accélération/décélération et arrêt aux étages", TOUS_LES_MOIS),
            new Item("Vérification de toutes les sécurités cabine et palière", TOUS_LES_MOIS),
            new Item("Vérification du fonctionnement du moteur en service normal", TOUS_LES_MOIS),
            new Item("Contrôle du régulateur de vitesse", TOUS_LES_MOIS),
            new Item("Contrôle du relais de protection du moteur", TOUS_LES_MOIS),
            new Item("Contrôle des bruits anormaux dans la gaine pendant le service normal", TOUS_LES_MOIS),
            new Item("Vérification de l'efficacité des serrures et condamnation de la cabine", TOUS_LES_MOIS),
            new Item("Vérification du fonctionnement électrique de toutes les portes", TOUS_LES_MOIS),
            new Item("Vérification du fonctionnement des contacts relais et du contrôleur", TOUS_LES_MOIS),
            new Item("Contrôle de l'éclairage de secours", TOUS_LES_MOIS),
            new Item("Contrôle de la signalisation lumineuse et sonore de surcharge", TOUS_LES_MOIS),
            new Item("Nettoyage du toit de cabine et fond de cuvette", MOIS_IMPAIRS),
            new Item("Contrôle des contacts de la boîte à boutons cabine et boutons paliers", MOIS_IMPAIRS),
            new Item("Contrôle du câble de traction et câble du régulateur", MOIS_IMPAIRS),
            new Item("Contrôle des réglages des freins", MOIS_IMPAIRS),
            new Item("Vérification des niveaux d'huile", MOIS_TRIMESTRIELS),
            new Item("Contrôle de la poulie de traction et de la poulie de renvoi", MOIS_TRIMESTRIELS),
            new Item("Vérification du fonctionnement des interrupteurs de fin de course", MOIS_SEMESTRIELS),
            new Item("Contrôle des réserves sous contre-poids", MOIS_SEMESTRIELS),
            new Item("Vérification et contrôle du fonctionnement du parachute", MOIS_JUIN),
            new Item("Vérification de la conformité des installations et de la main-d'œuvre", MOIS_JUIN),
            new Item("Essais et remise en service de l'appareil", TOUS_LES_MOIS)
    );

    @Override
    public void run(String... args) {
        if (modeleChecklistRepository.count() > 0) {
            return;
        }

        int ordre = 1;
        for (Item item : ITEMS) {
            ModeleChecklist m = new ModeleChecklist();
            m.setOrdre(ordre++);
            m.setLibelle(item.libelle());
            m.setMoisApplicables(item.mois());
            modeleChecklistRepository.save(m);
        }
    }
}