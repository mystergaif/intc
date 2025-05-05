package main.java.com.example.entity;

import main.java.com.example.render.Camera;
import org.joml.Vector3f;

/**
 * Класс игрока
 */
public class Player extends Entity {
    /**
     * Конструктор
     */
    public Player(Vector3f position) {
        super(position);
        this.speed = 5.0f;
    }

    /**
     * Обновление игрока
     */
    @Override
    public void update(float deltaTime) {
        // Обновление позиции на основе скорости
        position.add(new Vector3f(velocity).mul(deltaTime));

        // Сброс горизонтальной скорости (трение)
        velocity.x *= 0.9f;
        velocity.z *= 0.9f;

        // Фиксируем высоту игрока (убрали прыжок)
        position.y = 0.5f; // Половина высоты игрока
        velocity.y = 0.0f; // Нет вертикальной скорости
    }

    /**
     * Движение вперед относительно камеры
     */
    public void moveForward(float deltaTime, Camera camera) {
        // Получаем вектор направления камеры (без вертикальной составляющей)
        Vector3f direction = new Vector3f(camera.getFront());
        direction.y = 0; // Игнорируем вертикальную составляющую для движения по горизонтали
        direction.normalize();

        // Добавляем скорость в направлении камеры
        velocity.add(new Vector3f(direction).mul(speed * deltaTime));
    }

    /**
     * Движение назад относительно камеры
     */
    public void moveBackward(float deltaTime, Camera camera) {
        // Получаем вектор направления камеры (без вертикальной составляющей)
        Vector3f direction = new Vector3f(camera.getFront());
        direction.y = 0; // Игнорируем вертикальную составляющую для движения по горизонтали
        direction.normalize();

        // Добавляем скорость в направлении, противоположном камере
        velocity.add(new Vector3f(direction).mul(-speed * deltaTime));
    }

    /**
     * Движение влево относительно камеры
     */
    public void moveLeft(float deltaTime, Camera camera) {
        // Получаем вектор "вправо" камеры
        Vector3f right = new Vector3f(camera.getRight());

        // Добавляем скорость в направлении, противоположном "вправо"
        velocity.add(new Vector3f(right).mul(-speed * deltaTime));
    }

    /**
     * Движение вправо относительно камеры
     */
    public void moveRight(float deltaTime, Camera camera) {
        // Получаем вектор "вправо" камеры
        Vector3f right = new Vector3f(camera.getRight());

        // Добавляем скорость в направлении "вправо"
        velocity.add(new Vector3f(right).mul(speed * deltaTime));
    }
}
