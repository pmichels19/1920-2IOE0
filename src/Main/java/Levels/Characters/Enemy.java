package Levels.Characters;

import AI.AStar.AStarSolver;
import Graphics.OBJModel;
import Levels.Framework.Maze;
import Levels.Framework.Point;

import java.util.ArrayList;

public abstract class Enemy extends Character {
    public Enemy(int max_health, int max_mana, OBJModel model) {
        super(max_health, max_mana, model);
    }

    // Holds the location the enemy is heading when the player is not close enough
    private Point randomLocation = null;

    // The distance that an enemy should be from the player to move towards the player
    private int detectionDistance = 10;


    /**
     * Moves the enemy, either to a random location or the player (if it is close enough)
     *
     * @param playerLocation Current location of the player
     * @param grid           Grid that holds the maze
     */
    public void move(Point playerLocation, char[][] grid) {
        if (!isMoving()) {
            ArrayList<Point> pathToPlayer = getPathToPlayer(playerLocation, grid);
            if (pathToPlayer.size() > detectionDistance) {
                // if the
                doRandomMove(grid);
            } else {
                // If player is in detection range, move towards the player
                moveToPoint(pathToPlayer.get(pathToPlayer.size() - 1), grid.length);
            }
        } else {
            // Updates the movement step if the enemy is already moving
            reposition(grid.length);
        }
    }

    /**
     * Sets the enemy to move to the next block from location
     *
     * @param location   the new location grid point that should be next to the current location of the enemy
     * @param gridLength length of the grid for updating the y correctly
     */
    private void moveToPoint(Point location, int gridLength) {
        // Set the new maze location of the enemy
        super.setMazePosition(location);

        // Start moving the player
        reposition(gridLength);

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
        float locationX = super.getMazePosition().getY();
        float locationY = super.getMazePosition().getX();

        // Initialize new positions
        float newX = currentX;
        float newY = currentY;

        // Change in x
        if (currentX < locationX) {
            newX += getSpeed();
        } else if (currentX > locationX) {
            newX -= getSpeed();
        }

        // Change in y
        if (currentY < locationY) {
            newY += getSpeed();
        } else if (currentY > locationY) {
            newY -= getSpeed();
        }


        // Fix differences between float and int
        if (Math.abs(locationX - newX) < getSpeed() + 0.01f) {
            newX = locationX;
        }
        if (Math.abs(locationY - newY) < getSpeed() + 0.01f) {
            newY = locationY;
        }

        // Updates the location
        setGridPosition(newX, newY, gridLength);

    }

    /**
     * Picks a random location for the enemy on the grid
     *
     * @param grid holds the current maze
     */
    private void doRandomMove(char[][] grid) {
        if (randomLocation == null) { // If no random location has been set
            setRandomLocation(grid);
        } else if ((getGamePositionY() == randomLocation.getX())
                && (getGamePositionX() == randomLocation.getY())){ // If it has reached the set location
            randomLocation = null;
        } else { // if not reached the random location
            // Get next step towards new location
            AStarSolver ass = new AStarSolver();
            ArrayList<Point> nextPoint = ass.CalculateShortestPath(getMazePosition(), randomLocation, grid);
            moveToPoint(nextPoint.get(nextPoint.size()-1), grid.length);
        }
    }

    /**
     * Returns the shortest path from the enemy to the player from back to front (first move is path[-1])
     *
     * @param playerLocation Location of the player
     * @param grid           holds the current maze
     * @return Shortest path enemy to the player, from back to front (first move is path[-1]
     */
    public ArrayList<Point> getPathToPlayer(Point playerLocation, char[][] grid) {
        AStarSolver ass = new AStarSolver();
        return ass.CalculateShortestPath(this.getMazePosition(), playerLocation, grid);
    }

    /**
     * Selects a random point on the grid for the enemy to start moving towards
     *
     * @param grid a grid representation of the maze
     */
    private void setRandomLocation(char[][] grid) {
        int randomX = (int) (Math.random() * grid.length - 1);
        int randomY = (int) (Math.random() * grid[0].length -1 );
        while (grid[randomX][randomY] != Maze.MARKER_SPACE) {
            randomX = (int) (Math.random() * grid.length -1 );
            randomY = (int) (Math.random() * grid[0].length -1);
        }
        this.randomLocation = new Point(randomX, randomY);
    }

    /**
     * @return Whether the enemy is still moving
     */
    private boolean isMoving() {
        return !((getGamePositionX() == (getMazePosition().getY()))
                && (getGamePositionY() == getMazePosition().getX()));
    }

    public int getDetectionDistance() {
        return detectionDistance;
    }

    /**
     * Sets the path distance in grid positions that an enemy detects the player and starts going towards the player
     *
     * @param detectionDistance the distance in grid positon to set as the detection distance
     */
    public void setDetectionDistance(int detectionDistance) {
        this.detectionDistance = detectionDistance;
    }
}
