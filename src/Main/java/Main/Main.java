package Main;

import AI.ImageRecognition.OverlayedDrawingCanvas;
import AI.ImageRecognition.RunDrawingCanvas;
import Graphics.IO.Timer;
import Graphics.IO.Window;
import Levels.Characters.Player;
import Levels.Framework.Maze;
import org.lwjgl.opengl.GL;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.glClear;

public class Main {

    // standard variables for width and height of the game
    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 1080;

    // cap at 60 fps for now
    private static final double FRAME_CAP = 1.0 / 60.0;

    public static void main (String[] args) throws IOException {
        // start up GLFW
        if ( !glfwInit() ) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        // run the main game loop
        (new Main()).run();

        // terminate GLFW
        glfwTerminate();
    }

    public void run() throws IOException {
        // set up the window for displaying the game
        Window window = new Window(SCREEN_WIDTH, SCREEN_HEIGHT);
        window.setFullscreen(false);
        window.createWindow("Mazes of Magic");

        // initialize GLFW capabilities
        GL.createCapabilities();
        // enable use of textures
        glEnable(GL_TEXTURE_2D);
        // make transparent backgrounds in textures actually transparent
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // start the maze found in specified file and create the player object
        Maze maze = new Maze("level_1");
        Player player = Player.getInstance();

        // set up the renderer with the player and maze created above
        World world = new World(maze, SCREEN_WIDTH, SCREEN_HEIGHT);
        // and start up the GUI
        GUI gui = new GUI();
        // drawing canvas
        // OverlayedDrawingCanvas overlay = new OverlayedDrawingCanvas(window.getWindow());
        // and initialize the controller for input checking
        Controller controller = new Controller(maze, window);

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

                if (frame_time >= 1.0) {
                    frame_time = 0;
                    // System.out.println("FPS: " + frames);
                    frames = 0;
                }
            }

            // make sure we only draw 60 frames every second
            if (can_render) {
                // poll the events, to detect input
                glfwPollEvents();
                // clear previous renders
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                // check the inputs done by the player
                controller.checkInputs();

                // if the player still has movement frames left, execute those by moving the player
                if ( controller.getMovementCounter() > 0 ) {
                    world.movePlayer( controller.getSpeed(), controller.isVertical() );
                    controller.decrementMovementCounter();
                }

                // render the world
                world.render();
                gui.render();
                // overlay.render();
                window.swapBuffers();

                frames++;
            }
        }
    }
}
