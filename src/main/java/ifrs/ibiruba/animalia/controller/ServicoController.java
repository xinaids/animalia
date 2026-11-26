package ifrs.ibiruba.animalia.controller;

import ifrs.ibiruba.animalia.model.Servico;
import ifrs.ibiruba.animalia.repository.ServicoRepository;
import ifrs.ibiruba.animalia.service.ServicoService;
import ifrs.ibiruba.animalia.util.Dialogos;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class ServicoController {

    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
    private static final PseudoClass  INATIVO = PseudoClass.getPseudoClass("inativo");

    // Lista
    @FXML private Label    totalLabel;
    @FXML private Button   novoButton;
    @FXML private TextField buscaField;
    @FXML private CheckBox mostrarInativosCheck;
    @FXML private TableView<Servico>              tabela;
    @FXML private TableColumn<Servico, String>     nomeColuna;
    @FXML private TableColumn<Servico, String>     descricaoColuna;
    @FXML private TableColumn<Servico, BigDecimal> precoColuna;
    @FXML private TableColumn<Servico, Integer>    duracaoColuna;
    @FXML private TableColumn<Servico, String>     statusColuna;
    @FXML private Label    vazioLabel;
    @FXML private Button   editarButton;
    @FXML private Button   alternarAtivoButton;
    @FXML private Button   excluirButton;
    @FXML private Label    statusLabel;

    // Formulário
    @FXML private VBox     formPainel;
    @FXML private Label    formTituloLabel;
    @FXML private TextField nomeField;
    @FXML private TextArea descricaoField;
    @FXML private TextField precoField;
    @FXML private TextField duracaoField;
    @FXML private CheckBox ativoCheck;
    @FXML private Label    mensagemLabel;
    @FXML private Button   cancelarButton;
    @FXML private Button   salvarButton;

    private final ServicoRepository servicoRepository = new ServicoRepository();
    private final ServicoService    servicoService    = new ServicoService(servicoRepository);

    private final ObservableList<Servico> servicos  = FXCollections.observableArrayList();
    private final FilteredList<Servico>   filtrados = new FilteredList<>(servicos);
    private final PauseTransition         limparStatus = new PauseTransition(Duration.seconds(4));

    /** null = cadastrando um novo serviço; caso contrário, id do serviço em edição. */
    private Long idEmEdicao;

    @FXML
    public void initialize() {
        configurarColunas();
        configurarFiltro();
        configurarBindings();
        limparStatus.setOnFinished(e -> statusLabel.setText(""));
        carregar(null);
    }

    // ─── Configuração ──────────────────────────────────────────

    private void configurarColunas() {
        nomeColuna.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        descricaoColuna.setCellValueFactory(c ->
            new SimpleStringProperty(Objects.requireNonNullElse(c.getValue().getDescricao(), "")));
        precoColuna.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPreco()));
        duracaoColuna.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDuracaoMinutos()));
        statusColuna.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isAtivo() ? "Ativo" : "Inativo"));

        nomeColuna.setComparator(ServicoRepository.comparadorNomes());
        descricaoColuna.setComparator(ServicoRepository.comparadorNomes());

        precoColuna.setCellFactory(col -> celulaFormatada(v -> MOEDA.format(v)));
        duracaoColuna.setCellFactory(col -> celulaFormatada(ServicoController::formatarDuracao));

        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Linhas de serviços inativos ficam esmaecidas; duplo clique abre a edição.
        tabela.setRowFactory(tv -> {
            TableRow<Servico> linha = new TableRow<>() {
                @Override
                protected void updateItem(Servico item, boolean vazio) {
                    super.updateItem(item, vazio);
                    pseudoClassStateChanged(INATIVO, !vazio && item != null && !item.isAtivo());
                }
            };
            linha.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && !linha.isEmpty()) {
                    editar();
                }
            });
            return linha;
        });
    }

    private void configurarFiltro() {
        filtrados.predicateProperty().bind(Bindings.createObjectBinding(
            this::criarFiltro, buscaField.textProperty(), mostrarInativosCheck.selectedProperty()));

        SortedList<Servico> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(tabela.comparatorProperty());
        tabela.setItems(ordenados);
    }

    private Predicate<Servico> criarFiltro() {
        String termo = buscaField.getText() == null ? "" : buscaField.getText().trim().toLowerCase();
        boolean incluirInativos = mostrarInativosCheck.isSelected();
        return s -> (incluirInativos || s.isAtivo())
            && (termo.isEmpty()
                || s.getNome().toLowerCase().contains(termo)
                || (s.getDescricao() != null && s.getDescricao().toLowerCase().contains(termo)));
    }

    private void configurarBindings() {
        var selecionado = tabela.getSelectionModel().selectedItemProperty();

        editarButton.disableProperty().bind(selecionado.isNull());
        excluirButton.disableProperty().bind(selecionado.isNull());
        alternarAtivoButton.disableProperty().bind(selecionado.isNull());
        alternarAtivoButton.textProperty().bind(Bindings.createStringBinding(
            () -> selecionado.get() != null && !selecionado.get().isAtivo() ? "Ativar" : "Desativar",
            selecionado));

        totalLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            int exibidos = filtrados.size();
            int total = servicos.size();
            String texto = exibidos + (exibidos == 1 ? " serviço exibido" : " serviços exibidos");
            return exibidos == total ? texto : texto + " de " + total;
        }, filtrados, servicos));

        vazioLabel.textProperty().bind(Bindings.when(Bindings.isEmpty(servicos))
            .then("Nenhum serviço cadastrado. Clique em \"+ Novo serviço\".")
            .otherwise("Nenhum serviço encontrado para o filtro atual."));

        salvarButton.disableProperty().bind(
            nomeField.textProperty().isEmpty()
                .or(precoField.textProperty().isEmpty())
                .or(duracaoField.textProperty().isEmpty()));

        // Enter salva e Esc cancela somente com o formulário aberto.
        salvarButton.defaultButtonProperty().bind(formPainel.visibleProperty());
        cancelarButton.cancelButtonProperty().bind(formPainel.visibleProperty());
        formPainel.managedProperty().bind(formPainel.visibleProperty());
    }

    // ─── Ações da lista ────────────────────────────────────────

    @FXML
    private void novo() {
        idEmEdicao = null;
        formTituloLabel.setText("Novo serviço");
        nomeField.clear();
        descricaoField.clear();
        precoField.clear();
        duracaoField.clear();
        ativoCheck.setSelected(true);
        abrirFormulario();
    }

    @FXML
    private void editar() {
        Servico s = tabela.getSelectionModel().getSelectedItem();
        if (s == null) return;

        idEmEdicao = s.getId();
        formTituloLabel.setText("Editar serviço");
        nomeField.setText(s.getNome());
        descricaoField.setText(Objects.requireNonNullElse(s.getDescricao(), ""));
        precoField.setText(s.getPreco().toPlainString().replace(".", ","));
        duracaoField.setText(String.valueOf(s.getDuracaoMinutos()));
        ativoCheck.setSelected(s.isAtivo());
        abrirFormulario();
    }

    @FXML
    private void alternarAtivo() {
        Servico s = tabela.getSelectionModel().getSelectedItem();
        if (s == null) return;
        try {
            Servico atualizado = servicoService.alternarAtivo(s.getId());
            carregar(atualizado.getId());
            mostrarStatus("Serviço \"" + atualizado.getNome() + "\" "
                + (atualizado.isAtivo() ? "ativado." : "desativado."));
        } catch (Exception e) {
            Dialogos.erro("Erro ao alterar o serviço", e.getMessage());
            carregar(null);
        }
    }

    @FXML
    private void excluir() {
        Servico s = tabela.getSelectionModel().getSelectedItem();
        if (s == null) return;

        // Serviço já usado em agendamentos: oferece desativar em vez de excluir.
        Optional<String> bloqueio = servicoRepository.verificarExclusao(s.getId());
        if (bloqueio.isPresent() && s.isAtivo()) {
            boolean desativar = Dialogos.confirmar(Alert.AlertType.WARNING, "Exclusão não permitida",
                "Não é possível excluir este serviço.",
                bloqueio.get() + "\n\nDeseja desativar \"" + s.getNome() + "\" agora?", "Desativar");
            if (desativar) {
                alternarAtivo();
            }
            return;
        }

        boolean excluido = Dialogos.excluirComConfirmacao(
            servicoRepository, s.getId(), "serviço", s.getNome(), () -> carregar(null));
        if (excluido) {
            if (Objects.equals(idEmEdicao, s.getId())) {
                fecharFormulario();
            }
            mostrarStatus("Serviço \"" + s.getNome() + "\" excluído.");
        }
    }

    // ─── Ações do formulário ───────────────────────────────────

    @FXML
    private void salvar() {
        mensagemLabel.setText("");
        try {
            boolean novo = idEmEdicao == null;
            Servico salvo = servicoService.salvar(idEmEdicao,
                nomeField.getText(), descricaoField.getText(),
                precoField.getText(), duracaoField.getText(), ativoCheck.isSelected());

            // Garante que o serviço salvo apareça mesmo que esteja inativo.
            if (!salvo.isAtivo()) {
                mostrarInativosCheck.setSelected(true);
            }
            fecharFormulario();
            carregar(salvo.getId());
            mostrarStatus("Serviço \"" + salvo.getNome() + "\" " + (novo ? "cadastrado." : "atualizado."));
        } catch (IllegalArgumentException e) {
            mensagemLabel.setText(e.getMessage());
        } catch (Exception e) {
            mensagemLabel.setText("Não foi possível salvar o serviço: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        fecharFormulario();
    }

    // ─── Auxiliares ────────────────────────────────────────────

    /** Recarrega a lista do banco e, se informado, seleciona o serviço com esse id. */
    private void carregar(Long idParaSelecionar) {
        try {
            servicos.setAll(servicoService.listar());
        } catch (Exception e) {
            Dialogos.erro("Erro ao carregar serviços", e.getMessage());
            return;
        }
        if (idParaSelecionar != null) {
            tabela.getItems().stream()
                .filter(s -> s.getId().equals(idParaSelecionar))
                .findFirst()
                .ifPresent(s -> {
                    tabela.getSelectionModel().select(s);
                    tabela.scrollTo(s);
                });
        }
    }

    private void abrirFormulario() {
        mensagemLabel.setText("");
        formPainel.setVisible(true);
        nomeField.requestFocus();
    }

    private void fecharFormulario() {
        formPainel.setVisible(false);
        idEmEdicao = null;
        mensagemLabel.setText("");
    }

    private void mostrarStatus(String texto) {
        statusLabel.setText(texto);
        limparStatus.playFromStart();
    }

    static String formatarDuracao(int minutos) {
        if (minutos < 60) return minutos + " min";
        int h = minutos / 60, m = minutos % 60;
        return m == 0 ? h + "h" : h + "h " + m + "min";
    }

    private static <T> TableCell<Servico, T> celulaFormatada(java.util.function.Function<T, String> formato) {
        return new TableCell<>() {
            @Override
            protected void updateItem(T valor, boolean vazio) {
                super.updateItem(valor, vazio);
                setText(vazio || valor == null ? null : formato.apply(valor));
            }
        };
    }
}
