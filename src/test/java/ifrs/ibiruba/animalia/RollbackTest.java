package ifrs.ibiruba.animalia;

import ifrs.ibiruba.animalia.model.Usuario;
import ifrs.ibiruba.animalia.persistencia.Persistencia;
import ifrs.ibiruba.animalia.persistencia.RepositorioException;
import ifrs.ibiruba.animalia.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RollbackTest {

    private static Persistencia persistencia;
    private UsuarioRepository repo;

    @BeforeAll
    static void configurarBanco() {
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", "jdbc:sqlite::memory:");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        persistencia = new Persistencia(props);
        setPersistenciaSingleton(persistencia);
    }

    @AfterAll
    static void fechar() {
        if (persistencia != null) persistencia.fechar();
        setPersistenciaSingleton(null);
    }

    @BeforeEach
    void setUp() {
        repo = new UsuarioRepository();
    }

    private static void setPersistenciaSingleton(Persistencia p) {
        try {
            var campo = Persistencia.class.getDeclaredField("instancia");
            campo.setAccessible(true);
            campo.set(null, p);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testRollbackEmFalha() {
        // Salva um usuário com email único
        Usuario primeiro = new Usuario();
        primeiro.setNome("Primeiro");
        primeiro.setEmail("rollback@test.com");
        primeiro.setSenhaHash("h1");
        primeiro.setSalt("s1");
        repo.salvar(primeiro);

        long contantesAntes = repo.contar();

        // Tenta salvar segundo com mesmo email (viola unicidade) → ROLLBACK
        Usuario segundo = new Usuario();
        segundo.setNome("Segundo");
        segundo.setEmail("rollback@test.com"); // duplicado
        segundo.setSenhaHash("h2");
        segundo.setSalt("s2");

        assertThrows(RepositorioException.class, () -> repo.salvar(segundo));

        // Conta após a falha: não deve ter aumentado
        long contantesDepois = repo.contar();
        assertEquals(contantesAntes, contantesDepois,
            "ROLLBACK: nenhum registro parcial deve ter sido salvo");
    }

    @Test
    void testTransacaoExplicita() {
        // Verifica que transações incompletas fazem rollback ao fechar o EntityManager sem commit
        EntityManager em = persistencia.getEmf().createEntityManager();
        em.getTransaction().begin();

        Usuario u = new Usuario();
        u.setNome("Incompleto");
        u.setEmail("incompleto@test.com");
        u.setSenhaHash("h");
        u.setSalt("s");
        em.persist(u);

        // Rollback explícito
        em.getTransaction().rollback();
        em.close();

        assertFalse(repo.buscarPorEmail("incompleto@test.com").isPresent(),
            "Após rollback, o registro não deve estar no banco");
    }
}
