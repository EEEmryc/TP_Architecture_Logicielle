package fr.info.user_interface.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import fr.info.user_interface.modele.Infrastructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;

@Service
public class Services { 

    private static final Logger logger = LoggerFactory.getLogger(Services.class);
    private Map<String, Infrastructure> mapInfrastructure;
    private final Geolocalisation geolocalisation;

    @Autowired
    public Services(Geolocalisation geolocalisation) {
        this.mapInfrastructure = new LinkedHashMap<>();
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

        this.mapInfrastructure.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] champs = ligne.split(",");
                
                try {
                    if (champs.length == 5 && estUnNombre(champs[2].trim()) && estUnNombre(champs[3].trim())) {
                        Infrastructure infra = new Infrastructure();
                        infra.setLocalisation(champs[0].trim() + " ➔ " + champs[1].trim());
                        infra.setType("Data Center");
                        infra.setPortee("National");
                        infra.setLatitude(Double.parseDouble(champs[2].trim()));
                        infra.setLongitude(Double.parseDouble(champs[3].trim()));
                        infra.setNombre("dn=" + champs[4].trim());
                        infra.setComplement("Capitale");
                        infra.setMail(genererMail(champs[1].trim()));
                        
                        String villeCle = extraireVille(infra.getLocalisation());
                        this.mapInfrastructure.put(villeCle, infra);
                    } 
                    else if (champs.length >= 8) {
                        Infrastructure infra = new Infrastructure();
                        infra.setLocalisation(champs[0].trim());
                        infra.setType(champs[1].trim());
                        infra.setPortee(champs[2].trim());
                        
                        int indexFinNombre = champs.length - 4;
                        StringBuilder sbNombre = new StringBuilder();
                        for (int i = 3; i < indexFinNombre; i++) {
                            sbNombre.append(champs[i].trim());
                            if (i < indexFinNombre - 1) {
                                sbNombre.append(", ");
                            }
                        }
                        infra.setNombre(sbNombre.toString());
                        
                        infra.setComplement(champs[champs.length - 4].trim());
                        infra.setMail(champs[champs.length - 3].trim());
                        infra.setLatitude(Double.parseDouble(champs[champs.length - 2].trim()));
                        infra.setLongitude(Double.parseDouble(champs[champs.length - 1].trim()));
                        
                        String villeCle = extraireVille(infra.getLocalisation());
                        this.mapInfrastructure.put(villeCle, infra);
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    logger.error("Ligne malformée ignorée dans data.txt : {}", ligne);
                }
            }
            logger.info("Données initialisées : {} infrastructures chargées.", this.mapInfrastructure.size());
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

    /**
     * Vérifie si la requête provient de la machine hôte locale.
     */
    private boolean estRequeteLocale() {
        try {
            ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = sra.getRequest();
            String remoteAddr = request.getRemoteAddr();
            
            // Autorise localhost (IPv4/IPv6) et votre IP de machine de l'IUT
            return "127.0.0.1".equals(remoteAddr) || 
                   "0:0:0:0:0:0:0:1".equals(remoteAddr) || 
                   "192.168.203.26".equals(remoteAddr);
        } catch (Exception e) {
            // Autorise par défaut si exécuté hors contexte de servlet (ex: au démarrage lors de l'init)
            return true;
        }
    }

    public String genererMail(String localisation) {
        if (localisation == null || localisation.trim().isEmpty()) return "";
        String ville = extraireVille(localisation);
        String villeNettoyee = ville.replace("/", "");
        String villeSansAccent = Normalizer.normalize(villeNettoyee, Normalizer.Form.NFD)
                                           .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        
        return villeSansAccent.trim().toLowerCase()
                              .replaceAll("[^a-zA-Z0-9]", "_")
                              .replaceAll("_+", "_")
                              .replaceAll("^_|_$", "") + "@mail.com";
    }

    private String extraireVille(String localisation) {
        if (localisation == null) return "";
        String cible = localisation.trim();
        
        if (cible.contains("➔")) {
            String[] parts = cible.split("➔");
            if (parts.length > 1) {
                cible = parts[1].trim();
            } else {
                cible = parts[0].trim();
            }
        }
        
        if (cible.contains("/")) {
            int dernierEspace = cible.lastIndexOf(" ");
            if (dernierEspace != -1) {
                return cible.substring(dernierEspace + 1).trim();
            }
        }
        
        String chaineEpuree = cible.replaceAll("\\s+", " ");
        int premierEspace = chaineEpuree.indexOf(" ");
        if (premierEspace != -1) {
            return chaineEpuree.substring(premierEspace + 1);
        }
        return chaineEpuree;
    }

    public Infrastructure ajouter(Infrastructure infra) {
        // Sécurité : Blocage des requêtes distantes
        if (!estRequeteLocale()) {
            logger.warn("Tentative de modification non autorisée (Ajout) bloquée.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action interdite : Seul l'hôte peut modifier les données.");
        }

        String saisie = infra.getLocalisation().trim().replaceAll("\\s+", " ");
        
        if (!saisie.contains("➔")) {
            if (saisie.toLowerCase().startsWith("france ")) {
                saisie = "France ➔ " + saisie.substring(7).trim();
            } else if (saisie.toLowerCase().startsWith("république tchèque ")) {
                saisie = "République tchèque ➔ " + saisie.substring(19).trim();
            } else if (saisie.toLowerCase().startsWith("états unis ")) {
                saisie = "États-Unis ➔ " + saisie.substring(11).trim();
            } else if (saisie.contains("/")) {
                int dernierEspace = saisie.lastIndexOf(" ");
                if (dernierEspace != -1) {
                    String pays = saisie.substring(0, dernierEspace).trim();
                    String ville = saisie.substring(dernierEspace + 1).trim();
                    saisie = pays + " ➔ " + ville;
                } else {
                    saisie = "France ➔ " + saisie;
                }
            } else if (!saisie.contains(" ")) {
                saisie = "France ➔ " + saisie;
            } else {
                int premierEspace = saisie.indexOf(" ");
                saisie = saisie.substring(0, premierEspace) + " ➔ " + saisie.substring(premierEspace + 1);
            }
        }

        infra.setLocalisation(saisie);
        String villeCle = extraireVille(infra.getLocalisation());

        String typeCode = "Data Center".equalsIgnoreCase(infra.getType()) ? "d" : "cl";
        String porteeCode = "National".equalsIgnoreCase(infra.getPortee()) ? "n" : "l";
        String valeurNombre = infra.getNombre().replaceAll("[^0-9]", "");
        String nouveauBlocNombre = typeCode + porteeCode + "=" + valeurNombre;

        if (!this.mapInfrastructure.containsKey(villeCle)) {
            try {
                String requeteGeo = infra.getLocalisation().replace("➔", " ").replaceAll("\\s+", " ");
                double[] coords = geolocalisation.getCoordonnees(requeteGeo);
                if (coords != null && coords.length == 2 && coords[0] >= -90.0 && coords[0] <= 90.0 && coords[1] >= -180.0 && coords[1] <= 180.0) {
                    infra.setLatitude(coords[0]);
                    infra.setLongitude(coords[1]);
                } else {
                    infra.setLatitude(48.8566); 
                    infra.setLongitude(2.3522);
                }
            } catch (IOException e) {
                logger.error("Erreur géolocalisation : {}", e.getMessage());
                infra.setLatitude(48.8566);
                infra.setLongitude(2.3522);
            }

            infra.setMail(genererMail(infra.getLocalisation()));
            infra.setNombre(nouveauBlocNombre);
            this.mapInfrastructure.put(villeCle, infra);
        } 
        else {
            Infrastructure infraExistante = this.mapInfrastructure.get(villeCle);
            
            if (infraExistante.getType().contains(infra.getType()) && infraExistante.getPortee().contains(infra.getPortee())) {
                infraExistante.setNombre(nouveauBlocNombre);
            } 
            else {
                String ancienNombre = infraExistante.getNombre();
                if (!ancienNombre.contains(typeCode + porteeCode)) {
                    infraExistante.setNombre(ancienNombre + ", " + nouveauBlocNombre);
                } else {
                    infraExistante.setNombre(ancienNombre.replaceAll(typeCode + porteeCode + "=[0-9]+", nouveauBlocNombre));
                }
                
                if (!infraExistante.getType().contains(infra.getType())) {
                    infraExistante.setType(infraExistante.getType() + " / " + infra.getType());
                }
                if (!infraExistante.getPortee().contains(infra.getPortee())) {
                    infraExistante.setPortee(infraExistante.getPortee() + " / " + infra.getPortee());
                }
            }
        }

        sauvegarderDansFichier();
        return this.mapInfrastructure.get(villeCle);
    }

    public void supprimer(String localisation) {
        // Sécurité : Blocage des requêtes distantes
        if (!estRequeteLocale()) {
            logger.warn("Tentative de modification non autorisée (Suppression) bloquée.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action interdite : Seul l'hôte peut modifier les données.");
        }

        if (localisation == null) return;
        String villeCle = extraireVille(localisation);
        this.mapInfrastructure.remove(villeCle);
        sauvegarderDansFichier();
    }

    private void sauvegarderDansFichier() {
        File file = getFichierDonnees();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Infrastructure h : this.mapInfrastructure.values()) {
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
        return new ArrayList<>(this.mapInfrastructure.values());
    }
    
    public Infrastructure selection() {
        return new Infrastructure();
    }
}