package main.java.com.example.render;

import main.java.com.example.entity.Player;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Класс для рендеринга 3D сцены
 */
public class Renderer {
    // Идентификаторы OpenGL объектов
    private int vaoId;
    private int vboId;
    private int eboId;
    private int floorVaoId;
    private int floorVboId;
    private int floorEboId;
    private int grassVaoId;
    private int grassVboId;

    // Шейдерная программа
    private ShaderProgram shaderProgram;

    // Трава
    private List<Vector3f> grassPositions;
    private List<Float> grassHeights;
    private static final int GRASS_COUNT = 300; // Количество травинок (уменьшено для производительности)
    private Random random;

    /**
     * Конструктор
     */
    public Renderer() {
        // Инициализация генератора случайных чисел
        random = new Random();

        // Инициализация списков для травы
        grassPositions = new ArrayList<>();
        grassHeights = new ArrayList<>();

        // Генерация травы
        generateGrass();

        // Инициализация рендерера
        init();
    }

    /**
     * Генерация травы
     */
    private void generateGrass() {
        // Очистка списков
        grassPositions.clear();
        grassHeights.clear();

        // Размер сетки для равномерного распределения травы
        int gridSize = (int) Math.sqrt(GRASS_COUNT);
        float cellSize = 20.0f / gridSize;

        // Генерация позиций для травы в сетке с небольшим случайным смещением
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                // Базовая позиция в сетке
                float baseX = (i * cellSize) - 10.0f + (cellSize / 2);
                float baseZ = (j * cellSize) - 10.0f + (cellSize / 2);

                // Добавляем небольшое случайное смещение
                float x = baseX + (random.nextFloat() * cellSize * 0.8f) - (cellSize * 0.4f);
                float z = baseZ + (random.nextFloat() * cellSize * 0.8f) - (cellSize * 0.4f);

                // Случайная высота травы (0.1 до 0.5)
                float height = 0.1f + random.nextFloat() * 0.4f;

                // Добавление в списки
                grassPositions.add(new Vector3f(x, 0.0f, z));
                grassHeights.add(height);

                // Если достигли нужного количества травинок, выходим
                if (grassPositions.size() >= GRASS_COUNT) {
                    return;
                }
            }
        }
    }

    /**
     * Инициализация рендерера
     */
    private void init() {
        // Создание шейдерной программы
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(
            "#version 330 core\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "layout (location = 1) in vec3 aColor;\n" +
            "out vec3 ourColor;\n" +
            "uniform mat4 model;\n" +
            "uniform mat4 view;\n" +
            "uniform mat4 projection;\n" +
            "void main() {\n" +
            "    gl_Position = projection * view * model * vec4(aPos, 1.0);\n" +
            "    ourColor = aColor;\n" +
            "}"
        );
        shaderProgram.createFragmentShader(
            "#version 330 core\n" +
            "in vec3 ourColor;\n" +
            "out vec4 FragColor;\n" +
            "void main() {\n" +
            "    FragColor = vec4(ourColor, 1.0);\n" +
            "}"
        );
        shaderProgram.link();

        // Создание куба (игрок)
        float[] cubeVertices = {
            // Позиции            // Цвета
            -0.5f, -0.5f, -0.5f,  1.0f, 0.0f, 0.0f,
             0.5f, -0.5f, -0.5f,  1.0f, 0.0f, 0.0f,
             0.5f,  0.5f, -0.5f,  1.0f, 0.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  1.0f, 0.0f, 0.0f,

            -0.5f, -0.5f,  0.5f,  0.0f, 1.0f, 0.0f,
             0.5f, -0.5f,  0.5f,  0.0f, 1.0f, 0.0f,
             0.5f,  0.5f,  0.5f,  0.0f, 1.0f, 0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f, 1.0f, 0.0f,

            -0.5f,  0.5f,  0.5f,  0.0f, 0.0f, 1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f, 1.0f,

             0.5f,  0.5f,  0.5f,  1.0f, 1.0f, 0.0f,
             0.5f,  0.5f, -0.5f,  1.0f, 1.0f, 0.0f,
             0.5f, -0.5f, -0.5f,  1.0f, 1.0f, 0.0f,
             0.5f, -0.5f,  0.5f,  1.0f, 1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f,  1.0f, 0.0f, 1.0f,
             0.5f, -0.5f, -0.5f,  1.0f, 0.0f, 1.0f,
             0.5f, -0.5f,  0.5f,  1.0f, 0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  1.0f, 0.0f, 1.0f,

            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f, 1.0f,
             0.5f,  0.5f, -0.5f,  0.0f, 1.0f, 1.0f,
             0.5f,  0.5f,  0.5f,  0.0f, 1.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f, 1.0f, 1.0f
        };

        int[] cubeIndices = {
            0, 1, 2, 2, 3, 0,       // Передняя грань
            4, 5, 6, 6, 7, 4,       // Задняя грань
            8, 9, 10, 10, 11, 8,    // Левая грань
            12, 13, 14, 14, 15, 12, // Правая грань
            16, 17, 18, 18, 19, 16, // Нижняя грань
            20, 21, 22, 22, 23, 20  // Верхняя грань
        };

        // Создание VAO для куба
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        // Создание VBO для вершин куба
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(cubeVertices.length);
        vertexBuffer.put(cubeVertices).flip();

        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        // Позиции вершин
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        // Цвета вершин
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        // Создание EBO для индексов куба
        IntBuffer indexBuffer = MemoryUtil.memAllocInt(cubeIndices.length);
        indexBuffer.put(cubeIndices).flip();

        eboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

        // Освобождение буферов
        MemoryUtil.memFree(vertexBuffer);
        MemoryUtil.memFree(indexBuffer);

        // Создание пола
        float[] floorVertices = {
            // Позиции            // Цвета
            -10.0f, 0.0f, -10.0f,  0.5f, 0.5f, 0.5f,
             10.0f, 0.0f, -10.0f,  0.5f, 0.5f, 0.5f,
             10.0f, 0.0f,  10.0f,  0.5f, 0.5f, 0.5f,
            -10.0f, 0.0f,  10.0f,  0.5f, 0.5f, 0.5f
        };

        int[] floorIndices = {
            0, 1, 2, 2, 3, 0
        };

        // Создание VAO для пола
        floorVaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(floorVaoId);

        // Создание VBO для вершин пола
        FloatBuffer floorVertexBuffer = MemoryUtil.memAllocFloat(floorVertices.length);
        floorVertexBuffer.put(floorVertices).flip();

        floorVboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, floorVboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floorVertexBuffer, GL15.GL_STATIC_DRAW);

        // Позиции вершин
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        // Цвета вершин
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        // Создание EBO для индексов пола
        IntBuffer floorIndexBuffer = MemoryUtil.memAllocInt(floorIndices.length);
        floorIndexBuffer.put(floorIndices).flip();

        floorEboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, floorEboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, floorIndexBuffer, GL15.GL_STATIC_DRAW);

        // Освобождение буферов
        MemoryUtil.memFree(floorVertexBuffer);
        MemoryUtil.memFree(floorIndexBuffer);

        // Отвязка VAO
        GL30.glBindVertexArray(0);
    }

    /**
     * Рендеринг сцены
     */
    public void render(Camera camera, Player player) {
        shaderProgram.bind();

        // Установка матриц преобразования
        Matrix4f projectionMatrix = new Matrix4f().perspective(
            (float) Math.toRadians(45.0f),
            800.0f / 600.0f,
            0.1f,
            100.0f
        );

        Matrix4f viewMatrix = camera.getViewMatrix();

        shaderProgram.setUniform("projection", projectionMatrix);
        shaderProgram.setUniform("view", viewMatrix);

        // Рендеринг пола
        GL30.glBindVertexArray(floorVaoId);
        Matrix4f floorModelMatrix = new Matrix4f().identity();
        shaderProgram.setUniform("model", floorModelMatrix);
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);

        // Рендеринг игрока
        GL30.glBindVertexArray(vaoId);
        Matrix4f playerModelMatrix = new Matrix4f().identity()
            .translate(player.getPosition());
        shaderProgram.setUniform("model", playerModelMatrix);
        GL11.glDrawElements(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_INT, 0);

        // Рендеринг травы
        renderGrass(viewMatrix, projectionMatrix);

        // Отвязка VAO и шейдерной программы
        GL30.glBindVertexArray(0);
        shaderProgram.unbind();
    }

    /**
     * Рендеринг травы
     */
    private void renderGrass(Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        // Включаем смешивание для полупрозрачности
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Отрисовка каждой травинки
        for (int i = 0; i < grassPositions.size(); i++) {
            Vector3f position = grassPositions.get(i);
            float height = grassHeights.get(i);

            // Создаем модельную матрицу для травинки
            Matrix4f modelMatrix = new Matrix4f().identity()
                .translate(position);

            // Создаем вертикальный прямоугольник для травинки
            float[] grassVertices = {
                // Позиции            // Цвета (зеленые оттенки)
                -0.02f, 0.0f, 0.0f,   0.0f, 0.5f, 0.0f,  // Нижний левый угол
                 0.02f, 0.0f, 0.0f,   0.0f, 0.5f, 0.0f,  // Нижний правый угол
                 0.02f, height, 0.0f, 0.5f, 1.0f, 0.0f,  // Верхний правый угол
                -0.02f, height, 0.0f, 0.5f, 1.0f, 0.0f   // Верхний левый угол
            };

            int[] grassIndices = {
                0, 1, 2,  // Первый треугольник
                2, 3, 0   // Второй треугольник
            };

            // Создаем временные буферы для травинки
            int grassVaoId = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(grassVaoId);

            // Создаем VBO для вершин травинки
            FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(grassVertices.length);
            vertexBuffer.put(grassVertices).flip();

            int grassVboId = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, grassVboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

            // Позиции вершин
            GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 0);
            GL20.glEnableVertexAttribArray(0);

            // Цвета вершин
            GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
            GL20.glEnableVertexAttribArray(1);

            // Создаем EBO для индексов травинки
            IntBuffer indexBuffer = MemoryUtil.memAllocInt(grassIndices.length);
            indexBuffer.put(grassIndices).flip();

            int grassEboId = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, grassEboId);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

            // Устанавливаем матрицы для шейдера
            shaderProgram.setUniform("model", modelMatrix);

            // Рисуем травинку
            GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);

            // Освобождаем ресурсы
            MemoryUtil.memFree(vertexBuffer);
            MemoryUtil.memFree(indexBuffer);
            GL15.glDeleteBuffers(grassVboId);
            GL15.glDeleteBuffers(grassEboId);
            GL30.glDeleteVertexArrays(grassVaoId);

            // Создаем еще одну травинку под углом для объема
            modelMatrix = new Matrix4f().identity()
                .translate(position)
                .rotate((float) Math.toRadians(90), 0, 1, 0); // Поворот на 90 градусов

            // Создаем временные буферы для второй травинки
            grassVaoId = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(grassVaoId);

            // Создаем VBO для вершин второй травинки
            vertexBuffer = MemoryUtil.memAllocFloat(grassVertices.length);
            vertexBuffer.put(grassVertices).flip();

            grassVboId = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, grassVboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

            // Позиции вершин
            GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 0);
            GL20.glEnableVertexAttribArray(0);

            // Цвета вершин
            GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
            GL20.glEnableVertexAttribArray(1);

            // Создаем EBO для индексов второй травинки
            indexBuffer = MemoryUtil.memAllocInt(grassIndices.length);
            indexBuffer.put(grassIndices).flip();

            grassEboId = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, grassEboId);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

            // Устанавливаем матрицы для шейдера
            shaderProgram.setUniform("model", modelMatrix);

            // Рисуем вторую травинку
            GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);

            // Освобождаем ресурсы
            MemoryUtil.memFree(vertexBuffer);
            MemoryUtil.memFree(indexBuffer);
            GL15.glDeleteBuffers(grassVboId);
            GL15.glDeleteBuffers(grassEboId);
            GL30.glDeleteVertexArrays(grassVaoId);
        }

        // Отвязка VAO
        GL30.glBindVertexArray(0);

        // Возвращаем настройки OpenGL
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Освобождение ресурсов
     */
    public void cleanup() {
        shaderProgram.cleanup();

        // Удаление VBO и VAO
        GL15.glDeleteBuffers(vboId);
        GL15.glDeleteBuffers(eboId);
        GL30.glDeleteVertexArrays(vaoId);

        GL15.glDeleteBuffers(floorVboId);
        GL15.glDeleteBuffers(floorEboId);
        GL30.glDeleteVertexArrays(floorVaoId);
    }
}
