package ifrs.ibiruba.animalia;

import ifrs.ibiruba.animalia.persistencia.Persistencia;
import ifrs.ibiruba.animalia.repository.UsuarioRepository;
import ifrs.ibiruba.animalia.service.CadastroService;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CadastroServiceTest {

    private static Persistencia persistencia;
    private CadastroService service;

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
        service = new CadastroService(new UsuarioRepository());
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
    void testCadastroValido() {
        String codigo = service.registrar(
            "Maria Teste", "maria@test.com", "senha1234", "senha1234");

        assertNotNull(codigo, "Deve retornar o código de recuperação");
        assertFalse(codigo.isBlank());
        assertEquals(8, codigo.length());
    }

    @Test
    void testEmailInvalido() {
        assertThrows(IllegalArgumentException.class,
            () -> service.registrar("João", "emailinvalido", "senha1234", "senha1234"));
    }

    @Test
    void testSenhaCurta() {
        assertThrows(IllegalArgumentException.class,
            () -> service.registrar("João", "joao@test.com", "123", "123"));
    }

    @Test
    void testSenhasNaoConferem() {
        assertThrows(IllegalArgumentException.class,
            () -> service.registrar("João", "joao@test.com", "senha1234", "senhadiferente"));
    }

    @Test
    void testNomeObrigatorio() {
        assertThrows(IllegalArgumentException.class,
            () -> service.registrar("", "joao@test.com", "senha1234", "senha1234"));
    }

    @Test
    void testEmailDuplicado() {
        service.registrar("Primeiro", "duplicado@test.com", "senha1234", "senha1234");
        assertThrows(RuntimeException.class,
            () -> service.registrar("Segundo", "duplicado@test.com", "senha5678", "senha5678"));
    }
}
