package Graphics.Generators;

import Graphics.Model;
import Levels.Framework.Maze;
import Main.Point;
import Graphics.Renderer;

import java.util.Collection;

/**
 * abstract class for generating {@code Model} objects specified in subclasses
 */
abstract class Generator {
    final float BLOCK_WIDTH = Renderer.BLOCK_WIDTH;

    final float[] TEXTURES = new float[] {
            0, 1,
            1, 1,
            1, 0,
            0, 0
    };

    final Collection<Point> points;
    final Point playerLocation;

    final char[][] grid;

    public Generator(Maze maze, Collection<Point> points) {
        this.points = points;
        playerLocation = maze.getPlayerLocation();
        grid = maze.getGrid();
    }

    public abstract Model generate();
}
