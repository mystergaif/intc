#!/bin/bash

# Компиляция и упаковка проекта
mvn clean package

# Копирование нативной библиотеки
mkdir -p target/natives
cp src/main/native/build/lib/libphysics.so target/natives/

# Запуск игры с указанием пути к нативной библиотеке
java -Djava.library.path=target/natives -jar target/MyMavenProject-1.0-SNAPSHOT-with-dependencies.jar
