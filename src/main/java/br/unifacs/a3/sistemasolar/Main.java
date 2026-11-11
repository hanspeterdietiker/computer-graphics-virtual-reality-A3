package br.unifacs.a3.sistemasolar;

import br.unifacs.a3.sistemasolar.app.Window;
import br.unifacs.a3.sistemasolar.exception.AwtInternalsNotOpenException;
import com.jogamp.opengl.GLProfile;
import java.util.Arrays;
import javax.swing.SwingUtilities;

public final class Main {

    static final String WIREFRAME_FLAG = "--wireframe";

    private Main() {
    }

    public static void main(String[] args) {
        System.out.println("Sistema Solar 3D — A3 | Java " + System.getProperty("java.version")
                + " (" + System.getProperty("os.name") + ", " + System.getProperty("os.arch") + ")");

        AwtInternalsNotOpenException.throwIfNotOpen();

        GLProfile.initSingleton();
        if (!GLProfile.isAvailable(GLProfile.GL2)) {
            System.err.println("O perfil OpenGL GL2 é necessário para a aplicação. Verifique o driver de vídeo.");
            System.exit(1);
        }

        boolean wireframe = Arrays.asList(args).contains(WIREFRAME_FLAG);
        SwingUtilities.invokeLater(() -> new Window(wireframe).show());
    }
}
