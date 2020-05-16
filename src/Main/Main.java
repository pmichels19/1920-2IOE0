package Main;

import Graphics.IO.Timer;
import Graphics.IO.Window;
import Levels.Characters.Character;
import Levels.Characters.Player;
import Levels.Assets.Tiles.Background;
import Levels.Framework.Maze;
import org.lwjgl.opengl.GL;

import java.io.FileNotFoundException;
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

    private Character player;
    private World world;
    private Window window;
    private Maze maze;

    // cap at 60 fps for now
    private static final double FRAME_CAP = 1.0 / 60.0;
    private static final double MOVEMENT_CAP = 1.0 / 5.0;
    private double inputAllowed;

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
        window = new Window(SCREEN_WIDTH, SCREEN_HEIGHT);
        window.setFullscreen(false);
        window.createWindow("Mazes of Magic");

        // initialize GLFW capabilities
        GL.createCapabilities();

        // start the maze found in specified file and create the player object
        maze = new Maze("level_1");
        player = new Player(Background.PLAYER.getTexture(), 100, 100 );

        // set up the renderer with the player and maze created above
        world = new World(player, maze, SCREEN_WIDTH, SCREEN_HEIGHT);

        // enable use of textures
        glEnable(GL_TEXTURE_2D);
        // make transparent backgrounds in textures actually transparent
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Stuff to keep track of the fps
        double frame_time = 0;
        int frames = 0;
        int moving_frames = (int) ( MOVEMENT_CAP * 60.0f );

        // starting time
        double time = Timer.getTime();
        // time that has not yet been processed
        double unprocessed = 0;
        // time to keep track of whether input is allowed
        inputAllowed = 0;

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
                    System.out.println("FPS: " + frames);
                    frames = 0;
                }
            }

            // make sure we only draw 60 frames every second
            if (can_render) {
                glfwPollEvents();
                checkInputs(moving_frames, time);

                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                world.render();

                window.swapBuffers();

                frames++;
            }
        }
    }

    public void checkInputs(int moving_frames, double time) {
        // for now, allow exiting of the window by pressing escape
        if ( window.buttonClicked(GLFW_KEY_ESCAPE) ) {
            window.close();
        }

        if ( window.buttonClicked(GLFW_KEY_F5) ) {
            try {
                maze.saveCurrentMaze();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // booleans to check what buttons were pressed
        boolean WReq = window.inputW();
        boolean AReq = window.inputA();
        boolean SReq = window.inputS();
        boolean DReq = window.inputD();
        // booleans to check what movement is possible
        boolean WPoss = maze.canMoveUp();
        boolean APoss = maze.canMoveLeft();
        boolean SPoss = maze.canMoveDown();
        boolean DPoss = maze.canMoveRight();

        if (time > inputAllowed) {
            if ( WReq ) {
                if ( WPoss ) {
                    maze.moveUp();

                    inputAllowed = time + MOVEMENT_CAP;
                    world.setChange(moving_frames, 1.0f / (float) moving_frames, true);
                }
            } else if ( AReq ) {
                if ( APoss ) {
                    maze.moveLeft();

                    inputAllowed = time + MOVEMENT_CAP;
                    world.setChange(moving_frames, -1.0f / (float) moving_frames, false);
                }
            } else if ( SReq ) {
                if ( SPoss ) {
                    maze.moveDown();

                    inputAllowed = time + MOVEMENT_CAP;
                    world.setChange(moving_frames, -1.0f / (float) moving_frames, true);
                }
            } else if ( DReq ) {
                if ( DPoss ) {
                    maze.moveRight();

                    inputAllowed = time + MOVEMENT_CAP;
                    world.setChange(moving_frames, 1.0f / (float) moving_frames, false);
                }
            }
        }
    }
}
