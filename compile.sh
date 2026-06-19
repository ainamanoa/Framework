#! /bin/bash

compile() {
    echo "[INFO] compilation des fichiers sources..."

    find src/ -iname "*.java" > sources.txt
    javac -cp lib/* -d bin @sources.txt
    rm sources.txt

    echo "[INFO] initialisation du framework..."
    jar cf framework-m.jar -C bin/ . 

    echo "[INFO] compilation terminée..."
}

compile