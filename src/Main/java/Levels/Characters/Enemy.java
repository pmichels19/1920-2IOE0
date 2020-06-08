package Levels.Characters;

import AI.AStar.AStarSolver;
import Graphics.OBJModel;
import Levels.Framework.Point;

import java.util.ArrayList;

public abstract class Enemy extends Character {
    public Enemy(int max_health, int max_mana, OBJModel model) {
        super(max_health, max_mana, model);
    }

    private Point randomLocation = null;



    public void move(Point playerLocation, char[][] grid) {
        if (!isMoving()) {
            ArrayList<Point> pathToPlayer = getPathToPlayer(playerLocation, grid);
            if (pathToPlayer.size() > 10) {
                doRandomMove(grid);
            } else {
                moveToPoint(pathToPlayer.get(pathToPlayer.size()-1), grid.length);
            }
        } else {
            reposition(grid.length);
        }
    }

    private void moveToPoint(Point location, int gridLength) {
        // Set the new maze location of the enemy

        super.setMazePosition(location);


        reposition(gridLength);

    }

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


        if (currentX < locationX){
            newX += getSpeed();
        } else if (currentX > locationX){
            newX -= getSpeed();
        }

        if (currentY < locationY){
            newY += getSpeed();
        } else if (currentY > locationY){
            newY -= getSpeed();
        }

        if (Math.abs(locationX - newX) < getSpeed() + 0.01f){
            newX = locationX;
        }
        if (Math.abs(locationY - newY) < getSpeed() + 0.01f){
            newY = locationY;
        }
        setGridPosition(newX, newY, gridLength);

    }

    private void doRandomMove(char[][] grid) {
        if (randomLocation == null) {
            setRandomLocation();
        }
    }

    public ArrayList<Point> getPathToPlayer(Point playerLocation, char[][] grid) {
        AStarSolver ass = new AStarSolver();
        return ass.CalculateShortestPath(this.getMazePosition(), playerLocation, grid);
    }

    public void setRandomLocation() {
        Point randomLocatoin;
        this.randomLocation = randomLocation;
    }

    private boolean isMoving() {
        return !((getGamePositionX() == (getMazePosition().getY()))
                && (getGamePositionY() == getMazePosition().getX()));
    }
}
