package ifrs.ibiruba.animalia.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public final class Navegacao {

    private static Stage stagePrincipal;

    private Navegacao() {}

    public static void setStage(Stage stage) {
        stagePrincipal = stage;
    }

    public static Stage getStage() {
        return stagePrincipal;
    }

    /** Navega para o FXML indicado usando as dimensões atuais do Stage. */
    public static void irPara(String nomeArquivo, String titulo) {
        double largura = stagePrincipal.isShowing() ? stagePrincipal.getWidth()  : 900;
        double altura  = stagePrincipal.isShowing() ? stagePrincipal.getHeight() : 600;
        irPara(nomeArquivo, titulo, (int) largura, (int) altura);
    }

    /** Navega para o FXML indicado com dimensões explícitas. */
    public static void irPara(String nomeArquivo, String titulo, int largura, int altura) {
        try {
            URL url = Navegacao.class.getResource("/ifrs/ibiruba/animalia/view/" + nomeArquivo);
            if (url == null) {
                throw new IOException("FXML não encontrado: " + nomeArquivo);
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent raiz = loader.load();
            Scene cena = new Scene(raiz, largura, altura);
            stagePrincipal.setTitle(titulo);
            stagePrincipal.setScene(cena);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar tela '" + nomeArquivo + "': " + e.getMessage(), e);
        }
    }
}
