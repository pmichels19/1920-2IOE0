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

    private Player player;
    private World world;
    private Window window;
    private Maze maze;

    // cap at 60 fps for now
    private static final double FRAME_CAP = 1.0 / 60.0;
    private static final int MOVEMENT_CAP = 15;

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
        maze = new Maze("target");
        player = Player.getInstance();

        // set up the renderer with the player and maze created above
        world = new World(maze, SCREEN_WIDTH, SCREEN_HEIGHT);
        // and start up the GUI
        GUI gui = new GUI();

        // enable use of textures
        glEnable(GL_TEXTURE_2D);
        // make transparent backgrounds in textures actually transparent
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

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
                    System.out.println("FPS: " + frames);
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
                checkInputs();

                // if the player still has movement frames left, execute those by moving the player
                if ( movementCounter > 0 ) {
                    world.movePlayer( speed, vertical );
                    movementCounter--;
                }

                // render the world
                world.render();
                gui.render();
                window.swapBuffers();

                frames++;
            }
        }
    }

    private int movementCounter = 0;
    private float speed = 0;
    private boolean vertical;

    public void checkInputs() {
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

        if ( window.buttonClicked(GLFW_KEY_Q) ) {
             player.changeHealth(-1);
        } else if ( window.buttonClicked(GLFW_KEY_E) ) {
            player.changeHealth(1);
        }

        if ( window.buttonClicked(GLFW_KEY_Z) ) {
            player.changeMana(-1);
        } else if ( window.buttonClicked(GLFW_KEY_C) ) {
            player.changeMana(1);
        }

        // moving is only allowed if the player is not currently moving
        if (movementCounter == 0) {
            /*
             we check for the inputs: W/up, A/left, S/down, D/right
             If movement in the desired direction is allowed, we adjust the speed, counter and vertical variables
             accordingly and move the player in the maze
             */
            if ( window.inputW() ) {
                if ( maze.canMoveUp() ) {
                    maze.moveUp();

                    movementCounter = MOVEMENT_CAP;
                    speed = 1f / (float) MOVEMENT_CAP;
                    vertical = true;
                }
            } else if ( window.inputA() ) {
                if ( maze.canMoveLeft() ) {
                    maze.moveLeft();

                    movementCounter = MOVEMENT_CAP;
                    speed = -1f / (float) MOVEMENT_CAP;
                    vertical = false;
                }
            } else if ( window.inputS() ) {
                if ( maze.canMoveDown() ) {
                    maze.moveDown();

                    movementCounter = MOVEMENT_CAP;
                    speed = -1f / (float) MOVEMENT_CAP;
                    vertical = true;
                }
            } else if ( window.inputD() ) {
                if ( maze.canMoveRight() ) {
                    maze.moveRight();

                    movementCounter = MOVEMENT_CAP;
                    speed = 1f / (float) MOVEMENT_CAP;
                    vertical = false;
                }
            }
        }
    }
}
