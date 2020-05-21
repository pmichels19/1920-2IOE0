package Levels.Assets.Tiles;

import Graphics.OpenGL.Texture;

/**
 * Enum that holds data for wall tiles, these have a z-index of 1
 */
public enum Decoration {
    TABLE (
            false,
            "src/Textures/Decoration/table.png"
    ),
    TORCH (
            true,
            "src/Textures/Decoration/torch.jpg"
    ),
    CHEST (
            false,
            "src/Textures/Decoration/chest.jpg"
    ),
    DOOR (
            false,
            "src/Textures/Decoration/door.jpg"
    );

    private final boolean lightSource;
    private final Texture texture;

    Decoration(boolean lightSource, String filePath) {
        this.lightSource = lightSource;
        this.texture = new Texture(filePath);
    }

    public Texture getTexture() {
        return texture;
    }

    public boolean isLightSource() {
        return lightSource;
    }
}