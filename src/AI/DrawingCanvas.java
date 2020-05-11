package AI;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Iterator;

public class DrawingCanvas extends JFrame {
    private int verticalOffset = 27;
    private int windowX = 500;
    private int windowY = 500 + verticalOffset;

    private int pixelSize = 2;


    private int gridX = windowX / pixelSize;
    private int gridY = (windowY - verticalOffset) / pixelSize;
    private float[][] grid = new float[gridX][gridY];
    private Canvas canvas = new Canvas();

    private ArrayList<Point> current = new ArrayList<>();

    public DrawingCanvas() {
        this.setTitle("Drawing Canvas");
        this.setSize(windowX, windowY); // Add 6 for the side borders and add 29 for the title bar
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);

        // Create drawing canvas on window
        this.setContentPane(canvas);

        // Add listener for mouse movements
        Move move = new Move();
        this.addMouseMotionListener(move);

        //Add listener for mouse clicks
        Click click = new Click();
        this.addMouseListener(click);

    }

    public void ResetGrid() {
        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                grid[i][j] = (float) 0.0;
            }
        }
    }

    /**
     * Converts a pixel location into grid index for x pixel
     *
     * @param x input x coordinate of pixel location
     * @return integer corresponding to grid pixel x index
     */
    public int ConvertXToGrid(int x) {
        return x / pixelSize;
    }

    /**
     * Converts a pixel location into grid index for y pixel
     *
     * @param y input y coordinate of pixel location
     * @return integer corresponding to grid pixel y index
     */
    public int ConvertYToGrid(int y) {
        return (y - verticalOffset) / pixelSize;
    }

    public class Canvas extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, 920, 920);


            for (int i = 0; i < gridX; i++) {
                for (int j = 0; j < gridY; j++) {
                    int color = (int) (255 - (grid[i][j] * 255));
                    g.setColor(new Color(color, color, color));
                    g.fillRect(i * pixelSize, j * pixelSize, pixelSize, pixelSize);
                }
            }
        }
    }

    /**
     * Because the mouse update is not for every pixel movement we calculate the line between consecutive generated points
     * The problem is, that if you move the mouse quickly the line gets less precise and the points spread apart
     */
    private void updateGrid() {
        Iterator<Point> iter = current.iterator();
        Point startPoint;
        Point nextPoint = new Point(0, 0);
        if (iter.hasNext()) {
            nextPoint = iter.next();
        }
        while (iter.hasNext()) {
            startPoint = (Point) nextPoint.clone();
            nextPoint = iter.next();


            int distance = Math.max(Math.abs(nextPoint.x - startPoint.x), Math.abs(nextPoint.y - startPoint.y));

            if (distance >= pixelSize) { // Only if skip is significant
                float directionX = (float) (nextPoint.x - startPoint.x) / distance;
                float directionY = (float) (nextPoint.y - startPoint.y) / distance;
                System.out.println(directionX + "    ,    " + directionY);

                for (int i = 0; i <= distance; i++) {
                    Point point = new Point(0, 0);
                    point.x = startPoint.x + (int) (directionX * i);
                    point.y = startPoint.y + (int) (directionY * i);
                    ColorForPoint(point);
                    canvas.repaint();
                }
            }

        }

    }

    private void ColorForPoint(Point p) {
        int xPixel = p.x;
        int yPixel = p.y;
        int i = ConvertXToGrid(xPixel);
        int j = ConvertYToGrid(yPixel);

        int offset = 1;

        // make the grids close to the current line grey
        for (int iOfs = -1 * offset; iOfs <= offset; iOfs++) {
            for (int jOfs = -1 * offset; jOfs <= offset; jOfs++) {
                int x = i + iOfs;
                int y = j + jOfs;
                // Check if we are still within grid bounds
                if (x >= 0 && x < gridX && y >= 0 && y < gridY) {
                    // Distance is sqrt( |x|^2 + |y|^2)
                    if (iOfs != 0 || jOfs != 0) {
                        grid[x][y] += 0.25;
                    } else {
                        grid[x][y] += 1.0;
                    }


                    // Make sure the colors stay within bounds
                    if (grid[x][y] < (float) 0.0) {
                        grid[x][y] = (float) 0.0;
                    } else if (grid[x][y] > (float) 1.0) {
                        grid[x][y] = (float) 1.0;
                    }
                }
            }
        }
        canvas.repaint();


    }


    public class Move implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
            System.out.println("X is : " + e.getX() + ",    Y is: " + e.getY());
            current.add(new Point(e.getX(), e.getY()));

            int xPixel = e.getX();
            int yPixel = e.getY();
            int i = ConvertXToGrid(xPixel);
            int j = ConvertYToGrid(yPixel);

            int offset = 1;

            // make the grids close to the current line grey
            for (int iOfs = -1 * offset; iOfs <= offset; iOfs++) {
                for (int jOfs = -1 * offset; jOfs <= offset; jOfs++) {
                    int x = i + iOfs;
                    int y = j + jOfs;
                    // Check if we are still within grid bounds
                    if (x >= 0 && x < gridX && y >= 0 && y < gridY) {
                        // Distance is sqrt( |x|^2 + |y|^2)
                        if (iOfs != 0 || jOfs != 0) {
                            grid[x][y] += 0.25;
                        } else {
                            grid[x][y] += 1.0;
                        }


                        // Make sure the colors stay within bounds
                        if (grid[x][y] < (float) 0.0) {
                            grid[x][y] = (float) 0.0;
                        } else if (grid[x][y] > (float) 1.0) {
                            grid[x][y] = (float) 1.0;
                        }
                    }
                }
            }
            canvas.repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }

    public class Click implements MouseListener {


        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            updateGrid();
            repaint();
            current = new ArrayList<>();
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}