package Main;

import Graphics.Window;
import Levels.Framework.Maze;
import org.lwjgl.opengl.GL;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

    // standard varialbes for width and height of the game
    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 1080;

    // cap at 60 fps for now
    private static final double FRAME_CAP = 1.0 / 60.0;

    public static void main (String[] args) throws IOException {
        if ( !glfwInit() ) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        Window window = new Window(SCREEN_WIDTH, SCREEN_HEIGHT);
        window.setFullscreen(false);
        window.createWindow("Test");

        GL.createCapabilities();

        Maze maze = new Maze("level_1");
        Renderer renderer = new Renderer(maze, SCREEN_WIDTH, SCREEN_HEIGHT);

        // enable use of textures
        glEnable(GL_TEXTURE_2D);

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

                if ( window.buttonClicked(GLFW_KEY_W) ) {
                    maze.moveUp();
                }

                if ( window.buttonClicked(GLFW_KEY_A) ) {
                    maze.moveLeft();
                }

                if ( window.buttonClicked(GLFW_KEY_S) ) {
                    maze.moveDown();
                }

                if ( window.buttonClicked(GLFW_KEY_D) ) {
                    maze.moveRight();
                }

                glfwPollEvents();

                if (frame_time >= 1.0) {
                    frame_time = 0;
                    System.out.println("FPS: " + frames);
                    frames = 0;
                }
            }

            // make sure we only draw 60 frames every second
            if (can_render) {
                renderer.render();

                window.swapBuffers();

                frames++;
            }
        }

        glfwTerminate();
    }
}
