package Levels.Assets.Characters;

import Graphics.Model;
import Graphics.Texture;
import Levels.Assets.Items.Item;

import java.util.ArrayList;
import java.util.List;

import static Graphics.Renderer.BLOCK_WIDTH;
import static Graphics.Renderer.textures;

public class Player extends Character {
    private final List<Item> inventory;

    public Player(Texture texture, int max_health, int max_mana) {
        // the player character (for now) has 100 base health and mana
        super(texture, max_health, max_mana);
        // start with an empty inventory
        inventory = new ArrayList<>();
    }

    @Override
    void generateModel(float x_pos, float y_pos) {
        final float[] player = new float[]{
                // TOP RIGHT
                x_pos * BLOCK_WIDTH, (y_pos + 1.0f) * BLOCK_WIDTH, 0.0f,
                // BOTTOM RIGHT
                x_pos * BLOCK_WIDTH, (y_pos - 1.0f) * BLOCK_WIDTH, 0.0f,
                // BOTTOM LEFT
                x_pos * BLOCK_WIDTH, (y_pos - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                // TOP LEFT
                x_pos * BLOCK_WIDTH, (y_pos + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
        };

        model = new Model(player, textures);
    }

    public void addItem(Item item ) {
        inventory.add( item );
    }
}
