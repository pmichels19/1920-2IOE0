package Levels.Assets.Characters;

import Graphics.Model;
import Graphics.Shader;
import Graphics.Texture;

abstract class Character {
    Model model;
    Texture texture;

    public Character(Texture texture) {
        this.texture = texture;
    }

    abstract void generateModel(float x_pos, float y_pos);

    /**
     * renders the model specified in the subclass once
     */
    public void render(Shader shader, float x_pos, float y_pos) {
        generateModel(x_pos, y_pos);

        texture.bind(0);
        shader.setUniform("sampler", 0);

        model.render();
    }
}
