package Main;

import Graphics.Camera;
import Graphics.Renderer;
import Graphics.Shader;
import Graphics.Window;
import Levels.Framework.joml.Matrix4f;
import Levels.Tiles.Wall;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

    // standard varialbes for width and height of the game
    private static final int SCREEN_WIDTH = 640;
    private static final int SCREEN_HEIGHT = 480;

    // cap at 60 fps for now
    private static final double FRAME_CAP = 1.0 / 60.0;

    public static void main (String[] args) {
        if ( !glfwInit() ) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        Window window = new Window(SCREEN_WIDTH, SCREEN_HEIGHT);
        window.createWindow("Test");

        GL.createCapabilities();

        Camera camera = new Camera(SCREEN_WIDTH, SCREEN_HEIGHT);

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

        Matrix4f scale = new Matrix4f().scale(64);

        // Stuff to keep track of the fps
        double frame_time = 0;
        int frames = 0;

        // starting time
        double time = Timer.getTime();
        // time that has not yet been processed
        double unprocessed = 0;

        while ( !window.shouldClose() ) {
            boolean can_render = false;

            // update the time variables
            double time_2 = Timer.getTime();
            double passed = time_2 - time;

            unprocessed += passed;
            frame_time += passed;
            time = time_2;

            while (unprocessed >= FRAME_CAP) {
                unprocessed -= FRAME_CAP;
                can_render = true;

                // for now, allow exiting of the window by pressing escape
                if ( window.buttonClicked(GLFW_KEY_ESCAPE) ) {
                    window.close();
                }

                glfwPollEvents();

                if (frame_time >= 1.0) {
                    frame_time = 0;
                    System.out.println("FPS: " + frames);
                    frames = 0;
                }
            }

            if (can_render) {
                shader.bind();
                shader.setUniform("sampler", Wall.WALL.getSampler());
                shader.setUniform("projection", camera.getProjection().mul(scale));
                Wall.WALL.bindTexture();
                renderer.render();

                window.swapBuffers();

                frames++;
            }
        }

        glfwTerminate();
    }
}
