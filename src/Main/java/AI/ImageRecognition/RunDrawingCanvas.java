package AI.ImageRecognition;

public class RunDrawingCanvas {

    private DrawingCanvas canvas;
    private boolean running = false;

    public void start() {
        // run at most one thread
        if (!running) {
            running = true;
            canvas = new DrawingCanvas(this);
            new Thread(canvas, "canvas_name").start();
        }
    }

    // no more threads
    public void stop() {
        running = false;
    }
}
