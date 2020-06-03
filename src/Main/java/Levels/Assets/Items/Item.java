package Levels.Assets.Items;

import Graphics.OpenGL.Texture;

public class Item {
    // the id assigned to an item, this will be its index in the items array
    public static int base_id = 0;

    // the item id will specify the location in the items array
    private static final Item[] items = new Item[10];

    private static final Item emptyItem = new Item("src/Main/java/Textures/GUIElements/item_background.png");
    private static final Item heart = new Item("src/Main/java/Textures/Items/heart.png");
    private static final Item mana = new Item("src/Main/java/Textures/Items/heart.png");
    private static final Item boot = new Item("src/Main/java/Textures/Items/boot.png");
    private static final Item coin = new Item("src/Main/java/Textures/Items/coin.png");
    private static final Item hPot = new Item("src/Main/java/Textures/Items/health_potion.png");
    private static final Item mPot = new Item("src/Main/java/Textures/Items/mana_potion.png");

    private final int id;
    private final Texture texture;

    Item(String filePath) {
        // set the id and increment the base id
        id =  base_id;
        base_id++;
        // set the texture for this item
        this.texture = new Texture(filePath);
        // put the item in the items array
        items[id] = this;
    }

    public static Item getItemById(int id) {
        if (id < 0 || id >= items.length) {
            throw new IllegalArgumentException("no item with id: " + id);
        }

        return items[ id ];
    }

    public int getId() {
        return id;
    }

    public Texture getTexture() {
        return texture;
    }
}
