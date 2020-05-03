package Graphics;

import Levels.Framework.joml.Matrix4f;
import Levels.Framework.joml.Vector3f;

public class Camera {
    // the position of the camera
    private Vector3f position;

    // the projection the camera uses
    private Matrix4f projection;

    public Camera(int width, int height) {
        position = new Vector3f(0, 0, 0);

        projection = new Matrix4f().setOrtho2D(
                ((float) -width) / 2.0f,
                ((float) width) / 2.0f,
                ((float) -height) / 2.0f,
                ((float) height) / 2.0f
        );
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void addPosition(Vector3f position) {
        this.position.add(position);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Matrix4f getProjection() {
        Matrix4f target = new Matrix4f();
        Matrix4f pos = new Matrix4f().setTranslation(position);

        target = projection.mul(pos, target);
        return target;
    }
}
