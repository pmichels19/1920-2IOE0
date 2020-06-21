package Levels.Objects;

import Graphics.OBJLoader;
import Graphics.OBJModel;
import Graphics.OpenGL.Shader;
import Graphics.OpenGL.Texture;
import Levels.Framework.joml.Matrix4f;
import Levels.Framework.joml.Vector2f;
import Levels.Framework.joml.Vector3f;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class FloatingSphere {

    // Holds the model
    private OBJModel model;

    // Holds the current position
    protected Vector3f position;

    public Vector3f color = new Vector3f(1f, 0.03f, 0.23f);

    // Holds the scale
    public float scale;

    // holds the rotation
    protected Vector3f rotation;
    protected float rotationAngle = 90f;

    protected float gamePositionX;
    protected float gamePositionY;

    private double tVal = 0.0;
    Timer timer;

    // Name of the obj file in the res folder corresponding to the player model
    private static String objModelFile = "ball";

    // Path to the texture of the model
    private static String textureFile = "res/Models/nothing.png";

    // Path to the normal mapping of the model

    private static String normalMapFile = null;

    private float height = 3f;

    private Vector2f[] bezierCoords = new Vector2f[4];


    public FloatingSphere() {

        try {
            OBJModel model = OBJLoader.loadObjModel(this.objModelFile);

            if (textureFile != null) {
                // Set the texture
                model.setTexture(new Texture(textureFile));
            }

            // Add a normal map if there is one
            if (normalMapFile != null) {
                model.setNormalMap(new Texture(normalMapFile));
            }

            this.model = model;
            this.position = new Vector3f(0f, 0f, 0f);
            scale = 1f;
            rotationAngle = (float) ((-90f * Math.PI) / 180.0f);
            rotation = new Vector3f(0f, 0f, 1f);

            gamePositionX = 1;
            gamePositionY = 1;

            //Setting up the animation timer
            timer = new Timer(10, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    tVal += 0.005;
                } //increasing tVal
            }
            );

            timer.start();

            scale = 0.5f;

            bezierCoords[0] = new Vector2f(3.5f, 0.95f);
            bezierCoords[1] = new Vector2f(0f, 3f);
            bezierCoords[2] = new Vector2f(5.5f, 3f);
            bezierCoords[3] = new Vector2f(2f, 1.1f);
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    /**
     * renders the model specified in the subclass once
     */
    public void render(Shader shader) {
        model.render(shader);
    }

    public void render(Shader shader, Vector3f position) {
        float t = (float) Math.pow(Math.cos(Math.toRadians(tVal * 50)), 2);

        //get Bezier curve coords
        this.setCoordinates(this.bezierCoords, t);

        this.setPosition(new Vector3f(
                this.position.x + position.x - 2.5f,
                this.position.y + position.y - 1.5f, this.height
        ));

        shader.setUniform("transform", 1);
        shader.setUniform("changingColor", 1);
        shader.setUniform("objectColor", color);
        shader.setUniform("time", (float) tVal);
        Matrix4f modelTransform = new Matrix4f();
        modelTransform
                .translate(this.position)
                .scale(scale);

        shader.setUniform("modelTransform", modelTransform);
        model.render(shader);
        shader.setUniform("transform", 0);
        shader.setUniform("changingColor", 0);
        shader.setUniform("objectColor", new Vector3f(1,1,1));
    }

    /** de Casteljau's algorithm to find curve point
     *
     * @param bezierCoords for the bezier curve
     * @param t [0, 1]
     */
    private void setCoordinates(Vector2f[] bezierCoords, float t) {
        if (bezierCoords.length == 1) {
            this.setPosition(new Vector3f(bezierCoords[0].x, bezierCoords[0].y, 4));
        } else {
            Vector2f[] points = new Vector2f[bezierCoords.length - 1];
            for (int i = 0; i < points.length; i++) {
                float x = (1 - t) * bezierCoords[i].x + t * bezierCoords[i + 1].x;
                float y = (1 - t) * bezierCoords[i].y + t * bezierCoords[i + 1].y;
                points[i] = new Vector2f(x, y);
            }
            setCoordinates(points, t);
        }
    }

    /** Sets new Bezier coordinates.
     *
     * @param first coordinate
     * @param second coordinate
     * @param third coordinate
     * @param fourth coordinate
     */
    public void setBezierCoords(Vector2f first, Vector2f second, Vector2f third, Vector2f fourth) {
        bezierCoords[0] = first;
        bezierCoords[1] = second;
        bezierCoords[2] = third;
        bezierCoords[3] = fourth;
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

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public void setRotation(float angle, Vector3f vector) {
        this.rotationAngle = angle;
        this.rotation = vector;
    }

    public void setHeight(float height) {
        this.height = height;
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
