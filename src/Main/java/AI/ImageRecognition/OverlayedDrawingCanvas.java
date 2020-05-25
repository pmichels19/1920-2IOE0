package AI.ImageRecognition;

import Graphics.OpenGL.Shader;
import Graphics.TileRenderer;
import Graphics.Transforming.Camera;
import Graphics.Transforming.Transform;
import Levels.Assets.Tiles.GUIElements;
import Levels.Framework.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

public class OverlayedDrawingCanvas {

    // canvas
    private int width;
    private int height;
    private int canvasSize;
    private int pixelSize;
    private long window;
    private int[][] grid;

    // rendering
    private Shader shader;
    private Camera camera;
    private Transform transform;
    private TileRenderer renderer;

    public OverlayedDrawingCanvas(long window) {
        // canvas
        width = 1920;
        height = 1080;
        canvasSize = 500;
        pixelSize = 10;
        this.window = window;
        int pixels = canvasSize / pixelSize;
        grid = new int[pixels][pixels];

        // rendering
        shader = new Shader("flatShader");
        camera = new Camera();
        transform = new Transform();
        renderer = TileRenderer.getInstance();
        renderer.setShader(shader);
        renderer.setCamera(camera);
        renderer.setTransform(transform);
    }

    public void render() {
        simDrawing();
        renderCanvas();
        renderDrawing();
    }

    // simulate drawing in the grid
    private void simDrawing() {
        double[] pos = getCursor();
        // convert x to grid index
        pos[0] -= (width - canvasSize) / 2;
        pos[0] /= pixelSize;
        // magic numbers as rendering doesn't quite add up
        pos[1] -= (((height - canvasSize) / 2) - 6);
        pos[1] *= ((float) canvasSize / (float) 492);
        pos[1] = (int) pos[1];
        pos[1] /= pixelSize;
        // put 1 into grid
        int i = (int) pos[1];
        int j = (int) pos[0];
        if (i >= 0 && i < grid.length && j >=0 && j < grid[0].length) {
            grid[i][j] = 1;
        }
    }

    // canvas height isn't exactly 500 pixels on screen
    private void renderCanvas() {
        // bottom left of canvas
        Vector3f bottomLeft = new Vector3f((float) -canvasSize / (float) width,
                (float) -canvasSize / (float) height, 0f);
        transform.setPosition(bottomLeft);

        // window canvasSize dimensions
        Vector3f scaling = new Vector3f((float) canvasSize / (float) width,
                (float) canvasSize / (float) height, 1f);
        transform.setScale(scaling);

        // render canvas
        renderer.setTransform(transform);
        renderer.renderTile(GUIElements.CANVAS.getTexture(), 0.5f, 0.5f, TileRenderer.FLOOR);
    }

    private void renderDrawing() {

    }

    /**
    *  Retrieves mouse coordinates in terms of pixels
    *  @return double array holding x coordinate in 0 and y coordinate in 1
    */
    private double[] getCursor() {
        double[] pos = new double[2];
        DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, xBuffer, yBuffer);
        pos[0] = xBuffer.get(0);
        pos[1] = yBuffer.get(0);
        return pos;
    }
}
