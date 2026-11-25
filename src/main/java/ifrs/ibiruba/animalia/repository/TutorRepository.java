package ifrs.ibiruba.animalia.repository;

import ifrs.ibiruba.animalia.model.Tutor;

public class TutorRepository extends BaseRepository<Tutor, Long> {

    public TutorRepository() {
        super(Tutor.class);
    }
}
