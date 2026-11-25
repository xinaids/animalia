package ifrs.ibiruba.animalia.controller;

import ifrs.ibiruba.animalia.repository.UsuarioRepository;
import ifrs.ibiruba.animalia.service.RecuperacaoService;
import ifrs.ibiruba.animalia.util.Navegacao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RecuperacaoController {

    @FXML private TextField     emailField;
    @FXML private TextField     codigoField;
    @FXML private PasswordField novaSenhaField;
    @FXML private PasswordField confirmarNovaSenhaField;
    @FXML private Label         mensagemLabel;
    @FXML private Button        redefinirButton;
    @FXML private Hyperlink     voltarLoginLink;

    private final RecuperacaoService recuperacaoService =
        new RecuperacaoService(new UsuarioRepository());

    @FXML
    public void initialize() {
        configurarBindings();
    }

    private void configurarBindings() {
        redefinirButton.disableProperty().bind(
            emailField.textProperty().isEmpty()
                .or(codigoField.textProperty().isEmpty())
                .or(novaSenhaField.textProperty().isEmpty())
                .or(confirmarNovaSenhaField.textProperty().isEmpty())
        );
    }

    @FXML
    private void redefinir() {
        mensagemLabel.setText("");
        try {
            String novoCodigo = recuperacaoService.redefinir(
                emailField.getText().trim(),
                codigoField.getText().trim(),
                novaSenhaField.getText(),
                confirmarNovaSenhaField.getText()
            );
            exibirNovoCodigo(novoCodigo);
            Navegacao.irPara("login.fxml", "Animalia — Login", 400, 500);
        } catch (IllegalStateException e) {
            mensagemLabel.setText(e.getMessage());
            redefinirButton.setDisable(true);
        } catch (IllegalArgumentException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    private void exibirNovoCodigo(String codigo) {
        TextArea area = new TextArea(codigo);
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefHeight(60);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Novo código de recuperação");
        alert.setHeaderText("Senha redefinida com sucesso!");
        alert.setContentText(
            "Um novo código de recuperação foi gerado.\n" +
            "Guarde-o — ele não será exibido novamente.\n\n" +
            "Código:");
        alert.getDialogPane().setExpandableContent(area);
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }

    @FXML
    private void voltarLogin() {
        Navegacao.irPara("login.fxml", "Animalia — Login", 400, 500);
    }
}
