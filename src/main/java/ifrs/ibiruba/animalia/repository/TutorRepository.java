package ifrs.ibiruba.animalia.repository;

import ifrs.ibiruba.animalia.model.Tutor;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class TutorRepository extends BaseRepository<Tutor, Long> {

    public TutorRepository() {
        super(Tutor.class);
    }

    @Override
    protected Optional<String> motivoBloqueioExclusao(EntityManager em, Long id) {
        long pets = contarVinculos(em, "select count(p) from Pet p where p.tutor.id = :id", id);
        if (pets > 0) {
            return Optional.of("Este tutor possui " + pets + (pets == 1 ? " pet vinculado" : " pets vinculados")
                + ". Exclua ou transfira os pets antes de excluir o tutor.");
        }
        return Optional.empty();
    }
}
