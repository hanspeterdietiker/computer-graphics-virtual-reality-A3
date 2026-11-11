package br.unifacs.a3.sistemasolar.render;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;

public final class Renderer implements GLEventListener {

    static final double FOV_Y_DEGREES = 45.0;
    static final double NEAR = 0.1;
    static final double FAR = 1000.0;
    static final double MAX_DELTA_SECONDS = 0.25;
    static final double PREVIEW_SPIN_DEGREES_PER_SECOND = 20.0;

    private static final double NANOS_PER_SECOND = 1_000_000_000.0;
    private static final float PREVIEW_DISTANCE = 4f;
    private static final float PREVIEW_TILT_DEGREES = 20f;

    private final boolean wireframe;
    private final SphereMesh sphere = new SphereMesh();

    private boolean hasPreviousFrame;
    private long previousFrameNanos;
    private double lastDeltaSeconds;
    private double aspectRatio = 1.0;
    private double previewAngleDegrees;

    public Renderer() {
        this(false);
    }

    public Renderer(boolean wireframe) {
        this.wireframe = wireframe;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        System.out.println("OpenGL " + gl.glGetString(GL.GL_VERSION)
                + " | " + gl.glGetString(GL.GL_RENDERER)
                + " | " + gl.glGetString(GL.GL_VENDOR));

        gl.glClearColor(0f, 0f, 0f, 1f);

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);

        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_BACK);
        gl.glFrontFace(GL.GL_CCW);

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, wireframe ? GL2GL3.GL_LINE : GL2GL3.GL_FILL);

        gl.setSwapInterval(1);

        sphere.upload(gl);
        System.out.println("Malha esférica: " + sphere.data().vertexCount() + " vértices, "
                + sphere.data().triangleCount() + " triângulos" + (wireframe ? " (wireframe)" : ""));

        hasPreviousFrame = false;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, Math.max(height, 1));
        aspectRatio = computeAspectRatio(width, height);
        loadPerspectiveProjection(gl);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        lastDeltaSeconds = computeDeltaSeconds(System.nanoTime());
        previewAngleDegrees = advancePreviewAngle(previewAngleDegrees, lastDeltaSeconds);

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();

        drawPreviewSphere(gl);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        sphere.dispose(drawable.getGL().getGL2());
        System.out.println("Contexto OpenGL encerrado; recursos liberados.");
    }

    public double getLastDeltaSeconds() {
        return lastDeltaSeconds;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public boolean isWireframe() {
        return wireframe;
    }

    double computeDeltaSeconds(long nowNanos) {
        if (!hasPreviousFrame) {
            hasPreviousFrame = true;
            previousFrameNanos = nowNanos;
            return 0.0;
        }
        double delta = (nowNanos - previousFrameNanos) / NANOS_PER_SECOND;
        previousFrameNanos = nowNanos;
        return Math.min(Math.max(delta, 0.0), MAX_DELTA_SECONDS);
    }

    static double computeAspectRatio(int width, int height) {
        return (double) Math.max(width, 1) / Math.max(height, 1);
    }

    static double advancePreviewAngle(double angleDegrees, double deltaSeconds) {
        return (angleDegrees + deltaSeconds * PREVIEW_SPIN_DEGREES_PER_SECOND) % 360.0;
    }

    private void drawPreviewSphere(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(0f, 0f, -PREVIEW_DISTANCE);
        gl.glRotatef(PREVIEW_TILT_DEGREES, 1f, 0f, 0f);
        gl.glRotated(previewAngleDegrees, 0.0, 1.0, 0.0);
        gl.glColor3f(0.35f, 0.65f, 1f);
        sphere.draw(gl);
        gl.glPopMatrix();
    }

    private void loadPerspectiveProjection(GL2 gl) {
        double halfHeight = NEAR * Math.tan(Math.toRadians(FOV_Y_DEGREES) / 2.0);
        double halfWidth = halfHeight * aspectRatio;

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustum(-halfWidth, halfWidth, -halfHeight, halfHeight, NEAR, FAR);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    }
}
