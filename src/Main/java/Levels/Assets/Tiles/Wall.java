package Levels.Assets.Tiles;

import Graphics.OpenGL.Texture;

/**
 * Enum that holds data for wall tiles, these have a z-index of 2
 */
public enum Wall {
    WALL (
            "src/Main/java/Textures/Wall/wall.jpg"
    ),
    CASTLE_WALL (
            "src/Main/java/Textures/Wall/castle_wall.jpg"
    ),
    FENCE (
            "src/Main/java/Textures/Wall/fence.png"
    ),
    CEILING (
            "src/Main/java/Textures/Wall/wall_ceiling.png"
    ),
    BRICKWALL (
            "src/Main/java/Textures/Wall/brickwall.jpg"
    ),
    DOORWALL (
            "src/Main/java/Textures/Wall/doorwall.jpg"
    ),
    BRICKWALL_NORMAL (
            "src/Main/java/Textures/Wall/brickwall_normal.jpg"
    ),
    DOORWALL_NORMAL (
            "src/Main/java/Textures/Wall/doorwall_normal.jpg"
    );

    private final Texture texture;

    Wall(String filePath) {
        this.texture = new Texture(filePath);
    }

    public Texture getTexture() {
        return texture;
    }
}
