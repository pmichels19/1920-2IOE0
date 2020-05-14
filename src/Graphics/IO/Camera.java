package Graphics.IO;

import Levels.Framework.joml.Matrix4f;
import Levels.Framework.joml.Quaternionf;
import Levels.Framework.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private Quaternionf rotation;
    private Matrix4f projection;

    public Camera() {
        position = new Vector3f();
        rotation = new Quaternionf();
        projection = new Matrix4f();
    }

    public Matrix4f getTransformation() {
        Matrix4f result = new Matrix4f();

        result.rotate( rotation.conjugate( new Quaternionf() ) );
        result.translate( position.mul(-1, new Vector3f() ) );

        return result;
    }

    public void setOrthoGraphic(float left, float right, float top, float bottom) {
        projection.setOrtho2D(left, right, top, bottom);
    }

    public void setPerspective(float fov, float aspectRatio, float zNear, float zFar) {
        projection.setPerspective(fov, aspectRatio, zNear, zFar);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
    }

    public Matrix4f getProjection() {
        return projection;
    }
}
