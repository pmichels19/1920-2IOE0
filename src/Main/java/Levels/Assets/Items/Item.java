package Levels.Assets.Items;

import Graphics.OpenGL.Texture;
import Levels.Framework.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Item {
    // the id assigned to an item, this will be its index in the items array
    public static int base_id = 0;

    // the item id will specify the location in the items array
    private static final List<Item> items = new ArrayList<>();
    private static final List<Vector3f> colors = new ArrayList<>();

    public static final int EMPTY = 0;
    private static final Item emptyItem = new Item(
            "src/Main/java/Textures/GUIElements/item_background.png",
            null
    );

    public static final int HEART = 1;
    private static final Item heart = new Item(
            "src/Main/java/Textures/Items/heart.png",
            new Vector3f(1, 0, 0)               // RED
    );

    public static final int MANA = 2;
    private static final Item mana = new Item(
            "src/Main/java/Textures/Items/mana.png",
            new Vector3f(0, 0, 0.75f)               // BLUE
    );

    public static final int BOOT = 3;
    private static final Item boot = new Item(
            "src/Main/java/Textures/Items/boot.png",
            new Vector3f(0, 0.5f, 0)               // GREEN
    );

    public static final int COIN = 4;
    private static final Item coin = new Item(
            "src/Main/java/Textures/Items/coin.png",
            new Vector3f(1, 1, 0)               // YELLOW
    );

    public static final int H_POTION = 5;
    private static final Item hPot = new Item(
            "src/Main/java/Textures/Items/health_potion.png",
            new Vector3f(1, 0, 1)                //PINK
    );

    public static final int M_POTION = 6;
    private static final Item mPot = new Item(
            "src/Main/java/Textures/Items/mana_potion.png",
            new Vector3f(0, 1, 1)                // LIGHTBLUE
    );

    private final int id;
    private final Texture texture;

    Item(String filePath, Vector3f color) {
        // set the id and increment the base id
        id =  base_id;
        base_id++;

        // set the texture for this item
        this.texture = new Texture(filePath);

        // put the item in the items array
        items.add(this);
        colors.add(color);
    }

    /**
     * returns the item linked to the specified id
     *
     * @param id the id of the item in the {@code items} list
     * @return {@code items.get(id)}
     */
    public static Item getItemById(int id) {
        if (id < 0 || id >= items.size()) {
            throw new IllegalArgumentException("no item with id: " + id);
        }

        return items.get(id);
    }

    /**
     * returns the color of the item orb linked to the specified id
     *
     * @param id the id of the item in the {@code items} list
     * @return {@code colors.get(id)}
     */
    public static Vector3f getColorById(int id) {
        if (id < 0 || id >= colors.size()) {
            throw new IllegalArgumentException("no item with id: " + id);
        }

        return colors.get(id);
    }

    public int getId() {
        return id;
    }

    public Texture getTexture() {
        return texture;
    }
}
