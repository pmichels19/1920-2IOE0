package Main;

import Graphics.Renderer;
import Graphics.Texture;
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
        long window = glfwCreateWindow(SCREEN_WIDTH, SCREEN_HEIGHT, "Test", glfwGetPrimaryMonitor(), 0);

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
                -0.5f, 0.5f,    // TOP LEFT
                0.5f, 0.5f,     // TOP RIGHT
                0.5f, -0.5f,    // BOTTOM RIGHT

                0.5f, -0.5f,    // BOTTOM RIGHT
                -0.5f, -0.5f,   // BOTTOM LEFT
                -0.5f, 0.5f     // TOP LEFT
        };

        float[] testTexture = new float[] {
                0,0,
                1,0,
                1,1,

                1,1,
                0,1,
                0,0
        };

        Renderer renderer = new Renderer(testVertices, testTexture);

        Texture tex = new Texture("src/Textures/test_texture.jpg");

        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        while ( !glfwWindowShouldClose(window) ) {
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT);

            // for now, allow exiting of the window by pressing escape
            if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GL_TRUE) {
                glfwSetWindowShouldClose(window, true);
            }

            tex.bind();
            renderer.render();

            glfwSwapBuffers(window);
        }

        glfwTerminate();
    }
}
