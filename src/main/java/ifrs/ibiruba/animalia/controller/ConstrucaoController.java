package ifrs.ibiruba.animalia.controller;

import ifrs.ibiruba.animalia.util.Navegacao;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ConstrucaoController {

    @FXML private Button voltarButton;

    @FXML
    private void voltar() {
        Navegacao.irPara("dashboard.fxml", "Animalia — Dashboard", 900, 600);
    }
}
