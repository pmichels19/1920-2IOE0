package Levels.Assets.Tiles;

import Graphics.OpenGL.Texture;

public enum GUIElements {
    MANA (
            "mana_color.png"
    ),
    HEALTH (
            "red_color.png"
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
