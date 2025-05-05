#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <jni.h>
#include "physics.h"
#include "main_java_com_example_physics_PhysicsEngine.h"

// Константы физики
#define GRAVITY 9.81f
#define EPSILON 0.0001f

// Глобальные переменные
static PhysicsObject player;
static Plane ground;

// Вспомогательные функции для работы с векторами
Vector3 createVector3(float x, float y, float z) {
    Vector3 v;
    v.x = x;
    v.y = y;
    v.z = z;
    return v;
}

float dotProduct(Vector3 a, Vector3 b) {
    return a.x * b.x + a.y * b.y + a.z * b.z;
}

Vector3 scaleVector(Vector3 v, float scale) {
    Vector3 result;
    result.x = v.x * scale;
    result.y = v.y * scale;
    result.z = v.z * scale;
    return result;
}

Vector3 addVectors(Vector3 a, Vector3 b) {
    Vector3 result;
    result.x = a.x + b.x;
    result.y = a.y + b.y;
    result.z = a.z + b.z;
    return result;
}

// Инициализация физического движка
void initPhysics() {
    // Инициализация игрока
    player.position = createVector3(0.0f, 1.0f, 0.0f);
    player.velocity = createVector3(0.0f, 0.0f, 0.0f);
    player.acceleration = createVector3(0.0f, 0.0f, 0.0f);
    player.mass = 70.0f;
    player.restitution = 0.3f;
    player.friction = 0.8f;

    // Инициализация земли (плоскость Y=0)
    ground.normal = createVector3(0.0f, 1.0f, 0.0f);
    ground.distance = 0.0f;

    printf("Физический движок инициализирован\n");
}

// Освобождение ресурсов физического движка
void cleanupPhysics() {
    printf("Физический движок освобожден\n");
}

// Применение гравитации к объекту
void applyGravity(PhysicsObject* object, float deltaTime) {
    // F = m * g, a = F / m = g
    object->acceleration.y = -GRAVITY;

    // v = v0 + a * t
    object->velocity.y += object->acceleration.y * deltaTime;

    // Ограничение максимальной скорости падения
    if (object->velocity.y < -50.0f) {
        object->velocity.y = -50.0f;
    }
}

// Проверка коллизии с плоскостью
CollisionInfo checkPlaneCollision(PhysicsObject* object, Plane* plane) {
    CollisionInfo info;
    info.isColliding = 0;
    info.normal = plane->normal;
    info.penetration = 0.0f;

    // Расстояние от объекта до плоскости
    float distance = dotProduct(object->position, plane->normal) - plane->distance;

    // Если расстояние меньше 0.5 (радиус объекта), то есть коллизия
    if (distance < 0.5f) {
        info.isColliding = 1;
        info.penetration = 0.5f - distance;
    }

    return info;
}

// Разрешение коллизии
void resolveCollision(PhysicsObject* object, CollisionInfo* collision) {
    if (!collision->isColliding) {
        return;
    }

    // Корректировка позиции
    Vector3 correction = scaleVector(collision->normal, collision->penetration);
    object->position = addVectors(object->position, correction);

    // Расчет импульса
    float velocityAlongNormal = dotProduct(object->velocity, collision->normal);

    // Если объекты уже разлетаются, не применяем импульс
    if (velocityAlongNormal > 0) {
        return;
    }

    // Расчет импульса с учетом коэффициента упругости
    float j = -(1.0f + object->restitution) * velocityAlongNormal;

    // Применение импульса к скорости
    Vector3 impulse = scaleVector(collision->normal, j);
    object->velocity = addVectors(object->velocity, impulse);

    // Применение трения
    Vector3 tangent;
    float vDotN = dotProduct(object->velocity, collision->normal);
    Vector3 vn = scaleVector(collision->normal, vDotN);

    // Тангенциальная составляющая скорости
    tangent.x = object->velocity.x - vn.x;
    tangent.y = object->velocity.y - vn.y;
    tangent.z = object->velocity.z - vn.z;

    // Нормализация тангенциальной составляющей
    float tangentLength = sqrtf(
        tangent.x * tangent.x +
        tangent.y * tangent.y +
        tangent.z * tangent.z
    );

    if (tangentLength > EPSILON) {
        tangent.x /= tangentLength;
        tangent.y /= tangentLength;
        tangent.z /= tangentLength;

        // Применение трения
        float frictionMagnitude = -dotProduct(object->velocity, tangent) * object->friction;
        Vector3 frictionForce = scaleVector(tangent, frictionMagnitude);

        object->velocity = addVectors(object->velocity, frictionForce);
    }
}

// Обновление физики
void updatePhysics(float deltaTime) {
    // Применение гравитации
    applyGravity(&player, deltaTime);

    // Обновление позиции
    player.position.x += player.velocity.x * deltaTime;
    player.position.y += player.velocity.y * deltaTime;
    player.position.z += player.velocity.z * deltaTime;

    // Проверка коллизии с землей
    CollisionInfo groundCollision = checkPlaneCollision(&player, &ground);
    resolveCollision(&player, &groundCollision);
}

// Обновление физики игрока
void updatePlayerPhysics(
    float posX, float posY, float posZ,
    float velX, float velY, float velZ,
    float deltaTime,
    float* outPosition
) {
    // Обновление состояния игрока
    player.position.x = posX;
    player.position.y = posY;
    player.position.z = posZ;

    player.velocity.x = velX;
    player.velocity.y = velY;
    player.velocity.z = velZ;

    // Применение физики
    updatePhysics(deltaTime);

    // Возврат обновленной позиции
    outPosition[0] = player.position.x;
    outPosition[1] = player.position.y;
    outPosition[2] = player.position.z;
}

// Проверка коллизии
int checkCollision(float posX, float posY, float posZ) {
    PhysicsObject tempObject;
    tempObject.position = createVector3(posX, posY, posZ);

    CollisionInfo groundCollision = checkPlaneCollision(&tempObject, &ground);

    return groundCollision.isColliding;
}

// JNI функции

JNIEXPORT void JNICALL Java_main_java_com_example_physics_PhysicsEngine_initPhysics
  (JNIEnv *env, jobject obj) {
    initPhysics();
}

JNIEXPORT void JNICALL Java_main_java_com_example_physics_PhysicsEngine_cleanupPhysics
  (JNIEnv *env, jobject obj) {
    cleanupPhysics();
}

JNIEXPORT jfloatArray JNICALL Java_main_java_com_example_physics_PhysicsEngine_updatePhysics
  (JNIEnv *env, jobject obj, jfloat deltaTime) {
    updatePhysics(deltaTime);

    jfloatArray result = (*env)->NewFloatArray(env, 6);
    if (result == NULL) {
        return NULL; // OutOfMemoryError
    }

    float values[6] = {
        player.position.x, player.position.y, player.position.z,
        player.velocity.x, player.velocity.y, player.velocity.z
    };

    (*env)->SetFloatArrayRegion(env, result, 0, 6, values);
    return result;
}

JNIEXPORT jfloatArray JNICALL Java_main_java_com_example_physics_PhysicsEngine_updatePlayerPhysics
  (JNIEnv *env, jobject obj, jfloat posX, jfloat posY, jfloat posZ,
   jfloat velX, jfloat velY, jfloat velZ, jfloat deltaTime) {
    float outPosition[3];

    updatePlayerPhysics(posX, posY, posZ, velX, velY, velZ, deltaTime, outPosition);

    jfloatArray result = (*env)->NewFloatArray(env, 3);
    if (result == NULL) {
        return NULL; // OutOfMemoryError
    }

    (*env)->SetFloatArrayRegion(env, result, 0, 3, outPosition);
    return result;
}

JNIEXPORT jboolean JNICALL Java_main_java_com_example_physics_PhysicsEngine_checkCollision
  (JNIEnv *env, jobject obj, jfloat posX, jfloat posY, jfloat posZ) {
    return checkCollision(posX, posY, posZ);
}
