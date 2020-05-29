package Levels.Characters;

import Graphics.OpenGL.Model;
import Graphics.OpenGL.Texture;
import Levels.Assets.Items.Item;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private static Player player;

    // a list of items the player has collected so far
    private Item[] inventory;
    private int selectedItem = 0;

    // the max health and mana of the player
    private int max_health = 100;
    private int max_mana = 100;

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

    public void setMaxHealth(int max_health) {
        this.max_health = max_health;
    }

    public int getMaxMana() {
        return max_mana;
    }

    public void setMaxMana(int max_mana) {
        this.max_mana = max_mana;
    }

    public int getCurrentHealth() {
        return current_health;
    }

    public void setCurrentHealth(int current_health) {
        this.current_health = current_health;
    }

    public int getCurrentMana() {
        return current_mana;
    }

    public void setCurrent_mana(int current_mana) {
        this.current_mana = current_mana;
    }

    /**
     * Sets the item in the inventory at {@code selectedItem} to the provided item
     *
     * @param item the new item to add to the inventory
     */
    public void addItem(Item item) {
        inventory[selectedItem] = item;
    }

    /**
     * loads data from the provided player_data file into the player object
     *
     * @param player_data the data to put into the player object
     */
    public void loadData(File player_data) {

    }
}
