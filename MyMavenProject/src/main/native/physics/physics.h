#ifndef PHYSICS_H
#define PHYSICS_H

// Структура для хранения 3D вектора
typedef struct {
    float x;
    float y;
    float z;
} Vector3;

// Структура для хранения информации о коллизии
typedef struct {
    int isColliding;
    Vector3 normal;
    float penetration;
} CollisionInfo;

// Структура для хранения информации о физическом объекте
typedef struct {
    Vector3 position;
    Vector3 velocity;
    Vector3 acceleration;
    float mass;
    float restitution; // коэффициент упругости
    float friction;    // коэффициент трения
} PhysicsObject;

// Структура для хранения информации о плоскости (для коллизии)
typedef struct {
    Vector3 normal;
    float distance;
} Plane;

// Инициализация физического движка
void initPhysics();

// Освобождение ресурсов физического движка
void cleanupPhysics();

// Обновление физики
void updatePhysics(float deltaTime);

// Обновление физики игрока
void updatePlayerPhysics(
    float posX, float posY, float posZ,
    float velX, float velY, float velZ,
    float deltaTime,
    float* outPosition
);

// Проверка коллизии
int checkCollision(float posX, float posY, float posZ);

// Применение гравитации к объекту
void applyGravity(PhysicsObject* object, float deltaTime);

// Проверка коллизии с плоскостью
CollisionInfo checkPlaneCollision(PhysicsObject* object, Plane* plane);

// Разрешение коллизии
void resolveCollision(PhysicsObject* object, CollisionInfo* collision);

#endif /* PHYSICS_H */
