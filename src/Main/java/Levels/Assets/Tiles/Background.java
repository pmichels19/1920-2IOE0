package Levels.Assets.Tiles;

import Graphics.OpenGL.Texture;

/**
 * Enum that holds data for background tiles, these have a z-index of 0
 */
public enum Background {
    BASIC (
            "src/Main/java/Textures/Background/background.jpg"
    ),
    PLAYER (
            "src/Main/java/Textures/Background/test_PC.png"
    ),
    TORCH (
            "src/Main/java/Textures/Background/torch.png"
    ),
    NOTHING (
            "src/Main/java/Textures/Background/nothing.png"
    );

    private final Texture texture;

    Background(String filePath) {
        this.texture = new Texture(filePath);
    }

    public Texture getTexture() {
        return texture;
    }
}
