#!/bin/bash

# Компиляция и упаковка проекта
echo "Компиляция и упаковка проекта..."
mvn clean package

# Проверка успешности сборки Maven
if [ $? -ne 0 ]; then
    echo "Ошибка при сборке Maven проекта!"
    exit 1
fi

# Сборка нативной библиотеки
echo "Сборка нативной библиотеки..."
cd src/main/native
mkdir -p build
cd build
cmake ..

# Проверка успешности CMake
if [ $? -ne 0 ]; then
    echo "Ошибка при выполнении CMake!"
    exit 1
fi

make

# Проверка успешности Make
if [ $? -ne 0 ]; then
    echo "Ошибка при выполнении Make!"
    exit 1
fi

# Создание директории для нативных библиотек
echo "Копирование нативной библиотеки..."
mkdir -p ../../../../target/natives
cp lib/libphysics.so ../../../../target/natives/

# Создание символических ссылок для библиотеки
cd ../../../../target/natives/
ln -sf libphysics.so physics.so
ln -sf libphysics.so libphysics.so.0

# Возвращаемся в корневую директорию проекта
cd ../../

echo "Сборка завершена успешно!"
echo "Fat JAR создан: target/MyMavenProject-1.0-SNAPSHOT-fat.jar"
echo ""
echo "Для запуска игры выполните:"
echo "java -Djava.library.path=target/natives -jar target/MyMavenProject-1.0-SNAPSHOT-fat.jar"
