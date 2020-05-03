package Levels.Tiles;

import Graphics.Texture;

/**
 * Enum that holds data for background tiles, these have a z-index of 0
 */
public enum Background {
    BASIC (
            "src/Textures/Background/background.jpg",
            7
    );

    private final Texture texture;
    private final int sampler;

    Background(String filePath, int sampler) {
        this.texture = new Texture(filePath);
        this.sampler = sampler;
    }

    public Texture getTexture() {
        return texture;
    }

    public void bindTexture() {
        texture.bind(sampler);
    }

    public int getSampler() {
        return sampler;
    }
}
