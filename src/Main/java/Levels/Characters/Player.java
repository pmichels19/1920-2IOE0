package Levels.Characters;

import Graphics.OBJLoader;
import Graphics.OBJModel;
import Graphics.OpenGL.Model;
import Graphics.OpenGL.Texture;
import Levels.Assets.Items.EmptyItem;
import Levels.Assets.Items.Item;

import java.io.File;
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

    // a list of items the player has collected so far
    private Item[] inventory;
    private int selectedItem = 0;

    // the max health and mana of the player
    private int max_health = 100;
    private int max_mana = 100;

    private int current_health;
    private int current_mana;

    private Player(int hp, int mp, OBJModel model) {
        super(hp,mp,model);

        // start with an empty inventory of max size 5
        inventory = new Item[] {
                new EmptyItem(),
                new EmptyItem(),
                new EmptyItem(),
                new EmptyItem(),
                new EmptyItem()
        };

    }




    public static Player getInstance() {
        if (player == null) {
            try {
                // Create the model
                OBJModel model =  OBJLoader.loadObjModel(objModelFile);

                if (textureFile != null) {
                    // Set the texture
                    model.setTexture(new Texture(textureFile));
                }

                // Add a normal map if there is one
                if (normalMapFile != null) {
                model.setNormalMap(new Texture(normalMapFile));
                }

                // Instantiate the player
                player = new Player(100, 100,model);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return player;
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

    public int getCurrentHealth() {
        return current_health;
    }

    public void setCurrentHealth(int current_health) {
        this.current_health = current_health;
    }

    public int getCurrentMana() {
        return current_mana;
    }

    public void setCurrentMana(int current_mana) {
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
}
