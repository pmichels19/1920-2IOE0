package Levels.Characters;

import Graphics.OBJLoader;
import Graphics.OBJModel;
import Graphics.OpenGL.Texture;
import Levels.Assets.Items.Item;
import Levels.Framework.Point;

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
     * Sets the item in the inventory at {@code selectedItem} to the provided item
     *
     * @param item the new item to add to the inventory
     * @param point the point that the item was located at in the maze
     */
    public void addItem(Item item, Point point) {
        inventory[selectedItem] = item;
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
}
