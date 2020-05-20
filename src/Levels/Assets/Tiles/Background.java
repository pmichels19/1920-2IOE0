package Levels.Assets.Tiles;

import Graphics.OpenGL.Texture;

/**
 * Enum that holds data for background tiles, these have a z-index of 0
 */
public enum Background {
    BASIC (
            "src/Textures/Background/background.jpg"
    ),
    PLAYER (
            "src/Textures/Background/test_PC.png"
    ),
    TORCH (
            "src/Textures/Background/torch.png"
    );

    private final Texture texture;

    Background(String filePath) {
        this.texture = new Texture(filePath);
    }

    public Texture getTexture() {
        return texture;
    }
}
