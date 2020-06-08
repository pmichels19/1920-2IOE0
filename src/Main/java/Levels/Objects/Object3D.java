package Levels.Objects;

import Graphics.OBJModel;
import Graphics.OpenGL.Shader;
import Levels.Framework.joml.Matrix4f;
import Levels.Framework.joml.Vector3f;

public abstract class Object3D {
    // Holds the model
    private OBJModel model;

    // Holds the current position
    protected Vector3f position;

    // Holds the scale
    public float scale;

    // holds the rotation
    protected Vector3f rotation;
    protected float rotationAngle;

    protected float gamePositionX;
    protected float gamePositionY;

    public Object3D(OBJModel model) {
        this.model = model;
        position = new Vector3f(0, 0f, 1.5f);
        scale = 1.2f;
        rotationAngle = (float) ((-90f * Math.PI) / 180.0f);
        rotation = new Vector3f(0f, 0f, 1f);

        gamePositionX = 1;
        gamePositionY = 1;
    }

    /**
     * renders the model specified in the subclass once
     */
    public void render(Shader shader, float x_pos, float y_pos) {
        model.render(shader);
    }

    public void render(Shader shader) {
        shader.setUniform("transform", 1);
        Matrix4f modelTransform = new Matrix4f();
        modelTransform.translate(position).rotate(rotationAngle, rotation).scale(scale);
        shader.setUniform("modelTransform", modelTransform);
        model.render(shader);
        shader.setUniform("transform", 0);
    }

    public OBJModel getModel() {
        return model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getScale() {
        return scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setRotation(float angle, Vector3f vector) {
        this.rotationAngle = angle;
        this.rotation = vector;
    }

    public float getGamePositionX() {
        return gamePositionX;
    }

    public float getGamePositionY() {
        return gamePositionY;
    }

    public void setGamePositionX(float gamePositionX) {
        this.gamePositionX = gamePositionX;

        this.position.x = gamePositionX * 2f;
    }

    public void setGridPositionY(float gamePositionY, float gridLength) {
        this.gamePositionY = gamePositionY;
        this.position.y = (gridLength - 0.5f - gamePositionY) * 2f;
    }

    public void setGridPosition(float gamePositionX, float gamePositionY, float gridLength) {
        if (gamePositionX > this.gamePositionX) {
            this.rotationAngle = (float) ((-90f * Math.PI) / 180.0f);
            this.rotation = new Vector3f(0f, 0f, 1f);
        } else if (gamePositionX < this.gamePositionX) {
            this.rotationAngle = (float) ((90f * Math.PI) / 180.0f);

            this.rotation = new Vector3f(0f, 0f, 1f);
        }
        if (gamePositionY > this.gamePositionY) {
            this.rotationAngle = (float) ((-180f * Math.PI) / 180.0f);

            this.rotation = new Vector3f(0f, 0f, 1f);
        } else if (gamePositionY < this.gamePositionY) {
            this.rotationAngle = (float) ((0f * Math.PI) / 180.0f);

            this.rotation = new Vector3f(0f, 0f, 1f);
        }
        setGamePositionX(gamePositionX);
        setGridPositionY(gamePositionY, gridLength);
    }
}
