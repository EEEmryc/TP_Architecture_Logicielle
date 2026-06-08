# TP Architecture Logicielle - Gestion des Infrastructures

Ce projet est une application web Spring Boot permettant de gérer des infrastructures réseaux et de les visualiser sur une carte interactive.

## Prérequis

Avant de lancer le projet, assurez-vous d'avoir installé sur votre machine :
- **Java JDK 17** ou version supérieure
- **Maven** (si vous n'utilisez pas le wrapper fourni)

## Installation et Configuration

1. Clonez le dépôt sur votre machine locale :
   ```bash
   git clone https://github.com/EEEmryc/TP_Architecture_Logicielle.git
   cd TP_Architecture_Logicielle
   ```

2. Vérifiez que le fichier de données initiales `user_interface/data/data.txt` est bien présent et contient les coordonnées de base.

## Lancement du projet

Le projet utilise un script d'automatisation nommé `cx.sh` pour simplifier la compilation et l'exécution.

### 1. Compiler le projet

Exécutez la commande suivante pour nettoyer les anciens builds et recompiler l'application :
```bash
sh cx.sh cb
```

### 2. Lancer l'application

Une fois la compilation terminée avec succès, démarrez le serveur Tomcat embarqué :
```bash
sh cx.sh x
```

## Accès à l'application

Une fois le serveur démarré, ouvrez votre navigateur web et accédez à l'adresse suivante :
[http://localhost:8080/acceuil](http://localhost:8080/acceuil)

## Fonctionnalités disponibles

- **Accueil :** Carte du monde interactive affichant les infrastructures sous forme de cercles dynamiques.
- **Infrastructures :** Tableau de gestion permettant d'ajouter une nouvelle infrastructure avec géolocalisation automatique (via l'API Nominatim) et de supprimer une ligne via une boîte de dialogue modale personnalisée.
