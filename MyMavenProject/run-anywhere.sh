#!/bin/bash

# Получаем директорию, в которой находится скрипт
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

# Запуск игры с относительными путями от директории скрипта
java -Djava.library.path="$SCRIPT_DIR/target/natives" -jar "$SCRIPT_DIR/target/MyMavenProject-1.0-SNAPSHOT-fat.jar"
