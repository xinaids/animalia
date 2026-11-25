package ifrs.ibiruba.animalia;

import ifrs.ibiruba.animalia.model.Usuario;
import ifrs.ibiruba.animalia.persistencia.Persistencia;
import ifrs.ibiruba.animalia.repository.UsuarioRepository;
import ifrs.ibiruba.animalia.service.CadastroService;
import ifrs.ibiruba.animalia.service.LoginService;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {

    private static Persistencia persistencia;
    private static final String EMAIL_TESTE = "login@test.com";
    private static final String SENHA_TESTE = "senhaSegura99";

    private LoginService loginService;

    @BeforeAll
    static void configurarBanco() {
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", "jdbc:sqlite::memory:");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        persistencia = new Persistencia(props);
        setPersistenciaSingleton(persistencia);

        // Cria o usuário de teste uma única vez
        new CadastroService(new UsuarioRepository())
            .registrar("Login Teste", EMAIL_TESTE, SENHA_TESTE, SENHA_TESTE);
    }

    @AfterAll
    static void fechar() {
        if (persistencia != null) persistencia.fechar();
        setPersistenciaSingleton(null);
    }

    @BeforeEach
    void setUp() {
        loginService = new LoginService(new UsuarioRepository());
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
    void testLoginValido() {
        Usuario usuario = loginService.autenticar(EMAIL_TESTE, SENHA_TESTE);
        assertNotNull(usuario);
        assertEquals(EMAIL_TESTE, usuario.getEmail());
    }

    @Test
    void testSenhaErrada() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> loginService.autenticar(EMAIL_TESTE, "senhaErrada123"));
        assertEquals("E-mail ou senha inválidos.", ex.getMessage(),
            "Mensagem deve ser genérica");
    }

    @Test
    void testEmailNaoExiste() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> loginService.autenticar("naoexiste@test.com", SENHA_TESTE));
        assertEquals("E-mail ou senha inválidos.", ex.getMessage(),
            "Mensagem deve ser genérica — igual à de senha errada");
    }
}
