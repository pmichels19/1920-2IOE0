package Levels.Objects;

import Graphics.OpenGL.Shader;
import Graphics.OpenGL.Texture;
import Graphics.Transforming.Camera;
import Graphics.Transforming.Transform;
import Levels.Assets.Tiles.GUIElements;

public class Door {
    // the current door status
    private boolean closed;
    // whether the door is vertical '|' or horizontal '-'
    private boolean vertical;
    // the door coordinates in the maze
    private final int x;
    private final int y;
    // the texture of the door
    private static final Texture TEXTURE = GUIElements.BACKGROUND.getTexture();
    // it takes 15 frames to open/close a door
    private static final int toggleFrames = 45;

    private int offset;
    private int delta;

    /**
     * creates a new, closed door
     */
    public Door(boolean vertical, int x, int y) {
        this.vertical = vertical;
        this.x = x;
        this.y = y;
        // if the door is open, start of with angle == toggleFrames, else angle == 0
        offset = toggleFrames;
        // delta always starts at 0
        delta = 0;
        closed = true;
    }

    public static int getToggleFrames() {
        return toggleFrames;
    }

    /**
     * method that starts off opening or closing a door depending on the current state:
     * if the door is open, it will close and vice versa
     *
     * This method will not work if the door is currently opening or closing
     *
     * Note that this method does not actually change the door state, this happens after the animation of opening
     * or closing has been completed
     */
    public void toggle() {
        if ( offset != toggleFrames && offset != 0) {
            return;
        }
        delta = 1;
    }

    /**
     * returns the offset that is to be applied to the door
     *
     * @return {@code offset}
     */
    public int getOffset() {
        offset -= delta;
        if (offset == 0) {
            delta = 0;
            closed = false;
        }

        return offset;
    }

    public boolean isVertical() {
        return vertical;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isOpen() {
        return !closed;
    }

    public static Texture getTexture() {
        return TEXTURE;
    }
}
