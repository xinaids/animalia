package ifrs.ibiruba.animalia.util;

import ifrs.ibiruba.animalia.persistencia.ExclusaoBloqueadaException;
import ifrs.ibiruba.animalia.repository.BaseRepository;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Window;

import java.util.Optional;

/** Diálogos padronizados do sistema (confirmação de exclusão, avisos e erros). */
public final class Dialogos {

    private static final String CSS = "/ifrs/ibiruba/animalia/css/animalia.css";

    private Dialogos() {}

    /**
     * Pergunta ao usuário se deseja realmente excluir o registro.
     * O botão padrão (Enter) é "Cancelar", para evitar exclusões acidentais.
     *
     * @param tipo      tipo do registro, ex.: "tutor", "pet", "serviço"
     * @param descricao identificação do registro, ex.: nome do tutor
     * @return true somente se o usuário clicar em "Excluir"
     */
    public static boolean confirmarExclusao(String tipo, String descricao) {
        return confirmar(Alert.AlertType.CONFIRMATION, "Confirmar exclusão",
            "Excluir " + tipo + " \"" + descricao + "\"?",
            "Esta ação não pode ser desfeita.", "Excluir");
    }

    /**
     * Diálogo de confirmação com botões "Cancelar" e {@code textoAcao}.
     * O Enter aciona "Cancelar"; a ação exige clique explícito.
     *
     * @return true somente se o usuário clicar no botão da ação
     */
    public static boolean confirmar(Alert.AlertType tipoAlerta, String titulo, String cabecalho,
                                    String conteudo, String textoAcao) {
        ButtonType acao     = new ButtonType(textoAcao, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = criar(tipoAlerta, titulo, cabecalho, conteudo);
        alert.getButtonTypes().setAll(cancelar, acao);

        ((Button) alert.getDialogPane().lookupButton(acao)).setDefaultButton(false);
        ((Button) alert.getDialogPane().lookupButton(cancelar)).setDefaultButton(true);

        Optional<ButtonType> resposta = alert.showAndWait();
        return resposta.isPresent() && resposta.get() == acao;
    }

    /** Avisa que a exclusão não é permitida e explica o motivo. */
    public static void exclusaoBloqueada(String tipo, String motivo) {
        criar(Alert.AlertType.WARNING, "Exclusão não permitida",
            "Não é possível excluir este " + tipo + ".", motivo).showAndWait();
    }

    public static void erro(String titulo, String mensagem) {
        criar(Alert.AlertType.ERROR, titulo, titulo, mensagem).showAndWait();
    }

    /**
     * Fluxo completo de exclusão para as telas de CRUD:
     * 1. verifica vínculos — se houver, mostra o aviso e para;
     * 2. pede confirmação;
     * 3. exclui e executa {@code aoExcluir} (ex.: recarregar a tabela).
     *
     * @return true se o registro foi excluído
     */
    public static <T, ID> boolean excluirComConfirmacao(BaseRepository<T, ID> repositorio, ID id,
                                                        String tipo, String descricao,
                                                        Runnable aoExcluir) {
        try {
            Optional<String> bloqueio = repositorio.verificarExclusao(id);
            if (bloqueio.isPresent()) {
                exclusaoBloqueada(tipo, bloqueio.get());
                return false;
            }
            if (!confirmarExclusao(tipo, descricao)) {
                return false;
            }
            repositorio.excluir(id);
            if (aoExcluir != null) {
                aoExcluir.run();
            }
            return true;
        } catch (ExclusaoBloqueadaException e) {
            // Um vínculo pode ter surgido entre a verificação e a exclusão.
            exclusaoBloqueada(tipo, e.getMessage());
        } catch (Exception e) {
            erro("Erro ao excluir", "Não foi possível excluir o " + tipo + ".\n" + e.getMessage());
        }
        return false;
    }

    private static Alert criar(Alert.AlertType tipo, String titulo, String cabecalho, String conteudo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);

        Label texto = new Label(conteudo);
        texto.setWrapText(true);
        alert.getDialogPane().setContent(texto);

        Window dono = Navegacao.getStage();
        if (dono != null) {
            alert.initOwner(dono);
        }
        var css = Dialogos.class.getResource(CSS);
        if (css != null) {
            alert.getDialogPane().getStylesheets().add(css.toExternalForm());
        }
        return alert;
    }
}
