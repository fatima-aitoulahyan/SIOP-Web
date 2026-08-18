package com.example.backend_siop.ascenseur.enums;

public enum TypeComposant {

    // --- 1. Local machinerie / traction ---
    MOTEUR,
    FREIN,
    REDUCTEUR,
    POULIE,
    ENCODEUR,
    ROULEMENT,
    VENTILATEUR,
    SILENTBLOC,

    // --- Armoire de commande ---
    ARMOIRE_COMMANDE,
    AUTOMATE,
    VARIATEUR,
    CARTE_ELECTRONIQUE,
    TRANSFORMATEUR,
    ALIMENTATION,
    CONTACTEUR,
    RELAIS,
    DISJONCTEUR,
    FUSIBLE,
    BORNIER,

    // --- Secours / communication ---
    UPS,
    CHARGEUR_BATTERIE,
    BATTERIE,
    CARTE_ARD,
    MODULE_GSM,
    MODULE_ETHERNET,
    INTERFACE_DIAGNOSTIC,

    // --- Limiteur de vitesse ---
    LIMITEUR_VITESSE,
    CABLE_LIMITEUR,
    MASSELOTTE,

    // --- 2. Cabine ---
    CABINE,
    CHASSIS,
    PAROI,
    PLAFOND,
    SOL,
    MAIN_COURANTE,
    MIROIR,
    ECLAIRAGE,
    BOITE_BOUTONS,
    AFFICHEUR,
    TELEALARME,
    CAMERA,
    INTERPHONE,

    // --- Porte cabine ---
    PORTE_CABINE,
    OPERATEUR_PORTE,
    GALET,
    CONTRE_GALET,
    COURROIE,
    CELLULE_PHOTOELECTRIQUE,
    RIDEAU_LUMINEUX,

    // --- Guidage / suspension ---
    GUIDAGE,
    RAIL,
    PATIN,
    SABOT,
    CONSOLE,
    SUSPENSION,
    CABLE_SUSPENSION,
    ATTACHE_CABLE,
    CHAINE_COMPENSATRICE,
    TENDEUR,

    // --- Sécurité cabine ---
    PARACHUTE,
    FIN_DE_COURSE,
    CAPTEUR,

    // --- Contrepoids ---
    CONTREPOIDS,

    // --- 3. Paliers ---
    PORTE_PALIERE,
    VANTAIL,
    SERRURE,
    CROCHET,
    PERCUTEUR,
    SHUNT,
    MICROCONTACT,
    EQUIPEMENT_PALIER,
    PLAQUE_SIGNALETIQUE,
    BOUTON_APPEL,

    // --- 4. Fosse ---
    AMORTISSEUR,
    INTERRUPTEUR_STOP,
    BOITIER_INSPECTION,
    ECHELLE,
    DRAINAGE,
    CAPTEUR_EAU,

    // --- Spécifique hydraulique ---
    VERIN_HYDRAULIQUE,
    POMPE_HYDRAULIQUE,
    RESERVOIR_HUILE,
    ELECTROVANNE,
    CLAPET_ANTI_RETOUR,


}