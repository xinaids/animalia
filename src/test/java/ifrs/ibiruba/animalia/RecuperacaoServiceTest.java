package ifrs.ibiruba.animalia;

import ifrs.ibiruba.animalia.persistencia.Persistencia;
import ifrs.ibiruba.animalia.repository.UsuarioRepository;
import ifrs.ibiruba.animalia.service.CadastroService;
import ifrs.ibiruba.animalia.service.RecuperacaoService;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RecuperacaoServiceTest {

    private static Persistencia persistencia;
    private static final String EMAIL  = "recuperacao@test.com";
    private static final String SENHA  = "senhaOriginal1";
    private static String codigoInicial;

    private RecuperacaoService recuperacaoService;

    @BeforeAll
    static void configurarBanco() {
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", "jdbc:sqlite::memory:");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        persistencia = new Persistencia(props);
        setPersistenciaSingleton(persistencia);

        codigoInicial = new CadastroService(new UsuarioRepository())
            .registrar("Rec Teste", EMAIL, SENHA, SENHA);
    }

    @AfterAll
    static void fechar() {
        if (persistencia != null) persistencia.fechar();
        setPersistenciaSingleton(null);
    }

    @BeforeEach
    void setUp() {
        recuperacaoService = new RecuperacaoService(new UsuarioRepository());
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
    void testRecuperacaoValida() {
        String novoCodigo = recuperacaoService.redefinir(
            EMAIL, codigoInicial, "novaSenha99", "novaSenha99");

        assertNotNull(novoCodigo, "Deve retornar novo código");
        assertEquals(8, novoCodigo.length());

        // Atualiza para próximos testes que usem o mesmo usuário
        codigoInicial = novoCodigo;
    }

    @Test
    void testCodigoErrado() {
        RecuperacaoService srv = new RecuperacaoService(new UsuarioRepository());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> srv.redefinir(EMAIL, "CODIGOERRADO", "novaSenha99", "novaSenha99"));
        assertEquals("E-mail ou código inválidos.", ex.getMessage());
        assertEquals(1, srv.getTentativas(EMAIL));
    }

    @Test
    void testBloqueioApos5Tentativas() {
        RecuperacaoService srv = new RecuperacaoService(new UsuarioRepository());
        for (int i = 0; i < RecuperacaoService.LIMITE_TENTATIVAS - 1; i++) {
            try { srv.redefinir(EMAIL, "ERRADO" + i, "novaSenha99", "novaSenha99"); }
            catch (IllegalArgumentException ignored) {}
        }
        // Na 5ª tentativa deve bloquear
        assertThrows(IllegalStateException.class,
            () -> srv.redefinir(EMAIL, "ERRADOFINAL", "novaSenha99", "novaSenha99"));
    }
}
