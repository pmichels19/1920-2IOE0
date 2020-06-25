package Main.Input;

import AI.ImageRecognition.RunDrawingCanvas;
import Levels.Assets.Items.Item;
import Levels.Framework.Maze;
import Levels.Framework.Point;
import Levels.Objects.Door;
import Main.GameState;
import SpellCasting.Spell;

import java.util.ArrayList;
import java.util.List;

import static Graphics.IO.ScreenShot.takeScreenShot;
import static Levels.Characters.Character.*;
import static Main.Main.getMaze;
import static Main.Main.setState;
import static org.lwjgl.glfw.GLFW.*;

/**
 * class that will check the inputs while the player is in game
 */
public class InGameController extends Controller {
    // inventory cooldown allows for control when switching up/down in the inventory
    private int inventoryCooldown = 0;
    private int castCooldown = 0;
    private int doorCooldown = 0;

    // the variables used for player movement
    int movementCounter = 0;
    float speed = 0;
    boolean vertical;

    // variables used for the drawing canvas
    private final RunDrawingCanvas drawingCanvas = new RunDrawingCanvas();
    private boolean stopped = false;

    private boolean released = true;

    private Spell spell;

    private List<Spell> activeSpells = new ArrayList<>();

    @Override
    void checkInputs() {
        // if the player died, we need to go into the DEAD state
        if (player.getHealth() <= 0) {
            setState(GameState.DEAD);
            return;
        }

        // check if the player has reached the exit to the maze
        if (maze.playerLocation.getX() == maze.endLocation.getX() && maze.playerLocation.getY() == maze.endLocation.getY()) {
            setState(GameState.VICTORY);
            return;
        }

        // handle spell countdown
        activeSpells.removeIf(Spell::checkDuration);

        // check if the player wants to pause/unpause the game
        if (pauseCooldown == 0) {
            if (window.buttonClicked(GLFW_KEY_ESCAPE)) {
                pauseCooldown = 10;

                // take a screenshot and place it into the saves folder
                takeScreenShot("Saves/lastSave.png");
                setState(GameState.PAUSED);
            }
        } else {
            pauseCooldown--;
        }

        // we only check for inventory inputs if allowed and not paused
        if (inventoryCooldown == 0) {
            checkInventory();
        } else {
            // if switching is still on cooldown, decrement the cooldown by one
            inventoryCooldown--;
        }

        // we only check for movement inputs if movement is allowed
        if (movementCounter == 0) {
            checkMovement();
        } else if (movementCounter > 0) {
            // move the player with the specified speed and direction
            world.movePlayer(speed, vertical);
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
        if (window.buttonClicked(GLFW_KEY_UP)) {
            player.setSelectedItem(player.getSelectedItem() - 1);
            inventoryCooldown = 5;
        } else if (window.buttonClicked(GLFW_KEY_DOWN)) {
            player.setSelectedItem(player.getSelectedItem() + 1);
            inventoryCooldown = 5;
        }

        if ( window.buttonClicked(GLFW_KEY_1) ) {
            player.useItem(0);
        }

        if ( window.buttonClicked(GLFW_KEY_2) ) {
            player.useItem(1);
        }

        if ( window.buttonClicked(GLFW_KEY_3) ) {
            player.useItem(2);
        }

        if ( window.buttonClicked(GLFW_KEY_4) ) {
            player.useItem(3);
        }

        if ( window.buttonClicked(GLFW_KEY_5) ) {
            player.useItem(4);
        }
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
        if (window.buttonClicked(GLFW_KEY_W)) {
            if (maze.canMoveUp()) {
                maze.moveUp();

                movementCounter = (int) player.getSpeed();
                speed = 1f / player.getSpeed();
                vertical = true;
            } else {
                player.turnUp();
            }
        } else if (window.buttonClicked(GLFW_KEY_A)) {
            if (maze.canMoveLeft()) {
                maze.moveLeft();

                movementCounter = (int) player.getSpeed();
                speed = -1f / player.getSpeed();
                vertical = false;
            } else {
                player.turnLeft();
            }
        } else if (window.buttonClicked(GLFW_KEY_S)) {
            if (maze.canMoveDown()) {
                maze.moveDown();

                movementCounter = (int) player.getSpeed();
                speed = -1f / player.getSpeed();
                vertical = true;
            } else {
                player.turnDown();
            }
        } else if (window.buttonClicked(GLFW_KEY_D)) {
            if (maze.canMoveRight()) {
                maze.moveRight();

                movementCounter = (int) player.getSpeed();
                speed = 1f / player.getSpeed();
                vertical = false;
            } else {
                player.turnRight();
            }
        } else if (window.buttonClicked(GLFW_KEY_SPACE)) {
            player.setGamePositionX(player.getMazePosition().getY());
            player.setGamePositionY(player.getMazePosition().getX(), maze.getGrid().length);
            player.attack(maze, world.getEnemyList());
        } // spell casting
        else if (window.buttonClicked(GLFW_KEY_O)) {
            if (castCooldown == 0) {
                if (released) {
                    spell = Spell.determineSpell("guide");
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
                    Spell spell = Spell.determineSpell("beast");
                    activeSpells.add(spell);
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

        // enter can be used to pick up items, or to open doors
        if (window.buttonClicked(GLFW_KEY_ENTER)) {
            tryItemCollect();
            if (player.hasKey()) {
                if (doorCooldown == 0) {
                    doorCooldown = 10;
                    tryDoorInteraction(false);
                }else{
                    doorCooldown--;
                }
            }
        }
    }

    /**
     * method that looks at the grid, the player position and the direction the player is facing
     * to see if an item can be picked up
     *
     * @throws IllegalStateException if the direction the player is facing does not correspond with any direction
     */
    private void tryItemCollect() throws IllegalStateException {
        // get the grid and the facing tile
        char[][] grid = maze.getGrid();
        char facing = getFacingTile();
        // get the player direction and place
        int direction = player.getDirection();
        int x_player = maze.getPlayerLocation().getX();
        int y_player = maze.getPlayerLocation().getY();
        // get the numerical value of the potential item
        int facingValue = Character.getNumericValue( facing );

        // if the facing character is actually an item, we can pick it up
        if ( Maze.ITEM_MARKERS.contains( facing ) ) {
            switch (direction) {
                case DIRECTION_LEFT:
                    player.addItem(Item.getItemById(facingValue), new Point(x_player, y_player - 1));
                    grid[x_player][y_player - 1] = Maze.MARKER_SPACE;
                    break;
                case DIRECTION_RIGHT:
                    player.addItem(Item.getItemById(facingValue), new Point(x_player, y_player + 1));
                    grid[x_player][y_player + 1] = Maze.MARKER_SPACE;
                    break;
                case DIRECTION_UP:
                    player.addItem(Item.getItemById(facingValue), new Point(x_player - 1, y_player));
                    grid[x_player - 1][y_player] = Maze.MARKER_SPACE;
                    break;
                case DIRECTION_DOWN:
                    player.addItem(Item.getItemById(facingValue), new Point(x_player + 1, y_player));
                    grid[x_player + 1][y_player] = Maze.MARKER_SPACE;
                    break;
            }
        }
    }

    public static void tryDoorInteraction(boolean casted) {
        // get the grid and the facing tile
        int grid_length = maze.getGrid().length;
        char facing = getFacingTile();
        // get the player direction and place
        int direction = player.getDirection();
        int y_player = maze.getPlayerLocation().getX();
        int x_player = maze.getPlayerLocation().getY();

        // if a door is already open, we want to close it
        Door door = null;
        Point point = null;
        if ( facing == Maze.MARKER_DOOR ) {
            switch (direction) {
                case DIRECTION_LEFT:
                    point = new Point( x_player - 1, grid_length - y_player);
                    break;
                case DIRECTION_RIGHT:
                    point = new Point( x_player + 1, grid_length - y_player);
                    break;
                case DIRECTION_UP:
                    point = new Point( x_player, grid_length - (y_player - 1));
                    break;
                case DIRECTION_DOWN:
                    point = new Point( x_player, grid_length - (y_player + 1));
                    break;
            }
        }

        door = world.getDoor( point );
        if (door == null || point == null) {
            return;
        }
        // only use a key if the player wanted to unlock the door with a key
        if (!casted) {
            player.useKey(point);
        }
        door.toggle();
    }

    /**
     * returns the grid tile the player is currently facing as a character
     *
     * @return the tile the player is facing
     */
    private static char getFacingTile() {
        // get the grid
        char[][] grid = maze.getGrid();
        int direction = player.getDirection();
        int x_player = maze.getPlayerLocation().getX();
        int y_player = maze.getPlayerLocation().getY();

        char facing;
        if (direction == DIRECTION_LEFT) {
            facing = grid[x_player][y_player - 1];
        } else if (direction == DIRECTION_RIGHT) {
            facing = grid[x_player][y_player + 1];
        } else if (direction == DIRECTION_UP) {
            facing = grid[x_player - 1][y_player];
        } else if (direction == DIRECTION_DOWN) {
            facing = grid[x_player + 1][y_player];
        } else {
            throw new IllegalStateException("the player is facing a non-existent direction somehow");
        }

        return facing;
    }
}
