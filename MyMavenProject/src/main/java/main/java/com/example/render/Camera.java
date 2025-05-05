package main.java.com.example.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Класс для управления камерой
 */
public class Camera {
    private Vector3f position;
    private Vector3f front;
    private Vector3f up;
    private Vector3f right;
    private float yaw;
    private float pitch;

    // Чувствительность мыши
    private float mouseSensitivity;

    // Последняя позиция мыши
    private float lastX;
    private float lastY;
    private boolean firstMouse;

    /**
     * Конструктор
     */
    public Camera(Vector3f position, Vector3f front) {
        this.position = position;
        this.front = front;
        this.up = new Vector3f(0.0f, 1.0f, 0.0f);
        this.right = new Vector3f();
        this.yaw = -90.0f;
        this.pitch = 0.0f;
        this.mouseSensitivity = 0.1f;
        this.lastX = 400.0f;
        this.lastY = 300.0f;
        this.firstMouse = true;

        // Инициализация вектора right
        updateCameraVectors();
    }

    /**
     * Получение матрицы вида
     */
    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(
            position,
            new Vector3f(position).add(front),
            up
        );
    }

    /**
     * Установка позиции камеры
     */
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    /**
     * Получение позиции камеры
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Получение вектора направления камеры
     */
    public Vector3f getFront() {
        return front;
    }

    /**
     * Получение вектора "вправо" камеры
     */
    public Vector3f getRight() {
        return right;
    }

    /**
     * Получение вектора "вверх" камеры
     */
    public Vector3f getUp() {
        return up;
    }

    /**
     * Обработка движения мыши
     */
    public void processMouseMovement(float xoffset, float yoffset, boolean constrainPitch) {
        xoffset *= mouseSensitivity;
        yoffset *= mouseSensitivity;

        yaw += xoffset;
        pitch += yoffset;

        // Ограничение угла наклона
        if (constrainPitch) {
            if (pitch > 89.0f) {
                pitch = 89.0f;
            }
            if (pitch < -89.0f) {
                pitch = -89.0f;
            }
        }

        // Обновление векторов камеры
        updateCameraVectors();
    }

    /**
     * Обработка движения мыши (перегруженный метод)
     */
    public void processMouseMovement(float xpos, float ypos) {
        if (firstMouse) {
            lastX = xpos;
            lastY = ypos;
            firstMouse = false;
        }

        float xoffset = xpos - lastX;
        float yoffset = lastY - ypos; // Инвертировано, так как Y-координаты идут снизу вверх

        lastX = xpos;
        lastY = ypos;

        processMouseMovement(xoffset, yoffset, true);
    }

    /**
     * Установка направления камеры
     */
    public void setDirection(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;

        // Обновление векторов камеры
        updateCameraVectors();
    }

    /**
     * Обновление векторов камеры
     */
    private void updateCameraVectors() {
        // Расчет нового вектора front
        front.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front.y = (float) Math.sin(Math.toRadians(pitch));
        front.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front.normalize();

        // Расчет векторов right и up
        right = new Vector3f(front).cross(new Vector3f(0.0f, 1.0f, 0.0f)).normalize();
        up = new Vector3f(right).cross(front).normalize();
    }
}
