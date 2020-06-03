package Levels.Assets.Items;

import Graphics.OpenGL.Texture;

public class Item {
    // the id assigned to an item, this will be its index in the items array
    public static int base_id = 0;

    // the item id will specify the location in the items array
    private static final Item[] items = new Item[10];

    private final int id;
    private final Texture texture;

    Item(String fileName) {
        // set the id and increment the base id
        id =  base_id;
        base_id++;
        // set the texture for this item
        this.texture = new Texture("src/Main/java/Textures/Items/" + fileName + ".png");
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
