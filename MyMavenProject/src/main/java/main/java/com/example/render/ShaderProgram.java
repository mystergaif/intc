package main.java.com.example.render;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

/**
 * Класс для работы с шейдерными программами
 */
public class ShaderProgram {
    private final int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    
    /**
     * Конструктор
     */
    public ShaderProgram() {
        programId = GL20.glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Не удалось создать шейдерную программу");
        }
    }
    
    /**
     * Создание вершинного шейдера
     */
    public void createVertexShader(String shaderCode) {
        vertexShaderId = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }
    
    /**
     * Создание фрагментного шейдера
     */
    public void createFragmentShader(String shaderCode) {
        fragmentShaderId = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
    }
    
    /**
     * Создание шейдера
     */
    private int createShader(String shaderCode, int shaderType) {
        int shaderId = GL20.glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new RuntimeException("Ошибка создания шейдера. Тип: " + shaderType);
        }
        
        GL20.glShaderSource(shaderId, shaderCode);
        GL20.glCompileShader(shaderId);
        
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Ошибка компиляции шейдера: " + GL20.glGetShaderInfoLog(shaderId, 1024));
        }
        
        GL20.glAttachShader(programId, shaderId);
        
        return shaderId;
    }
    
    /**
     * Линковка шейдерной программы
     */
    public void link() {
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Ошибка линковки шейдерной программы: " + GL20.glGetProgramInfoLog(programId, 1024));
        }
        
        if (vertexShaderId != 0) {
            GL20.glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            GL20.glDetachShader(programId, fragmentShaderId);
        }
        
        GL20.glValidateProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0) {
            System.err.println("Предупреждение валидации шейдерной программы: " + GL20.glGetProgramInfoLog(programId, 1024));
        }
    }
    
    /**
     * Привязка шейдерной программы
     */
    public void bind() {
        GL20.glUseProgram(programId);
    }
    
    /**
     * Отвязка шейдерной программы
     */
    public void unbind() {
        GL20.glUseProgram(0);
    }
    
    /**
     * Установка uniform-переменной типа Matrix4f
     */
    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            int location = GL20.glGetUniformLocation(programId, uniformName);
            if (location != -1) {
                GL20.glUniformMatrix4fv(location, false, fb);
            }
        }
    }
    
    /**
     * Освобождение ресурсов
     */
    public void cleanup() {
        unbind();
        if (programId != 0) {
            GL20.glDeleteProgram(programId);
        }
    }
}
