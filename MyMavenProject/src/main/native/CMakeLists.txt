cmake_minimum_required(VERSION 3.10)
project(physics)

# Найти JNI
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# Добавить исходные файлы
add_library(physics SHARED
    physics/physics.c
)

# Добавить заголовочные файлы
include_directories(physics)

# Установить выходной каталог
set_target_properties(physics PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
)

# Добавить математическую библиотеку
target_link_libraries(physics m)
