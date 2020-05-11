package AI;

public class RunDrawingCanvas implements Runnable {

    DrawingCanvas drawingCanvas = new DrawingCanvas();

    public static void main(String[] args){
        new Thread(new RunDrawingCanvas()).start();
    }

    @Override
    public void run() {

    }
}
