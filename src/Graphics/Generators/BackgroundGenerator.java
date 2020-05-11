package Graphics.Generators;

import Graphics.Model;
import Levels.Framework.Maze;
import Main.Point;

import java.util.Collection;

public class BackgroundGenerator extends Generator {
    public BackgroundGenerator(Maze maze, Collection<Point> points) {
        super(maze, points);
    }

    @Override
    public Model generate() {
        float[] vertices_total = new float[points.size() * 12];
        float[] textures_total = new float[points.size() * 8];
        int i = 0;

        for (Point point : points) {
            // use the point to get the vertices so we can draw the wall tile
            float x = (point.getX() - playerLocation.getX()) * 2.0f;
            float y = (point.getY() - playerLocation.getY()) * 2.0f;
            final float[] vertices = new float[]{
                    // TOP LEFT
                    (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                    // TOP RIGHT
                    (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                    // BOTTOM RIGHT
                    (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
                    // BOTTOM LEFT
                    (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
            };

            System.arraycopy(vertices, 0, vertices_total, i * vertices.length, vertices.length);
            System.arraycopy(TEXTURES, 0, textures_total, i * TEXTURES.length, TEXTURES.length);
            i++;
        }

        return new Model(vertices_total, textures_total);
    }
}
