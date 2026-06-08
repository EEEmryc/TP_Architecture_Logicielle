package fr.info.user_interface.data;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class HashTable {

    private final Fichier fichier;

    @Autowired
    public HashTable(Fichier fichier) {
        this.fichier = fichier;
    }

    public ArrayList<String> lireFichier(String chemin) throws IOException {
        return fichier.lireFichier(chemin);
    }
}