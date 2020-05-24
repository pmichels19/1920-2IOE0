package Main;

import Graphics.IO.Timer;
import Graphics.IO.Window;
import Graphics.Rendering.*;
import Levels.Framework.Maze;
import Main.Input.MainController;
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

    // we start the game in the main menu
    private static GameState state = GameState.MAIN_MENU;
    // and with no save slot selected:
    private static int slot = -1;

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

        // set up the world, corresponding GUI, the main menu and pause screen
        World world = new World(maze, SCREEN_WIDTH, SCREEN_HEIGHT);
        GUI gui = new GUI();
        PauseScreen pauseScreen = new PauseScreen();
        MainMenu mainMenu = new MainMenu();
        LoadMenu loadMenu = new LoadMenu();
        SaveMenu saveMenu = new SaveMenu();
        NewGameMenu newGameMenu = new NewGameMenu();

        // and initialize the controller for input checking
        MainController mainController = new MainController(maze, window);

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
                mainController.checkInputs();

                // if the player still has movement frames left, execute those by moving the player
                if ( mainController.getMovementCounter() > 0 ) {
                    world.movePlayer( mainController.getSpeed(), mainController.isVertical() );
                    mainController.decrementMovementCounter();
                }

                if (state == GameState.MAIN_MENU) {
                    mainMenu.render();
                } else if (state == GameState.IN_GAME) {
                    world.render();
                    gui.render();
                } else if (state == GameState.PAUSED) {
                    pauseScreen.render();
                } else if (state == GameState.LOADING_SAVE) {
                    loadMenu.render();
                } else if (state == GameState.SAVING_GAME) {
                    saveMenu.render();
                } else if (state == GameState.STARTING_GAME) {
                    newGameMenu.render();
                }

                window.swapBuffers();

                frames++;
            }
        }
    }

    /**
     * getter for the current game state
     *
     * @return {@code state}
     */
    public static GameState getState() {
        return Main.state;
    }

    /**
     * sets the current game state to {@code state} provided in the parameter
     *
     * @param state the new game state
     */
    public static void setState(GameState state) {
        Main.state = state;
    }

    /**
     * getter for the current save slot
     *
     * @return {@code slot}
     */
    public static int getSlot() {
        return slot;
    }

    /**
     * sets the current slot to the provided slot
     *
     * @param slot the new save slot
     */
    public static void setSlot(int slot) {
        Main.slot = slot;
    }
}
