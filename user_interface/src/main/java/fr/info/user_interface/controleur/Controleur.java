package fr.info.user_interface.controleur;

import org.springframework.stereotype.Controller;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import fr.info.user_interface.services.Services;
import fr.info.user_interface.modele.Infrastructure;
import java.util.ArrayList;

@Controller
@ComponentScan("fr.info")
public class Controleur {
    private final Services services;

    @Autowired
    public Controleur(Services services) { 
        this.services = services;
    }

    @GetMapping("/infrastructure")
    public String liste(Model model) {
        model.addAttribute("menu", "infrastructure");
        model.addAttribute("liste", services.liste());
        model.addAttribute("infrastructure", new Infrastructure());
        return "infrastructure";
    }

    @GetMapping("/ajouter")
    public String ajouter(@ModelAttribute("infrastructure") Infrastructure infrastructure) {
        services.ajouter(infrastructure);
        return "redirect:/infrastructure";
    }

    @GetMapping("/supprimer")
    public String supprimerInfrastructure(@RequestParam("localisation") String localisation) {
        services.supprimer(localisation);
        return "redirect:/infrastructure";
    }

    @GetMapping("/api/infrastructures")
    @ResponseBody
    public ArrayList<Infrastructure> getInfrastructures() {
        return services.liste();
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/acceuil")
    public String acceuil() {
        return "acceuil";
    }
}