package ifrs.ibiruba.animalia.persistencia;

/**
 * Lançada quando uma exclusão é recusada porque o registro ainda possui
 * vínculos (ex.: tutor com pets, pet com agendamentos, serviço já utilizado).
 * A mensagem é amigável e pode ser exibida diretamente ao usuário.
 */
public class ExclusaoBloqueadaException extends RepositorioException {

    public ExclusaoBloqueadaException(String mensagem) {
        super(mensagem);
    }
}
