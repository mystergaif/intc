#!/bin/bash

# Компиляция проекта
mvn clean compile

# Копирование нативной библиотеки
mkdir -p target/natives
cp src/main/native/build/lib/libphysics.so target/natives/

# Создание classpath со всеми зависимостями
CLASSPATH="target/classes"
for jar in $(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q); do
    CLASSPATH="$CLASSPATH:$jar"
done

# Запуск игры с указанием пути к нативной библиотеке
java -Djava.library.path=target/natives -cp "$CLASSPATH" main.java.com.example.Game
