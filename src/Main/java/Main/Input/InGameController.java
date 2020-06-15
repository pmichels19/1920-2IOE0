package Main.Input;

import AI.ImageRecognition.RunDrawingCanvas;
import Main.GameState;
import SpellCasting.Spell;
import SpellCasting.SpellAgility;
import SpellCasting.SpellIlluminate;

import static Graphics.IO.ScreenShot.takeScreenShot;
import static Main.Main.setState;
import static org.lwjgl.glfw.GLFW.*;

/**
 * class that will check the inputs while the player is in game
 */
public class InGameController extends Controller {
    // inventory cooldown allows for control when switching up/down in the inventory
    private int inventoryCooldown = 0;
    private int castCooldown = 0;

    // the variables used for player movement
    int movementCounter = 0;
    float speed = 0;
    boolean vertical;

    // variables used for the drawing canvas
    private final RunDrawingCanvas drawingCanvas = new RunDrawingCanvas();
    private boolean stopped = false;

    private boolean released = true;

    private Spell spell;

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

        if (window.buttonClicked( GLFW_KEY_ENTER )) {
            player.useItem();
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
            }
        } else if ( window.buttonClicked( GLFW_KEY_A ) ) {
            if ( maze.canMoveLeft() ) {
                maze.moveLeft();

                movementCounter = (int) player.getSpeed();
                speed = -1f / player.getSpeed();
                vertical = false;
            }
        } else if ( window.buttonClicked( GLFW_KEY_S ) ) {
            if ( maze.canMoveDown() ) {
                maze.moveDown();

                movementCounter = (int) player.getSpeed();
                speed = -1f / player.getSpeed();
                vertical = true;
            }
        } else if ( window.buttonClicked( GLFW_KEY_D ) ) {
            if ( maze.canMoveRight() ) {
                maze.moveRight();

                movementCounter = (int) player.getSpeed();
                speed = 1f / player.getSpeed();
                vertical = false;
            }
        }

        // spell casting
        else if (window.buttonClicked(GLFW_KEY_O)) {
            if (castCooldown == 0) {
                if (released) {
                    spell = Spell.determineSpell("illuminate");
                    spell.castSpell(new Object[]{maze});
                    released = false;
                    castCooldown = 20;
                }
            } else {
                if (castCooldown > 0) {
                    castCooldown--;
                }
                released = true;
            }
        } else if (window.buttonClicked(GLFW_KEY_P)) {
            if (castCooldown == 0) {
                if (released) {
                    spell = Spell.determineSpell("guide");
                    spell.castSpell(new Object[]{maze});
                    released = false;
                    castCooldown = 20;
                }
            }
        } else {
            if (castCooldown > 0) {
                castCooldown--;
            }
            released = true;
        }

        // handle spell countdown (can make array or something later for more spells)
        if (spell != null) {
            if (spell instanceof SpellAgility) {
                ((SpellAgility) spell).checkDuration();
            }
        }
    }
}
