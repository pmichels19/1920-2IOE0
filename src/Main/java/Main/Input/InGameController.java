package Main.Input;

import AI.ImageRecognition.RunDrawingCanvas;
import Levels.Assets.Items.Item;
import Main.GameState;
import SpellCasting.Spell;
import SpellCasting.SpellAgility;

import static Graphics.IO.ScreenShot.takeScreenShot;
import static Main.Main.setState;
import static org.lwjgl.glfw.GLFW.*;

/**
 * class that will check the inputs while the player is in game
 */
public class InGameController extends Controller {
    // inventory cooldown allows for control when switching up/down in the inventory
    private int inventoryCooldown = 0;

    // the variables used for player movement
    final int MOVEMENT_CAP = 15;
    int movementCounter = 0;
    float speed = 0;
    boolean vertical;

    // variables used for the drawing canvas
    private RunDrawingCanvas drawingCanvas = new RunDrawingCanvas();
    private boolean stopped = false;

    private boolean released = true;

    private Spell spell;

    @Override
    void checkInputs() {
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

        if (window.buttonClicked(GLFW_KEY_N)) {
            player.addItem(Item.getItemById(Item.BOOT));
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

                movementCounter = player.getSpeed();
                speed = 1f / (float) player.getSpeed();
                vertical = true;
            }
        } else if ( window.buttonClicked( GLFW_KEY_A ) ) {
            if ( maze.canMoveLeft() ) {
                maze.moveLeft();

                movementCounter = player.getSpeed();
                speed = -1f / (float) player.getSpeed();
                vertical = false;
            }
        } else if ( window.buttonClicked( GLFW_KEY_S ) ) {
            if ( maze.canMoveDown() ) {
                maze.moveDown();

                movementCounter = player.getSpeed();
                speed = -1f / (float) player.getSpeed();
                vertical = true;
            }
        } else if ( window.buttonClicked( GLFW_KEY_D ) ) {
            if ( maze.canMoveRight() ) {
                maze.moveRight();

                movementCounter = player.getSpeed();
                speed = 1f / (float) player.getSpeed();
                vertical = false;
            }
        }

        // spell casting
        if (window.buttonClicked(GLFW_KEY_1)) {
            if (released) {
                spell = Spell.determineSpell("agility");
                spell.castSpell(new Object[]{maze});
                released = false;
            }
        } else {
            released = true;
        }

        // handle spell countdown (can make array or something later for more spells)
        if (spell != null) {
            if (spell instanceof SpellAgility) {
                ((SpellAgility) spell).countDown();
            }
        }
    }

    /**
     * reduces the cooldown left on moving by one
     */
    void decrementMovementCounter() {
        movementCounter--;
    }

    /**
     * returns the cooldown left on moving
     *
     * @return {@code movementCounter}
     */
    int getMovementCounter() {
        return movementCounter;
    }

    /**
     * returns the speed with which the player is currently moving
     *
     * @return {@code speed}
     */
    float getSpeed() {
        return speed;
    }

    /**
     * returns whether the current movement is vertical or not
     *
     * @return {@code vertical}
     */
    boolean isVertical() {
        return vertical;
    }
}
