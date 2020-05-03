package Main;

import Graphics.Renderer;
import Graphics.Shader;
import Graphics.Window;
import Levels.Framework.joml.Matrix4f;
import Levels.Tiles.Wall;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

    private static final int SCREEN_WIDTH = 640;
    private static final int SCREEN_HEIGHT = 480;

    public static void main (String[] args) {
        if ( !glfwInit() ) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        Window window = new Window(SCREEN_WIDTH, SCREEN_HEIGHT);
        window.createWindow("Test");

        GL.createCapabilities();

        // enable use of textures
        glEnable(GL_TEXTURE_2D);

        float[] testVertices = new float[] {
                -0.5f, 0.5f, 0.0f,    // TOP LEFT
                0.5f, 0.5f, 0.0f,     // TOP RIGHT
                0.5f, -0.5f, 0.0f,    // BOTTOM RIGHT
                -0.5f, -0.5f, 0.0f    // BOTTOM LEFT
        };

        float[] testTexture = new float[] {
                0,1,
                1,1,
                1,0,
                0,0,
        };

        Renderer renderer = new Renderer(testVertices, testTexture);
        Shader shader = new Shader("testshader");

        Matrix4f projection = new Matrix4f().ortho2D(
                ((float) -SCREEN_WIDTH) / 2.0f,
                ((float) SCREEN_WIDTH) / 2.0f,
                ((float) SCREEN_HEIGHT) / 2.0f,
                ((float) -SCREEN_HEIGHT) / 2.0f
        );
        Matrix4f scale = new Matrix4f().scale(128);
        Matrix4f target = new Matrix4f();

        projection.mul(scale, target);

        while ( !window.shouldClose() ) {
            glfwPollEvents();

            // for now, allow exiting of the window by pressing escape
            if ( window.buttonClicked(GLFW_KEY_ESCAPE) ) {
                window.close();
            }

            shader.bind();
            shader.setUniform("sampler", Wall.WALL.getSampler());
            shader.setUniform("projection", target);
            Wall.WALL.bindTexture();
            renderer.render();

            window.swapBuffers();
        }

        glfwTerminate();
    }
}
