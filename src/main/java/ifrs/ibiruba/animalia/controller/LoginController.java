package ifrs.ibiruba.animalia.controller;

import ifrs.ibiruba.animalia.repository.UsuarioRepository;
import ifrs.ibiruba.animalia.service.LoginService;
import ifrs.ibiruba.animalia.model.Usuario;
import ifrs.ibiruba.animalia.util.Navegacao;
import ifrs.ibiruba.animalia.util.Sessao;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField     emailField;
    @FXML private PasswordField senhaField;
    @FXML private Label         mensagemLabel;
    @FXML private Button        entrarButton;
    @FXML private Hyperlink     criarContaLink;
    @FXML private Hyperlink     esqueceuSenhaLink;

    private final LoginService loginService =
        new LoginService(new UsuarioRepository());

    @FXML
    public void initialize() {
        configurarBindings();
    }

    private void configurarBindings() {
        entrarButton.disableProperty().bind(
            emailField.textProperty().isEmpty()
                .or(senhaField.textProperty().isEmpty())
        );
    }

    @FXML
    private void entrar() {
        mensagemLabel.setText("");
        try {
            Usuario usuario = loginService.autenticar(
                emailField.getText().trim(),
                senhaField.getText()
            );
            Sessao.getInstance().iniciar(usuario);
            Navegacao.abrirAreaPrincipal();
        } catch (IllegalArgumentException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void criarConta() {
        Navegacao.irPara("cadastro.fxml", "Animalia — Criar conta", 420, 560);
    }

    @FXML
    private void esqueceuSenha() {
        Navegacao.irPara("recuperacao.fxml", "Animalia — Recuperar senha", 420, 520);
    }
}
