package main.java.com.example.entity;

import org.joml.Vector3f;

/**
 * Базовый класс для игровых сущностей
 */
public abstract class Entity {
    protected Vector3f position;
    protected Vector3f velocity;
    protected float speed;
    
    /**
     * Конструктор
     */
    public Entity(Vector3f position) {
        this.position = position;
        this.velocity = new Vector3f(0.0f, 0.0f, 0.0f);
        this.speed = 5.0f;
    }
    
    /**
     * Получение позиции
     */
    public Vector3f getPosition() {
        return position;
    }
    
    /**
     * Установка позиции
     */
    public void setPosition(Vector3f position) {
        this.position = position;
    }
    
    /**
     * Получение скорости
     */
    public Vector3f getVelocity() {
        return velocity;
    }
    
    /**
     * Установка скорости
     */
    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }
    
    /**
     * Обновление сущности
     */
    public abstract void update(float deltaTime);
}
