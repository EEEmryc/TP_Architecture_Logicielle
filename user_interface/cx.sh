#!/bin/sh
 
# Si vous voulez utiliser le script sur votre PC : Définir PathJava
PathJava="[son chemin]/jdk-21"

# PathJavaIUT : Chemin vers java
PathJavaIUT="/home/student/905/$USER/Bureau/Mes_Montages/TP/TPINFO/java/jdk-21"

if [ $1 = "usage" ] 
then  
   echo "ne pas travailler dans mes_montages"
   echo "travailler dans son répertoire personnel : celui au nom de l'étudiant"
   echo "exemple : /home/numero_etudiant/manip/projet/"
   echo "ici projet : est le projet spring boot créé"
   echo "on peut placer le script cx.sh dans projet"
   chmod a+x gradlew
fi

if [ $1 = "cb" ]
then
  echo "clean, compilation"
  ./gradlew -Dorg.gradle.java.home=$PathJavaIUT clean build
fi

if [ $1 = "b" ]
then

echo "Compilation"
  ./gradlew -Dorg.gradle.java.home=$PathJavaIUT build
fi


if [ $1 = "x" ]
then
  echo "exécution"
  $PathJavaIUT/bin/./java -jar ./build/libs/*-SNAPSHOT.jar 
fi


       
