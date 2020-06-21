package Levels.Characters;

import Graphics.OBJLoader;
import Graphics.OBJModel;
import Graphics.OpenGL.Texture;
import Levels.Assets.Items.Item;
import Levels.Framework.Maze;
import Levels.Framework.Point;

import static Levels.Framework.Maze.*;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Player extends Character {
    // Name of the obj file in the res folder corresponding to the player model
    private static String objModelFile = "character";

    // Path to the texture of the model
    private static String textureFile = "res/Models/eyeball.jpg";

    // Path to the normal mapping of the model
//    private static String normalMapFile = "res/Models/eyeball_normal.jpg";
    private static String normalMapFile = null;

    private static Player player;

    // an array of items the player has collected so far
    private Item[] inventory;
    List<Point> collected_items;
    private int selectedItem;

    // agility spell
    private double agilityPower = 0;

    // player attack damage
    private int playerAttackDamage = 25;

    private Enemy enemyToAttack = null;

    // Some states the enemy can be in to change behavior during states
    public final static int NORMAL_STATE = 0;
    public final static int ATTACKING_STATE = 1;
    public final static int RETURNING_STATE = 2;

    // State of the current enemy
    private int state = NORMAL_STATE;

    // Minimum amount of time between attacks (in milliseconds)
    private long attackDelay = 800;

    // Holds the last timestamp of last attack
    private long lastAttackTime = 0;

    // How big the steps are for the movement per frame of a attack
    private float attackMovementStep = 1f/super.getSpeed();

    private Player(int hp, int mp, int speed, OBJModel model) {
        super(hp, mp, speed, model);

        // start with an empty inventory of max size 5
        inventory = new Item[5];
        collected_items = new ArrayList<>();
        selectedItem = 0;
    }

    public static Player getInstance() {
        if (player == null) {
            try {
                // Create the model
                OBJModel model = OBJLoader.loadObjModel(objModelFile);

                if (textureFile != null) {
                    // Set the texture
                    model.setTexture(new Texture(textureFile));
                }

                // Add a normal map if there is one
                if (normalMapFile != null) {
                    model.setNormalMap(new Texture(normalMapFile));
                }

                // Instantiate the player
                player = new Player(100, 100, 12, model);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return player;
    }

    /**
     * resets the player to a null object
     */
    public static void resetPlayer() {
        player = null;
    }

    public Item[] getInventory() {
        return inventory;
    }

    public void setInventory(Item[] inventory) {
        this.inventory = inventory;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = (selectedItem + inventory.length) % inventory.length;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public int getMaxHealth() {
        return max_health;
    }

    public void setMaxHealth(int max_health) {
        this.max_health = max_health;
    }

    public int getMaxMana() {
        return max_mana;
    }

    public void setMaxMana(int max_mana) {
        this.max_mana = max_mana;
    }

    /**
     * the player class overrides the getspeed to take the boots in the inventory into account
     *
     * @return the speed of the player, with boots
     */
    @Override
    public float getSpeed() {
        // check the amount of boots the player gathered
        double bootCount = 1;
        for (Item item : inventory) {
            if (item != null && item.getId() == Item.BOOT) {
                bootCount++;
            }
        }

        int modifier = (int) Math.round(0.75 * (bootCount + agilityPower));

        return super.getSpeed() / modifier;
    }

    public int getDirection() {
        return direction;
    }

    /**
     * Sets the item in the inventory at {@code selectedItem} to the provided item
     *
     * @param item  the new item to add to the inventory
     * @param point the point that the item was located at in the maze
     */
    public void addItem(Item item, Point point) {
        inventory[selectedItem] = item;
        collected_items.add(point);
    }

    /**
     * uses the selected item, if possible: the boot, coin and empty item have no use
     *
     * @param slot the slot to use the item from
     */
    public void useItem(int slot) {
        // if there is no item in the desired slot yet, return and do nothing
        if (inventory[slot] == null) {
            return;
        }
        // we check what kind of item the player has selected
        switch (inventory[slot].getId()) {
            case Item.H_POTION:
                // if a health potion is used, add 25 health to the current health
                setHealth(getHealth() + 25);
                tossItem(slot);
                break;
            case Item.M_POTION:
                // if a mana potion is used, add 25 mana to the current mana
                setMana(getMana() + 25);
                tossItem(slot);
                break;
            case Item.HEART:
                // if a heart is used, increase the max health and current health by 25
                setMaxHealth(getMaxHealth() + 25);
                setHealth(getHealth() + 25);
                tossItem(slot);
                break;
            case Item.MANA:
                // if a heart is used, increase the max mana and current mana by 25
                setMaxMana(getMaxMana() + 25);
                setMana(getMana() + 25);
                tossItem(slot);
                break;
        }
    }

    /**
     * removes the specified item from the player inventory and places the empty item back
     *
     * @param slot the slot to remove the item from
     */
    public void tossItem(int slot) {
        // replace the consumed item with the empty item
        inventory[slot] = Item.getItemById(Item.EMPTY);
    }

    public void setAgilityPower(double agilityPower) {
        this.agilityPower = agilityPower;
    }

    public double getAgilityPower() {
        return agilityPower;
    }

    public void setCollected(List<Point> collected_items) {
        this.collected_items = collected_items;
    }

    public List<Point> getCollected() {
        return collected_items;
    }

    /**
     * Method that lets the player attack an enemy that is next to the player
     *
     * @param maze      current maze
     * @param enemyList list of enemies in game
     */
    public void attack(Maze maze, ArrayList<Enemy> enemyList) {
        if (canAttack() && state == NORMAL_STATE) {
            Point playerPos = maze.getPlayerLocation();
            for (Enemy enemy : enemyList) {
                if (playerPos.calculateManhattanDistance(enemy.getMazePosition()) == 1) {
                    state = ATTACKING_STATE;
                    enemyToAttack = enemy;
                    moveAttacking(maze.getGrid().length);
                    break;
                }
            }
        }
    }

    /**
     * Makes enemy attack the player
     *
     * @param gridLength Length of the grid array
     */
    public void moveAttacking(int gridLength) {
        if (state == ATTACKING_STATE) {
            // Get the current position in world coordinates
            float currentX = getGamePositionX();
            float currentY = getGamePositionY();


            // Set the desired X and Y, note that game positions have the X and Y flipped from the Maze positions
            float locationX = getMazePosition().getY() + ((enemyToAttack.getMazePosition().getY() - getMazePosition().getY()) / 2f);
            float locationY = getMazePosition().getX() + ((enemyToAttack.getMazePosition().getX() - getMazePosition().getX()) / 2f);

            // Initialize new positions
            float newX = currentX;
            float newY = currentY;

            // Change in x
            if (currentX < locationX) {
                newX += attackMovementStep;
            } else if (currentX > locationX) {
                newX -= attackMovementStep;
            }

            // Change in y
            if (currentY < locationY) {
                newY += attackMovementStep;
            } else if (currentY > locationY) {
                newY -= attackMovementStep;
            }


            // Fix differences between float and int, player has reached attack location
            if (Math.abs(locationX - newX) < attackMovementStep + 0.001 && Math.abs(locationX - newX) != 0f) {
                newX = locationX;

                enemyToAttack.damage(playerAttackDamage);
                lastAttackTime = System.currentTimeMillis();

                state = RETURNING_STATE;
            } else if (Math.abs(locationY - newY) < attackMovementStep + 0.001 && Math.abs(locationY - newY) != 0f) {
                newY = locationY;

                enemyToAttack.damage(playerAttackDamage);

                lastAttackTime = System.currentTimeMillis();
                state = RETURNING_STATE;
            }
            if (Math.abs(locationY - newY) == 0f && Math.abs(locationX - newX) == 0f) {

                enemyToAttack.damage(playerAttackDamage);
                lastAttackTime = System.currentTimeMillis();
                state = RETURNING_STATE;
            }
            // Updates the location
            setGamePositionAndRotate(newX, newY, gridLength);
        } else if (state == RETURNING_STATE) {
            // Get the current position in world coordinates
            float currentX = getGamePositionX();
            float currentY = getGamePositionY();

            // Set the desired X and Y, note that game positions have the X and Y flipped from the Maze positions
            float locationX = getMazePosition().getY();
            float locationY = getMazePosition().getX();

            // Initialize new positions
            float newX = currentX;
            float newY = currentY;

            // Change in x
            if (currentX < locationX) {
                newX += attackMovementStep;
            } else if (currentX > locationX) {
                newX -= attackMovementStep;
            }

            // Change in y
            if (currentY < locationY) {
                newY += attackMovementStep;
            } else if (currentY > locationY) {
                newY -= attackMovementStep;
            }


            // Fix differences between float and int, player has reached attack location
            if (Math.abs(locationX - newX) < attackMovementStep+ 0.001 && Math.abs(locationX - newX) != 0f) {
                newX = locationX;

                state = NORMAL_STATE;
            } else if (Math.abs(locationY - newY) < attackMovementStep+ 0.001 && Math.abs(locationY - newY) != 0f) {
                newY = locationY;

                state = NORMAL_STATE;
            }
            if (Math.abs(locationY - newY) == 0f && Math.abs(locationX - newX) == 0f) {

                state = NORMAL_STATE;
            }
            // Updates the location
            setGamePositionX(newX);
            setGamePositionY(newY, gridLength);
        }


    }

    private boolean canAttack() {
        return System.currentTimeMillis() > lastAttackTime + attackDelay;
    }

    public int getState() {
        return state;
    }

    public int getPlayerAttackDamage() {
        return playerAttackDamage;
    }

    public void setPlayerAttackDamage(int playerAttackDamage) {
        this.playerAttackDamage = playerAttackDamage;
    }
}
