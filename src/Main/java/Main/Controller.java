package Main;

import Graphics.IO.Window;
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
    private Maze maze;
    private Window window;
    private Player player;

    // the variables used for player movement
    private final int MOVEMENT_CAP = 15;
    private int movementCounter = 0;
    private float speed;
    private boolean vertical;

    // the variable for controlling what inventory slot is highlighted
    private int switchCooldown = 0;

    public Controller(Maze maze, Window window) {
        this.maze = maze;
        this.window = window;
        player = Player.getInstance();
    }

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

        if (switchCooldown == 0) {
            // we can go up and down in our inventory
            if ( window.buttonClicked(GLFW_KEY_UP) ) {
                player.setSelectedItem( player.getSelectedItem() - 1 );
            } else if ( window.buttonClicked(GLFW_KEY_DOWN) ) {
                player.setSelectedItem( player.getSelectedItem() + 1 );
            }

            // set a cooldown of 10 frames, so the player has better control over what item he wants to select
            switchCooldown = 10;
        } else {
            // if switching is still on cooldown, decrement the cooldown by one
            switchCooldown--;
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
}
