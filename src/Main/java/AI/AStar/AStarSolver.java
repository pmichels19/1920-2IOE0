package AI.AStar;

import Levels.Framework.Maze;
import Levels.Framework.Point;
import java.util.ArrayList;

/**
 * Class that handles all A* calculations
 */
public class AStarSolver {
    private char[][] grid;
    private ArrayList<AStarPoint> open;
    private ArrayList<AStarPoint> closed;
    private AStarPoint destinationPoint;

    private final Boolean allowDiagonal = false;
    private final int diagonalMoveCost = 14 * 99999999; //TODO: When diagonal movement is implemented change back to 14
    private final int normalMoveCost = 10;

    /**
     * Calculates the shortest path from the given start to the destination based on the given grid
     *
     * @param startLocation the start location
     * @param destination   the destination
     * @param grid          a grid of the environment
     * @return an ArrayList of Point objects of the shortest path
     */
    public ArrayList<Point> CalculateShortestPath(Point startLocation, Point destination, char[][] grid) {
        // Initialize lists
        open = new ArrayList<>();
        closed = new ArrayList<>();
        this.grid = grid;

        // Convert startPoint and destinationPoint
        AStarPoint startPoint = new AStarPoint(startLocation.getX(), startLocation.getY(), 0, 0, null);
        destinationPoint = new AStarPoint(destination.getX(), destination.getY(), 0, 0, null);

        // Add startpoint to open list
        open.add(startPoint);

        // Keep going until we have found the destination or run out of possibilities
        while (open.size() > 0 && destinationPoint.parent == null) {

            // Get the point with the lowest f val from the open list
            int maxF = Integer.MAX_VALUE;
            int maxIndex = -1;
            for (int i = 0; i < open.size(); i++) {
                AStarPoint aPoint = open.get(i);
                if (aPoint.f < maxF) {
                    maxF = aPoint.f;
                    maxIndex = i;
                }
            }

            AStarPoint q = open.remove(maxIndex);
            calculateSuccessors(q);
            closed.add(q);
        }

        ArrayList<Point> path = null;
        if (destinationPoint.parent != null) {
            path = new ArrayList<>();
            AStarPoint successor = destinationPoint;
            while (successor.parent != null) {
                path.add(new Point(successor.x, successor.y));
                successor = successor.parent;
            }
        }
        return path;
    }

    /**
     * Does calculations for the successor points of q
     *
     * @param q point to check successors for
     */
    private void calculateSuccessors(AStarPoint q) {
        int MIN_X = 0;
        int MAX_X = grid.length;
        int MIN_Y = 0;
        int MAX_Y = grid[0].length;
        int startPosX = (q.x - 1 < MIN_X) ? q.x : q.x - 1;
        int startPosY = (q.y - 1 < MIN_Y) ? q.y : q.y - 1;
        int endPosX = (q.x + 1 > MAX_X) ? q.x : q.x + 1;
        int endPosY = (q.y + 1 > MAX_Y) ? q.y : q.y + 1;


        // Go over all neighbouring grid points
        for (int rowNum = startPosX; rowNum <= endPosX; rowNum++) {
            for (int colNum = startPosY; colNum <= endPosY; colNum++) {
                if (grid[rowNum][colNum] != Maze.MARKER_WALL) { // Check if point is not a wall
                    AStarPoint successor = new AStarPoint();
                    successor.parent = q;
                    successor.x = rowNum;
                    successor.y = colNum;

                    // If we have reached the destination
                    if (successor.x == destinationPoint.x && successor.y == destinationPoint.y) {
                        if ((allowDiagonal) || (q.x == successor.x || q.y == successor.y)) {
                            destinationPoint.parent = q;
                            return;
                        }
                    }

                    // Calculate and set the new g value
                    int g = q.getG();
                    if (q.x == successor.x || q.y == successor.y) {
                        g += normalMoveCost;
                    } else {
                        g += diagonalMoveCost;
                    }
                    successor.setG(g);

                    // Calculate and set the new h value, will always be 0 here because it is destination
                    int h = calculateH(rowNum, colNum);
                    successor.setH(h);

                    // Check if there is a point in the open or closed list with the same coordinates as successor that has a smaller f value
                    if ((!containsSmaller(successor, open)) && (!containsSmaller(successor, closed))) {
                        open.add(successor);
                    }
                }
            }
        }
    }

    /**
     * Checks in the list if there is a point that has the same x and y but a smaller f as successor
     *
     * @param successor point to check for
     * @param list      list to look in
     * @return True if there is a point in list that has the same x and y but a smaller f as successor
     */
    private Boolean containsSmaller(AStarPoint successor, ArrayList<AStarPoint> list) {
        for (AStarPoint p : list) {
            if (p.x == successor.x && p.y == successor.y && p.f < successor.f) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the euclidean distance and multiplies it by 10 to correspond with the cost values
     *
     * @param x x value of current point
     * @param y y value of current point
     * @return the euclidean distance
     */
    private int calculateH(int x, int y) {
        return (int) (Math.sqrt(Math.pow(destinationPoint.x - x, 2) + Math.pow(destinationPoint.y - y, 2)) * 10);
    }
}


