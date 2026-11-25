package ifrs.ibiruba.animalia.repository;

import ifrs.ibiruba.animalia.model.Pet;

public class PetRepository extends BaseRepository<Pet, Long> {

    public PetRepository() {
        super(Pet.class);
    }
}
