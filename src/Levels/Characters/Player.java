package Levels.Characters;

import Graphics.OpenGL.Model;
import Graphics.OpenGL.Texture;
import Levels.Assets.Items.Item;

import java.util.ArrayList;
import java.util.List;

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
                x_pos, y_pos + 1.0f, 0.0f,
                // BOTTOM RIGHT
                x_pos, y_pos - 1.0f, 0.0f,
                // BOTTOM LEFT
                x_pos, y_pos - 1.0f, 2,
                // TOP LEFT
                x_pos, y_pos + 1.0f, 2,
        };

        model = new Model(
                player,
                new float[] {
                    0, 1,
                    1, 1,
                    1, 0,
                    0, 0
                }
        );
    }

    public void addItem(Item item ) {
        inventory.add( item );
    }
}
