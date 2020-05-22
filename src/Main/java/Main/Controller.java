package Main;

import Graphics.IO.Window;
import Graphics.Rendering.PauseScreen;
import Levels.Characters.Player;
import Levels.Framework.Maze;

import java.io.FileNotFoundException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;

/**
 * class for handling inputs to the game
 */
public class Controller {
    // the things necessary for all the inputs
    private final Maze maze;
    private final Window window;
    private final Player player;

    // the variables used for player movement
    private final int MOVEMENT_CAP = 15;
    private int movementCounter = 0;
    private float speed;
    private boolean vertical;

    // variables to prevent the user from having way more inputs then intended
    private int inventoryCooldown = 0;
    private int pauseMenuCooldown = 0;
    private int pauseCooldown = 0;

    public Controller(Maze maze, Window window) {
        this.maze = maze;
        this.window = window;
        player = Player.getInstance();
    }

    /**
     * method that will check for activation from all available inputs
     */
    public void checkInputs() {
        // check if the player wants to pause/unpause the game
        if (pauseCooldown == 0) {
            if (window.buttonClicked(GLFW_KEY_ESCAPE)) {
                PauseScreen.togglePause();
                pauseCooldown = 10;
            }
        } else {
            pauseCooldown--;
        }

        if ( paused() ) {
            // if the game is paused we wish to check if an option was selected from the pause menu
            checkPauseMenu();
        } else {
            // if the game is not paused, we check our normal inputs
            if ( window.buttonClicked(GLFW_KEY_F5) ) {
                try {
                    maze.saveCurrentMaze();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            // we only check for inventory inputs if allowed and not paused
            if ( inventoryCooldown == 0 ) {
                checkInventory();
            } else {
                // if switching is still on cooldown, decrement the cooldown by one
                inventoryCooldown--;
            }

            // we only check for movement inputs if movement is allowed and not paused
            if ( movementCounter == 0 ) {
                checkMovement();
            }
        }
    }

    private void checkPauseMenu() {
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
            }

            if ( window.buttonClicked(GLFW_KEY_UP) ) {
                PauseScreen.changeSelected(true);
            } else if ( window.buttonClicked(GLFW_KEY_DOWN) ) {
                PauseScreen.changeSelected(false);
            }

            pauseMenuCooldown = 5;
        } else {
            pauseMenuCooldown --;
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

    public void decrementMovementCounter() {
        movementCounter--;
    }

    public int getMovementCounter() {
        return movementCounter;
    }

    public float getSpeed() {
        return speed;
    }

    public boolean isVertical() {
        return vertical;
    }

    public boolean paused() {
        return PauseScreen.isPaused();
    }
}
