package Main;

import AI.ImageRecognition.RunDrawingCanvas;
import Graphics.IO.Timer;
import Graphics.IO.Window;
import Graphics.Rendering.*;
import Levels.Framework.Maze;
import Main.Input.MainController;
import org.lwjgl.opengl.GL;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.glClear;

public class Main {

    // standard variables for width and height of the game
    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 1080;

    // we start the game in the main menu
    private static GameState state = GameState.MAIN_MENU;

    // we want toC make sure all objects use the same maze, we accomplish this by making it static
    private static Maze maze;
    private static World world;
    private static GUI gui;
    private static PauseScreen pauseScreen;
    private static MainMenu mainMenu;
    private static LoadMenu loadMenu;
    private static SaveMenu saveMenu;
    private static NewGameMenu newGameMenu;
    private static DeathScreen deathScreen;
    private static VictoryScreen victoryScreen;

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
        // hard exit for now
        System.exit(1);
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

        maze = new Maze("playerAttackTest");
        world = new World(maze, SCREEN_WIDTH, SCREEN_HEIGHT);
        gui = new GUI();
        pauseScreen = new PauseScreen();
        mainMenu = new MainMenu();
        loadMenu = new LoadMenu();
        saveMenu = new SaveMenu();
        newGameMenu = new NewGameMenu();
        deathScreen = new DeathScreen();
        victoryScreen = new VictoryScreen();
        // drawing canvas
        RunDrawingCanvas drawingCanvas = new RunDrawingCanvas();

        // and initialize the controller for input checking
        MainController mainController = new MainController(maze, world, window);

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
                mainController.checkInputs();

                // check which state we are in, so we render the correct thing
                if (state == GameState.MAIN_MENU) {
                    mainMenu.render();
                } else if (state == GameState.IN_GAME) {
                    world.render();
                    gui.render();
                    if (drawingCanvas.isRunning()) {
                        drawingCanvas.render();
                    }
                } else if (state == GameState.PAUSED) {
                    pauseScreen.render();
                } else if (state == GameState.LOADING_SAVE) {
                    loadMenu.render();
                } else if (state == GameState.SAVING_GAME) {
                    saveMenu.render();
                } else if (state == GameState.STARTING_GAME) {
                    newGameMenu.render();
                } else if (state == GameState.DEAD) {
                    deathScreen.render();
                } else if (state == GameState.VICTORY) {
                    victoryScreen.render();
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
        // first we check if we go in game, if we do, we want to reset the camera position
        world.resetCameraPosition();
        Main.state = state;
        // communicate the fact that the state changed to the main controller
        MainController.changedState();
    }

    /**
     * getter for the current maze
     *
     * @return {@code maze}
     */
    public static Maze getMaze() {
        return Main.maze;
    }

    /**
     * sets the current maze to {@code maze} provided as a parameter
     *
     * @param maze the new maze
     */
    public static void setMaze(Maze maze) {
        Main.maze = maze;
    }

    public static World getWorld() { return Main.world; }

    public static GUI getGui() {
        return gui;
    }

    public static PauseScreen getPauseScreen() {
        return pauseScreen;
    }

    public static MainMenu getMainMenu() {
        return mainMenu;
    }

    public static LoadMenu getLoadMenu() {
        return loadMenu;
    }

    public static SaveMenu getSaveMenu() {
        return saveMenu;
    }

    public static NewGameMenu getNewGameMenu() {
        return newGameMenu;
    }

    public static DeathScreen getDeathScreen() {
        return deathScreen;
    }

    public static VictoryScreen getVictoryScreen() {
        return victoryScreen;
    }
}
