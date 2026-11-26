package ifrs.ibiruba.animalia.controller;

import ifrs.ibiruba.animalia.util.Modulo;
import ifrs.ibiruba.animalia.util.Navegacao;
import ifrs.ibiruba.animalia.util.Sessao;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

/** Janela principal: MenuBar e sidebar fixas; só a área central troca de conteúdo. */
public class PrincipalController {

    @FXML private StackPane conteudoPane;

    @FXML
    public void initialize() {
        Navegacao.registrarAreaConteudo(conteudoPane);
    }

    @FXML private void irDashboard()    { Navegacao.abrir(Modulo.DASHBOARD); }
    @FXML private void irAgendamentos() { Navegacao.abrir(Modulo.AGENDAMENTOS); }
    @FXML private void irTutores()      { Navegacao.abrir(Modulo.TUTORES); }
    @FXML private void irPets()         { Navegacao.abrir(Modulo.PETS); }
    @FXML private void irServicos()     { Navegacao.abrir(Modulo.SERVICOS); }

    @FXML
    private void sair() {
        Sessao.getInstance().encerrar();
        Navegacao.irPara("login.fxml", "Animalia — Login", 400, 500);
    }
}
