package Levels.Objects;

import AI.AStar.AStarSolver;
import Graphics.OBJLoader;
import Graphics.OBJModel;
import Graphics.OpenGL.Texture;
import Levels.Characters.Player;
import Levels.Framework.Point;
import Levels.Framework.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;

import static Levels.Framework.Maze.*;


public class GuideBall extends Object3D {
    // Name of the obj file in the res folder corresponding to the player model
    private static String objModelFile = "ball";

    // Path to the texture of the model
    private static String textureFile = "res/Models/nothing.png";

    private float speed = 1f / 30f;

    private Point mazePosition;

    public boolean isNew = true;

    // Path to the normal mapping of the model
//    private static String normalMapFile = "res/Models/eyeball_normal.jpg";
    private static String normalMapFile = null;

    private static GuideBall object;

    public GuideBall() {
        super(null);
        OBJModel model = null;
        try {
            // Create the model
            model = OBJLoader.loadObjModel(objModelFile);

            if (textureFile != null) {
                // Set the texture
                model.setTexture(new Texture(textureFile));
            }

            // Add a normal map if there is one
            if (normalMapFile != null) {
                model.setNormalMap(new Texture(normalMapFile));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        setScale(.5f);
        super.setModel(model);
    }

    /**
     * Moves the guide to an endpoint
     *
     * @param endPoint      Point to go to
     * @param grid          Grid that holds the maze
     */
    public void move(Point endPoint, char[][] grid) {
        if (!isMoving()) {
            AStarSolver ass = AStarSolver.getInstance();
            ArrayList<Point> pathToEnd = ass.CalculateShortestPath(mazePosition, endPoint, grid);
            if (pathToEnd != null) {
                mazePosition = pathToEnd.get(pathToEnd.size() - 1);
            } else {
                Player.getInstance().setGuide(false);
            }
        }
        reposition(grid.length);
    }

    /**
     * Updates the position of the player via steps
     *
     * @param gridLength length of the grid to update the y correctly
     */
    private void reposition(int gridLength) {
        // Get the current position in world coordinates
        float currentX = super.getGamePositionX();
        float currentY = super.getGamePositionY();


        // Set the desired X and Y, note that game positions have the X and Y flipped from the Maze positions
        float locationX = mazePosition.getY();
        float locationY = mazePosition.getX();

        // Initialize new positions
        float newX = currentX;
        float newY = currentY;

        // Change in x
        if (currentX < locationX) {
            newX += speed;
        } else if (currentX > locationX) {
            newX -= speed;
        }

        // Change in y
        if (currentY < locationY) {
            newY += speed;
        } else if (currentY > locationY) {
            newY -= speed;
        }


        // Fix differences between float and int
        if (Math.abs(locationX - newX) < speed + 0.01f) {
            newX = locationX;
        }
        if (Math.abs(locationY - newY) < speed + 0.01f) {
            newY = locationY;
        }

        // Updates the location
        setGridPosition(newX, newY, gridLength);

    }

    private boolean isMoving() {
        return !((getGamePositionX() == (mazePosition.getY()))
                && (getGamePositionY() == mazePosition.getX()));
    }

    public void initializePosition(int x, int y, int gridLength) {
        mazePosition = new Point(x, y);
        setGridPosition((float) y, (float) x, gridLength);
    }

    @Override
    public void setGridPosition(float gamePositionX, float gamePositionY, float gridLength) {
        if (gamePositionX > getGamePositionX()) {
            float rotationAngle = (float) ((0f * Math.PI) / 180.0f);
            Vector3f rotation = new Vector3f(0f, 0f, 1f);
            setRotation(rotationAngle, rotation);
        } else if (gamePositionX < getGamePositionX()) {
            float rotationAngle = (float) ((180f * Math.PI) / 180.0f);
            Vector3f rotation = new Vector3f(0f, 0f, 1f);
            setRotation(rotationAngle, rotation);
        }
        if (gamePositionY > getGamePositionY()) {
            float rotationAngle = (float) ((-90f * Math.PI) / 180.0f);
            Vector3f rotation = new Vector3f(0f, 0f, 1f);
            setRotation(rotationAngle, rotation);
        } else if (gamePositionY < getGamePositionY()) {
            float rotationAngle = (float) ((90f * Math.PI) / 180.0f);
            Vector3f rotation = new Vector3f(0f, 0f, 1f);
            setRotation(rotationAngle, rotation);
        }
        setGamePositionX(gamePositionX);
        setGridPositionY(gamePositionY, gridLength);
    }


}
