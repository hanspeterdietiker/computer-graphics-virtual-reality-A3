package br.unifacs.a3.sistemasolar;

import com.jogamp.opengl.GLProfile;

public final class Main {

    public static void main(String[] args) {
        System.out.println("Sistema Solar 3D — A3");
        System.out.println("Java " + System.getProperty("java.version")
                + " (" + System.getProperty("os.name") + ", " + System.getProperty("os.arch") + ")");

        GLProfile.initSingleton();
        boolean gl2 = GLProfile.isAvailable(GLProfile.GL2);
        System.out.println("JOGL carregado. Perfil padrão: " + GLProfile.getDefault().getName());
        System.out.println("Perfil GL2 disponível: " + (gl2 ? "sim" : "NÃO"));

        if (!gl2) {
            System.err.println("O perfil GL2 é necessário para a aplicação. Verifique o driver de vídeo.");
            System.exit(1);
        }
    }
}
