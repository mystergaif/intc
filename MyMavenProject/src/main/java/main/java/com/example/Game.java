package main.java.com.example;

import main.java.com.example.render.Renderer;
import main.java.com.example.render.Camera;
import main.java.com.example.entity.Player;
import main.java.com.example.physics.PhysicsEngine;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

/**
 * Основной класс игры, который управляет игровым циклом и инициализацией
 */
public class Game {
    // Константы окна
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String TITLE = "3D Игра с физикой на Java и C";

    // Компоненты игры
    private long window;
    private Renderer renderer;
    private Camera camera;
    private Player player;
    private PhysicsEngine physicsEngine;

    // Состояние игры
    private boolean running = false;

    /**
     * Точка входа в приложение
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }

    /**
     * Запуск игры
     */
    public void start() {
        init();
        gameLoop();
        cleanup();
    }

    /**
     * Инициализация игры
     */
    private void init() {
        // Настройка обработки ошибок GLFW
        GLFWErrorCallback.createPrint(System.err).set();

        // Инициализация GLFW
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Не удалось инициализировать GLFW");
        }

        // Настройка GLFW
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        // Создание окна
        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, TITLE, MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Не удалось создать окно GLFW");
        }

        // Настройка обратных вызовов
        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                GLFW.glfwSetWindowShouldClose(window, true);
            }
        });

        // Настройка обратного вызова для движения мыши
        GLFW.glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            camera.processMouseMovement((float) xpos, (float) ypos);
        });

        // Отключение видимости курсора и его захват
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

        // Получение разрешения экрана
        var vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (vidmode != null) {
            // Центрирование окна
            GLFW.glfwSetWindowPos(
                window,
                (vidmode.width() - WIDTH) / 2,
                (vidmode.height() - HEIGHT) / 2
            );
        }

        // Сделать контекст OpenGL текущим
        GLFW.glfwMakeContextCurrent(window);
        // Включить вертикальную синхронизацию
        GLFW.glfwSwapInterval(1);
        // Показать окно
        GLFW.glfwShowWindow(window);

        // Инициализация OpenGL
        GL.createCapabilities();
        GL11.glClearColor(0.2f, 0.3f, 0.4f, 1.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        // Инициализация компонентов игры
        renderer = new Renderer();
        camera = new Camera(new Vector3f(0, 2, 5), new Vector3f(0, 0, -1));
        player = new Player(new Vector3f(0, 0, 0));
        physicsEngine = new PhysicsEngine();

        // Инициализация физического движка
        physicsEngine.init();

        running = true;
    }

    /**
     * Основной игровой цикл
     */
    private void gameLoop() {
        float deltaTime;
        float lastFrame = 0;

        // Цикл выполняется, пока окно не должно быть закрыто
        while (running && !GLFW.glfwWindowShouldClose(window)) {
            // Расчет времени кадра
            float currentFrame = (float) GLFW.glfwGetTime();
            deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;

            // Обработка ввода
            processInput(deltaTime);

            // Обновление физики
            physicsEngine.update(deltaTime);

            // Обновление игровой логики
            update(deltaTime);

            // Рендеринг
            render();

            // Обмен буферами и опрос событий
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    /**
     * Обработка пользовательского ввода
     */
    private void processInput(float deltaTime) {
        // Перемещение игрока относительно камеры
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            player.moveForward(deltaTime, camera);
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            player.moveBackward(deltaTime, camera);
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            player.moveLeft(deltaTime, camera);
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            player.moveRight(deltaTime, camera);
        }
    }

    /**
     * Обновление игровой логики
     */
    private void update(float deltaTime) {
        // Обновление позиции игрока с учетом физики
        Vector3f playerPos = player.getPosition();
        float[] newPos = physicsEngine.updatePlayerPosition(
            playerPos.x, playerPos.y, playerPos.z,
            player.getVelocity().x, player.getVelocity().y, player.getVelocity().z,
            deltaTime
        );
        player.setPosition(new Vector3f(newPos[0], newPos[1], newPos[2]));

        // Обновление игрока
        player.update(deltaTime);

        // Обновление камеры для следования за игроком
        // Камера находится на высоте глаз игрока
        camera.setPosition(new Vector3f(
            player.getPosition().x,
            player.getPosition().y + 1.8f, // Высота глаз
            player.getPosition().z
        ));
    }

    /**
     * Рендеринг сцены
     */
    private void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // Рендеринг сцены
        renderer.render(camera, player);
    }

    /**
     * Освобождение ресурсов
     */
    private void cleanup() {
        // Освобождение ресурсов рендерера
        renderer.cleanup();

        // Освобождение ресурсов физического движка
        physicsEngine.cleanup();

        // Освобождение ресурсов GLFW
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }
}
