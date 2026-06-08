package fr.info.user_interface.data;

import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class Fichier {

    public ArrayList<String> lireFichier(String chemin) throws IOException {
        ArrayList<String> lignes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                lignes.add(ligne);
            }
        }
        return lignes;
    }
}