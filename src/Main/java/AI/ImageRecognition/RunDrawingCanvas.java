package AI.ImageRecognition;

import Graphics.OpenGL.Shader;
import Graphics.Rendering.TileRenderer;
import Graphics.Transforming.Camera;
import Graphics.Transforming.Transform;
import Levels.Assets.Tiles.GUIElements;
import Levels.Framework.joml.Vector3f;

public class RunDrawingCanvas {
    private static RunDrawingCanvas instance;

    private DrawingCanvas canvas;
    private boolean running = false;

    // rendering
    private Vector3f canvasTextureLoc;
    private Shader shader;
    private Camera camera;
    private Transform transform;
    private TileRenderer renderer;

    public static  RunDrawingCanvas getInstance(){
        if (instance == null) {
            instance = new RunDrawingCanvas();
        }
        return instance;
    }

    private RunDrawingCanvas() {
        // rendering
        canvasTextureLoc = new Vector3f(-1124f / 1920f, -687f / 1080f, 0f);
        shader = new Shader("flatShader");
        camera = new Camera();
        transform = new Transform();
        transform.setPosition(canvasTextureLoc);
        transform.setScale(new Vector3f(1124f / 1920f, 700f / 1080f, 1f));
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
