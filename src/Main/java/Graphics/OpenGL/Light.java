package Graphics.OpenGL;

import Levels.Framework.joml.Vector3f;
import Levels.Objects.Object3D;

public class Light {

    private Vector3f position;
    private Vector3f color;
    private Vector3f attenuation = new Vector3f(1,0,0);
    private Object3D object;

    public Light(Vector3f position, Vector3f color, Object3D object) {
        this.position = position;
        this.color = color;
        this.object = object;
    }

    public Light(Vector3f position, Vector3f color, Object3D object, Vector3f attenuation) {
        this.position = position;
        this.color = color;
        this.object = object;
        this.attenuation = attenuation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Object3D getObject() {
        return object;
    }

    public void setObject(Object3D object) {
        this.object = object;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Vector3f attenuation) {
        this.attenuation = attenuation;
    }






}
