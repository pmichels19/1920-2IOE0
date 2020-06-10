package Levels.Characters;

import Graphics.OBJLoader;
import Graphics.OBJModel;
import Graphics.OpenGL.Texture;
import Levels.Assets.Items.Item;

import java.io.IOException;

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

    // agility spell
    private double agilityPower = 0;

    private Player(int hp, int mp, int speed, OBJModel model) {
        super(hp, mp, speed, model);

        // start with an empty inventory of max size 5
        inventory = new Item[5];

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
     * Sets the item in the inventory at {@code selectedItem} to the provided item
     *
     * @param item the new item to add to the inventory
     */
    public void addItem(Item item) {
        inventory[selectedItem] = item;
    }

    /**
     * uses the selected item, if possible: the boot, coin and empty item have no use
     */
    public void useItem() {
        // we check what kind of item the player has selected
        switch (inventory[selectedItem].getId()) {
            case Item.H_POTION:
                // if a health potion is used, add 25 health to the current health
                setHealth(getHealth() + 25);
                tossItem();
                break;
            case Item.M_POTION:
                // if a mana potion is used, add 25 mana to the current mana
                setMana(getMana() + 25);
                tossItem();
                break;
            case Item.HEART:
                // if a heart is used, increase the max health and current health by 25
                setMaxHealth(getMaxHealth() + 25);
                setHealth(getHealth() + 25);
                tossItem();
                break;
            case Item.MANA:
                // if a heart is used, increase the max mana and current mana by 25
                setMaxMana(getMaxMana() + 25);
                setMana(getMana() + 25);
                tossItem();
                break;
        }
    }

    /**
     * removes the selected item from the player inventory and places the empty item back
     */
    public void tossItem() {
        // replace the consumed item with the empty item
        inventory[selectedItem] = Item.getItemById(Item.EMPTY);
    }

    public void setAgilityPower(double agilityPower) {
        this.agilityPower = agilityPower;
    }

    public double getAgilityPower() {
        return agilityPower;
    }
}
