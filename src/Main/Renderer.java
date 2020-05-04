package Main;

import Graphics.Camera;
import Graphics.Model;
import Graphics.Shader;
import Levels.Framework.Maze;
import Levels.Framework.joml.Matrix4f;
import Levels.Framework.joml.Vector3f;
import Levels.Framework.joml.Vector4f;
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

    private final Shader SHADER = new Shader("testShader");

    private final float BLOCK_WIDTH = 1.0f / 6.0f;

    private final float[] textures = new float[] {
            0, 1,
            1, 1,
            1, 0,
            0, 0
    };

    private final Matrix4f SCALE = new Matrix4f().scale(256);

    public Renderer(Maze maze, int width, int height) {
        camera = new Camera(width, height);

        char[][] grid = maze.getGrid();

        backgrounds = new HashMap<>();
        decorations = new HashMap<>();
        walls = new HashMap<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                // fill the mappings according to the grid in the maze class
                if (grid[i][j] == 'x') {
                    walls.put(new Point(i, j), Wall.WALL);
                } else if (grid[i][j] == 'P') {
                    playerLocation = new Point(i, j);
                } else if (grid[i][j] == ' ') {
                    backgrounds.put(new Point(i, j), Background.BASIC);
                }
            }
        }
    }

    public Vector3f getPlayerLocation() {
        return new Vector3f(playerLocation.getX(), playerLocation.getY(), 0.0f);
    }

    public void render() {
        // before drawing, adjust the camera position to the player, so we dont bounce the screen around at 60 Hertz
//        camera.setPosition( getPlayerLocation() );
        SHADER.bind();

        for ( Map.Entry<Point, Background> entry : backgrounds.entrySet() ) {
            renderBackgrounds( entry.getKey(), entry.getValue() );
        }

//        for ( Map.Entry<Point, Decoration> entry : decorations.entrySet() ) {
//            renderDecorations( entry.getKey(), entry.getValue() );
//        }

        for ( Map.Entry<Point, Wall> entry : walls.entrySet() ) {
            renderWalls( entry.getKey(), entry.getValue() );
        }

        // set the camera projection
        SHADER.setUniform("projection", camera.getProjection().mul(SCALE));
    }

    private void renderBackgrounds(Point point, Background background) {
        // use the point to get the vertices so we can draw the wall tile
        float x = point.getX() * 2.0f;
        float y = point.getY() * 2.0f;
        float[] vertices = new float[] {
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

    private void renderWalls(Point point, Wall wall) {
        // use the point to get the vertices so we can draw the wall tile
        float x = point.getX() * 2.0f;
        float y = point.getY() * 2.0f;
        float[] vertices = new float[] {
                (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
                (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
        };

        Model model = new Model(vertices, textures);

        // get the texture ready for rendering
        SHADER.setUniform("sampler", wall.getSampler());
        wall.bindTexture();

        model.render();
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
