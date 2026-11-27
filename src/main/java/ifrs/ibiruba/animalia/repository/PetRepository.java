package ifrs.ibiruba.animalia.repository;

import ifrs.ibiruba.animalia.model.Pet;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class PetRepository extends BaseRepository<Pet, Long> {

    public PetRepository() {
        super(Pet.class);
    }

    @Override
    protected Optional<String> motivoBloqueioExclusao(EntityManager em, Long id) {
        long agendamentos = contarVinculos(em,
            "select count(a) from Agendamento a where a.pet.id = :id", id);
        if (agendamentos > 0) {
            return Optional.of("Este pet possui " + agendamentos
                + (agendamentos == 1 ? " agendamento registrado" : " agendamentos registrados")
                + ". O histórico de agendamentos não pode ser apagado.");
        }
        return Optional.empty();
    }
}
