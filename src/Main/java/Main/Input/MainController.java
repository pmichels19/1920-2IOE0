package Main.Input;

import Graphics.IO.Window;
import Graphics.Rendering.PauseScreen;
import Levels.Characters.Player;
import Levels.Framework.Maze;
import Main.GameState;

import java.io.FileNotFoundException;

import static org.lwjgl.glfw.GLFW.*;
import static Main.Main.getState;
import static Main.Main.setState;

/**
 * class for handling inputs to the game
 */
public class MainController {
    // the things necessary for all the inputs
    private final Maze maze;
    private final Window window;
    private final Player player;

    // the variables used for player movement
    private final int MOVEMENT_CAP = 15;
    private int movementCounter = 0;
    private float speed;
    private boolean vertical;

    // variables to prevent the user from having way more inputs then intended:
    // inventory cooldown allows for control when switching up/down in the inventory
    private int inventoryCooldown = 0;
    // pauseMenu cooldown provides control when navigating the pause menu options
    private int pauseMenuCooldown = 0;
    // pause cooldown makes the pause menu appear and disappear in a controllable manner
    private int pauseCooldown = 0;

    public MainController(Maze maze, Window window) {
        this.maze = maze;
        this.window = window;
        player = Player.getInstance();
    }

    /**
     * method that will check for activation from all available inputs
     */
    public void checkInputs() {
        // depending on the current game state, we wish to check different inputs
        GameState state = Main.Main.getState();
        if ( state == GameState.IN_GAME ) {
            checkInGame();
        } else if ( state == GameState.PAUSED ) {
            checkPauseMenu();
        } else if ( state == GameState.MAIN_MENU ) {
            checkMainMenu();
        }
    }

    /**
     * checks the inputs the player can do while in game, so both for controlling the PC and the inventory
     */
    private void checkInGame() {
        // check if the player wants to pause/unpause the game
        if (pauseCooldown == 0) {
            if (window.buttonClicked(GLFW_KEY_ESCAPE)) {
                pauseCooldown = 10;
                setState( GameState.PAUSED );
            }
        } else {
            pauseCooldown--;
        }

        // we only check for inventory inputs if allowed and not paused
        if ( inventoryCooldown == 0 ) {
            checkInventory();
        } else {
            // if switching is still on cooldown, decrement the cooldown by one
            inventoryCooldown--;
        }

        // we only check for movement inputs if movement is allowed
        if ( movementCounter == 0 ) {
            checkMovement();
        }
    }

    /**
     * checks the inputs regarding the inventory
     */
    private void checkInventory() {
        // we can go up and down in our inventory
        if ( window.buttonClicked(GLFW_KEY_UP) ) {
            player.setSelectedItem( player.getSelectedItem() - 1 );
        } else if ( window.buttonClicked(GLFW_KEY_DOWN) ) {
            player.setSelectedItem( player.getSelectedItem() + 1 );
        }

        // set a cooldown of 5 frames, so the player has better control over what item he wants to select
        inventoryCooldown = 5;
    }

    /**
     * checks if the player wants to move anywhere using the WASD keys
     */
    private void checkMovement() {
        /*
         we check for the inputs: W/up, A/left, S/down, D/right
         If movement in the desired direction is allowed, we adjust the speed, counter and vertical variables
         accordingly and move the player in the maze
         */
        if ( window.buttonClicked( GLFW_KEY_W ) ) {
            if ( maze.canMoveUp() ) {
                maze.moveUp();

                movementCounter = MOVEMENT_CAP;
                speed = 1f / (float) MOVEMENT_CAP;
                vertical = true;
            }
        } else if ( window.buttonClicked( GLFW_KEY_A ) ) {
            if ( maze.canMoveLeft() ) {
                maze.moveLeft();

                movementCounter = MOVEMENT_CAP;
                speed = -1f / (float) MOVEMENT_CAP;
                vertical = false;
            }
        } else if ( window.buttonClicked( GLFW_KEY_S ) ) {
            if ( maze.canMoveDown() ) {
                maze.moveDown();

                movementCounter = MOVEMENT_CAP;
                speed = -1f / (float) MOVEMENT_CAP;
                vertical = true;
            }
        } else if ( window.buttonClicked( GLFW_KEY_D ) ) {
            if ( maze.canMoveRight() ) {
                maze.moveRight();

                movementCounter = MOVEMENT_CAP;
                speed = 1f / (float) MOVEMENT_CAP;
                vertical = false;
            }
        }
    }

    /**
     * checks the inputs provided to the main menu
     */
    private void checkMainMenu() {

    }

    /**
     * checks the inputs provided to the pause screen
     */
    private void checkPauseMenu() {
        // first we check if the player wants to go back in game again
        if ( pauseCooldown == 0 ) {
            if (window.buttonClicked(GLFW_KEY_ESCAPE)) {
                pauseCooldown = 10;
                setState( GameState.IN_GAME );
                return;
            }
        } else {
            pauseCooldown--;
        }

        // we can go up and down in the pause menu on cooldown
        if (pauseMenuCooldown == 0) {
            // if the player selected an option, we need to execute the selected option
            if ( window.buttonClicked(GLFW_KEY_ENTER) ) {
                switch ( PauseScreen.getSelectedOption() ) {
                    // 0 is mapped to saving the game
                    case 0:
                        try {
                            maze.saveCurrentMaze();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    // 1 is mapped to saving the game, then exiting
                    case 1:
                        try {
                            maze.saveCurrentMaze();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        glfwSetWindowShouldClose( window.getWindow(), true );
                        break;
                    // 2 is mapped to quitting the game, without saving
                    case 2:
                        glfwSetWindowShouldClose( window.getWindow(), true );
                        break;
                }

                pauseMenuCooldown = 5;
            }

            if ( window.buttonClicked(GLFW_KEY_UP) ) {
                PauseScreen.changeSelected(true);
                pauseMenuCooldown = 5;
            } else if ( window.buttonClicked(GLFW_KEY_DOWN) ) {
                PauseScreen.changeSelected(false);
                pauseMenuCooldown = 5;
            }
        } else {
            pauseMenuCooldown --;
        }
    }

    /**
     * reduces the cooldown left on moving by one
     */
    public void decrementMovementCounter() {
        movementCounter--;
    }

    /**
     * returns the cooldown left on moving
     *
     * @return {@code movementCounter}
     */
    public int getMovementCounter() {
        return movementCounter;
    }

    /**
     * returns the speed with which the player is currently moving
     *
     * @return {@code speed}
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * returns whether the current movement is vertical or not
     *
     * @return {@code vertical}
     */
    public boolean isVertical() {
        return vertical;
    }
}
