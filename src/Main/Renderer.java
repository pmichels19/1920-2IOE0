package Main;

import Graphics.Camera;
import Graphics.Model;
import Graphics.Shader;
import Graphics.Transform;
import Levels.Framework.Maze;
import Levels.Framework.joml.*;
import Levels.Tiles.Background;
import Levels.Tiles.Decoration;
import Levels.Tiles.Wall;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for rendering a maze
 */
public class Renderer {
    private Map<Point, Background> backgrounds;
    private Map<Point, Decoration> decorations;
    private Map<Point, Wall> walls;

    private Point playerLocation;

    private Camera camera;
    private Transform transform;

    private final Shader SHADER = new Shader("testShader");

    private final float BLOCK_WIDTH = 1.0f / 6.0f;

    private final float[] textures = new float[] {
            0, 1,
            1, 1,
            1, 0,
            0, 0
    };

    public Renderer(Maze maze, int width, int height) {
        // prepare the camera
        camera = new Camera();
        camera.setPerspective(
                (float) Math.toRadians(70.0),
                (float) width / (float) height,
                0.01f,
                1000.0f
        );
        camera.setPosition(new Vector3f(0, 0, 8));

        // prepare the transformations
        transform = new Transform();
        transform.getRotation().rotateAxis((float) Math.toRadians(180.0), 0, 0, 1);
        transform.getRotation().rotateAxis((float) Math.toRadians(45.0), 1, 0, 0);

        char[][] grid = maze.getGrid();

        backgrounds = new HashMap<>();
        decorations = new HashMap<>();
        walls = new HashMap<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                // fill the mappings according to the grid in the maze class
                if (grid[i][j] == 'x') {
                    walls.put(new Point(i, j), Wall.WALL);
                    continue;
                } else if (grid[i][j] == 'P') {
                    playerLocation = new Point(i, j);
                    backgrounds.put(playerLocation, Background.PLAYER);
                }

                // we always want a background if there is not a wall on a tile
                backgrounds.put(new Point(i, j), Background.BASIC);
            }
        }
    }

    public void render() {
        SHADER.bind();
        SHADER.setCamera(camera);
        SHADER.setTransform(transform);

        for ( Map.Entry<Point, Background> entry : backgrounds.entrySet() ) {
            renderBackgrounds( entry.getKey(), entry.getValue() );
        }

//        for ( Map.Entry<Point, Decoration> entry : decorations.entrySet() ) {
//            renderDecorations( entry.getKey(), entry.getValue() );
//        }

        int index = 0;
        Model[] ceilings = new Model[ walls.size() ];
        for ( Map.Entry<Point, Wall> entry : walls.entrySet() ) {
            ceilings[ index ] = renderWalls( entry.getKey(), entry.getValue() );
            index++;
        }

        // draw the ceilings
        SHADER.setUniform("sampler", Wall.CEILING.getSampler());
        Wall.CEILING.bindTexture();
        for (Model model : ceilings) {
            model.render();
        }
    }

    private void renderBackgrounds(Point point, Background background) {
        // use the point to get the vertices so we can draw the wall tile
        float x = ( point.getX() - playerLocation.getX() ) * 2.0f;
        float y = ( point.getY() - playerLocation.getY() ) * 2.0f;
        final float[] vertices = new float[] {
                (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
                (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
        };

        Model model = new Model(vertices, textures);

        // get the texture ready for rendering
        SHADER.setUniform("sampler", background.getSampler());
        background.bindTexture();

        model.render();
    }

    private void renderDecorations(Point point, Decoration decoration) {

    }

    private Model renderWalls(Point point, Wall wall) {
        // use the point to get the vertices so we can draw the wall tile
        float x = ( point.getX() - playerLocation.getX() ) * 2.0f;
        float y = ( point.getY() - playerLocation.getY() ) * 2.0f;

        final float[] vertices_left = new float[] {
                // TOP LEFT
                (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                // TOP RIGHT
                (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                // BOTTOM RIGHT
                (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
                // BOTTOM LEFT
                (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH
        };

        final float[] vertices_up = new float[] {
                // TOP LEFT
                (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                // TOP RIGHT
                (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                // BOTTOM RIGHT
                (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                // BOTTOM LEFT
                (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f
        };

        final float[] vertices_right = new float[] {
                // TOP LEFT
                (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                // TOP RIGHT
                (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                // BOTTOM RIGHT
                (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
                // BOTTOM LEFT
                (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH
        };

        final float[] ceiling = new float[] {
                // TOP LEFT
                (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                // TOP RIGHT
                (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                // BOTTOM RIGHT
                (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                // BOTTOM LEFT
                (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH
        };

        SHADER.setUniform("sampler", wall.getSampler());
        wall.bindTexture();

        // render the parts of the wall that can be visible
        new Model(vertices_left, textures).render();
        new Model(vertices_up, textures).render();
        new Model(vertices_right, textures).render();

        // and return the ceiling model
        return new Model(ceiling, textures);
    }
}

class Point {
    private int x;
    private int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
