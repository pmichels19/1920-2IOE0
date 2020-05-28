package AI.ImageRecognition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * DrawingCanvas generates a window that allows for the drawing of spells which are then converted into a grid array with coloring values
 */
public class DrawingCanvas extends JFrame implements Runnable {

    private RunDrawingCanvas rdc;

    private int bottomBarHeight = 50;
    private int windowX = 500; // Window width
    private int windowY = 500 + bottomBarHeight;

    private int pixelSize = 2; // Size of a pixel

    private int canvasSize = 475;
    private int gridX = canvasSize / pixelSize;
    private int gridY = canvasSize / pixelSize;
    private float[][] grid = new float[gridX][gridY];
    private Canvas canvas = new Canvas();

    private int canvasX;
    private int canvasY;

    private ArrayList<Point> current = new ArrayList<>();
    private JPanel panel1;
    private JButton button1;

    private volatile boolean exit = false;

    // GoogleConfig object for visionML
    private GoogleConfig googleConfig = new GoogleConfig();
    private String defaultLabel = "Predicted class and score: ";
    private JLabel imageClass = new JLabel();

    public DrawingCanvas(RunDrawingCanvas rdc) {
        this.rdc = rdc; // allow set stop process

        this.setUndecorated(true);  // remove border
        this.setTitle("Drawing Canvas");
        this.setSize(windowX, windowY);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null);   // place frame in center
        this.setResizable(false);

        this.setLayout(null);

        canvas.setSize(canvasSize, canvasSize);
        canvas.setLocation(7, 10);
        canvasX = canvas.getX();
        canvasY = canvas.getY();
        this.add(canvas);


        JButton clearButton = new JButton();
        JButton classifyButton = new JButton();

        imageClass.setText(defaultLabel);
        imageClass.setSize(400, 100);
        imageClass.setVisible(true);
        imageClass.setLocation(240, 450);

        clearButton.setText("Clear");
        clearButton.setSize(100, 40);

        clearButton.setVisible(true);
        clearButton.setLocation(10, 500);

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResetGrid();
                repaint();
            }
        });

        classifyButton.setText("Classify");
        classifyButton.setSize(100, 40);

        classifyButton.setVisible(true);
        classifyButton.setLocation(120, 500);

        classifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    String[] tempData = googleConfig.predict(saveGridAsImage());
                    imageClass.setText(defaultLabel + tempData[0] + ", " + tempData[1]);

                } catch (Exception ex) {
                    System.out.println(ex);
                }

                // stop the thread
                stop();
            }
        });


        this.add(clearButton);
        this.add(classifyButton);
        this.add(imageClass);


        // Add listener for mouse movements
        Move move = new Move();
        this.addMouseMotionListener(move);

        //Add listener for mouse clicks
        Click click = new Click();
        this.addMouseListener(click);

    }

    /**
     * Resets the grid to default 0 values
     */
    public void ResetGrid() {
        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                grid[i][j] = (float) 0.0;
            }
        }
    }

    /** Saves the drawing canvas as an image locally.
     *
     * @return the path to the image
     */
    private String saveGridAsImage() throws IOException {
        Container c = canvas;
        BufferedImage im = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);
        c.paint(im.getGraphics());

        String tempPath = Paths.get("").toAbsolutePath().toString() + "\\" + "spell.jpg";
        ImageIO.write(im, "jpg", new File(tempPath));

        System.out.println(tempPath);

        return tempPath;
    }

    /**
     * Converts a pixel location into grid index for x pixel
     *
     * @param x input x coordinate of pixel location
     * @return integer corresponding to grid pixel x index
     */
    public int ConvertXToGrid(int x) {
        return (x - canvasX-3) /pixelSize;
    }

    /**
     * Converts a pixel location into grid index for y pixel
     *
     * @param y input y coordinate of pixel location
     * @return integer corresponding to grid pixel y index
     */
    public int ConvertYToGrid(int y) {
        return (y - canvasY) / pixelSize;
    }

    /**
     * Canvas holds the grid rectangles and has a function to draw the canvas based on the values in the grid array
     */
    public class Canvas extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
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
            startPoint = nextPoint;
            nextPoint = iter.next();


            // Calculate the euclidean distance
            int distance = Math.max(Math.abs(nextPoint.x - startPoint.x), Math.abs(nextPoint.y - startPoint.y));

            if (distance >= pixelSize) { // Skip if not significant
                float directionX = (float) (nextPoint.x - startPoint.x) / distance;
                float directionY = (float) (nextPoint.y - startPoint.y) / distance;

                // Create new sub points between the 2 given points
                for (int i = 0; i <= distance; i++) {
                    Point point = new Point(0, 0);
                    point.x = startPoint.x + (int) (directionX * i);
                    point.y = startPoint.y + (int) (directionY * i);
                    // Calculate grid values for point
                    ColorForPoint(point);
                    // Update the canvas
                    canvas.repaint();
                }
            }

        }

    }

    /**
     * Calculates the grid values for the given point and some of it's neighbours
     *
     * @param p the point that was drawn over
     */
    private void ColorForPoint(Point p) {
        int xPixel = p.x;
        int yPixel = p.y;
        int i = ConvertXToGrid(xPixel);
        int j = ConvertYToGrid(yPixel);

        // Amount of neighbouring grid points to color
        int offset = 1;

        // make the grid points close to the current line grey
        for (int iOfs = -1 * offset; iOfs <= offset; iOfs++) {
            for (int jOfs = -1 * offset; jOfs <= offset; jOfs++) {
                int x = i + iOfs;
                int y = j + jOfs;
                // Check if we are still within grid bounds
                if (x >= 0 && x < gridX && y >= 0 && y < gridY) {
                    // if not directly drawn over, make less dark
                    if (iOfs != 0 || jOfs != 0) {
                        grid[x][y] += 0.25;
                    } else {
                        grid[x][y] += 1.0;
                    }


                    // Make sure the colors stay within color bounds
                    if (grid[x][y] < (float) 0.0) {
                        grid[x][y] = (float) 0.0;
                    } else if (grid[x][y] > (float) 1.0) {
                        grid[x][y] = (float) 1.0;
                    }
                }
            }
        }
    }

    public class Move implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
//            System.out.println("X is : " + e.getX() + ",    Y is: " + e.getY());
            Point point = new Point(e.getX(), e.getY());
            // Add point to current line list
            current.add(point);
            // Calculate value for current point
            ColorForPoint(point);
            // draw full line instead of separated dots
            updateGrid();
            // Update canvas
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
            // updateGrid();
            repaint();
            // Reset current line points
            current = new ArrayList<>();
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    @Override
    public void run() {
        // keep running while exit is false
        while (!exit) {
        }

        // neatly close thread
        this.dispose();
        rdc.stop();
    }

    public void stop() {
        exit = true;
    }
}