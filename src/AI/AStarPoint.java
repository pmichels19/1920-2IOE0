package AI;

import java.awt.*;

public class AStarPoint extends Point {
    public int x;
    public int y;
    public int f;
    private int g = 0;
    private int h = 0;
    public AStarPoint parent;

    /**
     * @param x      x value
     * @param y      y value
     * @param g      g value
     * @param h      h value
     * @param parent parent point
     */
    AStarPoint(int x, int y, int g, int h, AStarPoint parent) {
        this.x = x;
        this.y = y;
        this.g = g;
        this.h = h;
        this.f = this.g + this.h;
        this.parent = parent;
    }

    AStarPoint() {
    }

    /**
     * When you set g it should automatically update f
     *
     * @param g the g value to set
     */
    public void setG(int g) {
        this.g = g;
        this.f = this.g + this.h;
    }

    /**
     * Returns the g value of a point
     *
     * @return the g value to be returned
     */
    public int getG() {
        return this.g;
    }

    /**
     * When you set h it should automatically update f
     *
     * @param h the h value to set
     */
    public void setH(int h) {
        this.h = h;
        this.f = this.g + this.h;
    }
}
