package Levels.Tiles;

import Graphics.Texture;

/**
 * Enum that holds data for wall tiles, these have a z-index of 2
 */
public enum Wall {
    WALL (
            "src/Textures/Wall/wall.jpg",
            0
    ),
    CASTLE_WALL (
            "src/Textures/Wall/castle_wall.jpg",
            1
    ),
    CEILING (
            "src/Textures/Wall/wall_ceiling (1).png",
            9
    );

    private final Texture texture;
    private final int sampler;

    Wall(String filePath, int sampler) {
        this.texture = new Texture(filePath);
        this.sampler = sampler;
    }

    public void bindTexture() {
        texture.bind(sampler);
    }

    public int getSampler() {
        return sampler;
    }
}
