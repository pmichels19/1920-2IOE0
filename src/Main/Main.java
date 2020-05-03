package Main;

import Graphics.Renderer;
import Graphics.Shader;
import Graphics.Window;
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
                0.0f, 1.0f, 0.0f,    // TOP LEFT
                1.0f, 1.0f, 0.0f,     // TOP RIGHT
                1.0f, 0.0f, 0.0f,    // BOTTOM RIGHT
                0.0f, 0.0f, 0.0f    // BOTTOM LEFT
        };

        float[] testTexture = new float[] {
                0,1,
                1,1,
                1,0,
                0,0,
        };

        Renderer renderer = new Renderer(testVertices, testTexture);
        Shader shader = new Shader("testshader");

        while ( !window.shouldClose() ) {
            glfwPollEvents();

            // for now, allow exiting of the window by pressing escape
            if ( window.buttonClicked(GLFW_KEY_ESCAPE) ) {
                window.close();
            }

//            Wall.CASTLE_WALL.bindTexture();
            shader.bind();
            renderer.render();

            window.swapBuffers();
        }

        glfwTerminate();
    }
}
