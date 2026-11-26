package ifrs.ibiruba.animalia.controller;

import ifrs.ibiruba.animalia.util.Modulo;
import ifrs.ibiruba.animalia.util.Navegacao;
import ifrs.ibiruba.animalia.util.Sessao;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.EnumMap;
import java.util.Map;

/** Menu lateral fixo da janela principal; destaca o módulo aberto. */
public class SidebarController {

    private static final String CLASSE_ATIVO = "sidebar-btn-ativo";

    @FXML private Button dashboardButton;
    @FXML private Button agendamentosButton;
    @FXML private Button tutoresButton;
    @FXML private Button petsButton;
    @FXML private Button servicosButton;

    private final Map<Modulo, Button> botoes = new EnumMap<>(Modulo.class);

    // Referência forte mantida pelo controller; a propriedade estática só guarda uma referência fraca,
    // então sidebars de sessões anteriores (após logout) podem ser coletadas.
    private final ChangeListener<Modulo> aoTrocarModulo = (obs, antigo, novo) -> destacar(novo);

    @FXML
    public void initialize() {
        botoes.put(Modulo.DASHBOARD,    dashboardButton);
        botoes.put(Modulo.AGENDAMENTOS, agendamentosButton);
        botoes.put(Modulo.TUTORES,      tutoresButton);
        botoes.put(Modulo.PETS,         petsButton);
        botoes.put(Modulo.SERVICOS,     servicosButton);

        Navegacao.moduloAtualProperty().addListener(new WeakChangeListener<>(aoTrocarModulo));
        destacar(Navegacao.moduloAtualProperty().get());
    }

    private void destacar(Modulo modulo) {
        botoes.forEach((m, botao) -> {
            botao.getStyleClass().remove(CLASSE_ATIVO);
            if (m == modulo) {
                botao.getStyleClass().add(CLASSE_ATIVO);
            }
        });
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
