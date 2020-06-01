package Graphics;

import Levels.Framework.joml.Vector2f;
import Levels.Framework.joml.Vector3f;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OBJLoader {

    public static OBJModel loadObjModel(String fileName) throws IOException {
        // Create a file reader object
        FileReader fr = null;

        try {
            // Try to open the correct obj file
            fr = new FileReader(new File("res/Models/" + fileName + ".obj"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Transform the file reader to a buffered reader
        BufferedReader reader = new BufferedReader(fr);
        String line;                                    // holds the current line of the file
        List<Vector3f> vertices = new ArrayList<>();    // Create a list that holds al the 3d vertices
        List<Vector2f> textures = new ArrayList<>();    // Create a list that holds all the texture coordinates:
        List<Vector3f> normals = new ArrayList<>();     // Create a list that holds the normal vectors.
        List<Face> faces = new ArrayList<>();      // Create a list that holds the indices


        // Now parse the first model part
        while ((line = reader.readLine()) != null) {
            String[] currentLine = line.split(" ");
            String prefix = currentLine[0];   // get the prefix of the line
            switch (prefix) {
                case "v":  // add the vertex positions
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    // Store the extracted vertex
                    vertices.add(vertex);
                    break;
                case "vt":  // add the texture coords
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]));
                    // Store the extracted texture
                    textures.add(texture);
                    break;
                case "vn":  // add the normals
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    // Store the extracted normal
                    normals.add(normal);
                    break;
                case "f":  // keep track of the faces
                    Face face = new Face(currentLine[1], currentLine[2], currentLine[3]);
                    faces.add(face);
                default:
                    break;
            }
        }


        return reorderLists(vertices, textures, normals, faces);
    }

    public static OBJModel loadTexturedObjModel(String fileName) throws IOException {
        // Create a file reader object
        FileReader fr = null;

        try {
            // Try to open the correct obj file
            fr = new FileReader(new File("res/Models/" + fileName + ".obj"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Transform the file reader to a buffered reader
        BufferedReader reader = new BufferedReader(fr);
        String line;                                    // holds the current line of the file
        List<Vector3f> vertices = new ArrayList<>();    // Create a list that holds al the 3d vertices
        List<Vector2f> textures = new ArrayList<>();    // Create a list that holds all the texture coordinates:
        List<Vector3f> normals = new ArrayList<>();     // Create a list that holds the normal vectors.
        List<Face> faces = new ArrayList<>();      // Create a list that holds the indices


        // Now parse the first model part
        while ((line = reader.readLine()) != null) {
            String[] currentLine = line.split(" ");
            String prefix = currentLine[0];   // get the prefix of the line
            switch (prefix) {
                case "v":  // add the vertex positions
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    // Store the extracted vertex
                    vertices.add(vertex);
                    break;
                case "vt":  // add the texture coords
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]));
                    // Store the extracted texture
                    textures.add(texture);
                    break;
                case "vn":  // add the normals
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    // Store the extracted normal
                    normals.add(normal);
                    break;
                case "f":  // keep track of the faces
                    Face face = new Face(currentLine[1], currentLine[2], currentLine[3]);
                    faces.add(face);
                default:
                    break;
            }
        }


        return reorderLists(vertices, textures, normals, faces);
    }

    private static OBJModel reorderLists(List<Vector3f> posList, List<Vector2f> textCoordList, List<Vector3f> normList, List<Face> facesList) {
        List<Integer> indices = new ArrayList<>();
        // Create position array in the order it has been declared
        float[] posArr = new float[posList.size() * 3];
        int i = 0;
        for (Vector3f pos : posList) {
            posArr[i * 3] = pos.x;
            posArr[i * 3 + 1] = pos.y;
            posArr[i * 3 + 2] = pos.z;
            i++;
        }
        float[] textCoordArr = new float[posList.size() * 2];
        float[] normArr = new float[posList.size() * 3];

        for (Face face : facesList) {
            IdxGroup[] faceVertexIndices = face.getFaceVertexIndices();
            for (IdxGroup indValue : faceVertexIndices) {
                processFaceVertex(indValue, textCoordList, normList,
                        indices, textCoordArr, normArr);
            }
        }
        int[] indicesArr;
        indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();
        return new OBJModel(posArr, textCoordArr, normArr, indicesArr);
    }

    private static void processFaceVertex(IdxGroup indices, List<Vector2f> textCoordList,
                                          List<Vector3f> normList, List<Integer> indicesList,
                                          float[] texCoordArr, float[] normArr) {

        // Set index for vertex coordinates
        int posIndex = indices.idxPos;
        indicesList.add(posIndex);

        // Reorder texture coordinates
        if (indices.idxTextCoord >= 0) {
            Vector2f textCoord = textCoordList.get(indices.idxTextCoord);
            texCoordArr[posIndex * 2] = textCoord.x;
            texCoordArr[posIndex * 2 + 1] = 1 - textCoord.y;
        }
        if (indices.idxVecNormal >= 0) {
            // Reorder vectornormals
            Vector3f vecNorm = normList.get(indices.idxVecNormal);
            normArr[posIndex * 3] = vecNorm.x;
            normArr[posIndex * 3 + 1] = vecNorm.y;
            normArr[posIndex * 3 + 2] = vecNorm.z;
        }
    }

    protected static class Face {

        /**
         * List of idxGroup groups for a face triangle (3 vertices per face).
         */
        private final IdxGroup[] idxGroups;

        public Face(String v1, String v2, String v3) {
            idxGroups = new IdxGroup[3];
            // Parse the lines
            idxGroups[0] = parseLine(v1);
            idxGroups[1] = parseLine(v2);
            idxGroups[2] = parseLine(v3);
        }

        private IdxGroup parseLine(String line) {
            IdxGroup idxGroup = new IdxGroup();

            String[] lineTokens = line.split("/");
            int length = lineTokens.length;
            idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;
            if (length > 1) {
                // It can be empty if the obj does not define text coords
                String textCoord = lineTokens[1];
                idxGroup.idxTextCoord = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : IdxGroup.NO_VALUE;
                if (length > 2) {
                    idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
                }
            }

            return idxGroup;
        }

        public IdxGroup[] getFaceVertexIndices() {
            return idxGroups;
        }
    }

    protected static class IdxGroup {

        public static final int NO_VALUE = -1;

        public int idxPos;

        public int idxTextCoord;

        public int idxVecNormal;

        public IdxGroup() {
            idxPos = NO_VALUE;
            idxTextCoord = NO_VALUE;
            idxVecNormal = NO_VALUE;
        }
    }
}

