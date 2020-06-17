package Levels.Objects;

import Graphics.OpenGL.Texture;

public class Door {
    // the current door status
    private boolean open;
    // the texture of the door
    private static final Texture TEXTURE = new Texture("src/Main/java/Textures/GUIElements/item_background.png");
    // it takes 15 frames to open/close a door
    private static final int toggleFrames = 15;

    /**
     * creates a new, closed door
     */
    public Door() {
        this(false);
    }

    /**
     * creates a door with the status that is provided as parameter
     *
     * @param open the door status to start with
     */
    public Door(boolean open) {
        this.open = open;
    }

    /**
     * method that starts off opening or closing a door depending on the current state:
     * if the door is open, it will close and vice versa
     */
    public void toggle() {

    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public static Texture getTexture() {
        return TEXTURE;
    }
}
