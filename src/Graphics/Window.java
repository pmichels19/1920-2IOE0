package Graphics;

import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.*;

public class Window {

    private long window;

    private int width;
    private int height;

    private boolean fullscreen;

    public Window() {
        setSize(640, 480);
    }

    public Window(int width, int height) {
        setSize(width, height);
        setFullscreen(false);
    }

    public void createWindow(String title) {
        window = glfwCreateWindow(
                width,
                height,
                title,
                fullscreen ? glfwGetPrimaryMonitor() : 0,
                0);

        if (window == 0) {
            throw new IllegalStateException("=== Failed to create window ===");
        }

        // we do not need to place the window if the window is fullscreen
        if (!fullscreen) {
            // get the data about the primary monitor
            GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window,
                    (vid.width() - width) / 2,
                    (vid.height() - height) / 2
            );
        }

        // show the window to the user
        glfwShowWindow(window);

        // enable LWJGL making use of the window
        glfwMakeContextCurrent(window);
    }

    public void close() {
        glfwSetWindowShouldClose(window, true);
    }

    public boolean buttonClicked(int button) {
        return glfwGetKey(window, button) == GLFW_TRUE;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public void swapBuffers() {
        glfwSwapBuffers(window);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public long getWindow() {
        return window;
    }
}
