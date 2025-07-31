#!/bin/bash

# Résout le chemin absolu du dossier contenant ce script
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Nom du fichier JAR
JAR_NAME="GestionCheque.jar"

# Dossier lib JavaFX relatif au script
JAVAFX_LIB="$SCRIPT_DIR/javafx-sdk-21.0.8/lib"

# Vérifie que Java est installé
if ! command -v java &> /dev/null; then
    echo "Java n'est pas installé. Veuillez installer Java ou configurer JAVA_HOME."
    exit 1
fi

# Lancer l'application avec les modules JavaFX (rendu logiciel activé)
java \
-Dprism.order=sw \
--module-path "$JAVAFX_LIB" \
--add-modules javafx.controls,javafx.fxml \
-jar "$SCRIPT_DIR/$JAR_NAME"
