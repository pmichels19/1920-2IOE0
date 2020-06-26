package Levels.Characters;

import Graphics.OBJLoader;
import Graphics.OBJModel;
import Graphics.OpenGL.Shader;
import Graphics.OpenGL.Texture;
import Levels.Assets.Items.Item;
import Levels.Framework.Maze;
import Levels.Framework.Point;
import Levels.Framework.joml.Matrix4f;
import Levels.Framework.joml.Vector2f;
import Levels.Framework.joml.Vector3f;


import static Levels.Framework.Maze.*;

import Levels.Objects.FloatingSphere;


import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Player extends Character {
    // Holds the floating objects
    private FloatingSphere[] floatingElements;

    // Name of the obj file in the res folder corresponding to the player model
    private static String objModelFile = "character";

    // Path to the texture of the model
    private static String textureFile = "res/Models/eyeball.jpg";

    // Path to the normal mapping of the model
//    private static String normalMapFile = "res/Models/eyeball_normal.jpg";
    private static String normalMapFile = null;

    private static Player player;

    Vector3f DARK_ATTENUATION = new Vector3f(.5f, .2f, 1.5f);
    Vector3f STANDARD_ATTENUATION = new Vector3f(.5f, .2f, .5f);
    Vector3f LIGHT_ATTENUATION = new Vector3f(.5f, .2f, .2f);
    Vector3f currentAttenuation = STANDARD_ATTENUATION;

    private boolean hasGuide = false;
    private boolean hasShield = false;
    private boolean shotFireball = false;
    private int invisiblility = 0;

    // a list of items the player has collected so far
    private Item[] inventory;
    List<Point> collected_items;
    List<Point> opened_doors;
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
        opened_doors = new ArrayList<>();
        selectedItem = 0;

        setAnimationType("float");

        floatingElements = new FloatingSphere[2];
        floatingElements[0] = new FloatingSphere();
        floatingElements[1] = new FloatingSphere();

        floatingElements[0].setColor(new Vector3f(0f, 0.96f, 1f));
        floatingElements[0].setRotation(-90f, new Vector3f(0, 0, 1));
        floatingElements[0].setHeight(1.3f);
        floatingElements[0].setBezierCoords(new Vector2f(2f, 1.1f), new Vector2f(5.5f, 3f),
                new Vector2f(0f, 3f), new Vector2f(3.5f, 0.95f));
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
                player.setPosition( new Vector3f(0, 0f, 0.1f));
                player.setScale(1.3f);
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

    @Override
    public void render(Shader shader) {
        //floating spheres
        for (FloatingSphere a : floatingElements) {
            a.render(shader, super.getPosition());
        }

        shader.setUniform("playerCharacter", 1);
        super.render(shader);
        shader.setUniform("playerCharacter", 0);
    }

    public Item[] getInventory() {
        return inventory;
    }

    public void setInventory(Item[] inventory) {
        this.inventory = inventory;
    }

    /**
     * method that checks if the player is in possession of a key
     *
     * @return whether {@code inventory} contains a key item
     */
    public boolean hasKey() {
        for (Item item : inventory) {
            if (item != null && item.getId() == Item.KEY) {
                return true;
            }
        }

        return false;
    }

    public void useKey(Point point) {
        opened_doors.add(point);
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null && inventory[i].getId() == Item.KEY) {
                player.tossItem(i);
                return;
            }
        }
    }

    public List<Point> getOpenedDoors() {
        return opened_doors;
    }

    public void setOpenedDoors(List<Point> opened_doors) {
        this.opened_doors = opened_doors;
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

    public Vector3f getCurrentAttenuation() {
        return currentAttenuation;
    }

    public void setCurrentAttenuation(Vector3f currentAttenuation) {
        this.currentAttenuation = currentAttenuation;
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

        int modifier = (int) Math.round( 0.75 * (bootCount + agilityPower));

        return super.getSpeed() / modifier;
    }

    public int getDirection() {
        return direction;
    }

    /**
     * returns the index of the first open slot in the inventory, if no empty slot is found, it returns -1
     *
     * @return the first open index in {@code inventory}, -1 if no open slots exist
     */
    public int findFirstOpenSlot() {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null || inventory[i].getId() == Item.EMPTY) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Sets the item in the inventory at {@code selectedItem / findFirstOpenSlot()} to the provided item
     *
     * @param item the new item to add to the inventory
     * @param point the point that the item was located at in the maze
     */
    public void addItem(Item item, Point point) {
        int openSlot = findFirstOpenSlot();
        if ( openSlot == -1 ) {
            inventory[selectedItem] = item;
        } else {
            inventory[openSlot] = item;
        }
        collected_items.add(point);
    }

    /**
     * uses the selected item, if possible: the boot, key and empty item have no use
     *
     * @param slot the slot to use the item from
     */
    public void useItem(int slot) {
        // if there is no item in the desired slot yet, return and do nothing
        if (inventory[slot] == null) {
            return;
        }

        // we check what kind of item the player has selected
        switch (inventory[selectedItem].getId()) {
            case Item.H_POTION:
                // if a health potion is used, add 25 health to the current health
                if (getHealth() + 25 > getMaxHealth()) {
                    setHealth(getMaxHealth());
                } else {
                    setHealth(getHealth() + 25);
                }
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
                if (getHealth() + 25 > getMaxHealth()) {
                    setHealth(getMaxHealth());
                } else {
                    setHealth(getHealth() + 25);
                }
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

    public boolean hasGuide() {
        return hasGuide;
    }

    public boolean fireballShot() {
        return shotFireball;
    }

    public void setGuide(boolean guide) {
        this.hasGuide = guide;
    }

    public void setFireball(boolean shot) {
        this.shotFireball = shot;
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

    public void setInvisibility(int invisiblility) {
        this.invisiblility = invisiblility;
    }

    public int getInvisibility() {
        return this.invisiblility;
    }

    public boolean hasShield() {
        return hasShield;
    }

    public void setShield(boolean shield) {
        this.hasShield = shield;
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
