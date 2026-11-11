package br.unifacs.a3.sistemasolar.app;

import br.unifacs.a3.sistemasolar.render.Renderer;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public final class Window {

    public static final String TITLE = "Sistema Solar 3D — A3";
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final int FPS = 60;

    private final JFrame frame;
    private final GLCanvas canvas;
    private final FPSAnimator animator;
    private final AtomicBoolean closing = new AtomicBoolean(false);

    public Window() {
        this(false);
    }

    public Window(boolean wireframe) {
        GLCapabilities capabilities = new GLCapabilities(GLProfile.get(GLProfile.GL2));
        capabilities.setDoubleBuffered(true);
        capabilities.setDepthBits(24);

        canvas = new GLCanvas(capabilities);
        canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        canvas.addGLEventListener(new Renderer(wireframe));

        animator = new FPSAnimator(canvas, FPS, true);

        frame = new JFrame(TITLE);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
    }

    public void show() {
        frame.setVisible(true);
        animator.start();
        canvas.requestFocusInWindow();
    }

    private void close() {
        if (!closing.compareAndSet(false, true)) {
            return;
        }
        Thread shutdown = new Thread(() -> {
            if (animator.isStarted()) {
                animator.stop();
            }
            SwingUtilities.invokeLater(() -> {
                frame.dispose();
                System.exit(0);
            });
        }, "window-shutdown");
        shutdown.start();
    }
}
