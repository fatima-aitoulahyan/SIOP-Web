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
        composant(machineTraction, "Moteur", TypeComposant.MOTEUR, "Moteur électrique.jpg");
        composant(machineTraction, "Réducteur", TypeComposant.REDUCTEUR,"Réducteur.jpg");
        composant(machineTraction, "Frein", TypeComposant.FREIN,"Frein.jpg");
        composant(machineTraction, "Poulie motrice", TypeComposant.POULIE,"Poulie motrice.jpg");
        composant(machineTraction, "Encodeur", TypeComposant.ENCODEUR,"Encodeur.jpg");
        composant(machineTraction, "Ventilateur", TypeComposant.VENTILATEUR,"Ventilateur.jpg");
        composant(machineTraction, "Roulements", TypeComposant.ROULEMENT,"Roulements.jpg");

        AssemblageTemplate armoire = zone("Armoire de commande", 2, t, localMachinerie, "Armoire de commande.jpg");
        composant(armoire, "Automate", TypeComposant.AUTOMATE,"Automate.jpg");
        composant(armoire, "Variateur VVVF", TypeComposant.VARIATEUR,"Variateur VVVF.jpg");
        composant(armoire, "Cartes électroniques", TypeComposant.CARTE_ELECTRONIQUE,"Cartes électroniques.jpg");
        composant(armoire, "Transformateur", TypeComposant.TRANSFORMATEUR,"Transformateur.jpg");
        composant(armoire, "Alimentations", TypeComposant.ALIMENTATION,"Alimentations.jpg");
        composant(armoire, "Contacteurs", TypeComposant.CONTACTEUR,"Contacteurs.jpg");
        composant(armoire, "Relais", TypeComposant.RELAIS,"Relais.jpg");
        composant(armoire, "Disjoncteurs", TypeComposant.DISJONCTEUR,"Disjoncteurs.jpg");
        composant(armoire, "Fusibles", TypeComposant.FUSIBLE,"Fusibles.jpg");
        composant(armoire, "Borniers", TypeComposant.BORNIER,"Borniers.jpg");
        composant(armoire, "UPS", TypeComposant.UPS, "UPS.jpg");
        composant(armoire, "Chargeur de batteries", TypeComposant.CHARGEUR_BATTERIE,"Chargeur de batteries.jpg");
        composant(armoire, "Batteries", TypeComposant.BATTERIE, "Batteries.png");
        composant(armoire, "Carte ARD", TypeComposant.CARTE_ARD, "Carte ARD.jpg");
        composant(armoire, "Module GSM/4G", TypeComposant.MODULE_GSM, "Module GSM4G.jpg");
        composant(armoire, "Ethernet", TypeComposant.MODULE_ETHERNET,"Ethernet.jpg");
        composant(armoire, "Interface de diagnostic", TypeComposant.INTERFACE_DIAGNOSTIC,"Interface de diagnostic.jpg");

        AssemblageTemplate limiteurVitesse = zone("Limiteur de vitesse", 2, t, localMachinerie,"Limiteur de vitesse.jpg");
        composant(limiteurVitesse, "Poulie", TypeComposant.POULIE, "Poulie.jpg");
        composant(limiteurVitesse, "Masselottes", TypeComposant.MASSELOTTE,"Masselottes.jpg");
        composant(limiteurVitesse, "Roulements", TypeComposant.ROULEMENT,"Roulements.jpg");
        composant(limiteurVitesse, "Câble limiteur", TypeComposant.CABLE_LIMITEUR,"Câble limiteur.jpg");

        AssemblageTemplate gaine = zone("Gaine", 1, t, null,"Gaine.jpg");

        AssemblageTemplate cabine = zone("Cabine", 2, t, gaine, "Cabine.jpg");
        composant(cabine, "Châssis", TypeComposant.CHASSIS, "Châssis.jpg");
        composant(cabine, "Parois", TypeComposant.PAROI, "Parois.jpg");
        composant(cabine, "Plafond", TypeComposant.PLAFOND, "Plafond.jpg");
        composant(cabine, "Sol", TypeComposant.SOL, "Sol.jpg");
        composant(cabine, "Main courante", TypeComposant.MAIN_COURANTE,"Main courante.jpg");
        composant(cabine, "Miroir", TypeComposant.MIROIR,"Miroir.jpg");
        composant(cabine, "Éclairage", TypeComposant.ECLAIRAGE, "Éclairage.jpg");
        composant(cabine, "Ventilateur", TypeComposant.VENTILATEUR,"Ventilateur.jpg");
        composant(cabine, "Boîte à boutons", TypeComposant.BOITE_BOUTONS, "Boîte à boutons.jpg");
        composant(cabine, "Afficheur", TypeComposant.AFFICHEUR,"Afficheur.jpg");
        composant(cabine, "Téléalarme", TypeComposant.TELEALARME,"Téléalarme.jpg");
        composant(cabine, "Caméra", TypeComposant.CAMERA,"Caméra.jpg");

        AssemblageTemplate porteCabine = zone("Porte cabine", 3, t, cabine, "Porte cabine.jpg");
        composant(porteCabine, "Opérateur", TypeComposant.OPERATEUR_PORTE, "Opérateur.jpg");
        composant(porteCabine, "Courroie", TypeComposant.COURROIE,"Courroie.jpg");
        composant(porteCabine, "Galets", TypeComposant.GALET, "Galets.jpg");
        composant(porteCabine, "Contre-galets", TypeComposant.CONTRE_GALET, "Contre-galets.jpg");
        composant(porteCabine, "Rail", TypeComposant.RAIL,"Rail.jpg");
        composant(porteCabine, "Cellule photoélectrique", TypeComposant.CELLULE_PHOTOELECTRIQUE,"Cellule photoélectrique.jpg");
        composant(porteCabine, "Rideau lumineux", TypeComposant.RIDEAU_LUMINEUX,"Rideau lumineux.jpg");

        AssemblageTemplate contrepoids = zone("Contrepoids", 2, t, gaine,"Contrepoids.jpg");
        composant(contrepoids, "Châssis", TypeComposant.CHASSIS, "Châssis.jpg");
        composant(contrepoids, "Galets", TypeComposant.GALET, "Galets.jpg");

        AssemblageTemplate guidage = zone("Guidage", 2, t, gaine, "Guidage.jpg");
        composant(guidage, "Rails cabine", TypeComposant.RAIL,"Rails cabine.jpg");
        composant(guidage, "Consoles", TypeComposant.CONSOLE,"Consoles.jpg");

        AssemblageTemplate suspension = zone("Suspension", 2, t, gaine,"Suspension.jpg");
        composant(suspension, "Câbles", TypeComposant.CABLE_SUSPENSION,"Câbles.jpg");
        composant(suspension, "Attaches câble", TypeComposant.ATTACHE_CABLE,"Attaches câble.jpg");
        composant(suspension, "Chaînes compensatrices", TypeComposant.CHAINE_COMPENSATRICE,"Chaînes compensatrices.jpg");
        composant(suspension, "Tendeur", TypeComposant.TENDEUR,"Tendeur.jpg");

        AssemblageTemplate securite = zone("Sécurité", 2, t, gaine,"Sécurité.jpg");
        composant(securite, "Parachute", TypeComposant.PARACHUTE,"Parachute.jpg");
        composant(securite, "Fins de course", TypeComposant.FIN_DE_COURSE,"Fins de course.jpg");
        composant(securite, "Capteurs", TypeComposant.CAPTEUR,"Capteurs.jpg");

        AssemblageTemplate paliers = zone("Paliers", 1, t, null, "Paliers.jpg");

        AssemblageTemplate portePaliere = zone("Porte palière", 2, t, paliers, "Porte palière.jpg");
        composant(portePaliere, "Vantaux", TypeComposant.VANTAIL,"Vantaux.jpg");
        composant(portePaliere, "Rail", TypeComposant.RAIL,"Rail.jpg");
        composant(portePaliere, "Galets", TypeComposant.GALET, "Galets.jpg");
        composant(portePaliere, "Contre-galets", TypeComposant.CONTRE_GALET, "Contre-galets.jpg");
        composant(portePaliere, "Serrure", TypeComposant.SERRURE, "Serrure.jpg");
        composant(portePaliere, "Crochet", TypeComposant.CROCHET, "Crochet.jpg");
        composant(portePaliere, "Percuteur", TypeComposant.PERCUTEUR, "Percuteur.jpg");
        composant(portePaliere, "Shunt", TypeComposant.SHUNT, "Shunt.jpg");
        composant(portePaliere, "Microcontact", TypeComposant.MICROCONTACT,"Microcontact.jpg");
        composant(portePaliere, "Amortisseur", TypeComposant.AMORTISSEUR,"Amortisseurs.jpg");

        AssemblageTemplate equipPalier = zone("Équipements de palier", 2, t, paliers,"Équipements de palier.jpg");
        composant(equipPalier, "Boîte à boutons", TypeComposant.BOITE_BOUTONS, "Boîte à boutons.jpg");
        composant(equipPalier, "Afficheur", TypeComposant.AFFICHEUR,"Afficheur.jpg");
        composant(equipPalier, "Plaque signalétique", TypeComposant.PLAQUE_SIGNALETIQUE,"Plaque signalétique.jpg");
        composant(equipPalier, "Éclairage", TypeComposant.ECLAIRAGE, "Éclairage.jpg");

        AssemblageTemplate fosse = zone("Fosse", 1, t, null, "Fosse.jpg");
        composant(fosse, "Amortisseurs cabine", TypeComposant.AMORTISSEUR,"Amortisseurs cabine.jpg");
        composant(fosse, "Amortisseurs contrepoids", TypeComposant.AMORTISSEUR,"Amortisseurs contrepoids.jpg");
        composant(fosse, "Interrupteur STOP", TypeComposant.INTERRUPTEUR_STOP,"Interrupteur STOP.jpg");
        composant(fosse, "Boîtier inspection", TypeComposant.BOITIER_INSPECTION, "Boîtier inspection.jpg");
        composant(fosse, "Éclairage", TypeComposant.ECLAIRAGE, "Éclairage.jpg");
        composant(fosse, "Échelle", TypeComposant.ECHELLE, "Échelle.jpg");
        composant(fosse, "Tendeur câble limiteur", TypeComposant.TENDEUR,"Tendeur câble limiteur.jpg");
        composant(fosse, "Chaîne compensatrice", TypeComposant.CHAINE_COMPENSATRICE,"Chaîne compensatrice.jpg");
        composant(fosse, "Drainage", TypeComposant.DRAINAGE, "Drainage.jpg");
        composant(fosse, "Capteur d'eau", TypeComposant.CAPTEUR_EAU,"Capteur d'eau.jpg");

        return List.of(localMachinerie, gaine, paliers, fosse);
    }

    // ---------- HYDRAULIQUE ----------

    private List<AssemblageTemplate> construireHydraulique() {
        TypeAscenseur t = TypeAscenseur.HYDRAULIQUE;

        AssemblageTemplate localTechnique = zone("Local technique", 1, t, null,"local technique.jpg");

        AssemblageTemplate centrale = zone("Centrale hydraulique", 2, t, localTechnique,"Centrale hydraulique.jpg");
        composant(centrale, "Moteur électrique", TypeComposant.MOTEUR, "Moteur électrique.jpg");
        composant(centrale, "Pompe hydraulique", TypeComposant.POMPE_HYDRAULIQUE, "Pompe Hydraulique.jpg");
        composant(centrale, "Réservoir d'huile", TypeComposant.RESERVOIR_HUILE, "Réservoir d'huile.jpg");
        composant(centrale, "Électrovannes", TypeComposant.ELECTROVANNE, "Électrovannes.jpg");
        composant(centrale, "Clapet anti-retour", TypeComposant.CLAPET_ANTI_RETOUR,"Clapet anti-retour.jpg");

        AssemblageTemplate armoire = zone("Armoire de commande", 2, t, localTechnique, "Armoire de commande.jpg");
        composant(armoire, "UPS", TypeComposant.UPS, "UPS.jpg");
        composant(armoire, "Chargeur", TypeComposant.CHARGEUR_BATTERIE, "Chargeur.jpg");
        composant(armoire, "Batteries", TypeComposant.BATTERIE, "Batteries.png");
        composant(armoire, "Carte ARD", TypeComposant.CARTE_ARD, "Carte ARD.jpg");

        AssemblageTemplate gaine = zone("Intérieur de gaine", 1, t, null, "Intérieur de gaine.jpg");

        AssemblageTemplate cabine = zone("Cabine", 2, t, gaine, "Cabine.jpg");
        composant(cabine, "Châssis", TypeComposant.CHASSIS, "Châssis.jpg");
        composant(cabine, "Parois", TypeComposant.PAROI, "Parois.jpg");
        composant(cabine, "Plafond", TypeComposant.PLAFOND, "Plafond.jpg");
        composant(cabine, "Sol", TypeComposant.SOL, "Sol.jpg");
        composant(cabine, "Boîte à boutons", TypeComposant.BOITE_BOUTONS, "Boîte à boutons.jpg");

        AssemblageTemplate verin = zone("Vérin hydraulique", 2, t, gaine, "Vérin hydraulique.jpg");
        composant(verin, "Vérin", TypeComposant.VERIN_HYDRAULIQUE, "Vérin.jpg");

        AssemblageTemplate guidage = zone("Guidage", 2, t, gaine, "Guidage.jpg");
        composant(guidage, "Rails", TypeComposant.RAIL, "Rails.jpg");
        composant(guidage, "Sabots", TypeComposant.SABOT, "Sabot.jpg");

        AssemblageTemplate porteCabine = zone("Porte cabine", 2, t, gaine, "Porte cabine.jpg");
        composant(porteCabine, "Opérateur", TypeComposant.OPERATEUR_PORTE, "Opérateur.jpg");
        composant(porteCabine, "Galets", TypeComposant.GALET, "Galets.jpg");
        composant(porteCabine, "Contre-galets", TypeComposant.CONTRE_GALET, "Contre-galets.jpg");
        composant(porteCabine, "Courroie", TypeComposant.COURROIE,"Courroie.jpg");
        composant(porteCabine, "Cellule", TypeComposant.CELLULE_PHOTOELECTRIQUE,"Cellule.jpg");

        AssemblageTemplate paliers = zone("Paliers", 1, t, null, "Paliers.jpg");
        AssemblageTemplate portePaliere = zone("Porte palière", 2, t, paliers, "Porte palière.jpg");
        composant(portePaliere, "Galets", TypeComposant.GALET, "Galets.jpg");
        composant(portePaliere, "Contre-galets", TypeComposant.CONTRE_GALET, "Contre-galets.jpg");
        composant(portePaliere, "Percuteur", TypeComposant.PERCUTEUR, "Percuteur.jpg");
        composant(portePaliere, "Shunt", TypeComposant.SHUNT, "Shunt.jpg");
        composant(portePaliere, "Crochet", TypeComposant.CROCHET, "Crochet.jpg");
        composant(portePaliere, "Serrure", TypeComposant.SERRURE, "Serrure.jpg");
        composant(portePaliere, "Contrepoids", TypeComposant.CONTREPOIDS, "Contrepoids.jpg");
        composant(portePaliere, "Poulie", TypeComposant.POULIE, "Poulie.jpg");

        AssemblageTemplate fosse = zone("Fosse", 1, t, null, "Fosse.jpg");
        composant(fosse, "Amortisseurs", TypeComposant.AMORTISSEUR, "Amortisseurs.jpg");
        composant(fosse, "STOP", TypeComposant.INTERRUPTEUR_STOP, "STOP.jpg");
        composant(fosse, "Boîtier inspection", TypeComposant.BOITIER_INSPECTION, "Boîtier inspection.jpg");
        composant(fosse, "Éclairage", TypeComposant.ECLAIRAGE, "Éclairage.jpg");
        composant(fosse, "Échelle", TypeComposant.ECHELLE, "Échelle.jpg");
        composant(fosse, "Drainage", TypeComposant.DRAINAGE, "Drainage.jpg");

        return List.of(localTechnique, gaine, paliers, fosse);
    }

    // ---------- MRL ----------

    private List<AssemblageTemplate> construireMrl() {
        TypeAscenseur t = TypeAscenseur.MRL;

        AssemblageTemplate teteGaine = zone("Tête de gaine", 1, t, null,"Tête de gaine.png");

        AssemblageTemplate machineGearless = zone("Machine gearless", 2, t, teteGaine,"Machine gearless.jpg");
        composant(machineGearless, "Moteur synchrone à aimants permanents", TypeComposant.MOTEUR,"Moteur synchrone à aimants permanents.jpg");
        composant(machineGearless, "Poulie motrice", TypeComposant.POULIE,"Poulie motrice.jpg");
        composant(machineGearless, "Frein électromagnétique", TypeComposant.FREIN,"Frein électromagnétique.jpg");
        composant(machineGearless, "Encodeur", TypeComposant.ENCODEUR,"Encodeur.jpg");
        composant(machineGearless, "Roulements", TypeComposant.ROULEMENT,"Roulements.jpg");
        composant(machineGearless, "Ventilateur", TypeComposant.VENTILATEUR,"Ventilateur.jpg");
        composant(machineGearless, "Silentblocs", TypeComposant.SILENTBLOC,"Silentblocs.jpg");

        AssemblageTemplate limiteurVitesse = zone("Limiteur de vitesse", 2, t, teteGaine,"Limiteur de vitesse.jpg");
        composant(limiteurVitesse, "Poulie", TypeComposant.POULIE, "Poulie.jpg");
        composant(limiteurVitesse, "Masselottes", TypeComposant.MASSELOTTE,"Masselottes.jpg");
        composant(limiteurVitesse, "Câble limiteur", TypeComposant.CABLE_LIMITEUR,"Câble limiteur.jpg");

        AssemblageTemplate armoire = zone("Armoire de commande (déportée)", 1, t, null,"Armoire de commande (déportée).jpg");
        composant(armoire, "Automate", TypeComposant.AUTOMATE,"Automate.jpg");
        composant(armoire, "Variateur VVVF", TypeComposant.VARIATEUR,"Variateur VVVF.jpg");
        composant(armoire, "Cartes électroniques", TypeComposant.CARTE_ELECTRONIQUE,"Cartes électroniques.jpg");
        composant(armoire, "Alimentations", TypeComposant.ALIMENTATION,"Alimentations.jpg");
        composant(armoire, "Contacteurs", TypeComposant.CONTACTEUR,"Contacteurs.jpg");
        composant(armoire, "Relais", TypeComposant.RELAIS,"Relais.jpg");
        composant(armoire, "Disjoncteurs", TypeComposant.DISJONCTEUR,"Disjoncteurs.jpg");
        composant(armoire, "Borniers", TypeComposant.BORNIER,"Borniers.jpg");
        composant(armoire, "UPS", TypeComposant.UPS, "UPS.jpg");
        composant(armoire, "Chargeur de batteries", TypeComposant.CHARGEUR_BATTERIE,"Chargeur de batteries.jpg");
        composant(armoire, "Batteries", TypeComposant.BATTERIE, "Batteries.png");
        composant(armoire, "Carte ARD", TypeComposant.CARTE_ARD, "Carte ARD.jpg");
        composant(armoire, "Module GSM/4G", TypeComposant.MODULE_GSM, "Module GSM4G.jpg");
        composant(armoire, "Ethernet", TypeComposant.MODULE_ETHERNET,"Ethernet.jpg");
        composant(armoire, "Interface de diagnostic", TypeComposant.INTERFACE_DIAGNOSTIC,"Interface de diagnostic.jpg");

        AssemblageTemplate gaine = zone("Intérieur de gaine", 1, t, null, "Intérieur de gaine.jpg");

        AssemblageTemplate cabine = zone("Cabine", 2, t, gaine, "Cabine.jpg");
        composant(cabine, "Châssis", TypeComposant.CHASSIS, "Châssis.jpg");
        composant(cabine, "Parois", TypeComposant.PAROI, "Parois.jpg");
        composant(cabine, "Plafond", TypeComposant.PLAFOND, "Plafond.jpg");
        composant(cabine, "Boîte à boutons", TypeComposant.BOITE_BOUTONS, "Boîte à boutons.jpg");
        composant(cabine, "Afficheur", TypeComposant.AFFICHEUR,"Afficheur.jpg");
        composant(cabine, "Téléalarme", TypeComposant.TELEALARME,"Téléalarme.jpg");

        AssemblageTemplate porteCabine = zone("Porte cabine", 3, t, cabine, "Porte cabine.jpg");
        composant(porteCabine, "Opérateur", TypeComposant.OPERATEUR_PORTE, "Opérateur.jpg");
        composant(porteCabine, "Courroie", TypeComposant.COURROIE,"Courroie.jpg");
        composant(porteCabine, "Galets", TypeComposant.GALET, "Galets.jpg");
        composant(porteCabine, "Contre-galets", TypeComposant.CONTRE_GALET, "Contre-galets.jpg");
        composant(porteCabine, "Roulements", TypeComposant.ROULEMENT,"Roulements.jpg");
        composant(porteCabine, "Cellule infrarouge", TypeComposant.CELLULE_PHOTOELECTRIQUE,"Cellule infrarouge.png");
        composant(porteCabine, "Rideau lumineux", TypeComposant.RIDEAU_LUMINEUX,"Rideau lumineux.jpg");

        AssemblageTemplate contrepoids = zone("Contrepoids", 2, t, gaine,"Contrepoids.jpg");
        composant(contrepoids, "Châssis", TypeComposant.CHASSIS, "Châssis.jpg");
        composant(contrepoids, "Sabots", TypeComposant.SABOT, "Sabot.jpg");

        AssemblageTemplate guidage = zone("Guidage", 2, t, gaine, "Guidage.jpg");
        composant(guidage, "Rails", TypeComposant.RAIL, "Rails.jpg");
        composant(guidage, "Consoles", TypeComposant.CONSOLE,"Consoles.jpg");

        AssemblageTemplate paliers = zone("Paliers", 1, t, null, "Paliers.jpg");
        AssemblageTemplate portesPalieres = zone("Portes palières", 2, t, paliers,"Portes palières.jpg");
        composant(portesPalieres, "Vantaux", TypeComposant.VANTAIL,"Vantaux.jpg");
        composant(portesPalieres, "Galets", TypeComposant.GALET, "Galets.jpg");
        composant(portesPalieres, "Contre-galets", TypeComposant.CONTRE_GALET, "Contre-galets.jpg");
        composant(portesPalieres, "Percuteur", TypeComposant.PERCUTEUR, "Percuteur.jpg");
        composant(portesPalieres, "Shunt", TypeComposant.SHUNT, "Shunt.jpg");
        composant(portesPalieres, "Serrure", TypeComposant.SERRURE, "Serrure.jpg");
        composant(portesPalieres, "Crochet", TypeComposant.CROCHET, "Crochet.jpg");
        composant(portesPalieres, "Microcontact", TypeComposant.MICROCONTACT);
        composant(portesPalieres, "Contrepoids", TypeComposant.CONTREPOIDS, "Contrepoids.jpg");
        composant(portesPalieres, "Poulie", TypeComposant.POULIE, "Poulie.jpg");
        composant(portesPalieres, "Afficheur", TypeComposant.AFFICHEUR,"Afficheur.jpg");
        composant(portesPalieres, "Boutons d'appel", TypeComposant.BOUTON_APPEL,"Boutons d'appel.jpg");

        AssemblageTemplate fosse = zone("Fosse", 1, t, null, "Fosse.jpg");
        composant(fosse, "Amortisseurs", TypeComposant.AMORTISSEUR, "Amortisseurs.jpg");
        composant(fosse, "Interrupteur STOP", TypeComposant.INTERRUPTEUR_STOP);
        composant(fosse, "Boîtier inspection", TypeComposant.BOITIER_INSPECTION, "Boîtier inspection.jpg");
        composant(fosse, "Éclairage", TypeComposant.ECLAIRAGE, "Éclairage.jpg");
        composant(fosse, "Échelle", TypeComposant.ECHELLE, "Échelle.jpg");
        composant(fosse, "Tendeur", TypeComposant.TENDEUR,"Tendeur.jpg");
        composant(fosse, "Drainage", TypeComposant.DRAINAGE, "Drainage.jpg");
        composant(fosse, "Capteur d'eau", TypeComposant.CAPTEUR_EAU,"Capteur d'eau.jpg");

        return List.of(teteGaine, armoire, gaine, paliers, fosse);
    }
}