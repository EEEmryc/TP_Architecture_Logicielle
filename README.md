cat << 'EOF' > README.md
# TP Architecture Logicielle - Cartographie des Infrastructures (Fiche 7)

Ce projet est une application Spring Boot permettant de cartographier et de gérer des infrastructures réseau (Data Centers et services Cloud) à l'échelle mondiale à l'aide d'une interface web interactive basée sur la bibliothèque Leaflet.

## Fonctionnalités implémentées

* **Cartographie interactive** : Visualisation des infrastructures mondiales avec des codes couleurs spécifiques (Cyan pour Data Center, Vert pour Cloud, Orange pour les sites Mixtes).
* **Explosion dynamique au clic** : En cliquant sur une infrastructure mixte (orange), la carte applique un zoom automatique et sépare le marqueur en deux cercles distincts (Data Center à gauche, Cloud à droite) avec leurs volumes respectifs.
* **Support du défilement infini** : Les calques et cercles géométriques sont dupliqués à $-360^\circ$ et $+360^\circ$ de longitude pour rester visibles lors du défilement horizontal de la carte.
* **Sécurité et restriction IP** : Les opérations d'écriture (ajout et suppression) sont sécurisées dans la couche service. Seul l'ordinateur hôte (`127.0.0.1` ou l'IP physique dédiée) peut modifier le fichier `data.txt`.
* **Gestion des erreurs intégrée** : Les tentatives de modification par des postes distants tiers sont bloquées (HTTP 403) et redirigées vers une interface d'erreur sombre personnalisée aux couleurs du site.

## Structure des données (data/data.txt)

Le fichier de données utilise un format unifié supportant l'analyse de structures simples ou mixtes (séparateur : virgule).

Exemple d'infrastructure simple :
```text
France ➔ Lyon,Data Center,Local,dl=15,Zone Auvergne-Rhône-Alpes,lyon@mail.com,45.7640,4.8357