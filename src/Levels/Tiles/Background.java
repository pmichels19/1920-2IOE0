package Levels.Tiles;

import Graphics.Texture;

/**
 * Enum that holds data for background tiles, these have a z-index of 0
 */
public enum Background {
    BASIC (
            "src/Textures/Background/background.jpg"
    );

    private final Texture texture;

    Background(String filePath) {
        this.texture = new Texture(filePath);
    }

    public Texture getTexture() {
        return texture;
    }

    public void bindTexture() {
        texture.bind();
    }
}
