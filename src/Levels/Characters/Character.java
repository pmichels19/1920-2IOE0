package Levels.Characters;

import Graphics.OpenGL.Model;
import Graphics.OpenGL.Shader;
import Graphics.OpenGL.Texture;

public abstract class Character {
    Model model;
    Texture texture;

    // the values for max health and mana
    int max_health;
    int max_mana;

    // the values for current health and mana
    int cur_health;
    int cur_mana;

    public Character(Texture texture, int max_health, int max_mana) {
        this.texture = texture;

        // start the player off fresh, with full mana and health
        this.max_health = max_health;
        this.max_mana = max_mana;
        cur_health = max_health;
        cur_mana = max_mana;
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

    public int getHealth() {
        return cur_health;
    }

    public int getMana() {
        return cur_mana;
    }

    public void setHealth(int health) {
        cur_health = health;
    }

    public void setMana(int mana) {
        cur_mana = mana;
    }
}
