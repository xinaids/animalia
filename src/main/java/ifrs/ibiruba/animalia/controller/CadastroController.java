package ifrs.ibiruba.animalia.controller;

import ifrs.ibiruba.animalia.repository.UsuarioRepository;
import ifrs.ibiruba.animalia.service.CadastroService;
import ifrs.ibiruba.animalia.util.Navegacao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CadastroController {

    @FXML private TextField     nomeField;
    @FXML private TextField     emailField;
    @FXML private PasswordField senhaField;
    @FXML private PasswordField confirmarSenhaField;
    @FXML private Label         mensagemLabel;
    @FXML private Button        cadastrarButton;
    @FXML private Hyperlink     voltarLoginLink;

    private final CadastroService cadastroService =
        new CadastroService(new UsuarioRepository());

    @FXML
    public void initialize() {
        configurarBindings();
    }

    private void configurarBindings() {
        cadastrarButton.disableProperty().bind(
            nomeField.textProperty().isEmpty()
                .or(emailField.textProperty().isEmpty())
                .or(senhaField.textProperty().isEmpty())
                .or(confirmarSenhaField.textProperty().isEmpty())
        );
    }

    @FXML
    private void cadastrar() {
        mensagemLabel.setText("");
        try {
            String codigo = cadastroService.registrar(
                nomeField.getText().trim(),
                emailField.getText().trim(),
                senhaField.getText(),
                confirmarSenhaField.getText()
            );
            exibirCodigoRecuperacao(codigo);
            Navegacao.irPara("login.fxml", "Animalia — Login", 400, 500);
        } catch (Exception e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    private void exibirCodigoRecuperacao(String codigo) {
        TextArea area = new TextArea(codigo);
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefHeight(60);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Código de recuperação");
        alert.setHeaderText("Conta criada com sucesso!");
        alert.setContentText(
            "Guarde este código — ele não será exibido novamente.\n" +
            "Você precisará dele para redefinir a senha caso a esqueça.\n\n" +
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
