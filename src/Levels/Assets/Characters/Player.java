package Levels.Assets.Characters;

import Graphics.Model;
import Graphics.Texture;

import static Graphics.Renderer.BLOCK_WIDTH;
import static Graphics.Renderer.textures;

public class Player extends Character {
    public Player(Texture texture) {
        super(texture);
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
}
