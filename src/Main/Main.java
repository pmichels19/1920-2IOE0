package Main;

import Graphics.Renderer;
import Levels.Tiles.Wall;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 1080;

    public static void main (String[] args) {
        if ( !glfwInit() ) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        // sets the window to be full screen
        long window = glfwCreateWindow(SCREEN_WIDTH, SCREEN_HEIGHT, "Test", 0, 0);

        if (window == 0) {
            throw new IllegalStateException("Failed to create window");
        }

        glfwShowWindow(window);

        // enable events to happen and be processed on our window
        glfwMakeContextCurrent(window);
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

        while ( !glfwWindowShouldClose(window) ) {
            glfwPollEvents();

            // for now, allow exiting of the window by pressing escape
            if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GL_TRUE) {
                glfwSetWindowShouldClose(window, true);
            }

            Wall.CASTLE_WALL.bindTexture();
            renderer.render();

            glfwSwapBuffers(window);
        }

        glfwTerminate();
    }
}
