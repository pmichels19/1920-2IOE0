package Levels.Assets.Tiles;

import Graphics.OpenGL.Texture;

/**
 * Enum that holds data for wall tiles, these have a z-index of 2
 */
public enum Wall {
    WALL (
            "src/Textures/Wall/wall.jpg"
    ),
    CASTLE_WALL (
            "src/Textures/Wall/castle_wall.jpg"
    ),
    CEILING (
            "src/Textures/Wall/wall_ceiling.png"
    );

    private final Texture texture;

    Wall(String filePath) {
        this.texture = new Texture(filePath);
    }

    public Texture getTexture() {
        return texture;
    }
}
