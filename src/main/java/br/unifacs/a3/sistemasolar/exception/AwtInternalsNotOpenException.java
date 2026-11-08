package br.unifacs.a3.sistemasolar.exception;

public final class AwtInternalsNotOpenException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    static final String REQUIRED_FLAG = "--add-opens java.desktop/sun.awt=ALL-UNNAMED";

    public AwtInternalsNotOpenException() {
        super("O JOGL precisa de acesso ao pacote interno java.desktop/sun.awt."
                + " Execute a JVM com: " + REQUIRED_FLAG);
    }

    public static void throwIfNotOpen() {
        if (!isAwtInternalsOpen()) {
            throw new AwtInternalsNotOpenException();
        }
    }

    public static boolean isAwtInternalsOpen() {
        return ModuleLayer.boot().findModule("java.desktop")
                .map(desktop -> desktop.isOpen("sun.awt", AwtInternalsNotOpenException.class.getModule()))
                .orElse(false);
    }
}
