package Levels.Characters;

import Graphics.OBJModel;
import Graphics.OpenGL.Shader;
import Levels.Framework.Point;
import Levels.Framework.joml.Matrix4f;
import Levels.Framework.joml.Vector2f;
import Levels.Framework.joml.Vector3f;
import Levels.Objects.FloatingSphere;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public abstract class Character {
    // Holds the model
    private OBJModel model;

    // Holds the floating objects
    private FloatingSphere[] floatingElements;

    // Holds the current position
    private Vector3f position;

    // Holds maze position
    private Point mazePosition;

    // Holds the scale
    private float scale;

    // holds the rotation
    private Vector3f rotation;
    private float rotationAngle;
    protected int direction;

    private float gamePositionX;
    private float gamePositionY;

    // the amount of frames needed to move one tile
    //private int speed;

    // the values for max health and mana
    int max_health;
    int max_mana;

    // the values for current health and mana
    int cur_health;
    int cur_mana;

    // Speed of the character
    private float speed;

    //Time related objects to link animation to
    private double tVal = 0.0;
    Timer timer;

    public Character(int max_health, int max_mana, int speed, OBJModel model) {
        this.model = model;
        position = new Vector3f(0, 0f, 1.5f);
        scale = 1.2f;
        rotationAngle = (float) ((-90f * Math.PI) / 180.0f);
        rotation = new Vector3f(0f, 0f, 1f);

        gamePositionX = 1;
        gamePositionY = 1;

        // start the player off fresh, with full mana and health
        this.max_health = max_health;
        this.max_mana = max_mana;
        cur_health = max_health;
        cur_mana = max_mana;

        this.speed = speed;

        floatingElements = new FloatingSphere[2];
        floatingElements[0] = new FloatingSphere();
        floatingElements[1] = new FloatingSphere();

        floatingElements[0].setColor(new Vector3f(0f, 0.96f, 1f));
        floatingElements[0].setRotation(-90f, new Vector3f(0, 0, 1));
        floatingElements[0].setHeight(1.3f);
        floatingElements[0].setBezierCoords(new Vector2f(2f, 1.1f), new Vector2f(5.5f, 3f),
                new Vector2f(0f, 3f), new Vector2f(3.5f, 0.95f));

        //Setting up the animation timer
        timer = new Timer( 10, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tVal += 0.005;
            } //increasing tVal
        }
        );

        timer.start();
    }


//    abstract void generateModel(float x_pos, float y_pos);

    /**
     * renders the model specified in the subclass once
     */
    public void render(Shader shader, float x_pos, float y_pos) {
//        generateModel(x_pos, y_pos);

        model.render(shader);
    }

    public void render(Shader shader) {

        //floating spheres
        for (FloatingSphere a : floatingElements) {
            a.render(shader, position);
        }

        shader.setUniform("transform", 1);
        Matrix4f modelTransform = new Matrix4f();

        //floating animation linked to Z-axis
        float startingPoint = 1.5f;
        float floatingSpeed = 150f;
        position.z = (float) (startingPoint + Math.pow(Math.cos(Math.toRadians(tVal * floatingSpeed)),2)/2);

        modelTransform.translate(position).rotate(rotationAngle, rotation).scale(scale);
        shader.setUniform("modelTransform", modelTransform);
        model.render(shader);
        shader.setUniform("transform", 0);
    }

    public OBJModel getModel() {
        return model;
    }

    public void setModel(OBJModel model) {
        this.model = model;
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

    public int getHealth() {
        return cur_health;
    }

    public int getMana() {
        return cur_mana;
    }

    public void setHealth(int health) {
        cur_health = health;
    }

    public void setMana(int mana) {
        cur_mana = mana;
    }

    public float getGamePositionX() {
        return gamePositionX;
    }

    public float getGamePositionY() {
        return gamePositionY;
    }

//    public int getSpeed() {
//        return speed;
//    }

    public void setSpeed(int speed) {
        this.speed = speed;
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
            // right
            this.direction = 2;
        } else if (gamePositionX < this.gamePositionX) {
            this.rotationAngle = (float) ((90f * Math.PI) / 180.0f);

            this.rotation = new Vector3f(0f, 0f, 1f);
            // left
            this.direction = 0;
        }
        if (gamePositionY > this.gamePositionY) {
            this.rotationAngle = (float) ((-180f * Math.PI) / 180.0f);

            this.rotation = new Vector3f(0f, 0f, 1f);
            // down
            this.direction = 3;
        } else if (gamePositionY < this.gamePositionY) {
            this.rotationAngle = (float) ((0f * Math.PI) / 180.0f);

            this.rotation = new Vector3f(0f, 0f, 1f);
            // up
            this.direction = 1;
        }
        setGamePositionX(gamePositionX);
        setGridPositionY(gamePositionY, gridLength);
    }

    public Point getMazePosition() {
        return mazePosition;
    }

    public void setMazePosition(Point mazePosition) {
        this.mazePosition = mazePosition;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Initializes the starting position of the enemy
     *
     * @param x          x location
     * @param y          y location
     * @param gridLength length of the grid
     */
    public void initializePosition(int x, int y, int gridLength) {
        setMazePosition(new Point(x, y));
        setGridPosition((float) y, (float) x, gridLength);
    }
}
