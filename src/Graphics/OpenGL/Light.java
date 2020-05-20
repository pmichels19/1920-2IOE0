package Graphics.OpenGL;

import Levels.Framework.joml.Vector3f;

public class Light {

    private Vector3f position;
    private Vector3f color;
    private Vector3f attenuation = new Vector3f(1,0,0);
    private Texture texture;

    public Light(Vector3f position, Vector3f color, Texture texture) {
        this.position = position;
        this.color = color;
        this.texture = texture;
    }

    public Light(Vector3f position, Vector3f color, Texture texture, Vector3f attenuation) {
        this.position = position;
        this.color = color;
        this.texture = texture;
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

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Vector3f attenuation) {
        this.attenuation = attenuation;
    }






}
