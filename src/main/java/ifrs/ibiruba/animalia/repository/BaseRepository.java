package ifrs.ibiruba.animalia.repository;

import ifrs.ibiruba.animalia.persistencia.ExclusaoBloqueadaException;
import ifrs.ibiruba.animalia.persistencia.Persistencia;
import ifrs.ibiruba.animalia.persistencia.RepositorioException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T, ID> {

    protected final Class<T> tipo;

    protected BaseRepository(Class<T> tipo) {
        this.tipo = tipo;
    }

    protected EntityManager abrirEm() {
        EntityManagerFactory emf = Persistencia.getInstance().getEmf();
        return emf.createEntityManager();
    }

    public T salvar(T entidade) {
        EntityManager em = abrirEm();
        try {
            em.getTransaction().begin();
            T resultado = em.merge(entidade);
            em.getTransaction().commit();
            return resultado;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RepositorioException("Erro ao salvar " + tipo.getSimpleName() + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Optional<T> buscarPorId(ID id) {
        EntityManager em = abrirEm();
        try {
            return Optional.ofNullable(em.find(tipo, id));
        } finally {
            em.close();
        }
    }

    public List<T> listar() {
        EntityManager em = abrirEm();
        try {
            return em.createQuery("from " + tipo.getSimpleName(), tipo).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Verifica se o registro pode ser excluído.
     *
     * @return o motivo do bloqueio (mensagem para o usuário) ou vazio se a exclusão é permitida.
     */
    public Optional<String> verificarExclusao(ID id) {
        EntityManager em = abrirEm();
        try {
            return motivoBloqueioExclusao(em, id);
        } finally {
            em.close();
        }
    }

    /**
     * Ponto de extensão: subclasses informam por que um registro não pode ser excluído
     * (ex.: possui registros dependentes). Padrão: nenhum bloqueio.
     */
    protected Optional<String> motivoBloqueioExclusao(EntityManager em, ID id) {
        return Optional.empty();
    }

    /**
     * Exclui o registro. Não há exclusão em cascata: se existirem vínculos,
     * lança {@link ExclusaoBloqueadaException} e nada é removido.
     */
    public void excluir(ID id) {
        EntityManager em = abrirEm();
        try {
            em.getTransaction().begin();
            Optional<String> bloqueio = motivoBloqueioExclusao(em, id);
            if (bloqueio.isPresent()) {
                throw new ExclusaoBloqueadaException(bloqueio.get());
            }
            T entidade = em.find(tipo, id);
            if (entidade != null) {
                em.remove(entidade);
            }
            em.getTransaction().commit();
        } catch (ExclusaoBloqueadaException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RepositorioException("Erro ao excluir " + tipo.getSimpleName() + " id=" + id + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /** Executa uma contagem JPQL com um único parâmetro ":id". */
    protected long contarVinculos(EntityManager em, String jpql, ID id) {
        return em.createQuery(jpql, Long.class)
                 .setParameter("id", id)
                 .getSingleResult();
    }

    public long contar() {
        EntityManager em = abrirEm();
        try {
            return em.createQuery("select count(e) from " + tipo.getSimpleName() + " e", Long.class)
                     .getSingleResult();
        } finally {
            em.close();
        }
    }
}
