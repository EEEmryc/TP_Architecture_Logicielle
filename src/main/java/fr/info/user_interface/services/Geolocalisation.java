package fr.info.user_interface.services;

import org.springframework.stereotype.Service;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

@Service
public class Geolocalisation {
    private final OkHttpClient client = new OkHttpClient();

    public double[] getCoordonnees(String lieu) throws IOException {
        if (lieu == null || lieu.trim().isEmpty()) return null;
        
        // Nettoyage complet de la chaine (suppression des fleches et espaces multiples)
        String requeteNettoyee = lieu.replace("➔", " ").trim().replaceAll("\\s+", " ");

        // Utilisation du parametre 'q' pour laisser l'API interpreter le texte entier
        String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + java.net.URLEncoder.encode(requeteNettoyee, "UTF-8");

        Request requete = new Request.Builder()
          .url(url)
          .header("User-Agent", "AppGeo/1.0")
          .build();

        try (Response reponse = client.newCall(requete).execute()) {
            if (!reponse.isSuccessful()) return null;
            String corpsReponse = reponse.body().string();
            if (corpsReponse.trim().startsWith("[")) {
                JSONArray jsonArray = new JSONArray(corpsReponse);
                if (jsonArray.length() > 0) {
                    JSONObject obj = jsonArray.getJSONObject(0);
                    return new double[]{
                        Double.parseDouble(obj.getString("lat")),
                        Double.parseDouble(obj.getString("lon"))
                    };
                }
            }
            return null;
        }
    }
}