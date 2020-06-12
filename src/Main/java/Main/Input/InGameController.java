package Main.Input;

import AI.ImageRecognition.RunDrawingCanvas;
import Levels.Assets.Items.Item;
import Levels.Framework.Maze;
import Main.GameState;
import SpellCasting.Spell;
import SpellCasting.SpellAgility;

import static Graphics.IO.ScreenShot.takeScreenShot;
import static Levels.Characters.Character.DIRECTION_LEFT;
import static Levels.Characters.Character.DIRECTION_RIGHT;
import static Levels.Characters.Character.DIRECTION_DOWN;
import static Levels.Characters.Character.DIRECTION_UP;
import static Main.Main.setState;
import static org.lwjgl.glfw.GLFW.*;

/**
 * class that will check the inputs while the player is in game
 */
public class InGameController extends Controller {
    // inventory cooldown allows for control when switching up/down in the inventory
    private int inventoryCooldown = 0;

    // the variables used for player movement
    int movementCounter = 0;
    float speed = 0;
    boolean vertical;

    // variables used for the drawing canvas
    private final RunDrawingCanvas drawingCanvas = new RunDrawingCanvas();
    @Override
    void checkInputs() {
        // if the player died, we need to go into the DEAD state
        if (player.getHealth() == 0) {
            setState(GameState.DEAD);
            return;
        }

        // check if the player wants to pause/unpause the game
        if (pauseCooldown == 0) {
            if (window.buttonClicked(GLFW_KEY_ESCAPE)) {
                pauseCooldown = 10;

                // take a screenshot and place it into the saves folder
                takeScreenShot( "Saves/lastSave.png" );
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
        } else if (movementCounter > 0) {
            // move the player with the specified speed and direction
            world.movePlayer( speed, vertical );
            // decrement the amount of movement frames left
            movementCounter--;
        }

        if (window.buttonClicked(GLFW_KEY_L)) {
            drawingCanvas.start();
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

                movementCounter = (int) player.getSpeed();
                speed = 1f / player.getSpeed();
                vertical = true;
            } else {
                player.turnUp();
            }
        } else if ( window.buttonClicked( GLFW_KEY_A ) ) {
            if ( maze.canMoveLeft() ) {
                maze.moveLeft();

                movementCounter = (int) player.getSpeed();
                speed = -1f / player.getSpeed();
                vertical = false;
            } else {
                player.turnLeft();
            }
        } else if ( window.buttonClicked( GLFW_KEY_S ) ) {
            if ( maze.canMoveDown() ) {
                maze.moveDown();

                movementCounter = (int) player.getSpeed();
                speed = -1f / player.getSpeed();
                vertical = true;
            } else {
                player.turnDown();
            }
        } else if ( window.buttonClicked( GLFW_KEY_D ) ) {
            if ( maze.canMoveRight() ) {
                maze.moveRight();

                movementCounter = (int) player.getSpeed();
                speed = 1f / player.getSpeed();
                vertical = false;
            } else {
                player.turnRight();
            }
        }

        // enter can be used to pick up items
        if (window.buttonClicked( GLFW_KEY_ENTER )) {
            tryItemCollect();
        }
    }

    /**
     * method that looks at the grid, the player position and the direction the player is facing
     * to see if an item can be picked up
     */
    private void tryItemCollect() {
        // get the grid
        char[][] grid = maze.getGrid();
        int direction = player.getDirection();
        int x_player = maze.getPlayerLocation().getX();
        int y_player = maze.getPlayerLocation().getY();
        char facing;
        switch (direction) {
            case DIRECTION_LEFT:
                facing = grid[x_player][y_player - 1];
                if ( Maze.ITEM_MARKERS.contains( facing ) ) {
                    player.addItem( Item.getItemById( Character.getNumericValue( facing ) ) );
                    grid[x_player][y_player - 1] = Maze.MARKER_SPACE;
                }
                break;
            case DIRECTION_RIGHT:
                facing = grid[x_player][y_player + 1];
                if ( Maze.ITEM_MARKERS.contains( facing ) ) {
                    player.addItem( Item.getItemById( Character.getNumericValue( facing ) ) );
                    grid[x_player][y_player + 1] = Maze.MARKER_SPACE;
                }
                break;
            case DIRECTION_UP:
                facing = grid[x_player - 1][y_player];
                if ( Maze.ITEM_MARKERS.contains( facing ) ) {
                    player.addItem( Item.getItemById( Character.getNumericValue( facing ) ) );
                    grid[x_player - 1][y_player] = Maze.MARKER_SPACE;
                }
                break;
            case DIRECTION_DOWN:
                facing = grid[x_player + 1][y_player];
                if ( Maze.ITEM_MARKERS.contains( facing ) ) {
                    player.addItem( Item.getItemById( Character.getNumericValue( facing ) ) );
                    grid[x_player + 1][y_player] = Maze.MARKER_SPACE;
                }
                break;
        }
    }
}
