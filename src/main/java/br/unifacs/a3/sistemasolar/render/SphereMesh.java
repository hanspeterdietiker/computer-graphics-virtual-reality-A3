package br.unifacs.a3.sistemasolar.render;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLPointerFunc;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public final class SphereMesh {

    public static final int DEFAULT_STACKS = 48;
    public static final int DEFAULT_SLICES = 64;
    public static final int MIN_STACKS = 2;
    public static final int MIN_SLICES = 3;

    private final MeshData data;
    private final int[] bufferIds = new int[2];
    private boolean uploaded;

    public SphereMesh() {
        this(DEFAULT_STACKS, DEFAULT_SLICES);
    }

    public SphereMesh(int stacks, int slices) {
        this.data = generate(stacks, slices);
    }

    public MeshData data() {
        return data;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public static MeshData generate(int stacks, int slices) {
        if (stacks < MIN_STACKS) {
            throw new IllegalArgumentException("stacks deve ser >= " + MIN_STACKS + ", recebeu " + stacks);
        }
        if (slices < MIN_SLICES) {
            throw new IllegalArgumentException("slices deve ser >= " + MIN_SLICES + ", recebeu " + slices);
        }

        int vertexCount = (stacks + 1) * (slices + 1);
        float[] positions = new float[vertexCount * MeshData.POSITION_COMPONENTS];
        float[] normals = new float[vertexCount * MeshData.NORMAL_COMPONENTS];
        float[] texCoords = new float[vertexCount * MeshData.TEX_COORD_COMPONENTS];

        int vertex = 0;
        for (int i = 0; i <= stacks; i++) {
            double phi = Math.PI * i / stacks;
            double sinPhi = Math.sin(phi);
            double cosPhi = Math.cos(phi);
            for (int j = 0; j <= slices; j++) {
                double theta = 2.0 * Math.PI * j / slices;
                double x = sinPhi * Math.cos(theta);
                double y = cosPhi;
                double z = -sinPhi * Math.sin(theta);
                double length = Math.sqrt(x * x + y * y + z * z);

                int p = vertex * MeshData.POSITION_COMPONENTS;
                positions[p] = (float) x;
                positions[p + 1] = (float) y;
                positions[p + 2] = (float) z;

                normals[p] = (float) (x / length);
                normals[p + 1] = (float) (y / length);
                normals[p + 2] = (float) (z / length);

                int t = vertex * MeshData.TEX_COORD_COMPONENTS;
                texCoords[t] = (float) j / slices;
                texCoords[t + 1] = (float) i / stacks;

                vertex++;
            }
        }

        int[] indices = new int[6 * stacks * slices];
        int rowLength = slices + 1;
        int k = 0;
        for (int i = 0; i < stacks; i++) {
            for (int j = 0; j < slices; j++) {
                int topLeft = i * rowLength + j;
                int bottomLeft = topLeft + rowLength;
                int topRight = topLeft + 1;
                int bottomRight = bottomLeft + 1;

                indices[k++] = topLeft;
                indices[k++] = bottomLeft;
                indices[k++] = topRight;

                indices[k++] = topRight;
                indices[k++] = bottomLeft;
                indices[k++] = bottomRight;
            }
        }

        return new MeshData(positions, normals, texCoords, indices);
    }

    public void upload(GL2 gl) {
        if (uploaded) {
            dispose(gl);
        }

        float[] positions = data.positions();
        float[] normals = data.normals();
        float[] texCoords = data.texCoords();

        FloatBuffer vertices = Buffers.newDirectFloatBuffer(positions.length + normals.length + texCoords.length);
        vertices.put(positions).put(normals).put(texCoords).flip();
        IntBuffer indices = Buffers.newDirectIntBuffer(data.indices());

        gl.glGenBuffers(bufferIds.length, bufferIds, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, (long) vertices.limit() * Buffers.SIZEOF_FLOAT, vertices, GL.GL_STATIC_DRAW);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, (long) indices.limit() * Buffers.SIZEOF_INT, indices, GL.GL_STATIC_DRAW);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);

        uploaded = true;
    }

    public void draw(GL2 gl) {
        if (!uploaded) {
            throw new IllegalStateException("A malha precisa ser enviada para a GPU com upload() antes de desenhar");
        }

        long normalsOffset = (long) data.positions().length * Buffers.SIZEOF_FLOAT;
        long texCoordsOffset = normalsOffset + (long) data.normals().length * Buffers.SIZEOF_FLOAT;

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[0]);
        gl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
        gl.glVertexPointer(MeshData.POSITION_COMPONENTS, GL.GL_FLOAT, 0, 0L);
        gl.glNormalPointer(GL.GL_FLOAT, 0, normalsOffset);
        gl.glTexCoordPointer(MeshData.TEX_COORD_COMPONENTS, GL.GL_FLOAT, 0, texCoordsOffset);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);
        gl.glDrawElements(GL.GL_TRIANGLES, data.indexCount(), GL.GL_UNSIGNED_INT, 0L);

        gl.glDisableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
    }

    public void dispose(GL2 gl) {
        if (!uploaded) {
            return;
        }
        gl.glDeleteBuffers(bufferIds.length, bufferIds, 0);
        bufferIds[0] = 0;
        bufferIds[1] = 0;
        uploaded = false;
    }
}
