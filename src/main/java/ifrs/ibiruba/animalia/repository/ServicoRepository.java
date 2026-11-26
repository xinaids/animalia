package ifrs.ibiruba.animalia.repository;

import ifrs.ibiruba.animalia.model.Servico;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class ServicoRepository extends BaseRepository<Servico, Long> {

    public ServicoRepository() {
        super(Servico.class);
    }

    @Override
    protected Optional<String> motivoBloqueioExclusao(EntityManager em, Long id) {
        long usos = contarVinculos(em,
            "select count(i) from ItemAgendamento i where i.servico.id = :id", id);
        if (usos > 0) {
            return Optional.of("Este serviço já foi utilizado em " + usos
                + (usos == 1 ? " agendamento" : " agendamentos")
                + ". Desative o serviço em vez de excluí-lo.");
        }
        return Optional.empty();
    }

    /** Conta apenas os serviços com ativo=true. */
    public long contarAtivos() {
        EntityManager em = abrirEm();
        try {
            return em.createQuery("select count(s) from Servico s where s.ativo = true", Long.class)
                     .getSingleResult();
        } finally {
            em.close();
        }
    }
}
