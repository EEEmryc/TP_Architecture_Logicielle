package fr.info.user_interface.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import fr.info.user_interface.modele.Infrastructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class Services { 

    private static final Logger logger = LoggerFactory.getLogger(Services.class);
    private ArrayList<Infrastructure> listeInfrastructure;
    private final Geolocalisation geolocalisation;

    @Autowired
    public Services(Geolocalisation geolocalisation) {
        this.listeInfrastructure = new ArrayList<>();
        this.geolocalisation = geolocalisation;
        init();
    }

    private File getFichierDonnees() {
        String cheminAbsolu = new File("").getAbsolutePath();
        if (cheminAbsolu.endsWith("user_interface")) {
            return new File(cheminAbsolu + "/data/data.txt");
        }
        return new File(cheminAbsolu + "/user_interface/data/data.txt");
    }

    public void init() {
        File file = getFichierDonnees();
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.error("Impossible de créer le fichier initial : {}", e.getMessage());
            }
        }

        this.listeInfrastructure.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] champs = ligne.split(",");
                
                if (champs.length >= 5) {
                    Infrastructure infra = new Infrastructure();
                    
                    // Format historique : 5 colonnes (Pays, Ville, Lat, Lon, Nombre)
                    if (champs.length == 5 && estUnNombre(champs[2].trim()) && estUnNombre(champs[3].trim())) {
                        // Utilisation du séparateur flèche pour distinguer clairement le pays de la ville
                        infra.setLocalisation(champs[0].trim() + " ➔ " + champs[1].trim());
                        infra.setType("Data Center");
                        infra.setPortee("National");
                        infra.setLatitude(Double.parseDouble(champs[2].trim()));
                        infra.setLongitude(Double.parseDouble(champs[3].trim()));
                        infra.setNombre("dn=" + champs[4].trim());
                        infra.setComplement("Capitale");
                        infra.setMail(champs[1].trim().toLowerCase().replaceAll("\\s+", "_") + "@mail.com");
                    } 
                    // Format applicatif unifié : 8 colonnes
                    else if (champs.length >= 8) {
                        infra.setLocalisation(champs[0].trim());
                        infra.setType(champs[1].trim());
                        infra.setPortee(champs[2].trim());
                        infra.setNombre(champs[3].trim());
                        infra.setComplement(champs[4].trim());
                        infra.setMail(champs[5].trim());
                        infra.setLatitude(Double.parseDouble(champs[6].trim()));
                        infra.setLongitude(Double.parseDouble(champs[7].trim()));
                    }
                    this.listeInfrastructure.add(infra);
                }
            }
            logger.info("Données initialisées : {} infrastructures chargées.", this.listeInfrastructure.size());
        } catch (IOException e) {
            logger.error("Erreur lecture fichier : {}", e.getMessage());
        }
    }

    private boolean estUnNombre(String chaine) {
        try {
            Double.parseDouble(chaine);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String genererMail(String localisation) {
        if (localisation == null || localisation.trim().isEmpty()) return "";
        String ville = extraireVille(localisation);
        return ville.trim().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_").replaceAll("_+", "_") + "@mail.com";
    }

    private String extraireVille(String localisation) {
        if (localisation.contains("➔")) {
            String[] parts = localisation.split("➔");
            if (parts.length > 1) {
                return parts[1].trim();
            }
            return parts[0].trim();
        }
        
        String chaineEpuree = localisation.trim().replaceAll("\\s+", " ");
        int premierEspace = chaineEpuree.indexOf(" ");
        if (premierEspace != -1) {
            return chaineEpuree.substring(premierEspace + 1);
        }
        return chaineEpuree;
    }

    public Infrastructure ajouter(Infrastructure infra) {
        String saisie = infra.getLocalisation().trim().replaceAll("\\s+", " ");
        
        // Gestion de la saisie utilisateur sans séparateur explicite
        if (!saisie.contains("➔")) {
            if (saisie.toLowerCase().startsWith("france ")) {
                saisie = "France ➔ " + saisie.substring(7).trim();
            } else if (saisie.toLowerCase().startsWith("république tchèque ")) {
                saisie = "République tchèque ➔ " + saisie.substring(19).trim();
            } else if (saisie.toLowerCase().startsWith("états unis ")) {
                saisie = "États-Unis ➔ " + saisie.substring(11).trim();
            } else if (!saisie.contains(" ")) {
                saisie = "France ➔ " + saisie;
            } else {
                int premierEspace = saisie.indexOf(" ");
                saisie = saisie.substring(0, premierEspace) + " ➔ " + saisie.substring(premierEspace + 1);
            }
        }

        infra.setLocalisation(saisie);

        try {
            String requeteGeo = infra.getLocalisation().replace("➔", " ").replaceAll("\\s+", " ");
            double[] coords = geolocalisation.getCoordonnees(requeteGeo);
            if (coords != null) {
                infra.setLatitude(coords[0]);
                infra.setLongitude(coords[1]);
            } else {
                infra.setLatitude(48.8566);
                infra.setLongitude(2.3522);
            }
        } catch (IOException e) {
            logger.error("Erreur API géolocalisation : {}", e.getMessage());
            infra.setLatitude(48.8566);
            infra.setLongitude(2.3522);
        }

        infra.setMail(genererMail(infra.getLocalisation()));

        String typeCode = "Data Center".equalsIgnoreCase(infra.getType()) ? "d" : "cl";
        String porteeCode = "National".equalsIgnoreCase(infra.getPortee()) ? "n" : "l";
        String valeurNombre = infra.getNombre().replaceAll("[^0-9]", "");
        infra.setNombre(typeCode + porteeCode + "=" + valeurNombre);

        this.listeInfrastructure.add(infra);
        sauvegarderDansFichier();
        return infra;
    }

    public void supprimer(String localisation) {
        if (localisation == null) return;
        String cible = localisation.trim();
        this.listeInfrastructure.removeIf(infra -> infra.getLocalisation().trim().equalsIgnoreCase(cible));
        sauvegarderDansFichier();
    }

    private void sauvegarderDansFichier() {
        File file = getFichierDonnees();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Infrastructure h : this.listeInfrastructure) {
                String ligne = h.getLocalisation() + "," 
                             + h.getType() + "," 
                             + h.getPortee() + "," 
                             + h.getNombre() + "," 
                             + h.getComplement() + "," 
                             + h.getMail() + ","
                             + h.getLatitude() + ","
                             + h.getLongitude();
                bw.write(ligne);
                bw.newLine();
            }
        } catch (IOException e) {
            logger.error("Erreur écriture fichier : {}", e.getMessage());
        }
    }

    public ArrayList<Infrastructure> liste() {
        return this.listeInfrastructure;
    }
    
    public Infrastructure selection() {
        return new Infrastructure();
    }
}