package Levels.Assets.Items;

import Graphics.OBJModel;
import org.joml.Vector3f;

public class GameItem {

    private final OBJModel model;

    private final Vector3f position;

    private float scale;

    private final Vector3f rotation;

    public GameItem(OBJModel model) {
        this.model = model;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public OBJModel getMesh() {
        return model;
    }
}