#!/bin/bash

# Полный путь к директории проекта
PROJECT_DIR="/home/gafir/Рабочий стол/learning/MyMavenProject"

# Запуск игры с полными путями
java -Djava.library.path="$PROJECT_DIR/target/natives" -jar "$PROJECT_DIR/target/MyMavenProject-1.0-SNAPSHOT-fat.jar"
