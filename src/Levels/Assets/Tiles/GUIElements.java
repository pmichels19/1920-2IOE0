package Levels.Assets.Tiles;

import Graphics.OpenGL.Texture;

public enum GUIElements {
    MANA (
            "mana_color.png"
    ),
    MISSING_MANA (
            "missing_mana_color.png"
    ),
    HEALTH (
            "health_color.png"
    ),
    MISSING_HEALTH (
            "missing_health_color.png"
    ),
    ITEM_BACKGROUND (
            "item_background.png"
    ),
    BACKGROUND (
            "GUI_background.png"
    );

    private final Texture texture;

    GUIElements(String fileName) {
        texture = new Texture( "src/Textures/GUIElements/" + fileName );
    }

    public Texture getTexture() {
        return texture;
    }
}
