package ifrs.ibiruba.animalia.util;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Navegação do sistema.
 * <ul>
 *   <li>Telas avulsas (login, cadastro, recuperação): {@link #irPara} troca a cena inteira.</li>
 *   <li>Área logada: {@link #abrirAreaPrincipal()} cria a janela principal uma única vez e
 *       {@link #abrir(Modulo)} troca apenas o conteúdo central — a janela mantém o tamanho
 *       e o estado maximizado escolhidos pelo usuário.</li>
 * </ul>
 */
public final class Navegacao {

    private static final String PASTA_VIEW = "/ifrs/ibiruba/animalia/view/";
    private static final int LARGURA_PRINCIPAL = 900;
    private static final int ALTURA_PRINCIPAL  = 600;

    private static Stage stagePrincipal;
    private static Pane  areaConteudo;
    private static final ReadOnlyObjectWrapper<Modulo> moduloAtual = new ReadOnlyObjectWrapper<>();

    private Navegacao() {}

    public static void setStage(Stage stage) {
        stagePrincipal = stage;
    }

    public static Stage getStage() {
        return stagePrincipal;
    }

    /** Módulo exibido na área central (null fora da área logada). */
    public static ReadOnlyObjectProperty<Modulo> moduloAtualProperty() {
        return moduloAtual.getReadOnlyProperty();
    }

    /** Chamado pelo controller da janela principal para registrar onde os módulos são exibidos. */
    public static void registrarAreaConteudo(Pane area) {
        areaConteudo = area;
    }

    /** Troca a cena inteira por uma tela avulsa com tamanho fixo (login, cadastro, recuperação). */
    public static void irPara(String nomeArquivo, String titulo, int largura, int altura) {
        Parent raiz = carregar(nomeArquivo);
        sairDaAreaPrincipal();
        stagePrincipal.setMaximized(false);
        stagePrincipal.setTitle(titulo);
        stagePrincipal.setScene(new Scene(raiz, largura, altura));
        stagePrincipal.sizeToScene();
    }

    /** Monta a janela principal (menu + sidebar + área central) e abre o Dashboard. */
    public static void abrirAreaPrincipal() {
        Parent raiz = carregar("principal.fxml"); // registra areaConteudo
        stagePrincipal.setScene(new Scene(raiz, LARGURA_PRINCIPAL, ALTURA_PRINCIPAL));
        stagePrincipal.sizeToScene();
        stagePrincipal.centerOnScreen();
        abrir(Modulo.DASHBOARD);
    }

    /** Exibe o módulo na área central, sem recriar a cena nem alterar o tamanho da janela. */
    public static void abrir(Modulo modulo) {
        if (areaConteudo == null) {
            throw new IllegalStateException("A janela principal ainda não foi aberta.");
        }
        Parent conteudo = carregar(modulo.getFxml());
        areaConteudo.getChildren().setAll(conteudo);
        moduloAtual.set(modulo);
        stagePrincipal.setTitle("Animalia — " + modulo.getTitulo());
    }

    private static void sairDaAreaPrincipal() {
        areaConteudo = null;
        moduloAtual.set(null);
    }

    private static Parent carregar(String nomeArquivo) {
        try {
            URL url = Navegacao.class.getResource(PASTA_VIEW + nomeArquivo);
            if (url == null) {
                throw new IOException("FXML não encontrado: " + nomeArquivo);
            }
            return new FXMLLoader(url).load();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar tela '" + nomeArquivo + "': " + e.getMessage(), e);
        }
    }
}
