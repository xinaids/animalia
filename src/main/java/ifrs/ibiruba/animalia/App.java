package ifrs.ibiruba.animalia;

import ifrs.ibiruba.animalia.persistencia.Persistencia;
import ifrs.ibiruba.animalia.util.Navegacao;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        configurarHandlerGlobalDeExcecoes();

        Persistencia.getInstance(); // força criação das tabelas via hbm2ddl

        Navegacao.setStage(stage);
        Navegacao.irPara("login.fxml", "Animalia — Login", 400, 500);
        stage.show();
    }

    @Override
    public void stop() {
        Persistencia.getInstance().fechar();
    }

    private void configurarHandlerGlobalDeExcecoes() {
        Thread.setDefaultUncaughtExceptionHandler((thread, erro) ->
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro inesperado");
                alert.setHeaderText("Ocorreu um erro não tratado.");
                alert.setContentText(erro.getMessage() != null ? erro.getMessage() : erro.toString());
                alert.showAndWait();
            })
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
