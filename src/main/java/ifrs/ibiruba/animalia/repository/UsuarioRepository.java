package ifrs.ibiruba.animalia.repository;

import ifrs.ibiruba.animalia.model.Usuario;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class UsuarioRepository extends BaseRepository<Usuario, Long> {

    public UsuarioRepository() {
        super(Usuario.class);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        EntityManager em = abrirEm();
        try {
            return em.createQuery("from Usuario u where lower(u.email) = lower(:email)", Usuario.class)
                     .setParameter("email", email)
                     .getResultStream()
                     .findFirst();
        } finally {
            em.close();
        }
    }

    public boolean existePorEmail(String email) {
        return buscarPorEmail(email).isPresent();
    }
}
