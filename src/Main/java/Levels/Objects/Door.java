package Levels.Objects;

import Graphics.OpenGL.Texture;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.PI;

public class Door {
    // the current door status
    private boolean open;
    // whether the door is vertical ( | ) or horizontal ( -- ) in its closed state
    private boolean vertical;
    // the door coordinates in the maze
    private final int x;
    private final int y;
    // the texture of the door
    private static final Texture TEXTURE = new Texture("src/Main/java/Textures/GUIElements/item_background.png");
    // it takes 15 frames to open/close a door
    private static final int toggleFrames = 15;

    private int angle;
    private int delta_angle;

    /**
     * creates a new, closed door
     */
    public Door(boolean vertical, int x, int y) {
        this(false, vertical, x, y);
    }

    /**
     * creates a door with the status that is provided as parameter
     *
     * @param open the door status to start with
     */
    public Door(boolean open, boolean vertical, int x, int y) {
        this.open = open;
        this.x = x;
        this.y = y;
        this.vertical = vertical;
        // if the door is open, start of with angle == toggleFrames, else angle == 0
        angle = open ? toggleFrames : 0;
        // deltaAngle always starts at 0
        delta_angle = 0;
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
        if ( angle != 15 && angle != 0) {
            return;
        }
        // if the door is open, we need to close, so delta_angle will be set to -1
        delta_angle = open ? -1 : 1;
    }

    /**
     * method that will actually render the door
     */
    public void render() {
        // correct the angle based on delta angle
        angle += delta_angle;
        // if the door reaches either 0 or toggleFrames, change the door state and reset delta_angle to 0
        if (angle == toggleFrames || angle == 0) {
            open = !open;
            delta_angle = 0;
        }
    }

    /**
     * returns the x coordinate of the right / bottom door
     *
     * @return {@code x - cos( getDoorAngle() )}
     */
    private double getRightRenderX() {
        if (!vertical) {
            return ( (double) x) + 1.0 - ( cos( getHorizontalDoorAngle() ) / 2.0 );
        } else {
            return ( (double) x) - ( -cos( getVerticalDoorAngle() ) / 2.0 );
        }
    }

    /**
     * returns the x coordinate of the left / top door
     *
     * @return {@code tba}
     */
    private double getLeftRenderX() {
        if (!vertical) {
            return ( (double) x) - 1.0 + ( cos( getHorizontalDoorAngle() ) / 2.0 );
        } else {
            return ( (double) x) - ( -cos( getVerticalDoorAngle() ) / 2.0 );
        }
    }

    /**
     * returns the y coordinate of the right / bottom door
     *
     * @return {@code sin( getDoorAngle() )}
     */
    private double getRightRenderY() {
        if (!vertical) {
            return ((double) y) + ( sin( getHorizontalDoorAngle() ) / 2.0 );
        } else {
            return ( (double) y ) - 1.0 - ( sin( getVerticalDoorAngle() ) / 2.0 );
        }
    }

    /**
     * returns the y coordinate of the left / top door
     *
     * @return {@code tba}
     */
    private double getLeftRenderY() {
        if (!vertical) {
            return ((double) y) + ( sin( getHorizontalDoorAngle() ) / 2.0 );
        } else {
            return ( (double) y ) + 1.0 - ( sin( getVerticalDoorAngle() ) / 2.0 );
        }
    }

    /**
     * returns the angle to render the door at in radians
     *
     * @return {@code ( angle * PI) / ( toggleFrames * 2);}
     */
    private double getHorizontalDoorAngle() {
        return ( ( (double) angle ) * PI) / ( ( (double) toggleFrames ) * 2.0);
    }

    /**
     * returns the angle to render the door at in radians
     *
     * @return {@code ( angle * PI) / ( toggleFrames * 2);}
     */
    private double getVerticalDoorAngle() {
        return ( ( (double) toggleFrames - angle ) * PI) / ( ( (double) toggleFrames ) * 2.0);
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
