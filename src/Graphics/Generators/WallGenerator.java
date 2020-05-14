package Graphics.Generators;

import Graphics.Model;
import Levels.Framework.Maze;
import Levels.Framework.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WallGenerator extends Generator {
    public WallGenerator(Maze maze, Collection<Point> points) {
        super(maze, points);
    }

    @Override
    public Model generate() {
        List<float[]> vertices_list = new ArrayList<>();

        for (Point point : points) {
            final int x_point = point.getX();
            final int y_point = point.getY();

            // use the point to get the vertices so we can draw the wall tile
            float x = (x_point - playerLocation.getX()) * 2.0f;
            float y = (y_point - playerLocation.getY()) * 2.0f;

            if (y_point > 0 && grid[x_point][y_point - 1] != Maze.MARKER_WALL) {
                // walls on the left
                final float[] vertices_left = new float[]{
                        // TOP LEFT
                        (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // TOP RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // BOTTOM RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
                        // BOTTOM LEFT
                        (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f
                };
                vertices_list.add(vertices_left);
            }

            if (y_point < grid.length - 1 && grid[x_point][y_point + 1] != Maze.MARKER_WALL) {
                // walls on the right
                final float[] vertices_right = new float[]{
                        // TOP LEFT
                        (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // TOP RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // BOTTOM RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                        // BOTTOM LEFT
                        (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f
                };
                vertices_list.add(vertices_right);
            }

            if (x_point < grid.length - 1 && grid[x_point + 1][y_point] != Maze.MARKER_WALL) {
                // walls on the face
                final float[] vertices_face = new float[]{
                        // BOTTOM LEFT
                        (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // TOP LEFT
                        (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // TOP RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                        // BOTTOM RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f
                };
                vertices_list.add(vertices_face);
            }
        }

        float[] vertices_total = new float[vertices_list.size() * 12];
        float[] wall_textures = new float[vertices_list.size() * 8];
        for (int i = 0; i < vertices_list.size(); i++) {
            System.arraycopy(
                    vertices_list.get(i), 0, vertices_total, i * 12, vertices_list.get(i).length
            );
            System.arraycopy(
                    TEXTURES, 0, wall_textures, i * TEXTURES.length, TEXTURES.length
            );
        }

        return new Model(vertices_total, wall_textures);
    }
}
