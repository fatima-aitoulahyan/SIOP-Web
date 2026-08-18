package com.example.backend_siop.ascenseur.config;

import com.example.backend_siop.ascenseur.entity.AssemblageTemplate;
import com.example.backend_siop.ascenseur.entity.ComposantTemplate;
import com.example.backend_siop.ascenseur.enums.TypeAscenseur;
import com.example.backend_siop.ascenseur.enums.TypeComposant;
import com.example.backend_siop.ascenseur.repository.AssemblageTemplateRepository;
import com.example.backend_siop.common.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TemplateAscenseurSeeder implements ApplicationRunner {

    private final AssemblageTemplateRepository repository;
    private final FileStorageUtil fileStorageUtil;

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            return; // déjà seedé, on ne fait rien
        }

        List<AssemblageTemplate> racines = new ArrayList<>();
        racines.addAll(construireTraction());
        racines.addAll(construireHydraulique());
        racines.addAll(construireMrl());

        repository.saveAll(racines); // cascade = ALL propage tout l'arbre
    }

    // ---------- Helpers ----------

    private AssemblageTemplate zone(String nom, int niveau, TypeAscenseur type, AssemblageTemplate parent) {
        return zone(nom, niveau, type, parent, null);
    }

    private AssemblageTemplate zone(String nom, int niveau, TypeAscenseur type, AssemblageTemplate parent, String imageRessource) {
        AssemblageTemplate z = new AssemblageTemplate();
        z.setNom(nom);
        z.setNiveau(niveau);
        z.setTypeAscenseur(type);
        z.setParent(parent);

        if (imageRessource != null) {
            // Vous pouvez stocker les images des zones dans un dossier distinct dans MinIO
            String chemin = fileStorageUtil.storeFromClasspath(
                    "images/templates/" + imageRessource, "templates/assemblages");
            z.setImageUrl(chemin); // Assurez-vous que le champ imageUrl existe dans AssemblageTemplate
        }

        if (parent != null) {
            parent.getSousAssemblages().add(z);
        }
        return z;
    }

    private void composant(AssemblageTemplate zone, String nom, TypeComposant type) {
        composant(zone, nom, type, null);
    }

    private void composant(AssemblageTemplate zone, String nom, TypeComposant type, String imageRessource) {
        ComposantTemplate c = new ComposantTemplate();
        c.setNom(nom);
        c.setType(type);
        c.setAssemblageTemplate(zone);

        if (imageRessource != null) {
            String chemin = fileStorageUtil.storeFromClasspath(
                    "images/templates/" + imageRessource, "templates/composants");
            c.setImageUrl(chemin);
        }

        zone.getComposants().add(c);
    }

    // ---------- TRACTION ----------

    private List<AssemblageTemplate> construireTraction() {
        TypeAscenseur t = TypeAscenseur.TRACTION;

        AssemblageTemplate localMachinerie = zone("Local machinerie", 1, t, null);

        AssemblageTemplate machineTraction = zone("Machine de traction", 2, t, localMachinerie);
        composant(machineTraction, "Moteur", TypeComposant.MOTEUR , "Moteur électrique.jpg");
        composant(machineTraction, "Réducteur", TypeComposant.REDUCTEUR);
        composant(machineTraction, "Frein", TypeComposant.FREIN);
        composant(machineTraction, "Poulie motrice", TypeComposant.POULIE);
        composant(machineTraction, "Encodeur", TypeComposant.ENCODEUR);
        composant(machineTraction, "Ventilateur", TypeComposant.VENTILATEUR);
        composant(machineTraction, "Roulements", TypeComposant.ROULEMENT);

        AssemblageTemplate armoire = zone("Armoire de commande", 2, t, localMachinerie);
        composant(armoire, "Automate", TypeComposant.AUTOMATE);
        composant(armoire, "Variateur VVVF", TypeComposant.VARIATEUR);
        composant(armoire, "Cartes électroniques", TypeComposant.CARTE_ELECTRONIQUE);
        composant(armoire, "Transformateur", TypeComposant.TRANSFORMATEUR);
        composant(armoire, "Alimentations", TypeComposant.ALIMENTATION);
        composant(armoire, "Contacteurs", TypeComposant.CONTACTEUR);
        composant(armoire, "Relais", TypeComposant.RELAIS);
        composant(armoire, "Disjoncteurs", TypeComposant.DISJONCTEUR);
        composant(armoire, "Fusibles", TypeComposant.FUSIBLE);
        composant(armoire, "Borniers", TypeComposant.BORNIER);
        composant(armoire, "UPS", TypeComposant.UPS);
        composant(armoire, "Chargeur de batteries", TypeComposant.CHARGEUR_BATTERIE);
        composant(armoire, "Batteries", TypeComposant.BATTERIE);
        composant(armoire, "Carte ARD", TypeComposant.CARTE_ARD);
        composant(armoire, "Module GSM/4G", TypeComposant.MODULE_GSM);
        composant(armoire, "Ethernet", TypeComposant.MODULE_ETHERNET);
        composant(armoire, "Interface de diagnostic", TypeComposant.INTERFACE_DIAGNOSTIC);

        AssemblageTemplate limiteurVitesse = zone("Limiteur de vitesse", 2, t, localMachinerie);
        composant(limiteurVitesse, "Poulie", TypeComposant.POULIE);
        composant(limiteurVitesse, "Masselottes", TypeComposant.MASSELOTTE);
        composant(limiteurVitesse, "Roulements", TypeComposant.ROULEMENT);
        composant(limiteurVitesse, "Câble limiteur", TypeComposant.CABLE_LIMITEUR);

        AssemblageTemplate gaine = zone("Gaine", 1, t, null);

        AssemblageTemplate cabine = zone("Cabine", 2, t, gaine);
        composant(cabine, "Châssis", TypeComposant.CHASSIS);
        composant(cabine, "Parois", TypeComposant.PAROI);
        composant(cabine, "Plafond", TypeComposant.PLAFOND);
        composant(cabine, "Sol", TypeComposant.SOL);
        composant(cabine, "Main courante", TypeComposant.MAIN_COURANTE);
        composant(cabine, "Miroir", TypeComposant.MIROIR);
        composant(cabine, "Éclairage", TypeComposant.ECLAIRAGE);
        composant(cabine, "Ventilateur", TypeComposant.VENTILATEUR);
        composant(cabine, "Boîte à boutons", TypeComposant.BOITE_BOUTONS);
        composant(cabine, "Afficheur", TypeComposant.AFFICHEUR);
        composant(cabine, "Téléalarme", TypeComposant.TELEALARME);
        composant(cabine, "Caméra", TypeComposant.CAMERA);

        AssemblageTemplate porteCabine = zone("Porte cabine", 3, t, cabine);
        composant(porteCabine, "Opérateur", TypeComposant.OPERATEUR_PORTE);
        composant(porteCabine, "Courroie", TypeComposant.COURROIE);
        composant(porteCabine, "Galets", TypeComposant.GALET);
        composant(porteCabine, "Contre-galets", TypeComposant.CONTRE_GALET);
        composant(porteCabine, "Rail", TypeComposant.RAIL);
        composant(porteCabine, "Cellule photoélectrique", TypeComposant.CELLULE_PHOTOELECTRIQUE);
        composant(porteCabine, "Rideau lumineux", TypeComposant.RIDEAU_LUMINEUX);

        AssemblageTemplate contrepoids = zone("Contrepoids", 2, t, gaine);
        composant(contrepoids, "Châssis", TypeComposant.CHASSIS);
        composant(contrepoids, "Galets", TypeComposant.GALET);

        AssemblageTemplate guidage = zone("Guidage", 2, t, gaine);
        composant(guidage, "Rails cabine", TypeComposant.RAIL);
        composant(guidage, "Consoles", TypeComposant.CONSOLE);

        AssemblageTemplate suspension = zone("Suspension", 2, t, gaine);
        composant(suspension, "Câbles", TypeComposant.CABLE_SUSPENSION);
        composant(suspension, "Attaches câble", TypeComposant.ATTACHE_CABLE);
        composant(suspension, "Chaînes compensatrices", TypeComposant.CHAINE_COMPENSATRICE);
        composant(suspension, "Tendeur", TypeComposant.TENDEUR);

        AssemblageTemplate securite = zone("Sécurité", 2, t, gaine);
        composant(securite, "Parachute", TypeComposant.PARACHUTE);
        composant(securite, "Fins de course", TypeComposant.FIN_DE_COURSE);
        composant(securite, "Capteurs", TypeComposant.CAPTEUR);

        AssemblageTemplate paliers = zone("Paliers", 1, t, null);

        AssemblageTemplate portePaliere = zone("Porte palière", 2, t, paliers);
        composant(portePaliere, "Vantaux", TypeComposant.VANTAIL);
        composant(portePaliere, "Rail", TypeComposant.RAIL);
        composant(portePaliere, "Galets", TypeComposant.GALET);
        composant(portePaliere, "Contre-galets", TypeComposant.CONTRE_GALET);
        composant(portePaliere, "Serrure", TypeComposant.SERRURE);
        composant(portePaliere, "Crochet", TypeComposant.CROCHET);
        composant(portePaliere, "Percuteur", TypeComposant.PERCUTEUR);
        composant(portePaliere, "Shunt", TypeComposant.SHUNT);
        composant(portePaliere, "Microcontact", TypeComposant.MICROCONTACT);
        composant(portePaliere, "Amortisseur", TypeComposant.AMORTISSEUR);

        AssemblageTemplate equipPalier = zone("Équipements de palier", 2, t, paliers);
        composant(equipPalier, "Boîte à boutons", TypeComposant.BOITE_BOUTONS);
        composant(equipPalier, "Afficheur", TypeComposant.AFFICHEUR);
        composant(equipPalier, "Plaque signalétique", TypeComposant.PLAQUE_SIGNALETIQUE);
        composant(equipPalier, "Éclairage", TypeComposant.ECLAIRAGE);

        AssemblageTemplate fosse = zone("Fosse", 1, t, null);
        composant(fosse, "Amortisseurs cabine", TypeComposant.AMORTISSEUR);
        composant(fosse, "Amortisseurs contrepoids", TypeComposant.AMORTISSEUR);
        composant(fosse, "Interrupteur STOP", TypeComposant.INTERRUPTEUR_STOP);
        composant(fosse, "Boîtier inspection", TypeComposant.BOITIER_INSPECTION);
        composant(fosse, "Éclairage", TypeComposant.ECLAIRAGE);
        composant(fosse, "Échelle", TypeComposant.ECHELLE);
        composant(fosse, "Tendeur câble limiteur", TypeComposant.TENDEUR);
        composant(fosse, "Chaîne compensatrice", TypeComposant.CHAINE_COMPENSATRICE);
        composant(fosse, "Drainage", TypeComposant.DRAINAGE);
        composant(fosse, "Capteur d'eau", TypeComposant.CAPTEUR_EAU);

        return List.of(localMachinerie, gaine, paliers, fosse);
    }

    // ---------- HYDRAULIQUE ----------

    private List<AssemblageTemplate> construireHydraulique() {
        TypeAscenseur t = TypeAscenseur.HYDRAULIQUE;

        AssemblageTemplate localTechnique = zone("Local technique", 1, t, null);

        AssemblageTemplate centrale = zone("Centrale hydraulique", 2, t, localTechnique);
        composant(centrale, "Moteur électrique", TypeComposant.MOTEUR ,"Moteur électrique.jpg");
        composant(centrale, "Pompe hydraulique", TypeComposant.POMPE_HYDRAULIQUE, "Pompe Hydraulique.jpg");
        composant(centrale, "Réservoir d'huile", TypeComposant.RESERVOIR_HUILE,"Réservoir d'huile.jpg");
        composant(centrale, "Électrovannes", TypeComposant.ELECTROVANNE,"Électrovannes.jpg");
        composant(centrale, "Clapet anti-retour", TypeComposant.CLAPET_ANTI_RETOUR);

        AssemblageTemplate armoire = zone("Armoire de commande", 2, t, localTechnique ,"Armoire de commande.jpg");
        composant(armoire, "UPS", TypeComposant.UPS,"UPS.jpg");
        composant(armoire, "Chargeur", TypeComposant.CHARGEUR_BATTERIE,"Chargeur.jpg");
        composant(armoire, "Batteries", TypeComposant.BATTERIE,"Batteries.png");
        composant(armoire, "Carte ARD", TypeComposant.CARTE_ARD,"Carte ARD.jpg");

        AssemblageTemplate gaine = zone("Intérieur de gaine", 1, t, null,"Intérieur de gaine.jpg");

        AssemblageTemplate cabine = zone("Cabine", 2, t, gaine,"Cabine.jpg");
        composant(cabine, "Châssis", TypeComposant.CHASSIS,"Châssis.jpg");
        composant(cabine, "Parois", TypeComposant.PAROI,"Parois.jpg");
        composant(cabine, "Plafond", TypeComposant.PLAFOND,"Plafond.jpg");
        composant(cabine, "Sol", TypeComposant.SOL,"Sol.jpg");
        composant(cabine, "Boîte à boutons", TypeComposant.BOITE_BOUTONS,"Boîte à boutons.jpg");

        AssemblageTemplate verin = zone("Vérin hydraulique", 2, t, gaine,"Vérin hydraulique.jpg");
        composant(verin, "Vérin", TypeComposant.VERIN_HYDRAULIQUE,"Vérin.jpg");

        AssemblageTemplate guidage = zone("Guidage", 2, t, gaine,"Guidage.jpg");
        composant(guidage, "Rails", TypeComposant.RAIL,"Rails.jpg");
        composant(guidage, "Sabots", TypeComposant.SABOT,"Sabot.jpg");

        AssemblageTemplate porteCabine = zone("Porte cabine", 2, t, gaine,"Porte cabine.jpg");
        composant(porteCabine, "Opérateur", TypeComposant.OPERATEUR_PORTE,"Opérateur.jpg");
        composant(porteCabine, "Galets", TypeComposant.GALET,"Galets.jpg");
        composant(porteCabine, "Contre-galets", TypeComposant.CONTRE_GALET,"Contre-galets.jpg");
        composant(porteCabine, "Courroie", TypeComposant.COURROIE);
        composant(porteCabine, "Cellule", TypeComposant.CELLULE_PHOTOELECTRIQUE);

        AssemblageTemplate paliers = zone("Paliers", 1, t, null,"Paliers.jpg");
        AssemblageTemplate portePaliere = zone("Porte palière", 2, t, paliers,"Porte palière.jpg");
        composant(portePaliere, "Galets", TypeComposant.GALET,"Galets.jpg");
        composant(portePaliere, "Contre-galets", TypeComposant.CONTRE_GALET,"Contre-galets.jpg");
        composant(portePaliere, "Percuteur", TypeComposant.PERCUTEUR,"Percuteur.jpg");
        composant(portePaliere, "Shunt", TypeComposant.SHUNT,"Shunt.jpg");
        composant(portePaliere, "Crochet", TypeComposant.CROCHET,"Crochet.jpg");
        composant(portePaliere, "Serrure", TypeComposant.SERRURE,"Serrure.jpg");
        composant(portePaliere, "Contrepoids", TypeComposant.CONTREPOIDS,"Contrepoids.jpg");
        composant(portePaliere, "Poulie", TypeComposant.POULIE,"Poulie.jpg");

        AssemblageTemplate fosse = zone("Fosse", 1, t, null,"Fosse.jpg");
        composant(fosse, "Amortisseurs", TypeComposant.AMORTISSEUR,"Amortisseurs.jpg");
        composant(fosse, "STOP", TypeComposant.INTERRUPTEUR_STOP,"STOP.jpg");
        composant(fosse, "Boîtier inspection", TypeComposant.BOITIER_INSPECTION,"Boîtier inspection.jpg");
        composant(fosse, "Éclairage", TypeComposant.ECLAIRAGE,"Éclairage.jpg");
        composant(fosse, "Échelle", TypeComposant.ECHELLE,"Échelle.jpg");
        composant(fosse, "Drainage", TypeComposant.DRAINAGE,"Drainage.jpg");

        return List.of(localTechnique, gaine, paliers, fosse);
    }

    // ---------- MRL ----------

    private List<AssemblageTemplate> construireMrl() {
        TypeAscenseur t = TypeAscenseur.MRL;

        AssemblageTemplate teteGaine = zone("Tête de gaine", 1, t, null);

        AssemblageTemplate machineGearless = zone("Machine gearless", 2, t, teteGaine);
        composant(machineGearless, "Moteur synchrone à aimants permanents", TypeComposant.MOTEUR);
        composant(machineGearless, "Poulie motrice", TypeComposant.POULIE);
        composant(machineGearless, "Frein électromagnétique", TypeComposant.FREIN);
        composant(machineGearless, "Encodeur", TypeComposant.ENCODEUR);
        composant(machineGearless, "Roulements", TypeComposant.ROULEMENT);
        composant(machineGearless, "Ventilateur", TypeComposant.VENTILATEUR);
        composant(machineGearless, "Silentblocs", TypeComposant.SILENTBLOC);

        AssemblageTemplate limiteurVitesse = zone("Limiteur de vitesse", 2, t, teteGaine);
        composant(limiteurVitesse, "Poulie", TypeComposant.POULIE);
        composant(limiteurVitesse, "Masselottes", TypeComposant.MASSELOTTE);
        composant(limiteurVitesse, "Câble limiteur", TypeComposant.CABLE_LIMITEUR);

        AssemblageTemplate armoire = zone("Armoire de commande (déportée)", 1, t, null);
        composant(armoire, "Automate", TypeComposant.AUTOMATE);
        composant(armoire, "Variateur VVVF", TypeComposant.VARIATEUR);
        composant(armoire, "Cartes électroniques", TypeComposant.CARTE_ELECTRONIQUE);
        composant(armoire, "Alimentations", TypeComposant.ALIMENTATION);
        composant(armoire, "Contacteurs", TypeComposant.CONTACTEUR);
        composant(armoire, "Relais", TypeComposant.RELAIS);
        composant(armoire, "Disjoncteurs", TypeComposant.DISJONCTEUR);
        composant(armoire, "Borniers", TypeComposant.BORNIER);
        composant(armoire, "UPS", TypeComposant.UPS);
        composant(armoire, "Chargeur de batteries", TypeComposant.CHARGEUR_BATTERIE);
        composant(armoire, "Batteries", TypeComposant.BATTERIE);
        composant(armoire, "Carte ARD", TypeComposant.CARTE_ARD);
        composant(armoire, "Module GSM/4G", TypeComposant.MODULE_GSM);
        composant(armoire, "Ethernet", TypeComposant.MODULE_ETHERNET);
        composant(armoire, "Interface de diagnostic", TypeComposant.INTERFACE_DIAGNOSTIC);

        AssemblageTemplate gaine = zone("Intérieur de gaine", 1, t, null);

        AssemblageTemplate cabine = zone("Cabine", 2, t, gaine);
        composant(cabine, "Châssis", TypeComposant.CHASSIS);
        composant(cabine, "Parois", TypeComposant.PAROI);
        composant(cabine, "Plafond", TypeComposant.PLAFOND);
        composant(cabine, "Boîte à boutons", TypeComposant.BOITE_BOUTONS);
        composant(cabine, "Afficheur", TypeComposant.AFFICHEUR);
        composant(cabine, "Téléalarme", TypeComposant.TELEALARME);

        AssemblageTemplate porteCabine = zone("Porte cabine", 3, t, cabine);
        composant(porteCabine, "Opérateur", TypeComposant.OPERATEUR_PORTE);
        composant(porteCabine, "Courroie", TypeComposant.COURROIE);
        composant(porteCabine, "Galets", TypeComposant.GALET);
        composant(porteCabine, "Contre-galets", TypeComposant.CONTRE_GALET);
        composant(porteCabine, "Roulements", TypeComposant.ROULEMENT);
        composant(porteCabine, "Cellule infrarouge", TypeComposant.CELLULE_PHOTOELECTRIQUE);
        composant(porteCabine, "Rideau lumineux", TypeComposant.RIDEAU_LUMINEUX);

        AssemblageTemplate contrepoids = zone("Contrepoids", 2, t, gaine);
        composant(contrepoids, "Châssis", TypeComposant.CHASSIS);
        composant(contrepoids, "Sabots", TypeComposant.SABOT);

        AssemblageTemplate guidage = zone("Guidage", 2, t, gaine);
        composant(guidage, "Rails", TypeComposant.RAIL);
        composant(guidage, "Consoles", TypeComposant.CONSOLE);

        AssemblageTemplate paliers = zone("Paliers", 1, t, null);
        AssemblageTemplate portesPalieres = zone("Portes palières", 2, t, paliers);
        composant(portesPalieres, "Vantaux", TypeComposant.VANTAIL);
        composant(portesPalieres, "Galets", TypeComposant.GALET);
        composant(portesPalieres, "Contre-galets", TypeComposant.CONTRE_GALET);
        composant(portesPalieres, "Percuteur", TypeComposant.PERCUTEUR);
        composant(portesPalieres, "Shunt", TypeComposant.SHUNT);
        composant(portesPalieres, "Serrure", TypeComposant.SERRURE);
        composant(portesPalieres, "Crochet", TypeComposant.CROCHET);
        composant(portesPalieres, "Microcontact", TypeComposant.MICROCONTACT);
        composant(portesPalieres, "Contrepoids", TypeComposant.CONTREPOIDS);
        composant(portesPalieres, "Poulie", TypeComposant.POULIE);
        composant(portesPalieres, "Afficheur", TypeComposant.AFFICHEUR);
        composant(portesPalieres, "Boutons d'appel", TypeComposant.BOUTON_APPEL);

        AssemblageTemplate fosse = zone("Fosse", 1, t, null);
        composant(fosse, "Amortisseurs", TypeComposant.AMORTISSEUR);
        composant(fosse, "Interrupteur STOP", TypeComposant.INTERRUPTEUR_STOP);
        composant(fosse, "Boîtier inspection", TypeComposant.BOITIER_INSPECTION);
        composant(fosse, "Éclairage", TypeComposant.ECLAIRAGE);
        composant(fosse, "Échelle", TypeComposant.ECHELLE);
        composant(fosse, "Tendeur", TypeComposant.TENDEUR);
        composant(fosse, "Drainage", TypeComposant.DRAINAGE);
        composant(fosse, "Capteur d'eau", TypeComposant.CAPTEUR_EAU);

        return List.of(teteGaine, armoire, gaine, paliers, fosse);
    }
}