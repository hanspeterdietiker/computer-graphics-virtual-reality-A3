package br.unifacs.a3.sistemasolar.render;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.unifacs.a3.sistemasolar.math.Vec3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SphereMeshTest {

    private static final float UNIT_TOLERANCE = 1e-6f;
    private static final float DEGENERATE_AREA = 1e-9f;

    @ParameterizedTest
    @CsvSource({"48, 64", "2, 3", "10, 7", "1000, 3"})
    void vertexCountIsStacksPlusOneTimesSlicesPlusOne(int stacks, int slices) {
        MeshData mesh = SphereMesh.generate(stacks, slices);
        int expected = (stacks + 1) * (slices + 1);
        assertEquals(expected, mesh.vertexCount());
        assertEquals(expected * 3, mesh.normals().length);
        assertEquals(expected * 2, mesh.texCoords().length);
    }

    @ParameterizedTest
    @CsvSource({"48, 64", "2, 3", "10, 7", "1000, 3"})
    void indexCountIsSixTimesStacksTimesSlices(int stacks, int slices) {
        assertEquals(6 * stacks * slices, SphereMesh.generate(stacks, slices).indexCount());
    }

    @Test
    void defaultConstructorUsesScopeResolution() {
        MeshData mesh = new SphereMesh().data();
        assertEquals(49 * 65, mesh.vertexCount());
        assertEquals(6 * 48 * 64, mesh.indexCount());
    }

    @Test
    void everyNormalHasUnitLength() {
        MeshData mesh = defaultMesh();
        for (int v = 0; v < mesh.vertexCount(); v++) {
            assertEquals(1f, normal(mesh, v).length(), UNIT_TOLERANCE, "normal do vértice " + v);
        }
    }

    @Test
    void positionsLieOnUnitSphereAndMatchNormals() {
        MeshData mesh = defaultMesh();
        for (int v = 0; v < mesh.vertexCount(); v++) {
            Vec3 p = position(mesh, v);
            Vec3 n = normal(mesh, v);
            assertEquals(1f, p.length(), UNIT_TOLERANCE, "posição do vértice " + v);
            assertEquals(0f, p.subtract(n).length(), UNIT_TOLERANCE, "normal do vértice " + v);
        }
    }

    @Test
    void texCoordsAreInsideUnitSquare() {
        float[] uv = defaultMesh().texCoords();
        for (int k = 0; k < uv.length; k++) {
            assertTrue(uv[k] >= 0f && uv[k] <= 1f, "uv[" + k + "] = " + uv[k]);
        }
    }

    @Test
    void texCoordsSpanWholeTexture() {
        MeshData mesh = defaultMesh();
        float[] uv = mesh.texCoords();
        int last = (mesh.vertexCount() - 1) * 2;
        assertEquals(0f, uv[0]);
        assertEquals(0f, uv[1]);
        assertEquals(1f, uv[last]);
        assertEquals(1f, uv[last + 1]);
    }

    @Test
    void firstRowIsNorthPoleAndLastRowIsSouthPole() {
        MeshData mesh = defaultMesh();
        assertVec(Vec3.UNIT_Y, position(mesh, 0));
        assertVec(Vec3.UNIT_Y.scale(-1f), position(mesh, mesh.vertexCount() - 1));
    }

    @Test
    void longitudeAdvancesEastwardWhenRotatingAroundPositiveY() {
        int stacks = 2;
        int slices = 4;
        MeshData mesh = SphereMesh.generate(stacks, slices);
        int equatorRow = (slices + 1);
        assertVec(Vec3.UNIT_X, position(mesh, equatorRow));
        assertVec(Vec3.UNIT_Z.scale(-1f), position(mesh, equatorRow + 1));
    }

    @Test
    void seamColumnsShareSamePositions() {
        int stacks = 48;
        int slices = 64;
        MeshData mesh = SphereMesh.generate(stacks, slices);
        for (int i = 0; i <= stacks; i++) {
            int first = i * (slices + 1);
            int last = first + slices;
            assertEquals(0f, position(mesh, first).subtract(position(mesh, last)).length(), UNIT_TOLERANCE,
                    "costura na linha " + i);
        }
    }

    @Test
    void indicesReferenceValidVerticesAndCoverAllOfThem() {
        MeshData mesh = defaultMesh();
        boolean[] used = new boolean[mesh.vertexCount()];
        for (int index : mesh.indices()) {
            assertTrue(index >= 0 && index < mesh.vertexCount(), "índice fora do intervalo: " + index);
            used[index] = true;
        }
        for (int v = 0; v < used.length; v++) {
            assertTrue(used[v], "vértice " + v + " não é usado por nenhum triângulo");
        }
    }

    @Test
    void trianglesAreCounterClockwiseSeenFromOutside() {
        MeshData mesh = defaultMesh();
        int[] idx = mesh.indices();
        int nonDegenerate = 0;
        for (int t = 0; t < mesh.triangleCount(); t++) {
            Vec3 a = position(mesh, idx[3 * t]);
            Vec3 b = position(mesh, idx[3 * t + 1]);
            Vec3 c = position(mesh, idx[3 * t + 2]);
            Vec3 faceNormal = b.subtract(a).cross(c.subtract(a));
            if (faceNormal.length() < DEGENERATE_AREA) {
                continue;
            }
            Vec3 centroid = a.add(b).add(c);
            assertTrue(faceNormal.dot(centroid) > 0f, "triângulo " + t + " está no sentido horário visto de fora");
            nonDegenerate++;
        }
        assertEquals(mesh.triangleCount() - 2 * 64, nonDegenerate);
    }

    @Test
    void surfaceAreaApproachesUnitSphereArea() {
        MeshData mesh = defaultMesh();
        int[] idx = mesh.indices();
        double area = 0.0;
        for (int t = 0; t < mesh.triangleCount(); t++) {
            Vec3 a = position(mesh, idx[3 * t]);
            Vec3 b = position(mesh, idx[3 * t + 1]);
            Vec3 c = position(mesh, idx[3 * t + 2]);
            area += 0.5 * b.subtract(a).cross(c.subtract(a)).length();
        }
        assertTrue(area < 4.0 * Math.PI, "área " + area);
        assertEquals(4.0 * Math.PI, area, 0.05);
    }

    @Test
    void rejectsTooFewStacksOrSlices() {
        assertThrows(IllegalArgumentException.class, () -> SphereMesh.generate(1, 64));
        assertThrows(IllegalArgumentException.class, () -> SphereMesh.generate(48, 2));
        assertThrows(IllegalArgumentException.class, () -> new SphereMesh(0, 0));
    }

    @Test
    void generationDoesNotRequireOpenGl() {
        assertFalse(new SphereMesh(4, 4).isUploaded());
    }

    @Test
    void drawingBeforeUploadFails() {
        SphereMesh sphere = new SphereMesh(4, 4);
        assertThrows(IllegalStateException.class, () -> sphere.draw(null));
    }

    private static MeshData defaultMesh() {
        return SphereMesh.generate(SphereMesh.DEFAULT_STACKS, SphereMesh.DEFAULT_SLICES);
    }

    private static Vec3 position(MeshData mesh, int vertex) {
        float[] p = mesh.positions();
        return new Vec3(p[3 * vertex], p[3 * vertex + 1], p[3 * vertex + 2]);
    }

    private static Vec3 normal(MeshData mesh, int vertex) {
        float[] n = mesh.normals();
        return new Vec3(n[3 * vertex], n[3 * vertex + 1], n[3 * vertex + 2]);
    }

    private static void assertVec(Vec3 expected, Vec3 actual) {
        assertEquals(expected.x(), actual.x(), UNIT_TOLERANCE, "x de " + actual);
        assertEquals(expected.y(), actual.y(), UNIT_TOLERANCE, "y de " + actual);
        assertEquals(expected.z(), actual.z(), UNIT_TOLERANCE, "z de " + actual);
    }
}
