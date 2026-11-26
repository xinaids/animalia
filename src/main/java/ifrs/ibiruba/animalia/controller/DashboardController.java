package ifrs.ibiruba.animalia.controller;

import ifrs.ibiruba.animalia.repository.AgendamentoRepository;
import ifrs.ibiruba.animalia.repository.PetRepository;
import ifrs.ibiruba.animalia.repository.ServicoRepository;
import ifrs.ibiruba.animalia.repository.TutorRepository;
import ifrs.ibiruba.animalia.util.Sessao;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/** Módulo Dashboard (área central da janela principal). Menu e sidebar ficam no PrincipalController. */
public class DashboardController {

    @FXML private Label     saudacaoLabel;
    @FXML private Label     agendamentosHojeLabel;
    @FXML private Label     tutoresLabel;
    @FXML private Label     petsLabel;
    @FXML private Label     servicosAtivosLabel;

    private final SimpleIntegerProperty agendamentosHoje  = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty totalTutores      = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty totalPets         = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty servicosAtivos    = new SimpleIntegerProperty(0);

    private final AgendamentoRepository agendamentoRepository = new AgendamentoRepository();
    private final TutorRepository       tutorRepository       = new TutorRepository();
    private final PetRepository         petRepository         = new PetRepository();
    private final ServicoRepository     servicoRepository     = new ServicoRepository();

    @FXML
    public void initialize() {
        configurarBindings();
        carregarDados();
        configurarSaudacao();
    }

    private void configurarBindings() {
        agendamentosHojeLabel.textProperty().bind(agendamentosHoje.asString());
        tutoresLabel.textProperty().bind(totalTutores.asString());
        petsLabel.textProperty().bind(totalPets.asString());
        servicosAtivosLabel.textProperty().bind(servicosAtivos.asString());
    }

    private void carregarDados() {
        try {
            agendamentosHoje.set((int) agendamentoRepository.contarAgendadosHoje());
            totalTutores.set((int) tutorRepository.contar());
            totalPets.set((int) petRepository.contar());
            servicosAtivos.set((int) servicoRepository.contarAtivos());
        } catch (Exception e) {
            // Falha silenciosa na carga dos cards; contagens ficam em zero
        }
    }

    private void configurarSaudacao() {
        if (Sessao.getInstance().estaAtiva()) {
            saudacaoLabel.setText("Bem-vindo, " + Sessao.getInstance().getUsuario().getNome() + "!");
        }
    }
}
