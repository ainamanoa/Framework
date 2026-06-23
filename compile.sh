#! /bin/bash

compile() {
    echo "[INFO] compilation des fichiers sources..."

    find src/ -iname "*.java" > sources.txt
    javac -cp lib/* -d bin @sources.txt
    rm sources.txt

    echo "[INFO] initialisation du framework..."
    jar cf framework-m.jar -C bin/ . 

    echo "[INFO] exportation du framework..."
    rm /home/manoa/Documents/ITU/L2/S3/TRAININGS/VAHATRINIAINA/SERVLET/COLLABS/lib/framework-m.jar
    cp framework-m.jar /home/manoa/Documents/ITU/L2/S3/TRAININGS/VAHATRINIAINA/SERVLET/COLLABS/lib/

    echo "[INFO] compilation terminée..."
}

compile