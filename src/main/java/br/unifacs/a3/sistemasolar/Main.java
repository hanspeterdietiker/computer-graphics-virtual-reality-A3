package br.unifacs.a3.sistemasolar;

import br.unifacs.a3.sistemasolar.app.Window;
import com.jogamp.opengl.GLProfile;
import javax.swing.SwingUtilities;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        System.out.println("Sistema Solar 3D — A3 | Java " + System.getProperty("java.version")
                + " (" + System.getProperty("os.name") + ", " + System.getProperty("os.arch") + ")");

        if (!isAwtInternalsOpen()) {
            System.err.println("O JOGL precisa de acesso ao pacote interno java.desktop/sun.awt."
                    + " Execute a JVM com: --add-opens java.desktop/sun.awt=ALL-UNNAMED");
            System.exit(1);
        }

        GLProfile.initSingleton();
        if (!GLProfile.isAvailable(GLProfile.GL2)) {
            System.err.println("O perfil OpenGL GL2 é necessário para a aplicação. Verifique o driver de vídeo.");
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> new Window().show());
    }

    private static boolean isAwtInternalsOpen() {
        return ModuleLayer.boot().findModule("java.desktop")
                .map(desktop -> desktop.isOpen("sun.awt", Main.class.getModule()))
                .orElse(false);
    }
}
