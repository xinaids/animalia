package ifrs.ibiruba.animalia.repository;

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

    public void excluir(ID id) {
        EntityManager em = abrirEm();
        try {
            em.getTransaction().begin();
            T entidade = em.find(tipo, id);
            if (entidade != null) {
                em.remove(entidade);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RepositorioException("Erro ao excluir " + tipo.getSimpleName() + " id=" + id + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
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
