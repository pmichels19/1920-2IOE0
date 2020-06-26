package AI.ImageRecognition;

import SpellCasting.Spell;
import SpellCasting.SpellAgility;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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

    private Spell spell;

    private int windowX = 500; // Window width
    private int windowY = 500;

    private int pixelSize = 5; // Size of a pixel

    private int canvasSize = 500;
    private int gridX = canvasSize / pixelSize;
    private int gridY = canvasSize / pixelSize;
    private float[][] grid = new float[gridX][gridY];
    private Canvas canvas = new Canvas();

    private int canvasX;
    private int canvasY;

    private ArrayList<Point> current = new ArrayList<>();

    // canvas background color
    private Color canvasColor = new Color(199, 189, 167);

    // exit call
    private volatile boolean exit = false;

    // GoogleConfig object for visionML
    private GoogleConfig googleConfig = new GoogleConfig();
    private String defaultLabel = "Predicted class and score: ";
    private JLabel imageClass = new JLabel();

    public DrawingCanvas(RunDrawingCanvas rdc) {
        this.rdc = rdc; // allow set stop process

        // canvas settings
        canvas.setSize(canvasSize, canvasSize);
        canvasX = canvas.getX();
        canvasY = canvas.getY();
        this.add(canvas);

        // image settings
        imageClass.setText(defaultLabel);
        imageClass.setSize(400, 100);
        imageClass.setVisible(true);
        imageClass.setLocation(240, 450);
        this.add(imageClass);

        // jframe settings
        this.setUndecorated(true);  // remove border
        this.setTitle("Drawing Canvas");
        this.setSize(windowX, windowY);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null);   // place frame in center
        this.setResizable(false);
        this.setLayout(null);

        // Add listener for mouse movements
        Move move = new Move();
        this.addMouseMotionListener(move);

        // Add listener for mouse clicks
        Click click = new Click();
        this.addMouseListener(click);

        // add keyboard listener
        Keypress press = new Keypress();
        this.addKeyListener(press);
    }

    /**
     * Resets the grid to default 0 values
     */
    public void resetGrid() {
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
        BufferedImage im = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
        // need black and white for image recognition
        canvasColor = Color.WHITE;
        canvas.paintComponent(im.getGraphics());
        // revert to old color
        canvasColor = new Color(199, 189, 167);

        String tempPath = Paths.get("").toAbsolutePath().toString() + "\\" + "spell.jpg";
        ImageIO.write(im, "jpg", new File(tempPath));

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
     * and draw the text on how to use the canvas on top of it
     */
    public class Canvas extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int i = 0; i < gridX; i++) {
                for (int j = 0; j < gridY; j++) {
                    g.setColor((grid[i][j] == 0 ? canvasColor : Color.BLACK));
                    g.fillRect(i * pixelSize, j * pixelSize, pixelSize, pixelSize);
                }
            }
            // don't render text when saving image
            if (canvasColor != Color.WHITE) {
                // drawing canvas usage text (may be placed in canvas border texture later)
                g.setColor(Color.BLACK);
                g.setFont(new Font("DialogInput", Font.PLAIN, 20));
                g.drawString("\'E\' to cast spell, \'Q\' to clear drawing", 5, 20);
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

        int x = i;
        int y = j;
        // Check if we are still within grid bounds
        if (x >= 0 && x < gridX && y >= 0 && y < gridY) {
            // if not directly drawn over, make less dark
            grid[x][y] = 1f;
        }
    }

    public class Move implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
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

    public class Keypress implements KeyListener {

        private boolean released = true;
        private boolean classify = false;

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (released && !classify) {
                // classify image
                if (e.getKeyChar() == 'e') {
                    classify = true;
                    // classify image with google classifier
                    try {
                        String[] tempData = googleConfig.predict(saveGridAsImage());
                        imageClass.setText(defaultLabel + tempData[0] + ", " + tempData[1]);
                        Spell cast = Spell.determineSpell(tempData[0]);
                        cast.castSpell(null);
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                    // stop the thread
                    released = false;
                    stop();
                // reset image
                } else if (e.getKeyChar() == 'q') {
                    resetGrid();
                    repaint();
                    released = false;
                } else if (e.getKeyChar() == 'l') {
                    released = false;
                    stop();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            released = true;
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