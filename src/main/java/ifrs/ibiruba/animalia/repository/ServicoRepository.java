package ifrs.ibiruba.animalia.repository;

import ifrs.ibiruba.animalia.model.Servico;
import jakarta.persistence.EntityManager;

public class ServicoRepository extends BaseRepository<Servico, Long> {

    public ServicoRepository() {
        super(Servico.class);
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
