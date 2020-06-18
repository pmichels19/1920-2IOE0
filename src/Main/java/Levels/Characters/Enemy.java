package Levels.Characters;

import AI.AStar.AStarSolver;
import Graphics.OBJModel;
import Graphics.OpenGL.Shader;
import Levels.Framework.Point;

import java.util.ArrayList;
import java.util.Timer;

import static Levels.Framework.Maze.*;

public abstract class Enemy extends Character {
    public Enemy(int max_health, int max_mana, OBJModel model) {
        super(max_health, max_mana, 15, model);
    }

    // Holds the location the enemy is heading when the player is not close enough
    private Point randomLocation = null;

    // The distance that an enemy should be from the player to move towards the player
    private int detectionDistance = 10;

    // Random location grid size (should be an even number)
    private final int randomPathDistance = 16;

    private boolean highAmbience;

    @Override
    public void render(Shader shader) {
        if (highAmbience) {
            shader.setUniform("highAmbience", 1);
        }
        shader.setUniform("enemyCharacter", 1);

        super.render(shader);
        shader.setUniform("enemyCharacter", 0);
        shader.setUniform("highAmbience", 0);
    }
    // Some states the enemy can be in to change behavior during states
    public final static int NORMAL_STATE = 0;
    public final static int ATTACKING_STATE = 1;
    public final static int RETURNING_STATE = 2;

    // State of the current enemy
    private int state = NORMAL_STATE;

    // Minimum amount of time between attacks (in milliseconds)
    private long attackDelay = 1000;

    // Holds the last timestamp of last attack
    private long lastAttackTime = 0;


    /**
     * Moves the enemy, either to a random location or the player (if it is close enough)
     *
     * @param playerLocation Current location of the player
     * @param grid           Grid that holds the maze
     */
    public void move(Point playerLocation, char[][] grid) {
        if (!isMoving()) {
            if (getMazePosition().calculateManhattanDistance(playerLocation) <= detectionDistance) { // First check manhattan distance since it's faster
                if (getMazePosition().calculateManhattanDistance(playerLocation) == 1) {
                    if (canAttack()) {
                        startAttack(playerLocation, grid.length);
                    }
                } else {
                    setNewLocation(playerLocation, grid);
                }
            } else {
                // if the player is outside of the detection distance
                doRandomMove(grid);

            }
        } else if (state == ATTACKING_STATE) {
            attackPlayer(playerLocation, grid.length);
        } else {
            // Updates the movement step if the enemy is already moving
            reposition(grid.length);
        }
    }

    /**
     * Sets a new location for the enemy, either towards the player or towards a random point
     *
     * @param playerLocation Current maze location of the player
     * @param grid           Current maze state
     */
    private void setNewLocation(Point playerLocation, char[][] grid) {
        ArrayList<Point> pathToPlayer = getPathToPlayer(playerLocation, grid);
        if (pathToPlayer.size() > detectionDistance) {
            // if the player is outside of the detection distance
            doRandomMove(grid);
        } else {
            // If player is in detection range, move towards the player
            moveToPoint(pathToPlayer.get(pathToPlayer.size() - 1), grid.length);
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

        // Updates the location based on state
        if (state == RETURNING_STATE) {
            // If returned to original position
            if (Math.abs(locationX - newX) < getSpeed() && Math.abs(locationY - newY) < getSpeed()) {
                newX = locationX;
                newY = locationY;
                state = NORMAL_STATE;
            } else if (Math.abs(locationX - newX) < getSpeed()) {
                newX = locationX;
            } else if (Math.abs(locationY - newY) < getSpeed()) {
                newY = locationY;
            }


            setGamePositionX(newX);
            setGamePositionY(newY, gridLength);

        } else {// Fix differences between float and int
            if (Math.abs(locationX - newX) < getSpeed()) {
                newX = locationX;
            }
            if (Math.abs(locationY - newY) < getSpeed()) {
                newY = locationY;
            }

            setGamePositionAndRotate(newX, newY, gridLength);
        }
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
                && (getGamePositionX() == randomLocation.getY())) { // If it has reached the set location
            randomLocation = null;
        } else { // if not reached the random location
            // Get next step towards new location
            AStarSolver ass = AStarSolver.getInstance();
            ArrayList<Point> nextPoint = ass.CalculateShortestPath(getMazePosition(), randomLocation, grid);
            moveToPoint(nextPoint.get(nextPoint.size() - 1), grid.length);
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
        AStarSolver ass = AStarSolver.getInstance();
        return ass.CalculateShortestPath(this.getMazePosition(), playerLocation, grid);
    }

    /**
     * Selects a random point on the grid for the enemy to start moving towards
     *
     * @param grid a grid representation of the maze
     */
    private void setRandomLocation(char[][] grid) {
        int randomX = (int) (Math.random() * randomPathDistance) - randomPathDistance / 2 + getMazePosition().getX(); // new location is within a 8x8 grid around the current location
        int randomY = (int) (Math.random() * randomPathDistance) - randomPathDistance / 2 + getMazePosition().getY();
//        while ((randomX < 0 || randomX >= grid.length)
//                || randomY < 0 || randomY >= grid[0].length
//                || grid[randomX][randomY] != Maze.MARKER_SPACE) {
//            randomX = (int) (Math.random() * 16) - 8 + getMazePosition().getX(); // new location is within a 8x8 grid around the current location
//            randomY = (int) (Math.random() * 16) - 8 + getMazePosition().getY();
//        }
        if (!(randomX < 0 || randomX >= grid.length
                || randomY < 0 || randomY >= grid[0].length
                || grid[randomX][randomY] != MARKER_SPACE)) { // check if the random location is a correct one
            this.randomLocation = new Point(randomX, randomY);
        }

    }

    /**
     * Initiates an attack from the enemy
     *
     * @param playerLocation current maze location of the player
     * @param gridLength     the length of the maze
     */
    private void startAttack(Point playerLocation, int gridLength) {
        state = ATTACKING_STATE;
        attackPlayer(playerLocation, gridLength);
    }

    /**
     * Makes enemy attack the player
     *
     * @param playerLocation Current maze position of the player
     */
    private void attackPlayer(Point playerLocation, int gridLength) {
        // Get the current position in world coordinates
        float currentX = super.getGamePositionX();
        float currentY = super.getGamePositionY();

        // Set the desired X and Y, note that game positions have the X and Y flipped from the Maze positions
        float locationX = getMazePosition().getY() + ((playerLocation.getY() - getMazePosition().getY()) / 2f);
        float locationY = getMazePosition().getX() + ((playerLocation.getX() - getMazePosition().getX()) / 2f);

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


        // Fix differences between float and int, player has reached attack location
        if (Math.abs(locationX - newX) < getSpeed() && Math.abs(locationX - newX) != 0f) {
            newX = locationX;
            if (playerLocation.calculateManhattanDistance(getMazePosition()) < 2) { // check for dodge
                Player.getInstance().damage(damage);
                lastAttackTime = System.currentTimeMillis();
            }

            state = RETURNING_STATE;
        }
        if (Math.abs(locationY - newY) < getSpeed() && Math.abs(locationY - newY) != 0f) {
            newY = locationY;
            if (playerLocation.calculateManhattanDistance(getMazePosition()) < 2) { // check for dodge
                Player.getInstance().damage(damage);
                lastAttackTime = System.currentTimeMillis();
            }
            lastAttackTime = System.currentTimeMillis();
            state = RETURNING_STATE;
        }
        if (Math.abs(locationY - newY) == 0f && Math.abs(locationX - newX) == 0f) {
            if (playerLocation.calculateManhattanDistance(getMazePosition()) < 2) { // check for dodge
                Player.getInstance().damage(damage);
                lastAttackTime = System.currentTimeMillis();
            }
            lastAttackTime = System.currentTimeMillis();
            state = RETURNING_STATE;
        }

        // Updates the location
        setGamePositionAndRotate(newX, newY, gridLength);
    }


    /**
     * @return Whether the enemy is still moving
     */
    public boolean isMoving() {
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

    public void setHighAmbience(boolean highAmbience) {
        this.highAmbience = highAmbience;
    }

    private boolean canAttack() {
        return System.currentTimeMillis() > lastAttackTime + attackDelay;
    }

}
