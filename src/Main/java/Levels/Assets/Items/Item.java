package Levels.Assets.Items;

public abstract class Item {
    // the item id that can be specified in the subclass
    int id;

    // the item id will specify the location in the items array
    public static final Item[] items = new Item[10];

    public static Item getItemById(int id) {
        if (id < 0 || id >= items.length) {
            throw new IllegalArgumentException("no item with id: " + id);
        }
        return items[ id ];
    }

    public int getId() {
        return id;
    }
}
