package AI.ImageRecognition;

import Graphics.OpenGL.Shader;
import Graphics.TileRenderer;
import Graphics.Transforming.Camera;
import Graphics.Transforming.Transform;
import Levels.Assets.Tiles.GUIElements;
import Levels.Framework.joml.Vector3f;

public class RunDrawingCanvas {

    private DrawingCanvas canvas;
    private boolean running = false;

    // rendering
    private Vector3f canvasTextureLoc;
    private Shader shader;
    private Camera camera;
    private Transform transform;
    private TileRenderer renderer;

    public RunDrawingCanvas() {
        // rendering
        // magic numbers subtracted from 550 to fit to screen
        canvasTextureLoc = new Vector3f(-600f / 1920f, -600f / 1080f, 0f);
        shader = new Shader("flatShader");
        camera = new Camera();
        transform = new Transform();
        transform.setPosition(canvasTextureLoc);
        // the 620 should be 600 but window is weird
        transform.setScale(new Vector3f(600f / 1920f, 620f / 1080, 1f));
        renderer = TileRenderer.getInstance();
    }

    public void start() {
        // run at most one thread
        if (!running) {
            running = true;
            canvas = new DrawingCanvas(this);
            new Thread(canvas, "canvas_name").start();
        }
    }

    public void render() {
        renderer.setShader(shader);
        renderer.setCamera(camera);
        renderer.setTransform(transform);
        renderer.renderTile(GUIElements.CANVAS.getTexture(), 0.5f, 0.5f, TileRenderer.FLOOR);
    }

    public boolean isRunning() {
        return running;
    }

    // no more threads
    public void stop() {
        running = false;
    }
}
