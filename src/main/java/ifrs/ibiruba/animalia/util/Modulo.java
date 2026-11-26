package ifrs.ibiruba.animalia.util;

/**
 * Módulos exibidos na área central da janela principal.
 * Menu e sidebar abrem sempre um destes; para publicar um CRUD novo,
 * basta trocar o FXML do módulo correspondente.
 */
public enum Modulo {
    DASHBOARD    ("dashboard.fxml",  "Dashboard"),
    AGENDAMENTOS ("construcao.fxml", "Agendamentos"),
    TUTORES      ("construcao.fxml", "Tutores"),
    PETS         ("construcao.fxml", "Pets"),
    SERVICOS     ("servicos.fxml",   "Serviços");

    private final String fxml;
    private final String titulo;

    Modulo(String fxml, String titulo) {
        this.fxml = fxml;
        this.titulo = titulo;
    }

    public String getFxml()   { return fxml; }
    public String getTitulo() { return titulo; }
}
