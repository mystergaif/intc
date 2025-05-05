#!/bin/bash

# Проверка наличия Fat JAR
if [ ! -f "target/MyMavenProject-1.0-SNAPSHOT-fat.jar" ]; then
    echo "Fat JAR не найден! Запускаем сборку..."
    ./build-fat-jar.sh
    
    # Проверка успешности сборки
    if [ $? -ne 0 ]; then
        echo "Ошибка при сборке Fat JAR!"
        exit 1
    fi
fi

# Проверка наличия нативной библиотеки
if [ ! -f "target/natives/libphysics.so" ]; then
    echo "Нативная библиотека не найдена! Запускаем сборку..."
    ./build-fat-jar.sh
    
    # Проверка успешности сборки
    if [ $? -ne 0 ]; then
        echo "Ошибка при сборке нативной библиотеки!"
        exit 1
    fi
fi

# Запуск игры
echo "Запуск игры..."
java -Djava.library.path=target/natives -jar target/MyMavenProject-1.0-SNAPSHOT-fat.jar
