package br.unifacs.a3.sistemasolar.render;

public record MeshData(float[] positions, float[] normals, float[] texCoords, int[] indices) {

    public static final int POSITION_COMPONENTS = 3;
    public static final int NORMAL_COMPONENTS = 3;
    public static final int TEX_COORD_COMPONENTS = 2;

    public MeshData {
        if (positions.length % POSITION_COMPONENTS != 0) {
            throw new IllegalArgumentException("Posições devem ter 3 componentes por vértice");
        }
        int vertexCount = positions.length / POSITION_COMPONENTS;
        if (normals.length != vertexCount * NORMAL_COMPONENTS) {
            throw new IllegalArgumentException("Esperado 1 normal por vértice");
        }
        if (texCoords.length != vertexCount * TEX_COORD_COMPONENTS) {
            throw new IllegalArgumentException("Esperado 1 coordenada de textura por vértice");
        }
        if (indices.length % 3 != 0) {
            throw new IllegalArgumentException("Índices devem formar triângulos completos");
        }
    }

    public int vertexCount() {
        return positions.length / POSITION_COMPONENTS;
    }

    public int indexCount() {
        return indices.length;
    }

    public int triangleCount() {
        return indices.length / 3;
    }
}
