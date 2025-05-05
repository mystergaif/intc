package main.java.com.example.physics;

/**
 * Класс для взаимодействия с физическим движком на C
 */
public class PhysicsEngine {
    // Загрузка нативной библиотеки
    static {
        try {
            // Сначала пробуем загрузить библиотеку по имени
            System.loadLibrary("physics");
        } catch (UnsatisfiedLinkError e1) {
            try {
                // Если не получилось, пробуем загрузить с полным путем
                String userDir = System.getProperty("user.dir");
                String libPath = userDir + "/target/natives/libphysics.so";
                System.load(libPath);
                System.out.println("Загружена библиотека: " + libPath);
            } catch (UnsatisfiedLinkError e2) {
                System.err.println("Не удалось загрузить нативную библиотеку: " + e2.getMessage());
                System.err.println("java.library.path: " + System.getProperty("java.library.path"));
                System.err.println("user.dir: " + System.getProperty("user.dir"));
            }
        }
    }

    // Нативные методы
    private native void initPhysics();
    private native void cleanupPhysics();
    private native float[] updatePhysics(float deltaTime);
    private native float[] updatePlayerPhysics(
        float posX, float posY, float posZ,
        float velX, float velY, float velZ,
        float deltaTime
    );
    private native boolean checkCollision(float posX, float posY, float posZ);

    /**
     * Инициализация физического движка
     */
    public void init() {
        initPhysics();
    }

    /**
     * Обновление физики
     */
    public void update(float deltaTime) {
        updatePhysics(deltaTime);
    }

    /**
     * Обновление позиции игрока с учетом физики
     */
    public float[] updatePlayerPosition(
        float posX, float posY, float posZ,
        float velX, float velY, float velZ,
        float deltaTime
    ) {
        return updatePlayerPhysics(posX, posY, posZ, velX, velY, velZ, deltaTime);
    }

    /**
     * Проверка коллизии
     */
    public boolean isColliding(float posX, float posY, float posZ) {
        return checkCollision(posX, posY, posZ);
    }

    /**
     * Освобождение ресурсов
     */
    public void cleanup() {
        cleanupPhysics();
    }
}
