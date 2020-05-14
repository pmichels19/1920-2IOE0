package Levels.Assets.Tiles;

import Graphics.Texture;

/**
 * Enum that holds data for wall tiles, these have a z-index of 1
 */
public enum Decoration {
    TABLE (
            false,
            "src/Textures/Decoration/table.png",
            2
    ),
    TORCH (
            true,
            "src/Textures/Decoration/torch.jpg",
            3
    ),
    CHEST (
            false,
            "src/Textures/Decoration/chest.jpg",
            4
    ),
    DOOR (
            false,
            "src/Textures/Decoration/door.jpg",
            5
    ),
    CAMPFIRE (
            true,
            "src/Textures/Decoration/campfire.jpg",
            6
    );

    private final boolean lightSource;
    private final Texture texture;
    private final int sampler;

    Decoration(boolean lightSource, String filePath, int sampler) {
        this.lightSource = lightSource;
        this.texture = new Texture(filePath);
        this.sampler = sampler;
    }

    public Texture getTexture() {
        return texture;
    }

    public void bindTexture() {
        texture.bind(sampler);
    }

    public boolean isLightSource() {
        return lightSource;
    }

    public int getSampler() {
        return sampler;
    }
}
