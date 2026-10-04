#!/bin/bash

compile() {

    echo "[INFO] nettoyage du projet..."

    mvn clean


    echo "[INFO] compilation des fichiers sources..."

    mvn package


    echo "[INFO] initialisation du framework..."

    cp target/framework-m.jar framework-m.jar


    echo "[INFO] exportation du framework..."

    rm -f /home/manoa/Documents/ITU/L2/COLLABS/lib/framework-m.jar

    cp framework-m.jar /home/manoa/Documents/ITU/L2/COLLABS/lib/


    echo "[INFO] compilation terminée..."

}

compile