package Graphics.Generators;

import Graphics.Model;
import Levels.Framework.Maze;
import Main.Point;

import java.util.Collection;

public class CeilingGenerator extends Generator {
    public CeilingGenerator(Maze maze, Collection<Point> points) {
        super(maze, points);
    }

    @Override
    public Model generate() {
        float[] ceilings_total = new float[points.size() * 12];
        float[] ceil_textures = new float[points.size() * 8];
        int i = 0;

        for (Point point : points) {
            final int x_point = point.getX();
            final int y_point = point.getY();

            // use the point to get the vertices so we can draw the wall tile
            float x = (x_point - playerLocation.getX()) * 2.0f;
            float y = (y_point - playerLocation.getY()) * 2.0f;

            final float[] ceiling = new float[]{
                    // TOP LEFT
                    (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                    // TOP RIGHT
                    (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                    // BOTTOM RIGHT
                    (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                    // BOTTOM LEFT
                    (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH
            };

            System.arraycopy( ceiling, 0, ceilings_total, i * ceiling.length, ceiling.length );
            System.arraycopy( TEXTURES, 0, ceil_textures, i * TEXTURES.length, TEXTURES.length );
            i++;
        }

        return new Model(ceilings_total, ceil_textures);
    }
}
