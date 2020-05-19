package Graphics.OpenGL;

public class Maths {

    /**
     * Maths method to compute a crossproduct.
     *
     * @param a vector a
     * @param b vector b
     * @return the crossproduct of vector a and vector b
     */
    public static float[] getCrossproduct(float[] a, float[] b) {
        float i = a[1] * b[2] - a[2] * b[1];
        float j = a[2] * b[0] - a[0] * b[2];
        float k = a[0] * b[1] - a[1] * b[0];
        return (new float[]{i, j, k});
    }

    /**
     * Maths method to compute the normal of a corner in a quad.
     *
     * @param vertices list of the four vertices in the quad
     * @param i index of the vertex (corner) to compute the normal for
     * @return the normal of vertex i
     */
    public static float[] getNormal(float[] vertices, int i) {
        float[] v1 = new float[] {vertices[0], vertices[1], vertices[2]};
        float[] v2 = new float[] {vertices[3], vertices[4], vertices[5]};
        float[] v3 = new float[] {vertices[6], vertices[7], vertices[8]};
        float[] v4 = new float[] {vertices[9], vertices[10], vertices[11]};
        float[][] v = new float[][] {v1,v2,v3,v4};

        float[] n;

        if (i == 0) { n = getCrossproduct(subtract(v[2], v[0]), subtract(v[1], v[0])); }
        else if (i == 1) { n = getCrossproduct(subtract(v[0], v[1]), subtract(v[2], v[1])); }
        else if (i == 2) { n = getCrossproduct(subtract(v[1], v[2]), subtract(v[3], v[2])); }
        else { n = getCrossproduct(subtract(v[2], v[3]), subtract(v[0], v[3])); }

        return n;
    }

    /**
     * Maths method to compute the normal of all corners in a quad.
     *
     * @param vertices list of the four vertices in the quad
     * @return the normal of all vertices
     */
    public static float[] getNormals(float[] vertices) {
        float[][] normals = new float[][] {
                getNormal(vertices, 0),
                getNormal(vertices, 1),
                getNormal(vertices, 2),
                getNormal(vertices, 3)
        };
        return flatten(normals);
    }

    /**
     * Convert float[][] to float[].
     *
     * @param fll input list
     * @return flattened list
     */
    public static float[] flatten(float[][] fll) {
        float[] result = new float[fll.length*fll[0].length];
        int pos = 0;

        for (float[] fl : fll) {
            for (float f : fl) {
                result[pos] = f;
                pos++;
            }
        }

        return result;
    }

    /**
     * Maths method to subtract a vector from another vector.
     *
     * @param a vector a
     * @param b vector b
     * @return vector a - vector b
     */
    private static float[] subtract(float[] a, float[] b) {
        float[] r = a;
        for (int i = 0; i < a.length; i++) {
            r[i] -= b[i];
        }
        return r;
    }
}
