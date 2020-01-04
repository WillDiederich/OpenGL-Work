package a2;

public class PlanetTwoMoon {
    float[] vertices;
    float[] textures;
    public PlanetTwoMoon(){
        vertices = new float[]
                {
                    0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
                    1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, -1.0f,
                    1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f,
                    0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f,
                    .0f, 0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, -1.0f,
                    -1.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, -1, -1.0f, 0.0f, 1.0f
                };

        textures = new float[]
                {
                    0.125f, 1.0f, 0.25f, 0.0f, 0.0f, 0.0f, 0.375f, 1.0f, 0.5f, 0.0f, 0.25f, 0.0f,
                    0.625f, 1.0f, 0.75f, 0.0f, 0.5f, 0.0f, 0.875f, 1.0f, 1.0f, 0.0f, 0.75f, 0.0f,
                    0.125f, 1.0f, 0.0f, 0.0f, 0.25f, 0.0f, 0.375f, 1.0f, 0.25f, 0.0f, 0.5f, 0.0f,
                    0.625f, 1.0f, 0.5f, 0.0f, 0.75f, 0.0f, 0.875f, 1.0f, 0.75f, 0.0f, 1.0f, 0.0f
                };
    }

    public float[] getVertices(){
        return vertices;
    }

    public float[] getTextures(){
        return textures;
    }

}
