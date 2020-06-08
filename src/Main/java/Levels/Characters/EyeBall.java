package Levels.Characters;

import Graphics.OBJLoader;
import Graphics.OBJModel;
import Graphics.OpenGL.Texture;
import Levels.Framework.joml.Vector3f;

import java.io.IOException;

public class EyeBall extends Enemy {
    // Name of the obj file in the res folder corresponding to the eyeball model
    private static String objModelFile = "eyeball";

    // Path to the texture of the model
    private static String textureFile = "res/Models/eyeball.jpg";

    // Path to the normal mapping of the model
    private static String normalMapFile = "res/Models/eyeball_normal.jpg";
//    private static String normalMapFile = null;

    // Speed of the eyeball
    private static float speed = 1f / 30f;

    public EyeBall(int max_health, int max_mana) {
        super(max_health, max_mana, null);
        OBJModel eyeModel = null;
        try {
            // Create the model
            eyeModel = OBJLoader.loadObjModel(objModelFile);

            if (textureFile != null) {
                // Set the texture
                eyeModel.setTexture(new Texture(textureFile));
            }

            // Add a normal map if there is one
            if (normalMapFile != null) {
                eyeModel.setNormalMap(new Texture(normalMapFile));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        setScale(0.4f);
        setPosition(new Vector3f(1f, 1f, 2f));
        setSpeed(speed);
        setDetectionDistance(10);
        setModel(eyeModel);
    }

    @Override
    public void setGridPosition(float gamePositionX, float gamePositionY, float gridLength) {
        if (gamePositionX > getGamePositionX()) {
            float rotationAngle = (float) ((0f * Math.PI) / 180.0f);
            Vector3f rotation = new Vector3f(0f, 0f, 1f);
            setRotation(rotationAngle, rotation);
        } else if (gamePositionX < getGamePositionX()) {
            float rotationAngle = (float) ((180f * Math.PI) / 180.0f);
            Vector3f rotation = new Vector3f(0f, 0f, 1f);
            setRotation(rotationAngle, rotation);
        }
        if (gamePositionY > getGamePositionY()) {
            float rotationAngle = (float) ((-90f * Math.PI) / 180.0f);
            Vector3f rotation = new Vector3f(0f, 0f, 1f);
            setRotation(rotationAngle, rotation);
        } else if (gamePositionY < getGamePositionY()) {
            float rotationAngle = (float) ((90f * Math.PI) / 180.0f);
            Vector3f rotation = new Vector3f(0f, 0f, 1f);
            setRotation(rotationAngle, rotation);
        }
        setGamePositionX(gamePositionX);
        setGridPositionY(gamePositionY, gridLength);
    }
}
