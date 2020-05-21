package Levels.Characters;

import Graphics.OpenGL.Model;
import Graphics.OpenGL.Texture;
import Levels.Assets.Items.Item;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private static Player player;

    // a list of items the player has collected so far
    private final Item[] inventory;
    private int selectedItem = 0;

    // the max health and mana of the player
    private final int max_health = 100;
    private final int max_mana = 100;

    private int current_health;
    private int current_mana;

    private Player() {
        // start with an empty inventory of max size 5
        inventory = new Item[5];
        // player starts at max health and mana
        current_health = max_health;
        current_mana = max_mana;
    }

    public static Player getInstance() {
        if (player == null) {
            player = new Player();
        }
        return player;
    }

    public Item[] getInventory() {
        return inventory;
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

    public int getMaxMana() {
        return max_mana;
    }

    public int getCurrentHealth() {
        return current_health;
    }

    public int getCurrentMana() {
        return current_mana;
    }

    /**
     * changes the current health of the player by {@code health}
     *
     * @param health the change of health to apply to the player
     */
    public void changeHealth(int health) {
        current_health += health;

        // correct the current health variable to be in between 0 and max health
        if ( current_health > max_health ) {
            current_health = max_health;
        }
        if ( current_health < 0 ) {
            current_health = 0;
        }
    }

    /**
     * changes the current mana of the player by {@code health}
     *
     * @param mana the change in mana to apply to the player
     */
    public void changeMana(int mana) {
        current_mana += mana;

        // correct the current mana variable to be in between 0 and max mana
        if ( current_mana > max_mana ) {
            current_mana = max_mana;
        }
        if ( current_mana < 0 ) {
            current_mana = 0;
        }
    }

    /**
     * Sets the item in the inventory at {@code selectedItem} to the provided item
     *
     * @param item the new item to add to the inventory
     */
    public void addItem(Item item) {
        inventory[selectedItem] = item;
    }
}
