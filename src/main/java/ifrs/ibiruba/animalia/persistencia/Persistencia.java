package ifrs.ibiruba.animalia.persistencia;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Map;

public class Persistencia {

    private static Persistencia instancia;

    private final EntityManagerFactory emf;

    /** Construtor de produção — usa a persistence-unit "animalia" do persistence.xml. */
    private Persistencia() {
        emf = Persistence.createEntityManagerFactory("animalia");
    }

    /** Construtor para testes — permite sobrescrever propriedades (ex.: URL do banco). */
    public Persistencia(Map<String, Object> props) {
        emf = Persistence.createEntityManagerFactory("animalia", props);
    }

    public static synchronized Persistencia getInstance() {
        if (instancia == null) {
            instancia = new Persistencia();
        }
        return instancia;
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }

    public void fechar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
