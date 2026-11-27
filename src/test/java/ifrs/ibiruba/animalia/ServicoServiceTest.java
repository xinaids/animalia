package ifrs.ibiruba.animalia;

import ifrs.ibiruba.animalia.model.Servico;
import ifrs.ibiruba.animalia.persistencia.Persistencia;
import ifrs.ibiruba.animalia.repository.ServicoRepository;
import ifrs.ibiruba.animalia.service.ServicoService;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ServicoServiceTest {

    private static Persistencia persistencia;
    private static ServicoRepository repo;
    private static ServicoService service;
    private static int sequencia = 0;

    @BeforeAll
    static void configurar() {
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", "jdbc:sqlite::memory:");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        persistencia = new Persistencia(props);
        setPersistenciaSingleton(persistencia);

        repo = new ServicoRepository();
        service = new ServicoService(repo);
    }

    @AfterAll
    static void fechar() {
        if (persistencia != null) persistencia.fechar();
        setPersistenciaSingleton(null);
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

    private String nomeUnico() {
        return "Serviço " + (++sequencia);
    }

    @Test
    void cadastraServicoValido() {
        Servico s = service.salvar(null, "  Banho Completo  ", "Com hidratação", "89,90", "60", true);

        assertNotNull(s.getId());
        assertEquals("Banho Completo", s.getNome(), "nome deve ser salvo sem espaços nas pontas");
        assertEquals(new BigDecimal("89.90"), s.getPreco());
        assertEquals(60, s.getDuracaoMinutos());
        assertTrue(s.isAtivo());
    }

    @Test
    void aceitaFormatosDePreco() {
        assertEquals(new BigDecimal("59.90"),   service.salvar(null, nomeUnico(), null, "59.90",    "30", true).getPreco());
        assertEquals(new BigDecimal("1234.56"), service.salvar(null, nomeUnico(), null, "1.234,56", "30", true).getPreco());
        assertEquals(new BigDecimal("40.00"),   service.salvar(null, nomeUnico(), null, "R$ 40",    "30", true).getPreco());
    }

    @Test
    void descricaoEmBrancoViraNull() {
        Servico s = service.salvar(null, nomeUnico(), "   ", "10", "15", true);
        assertNull(s.getDescricao());
    }

    @Test
    void rejeitaCamposInvalidosComTodasAsMensagens() {
        var ex = assertThrows(IllegalArgumentException.class,
            () -> service.salvar(null, " ", null, "abc", "0", true));
        assertTrue(ex.getMessage().contains("nome é obrigatório"));
        assertTrue(ex.getMessage().contains("preço válido"));
        assertTrue(ex.getMessage().contains("duração"));
    }

    @Test
    void rejeitaPrecoZeroOuNegativoEDuracaoExcessiva() {
        assertThrows(IllegalArgumentException.class, () -> service.salvar(null, nomeUnico(), null, "0,00", "30", true));
        assertThrows(IllegalArgumentException.class, () -> service.salvar(null, nomeUnico(), null, "-5", "30", true));
        assertThrows(IllegalArgumentException.class, () -> service.salvar(null, nomeUnico(), null, "10,999", "30", true));
        assertThrows(IllegalArgumentException.class, () -> service.salvar(null, nomeUnico(), null, "10", "1441", true));
    }

    @Test
    void rejeitaNomeDuplicadoSemDiferenciarMaiusculas() {
        service.salvar(null, "Tosa Higiênica", null, "50", "40", true);
        var ex = assertThrows(IllegalArgumentException.class,
            () -> service.salvar(null, "TOSA HIGIÊNICA", null, "50", "40", true));
        assertTrue(ex.getMessage().contains("Já existe"));
    }

    @Test
    void edicaoMantemIdEPermiteOMesmoNome() {
        Servico original = service.salvar(null, "Hidratação", null, "30", "20", true);
        Servico editado  = service.salvar(original.getId(), "Hidratação", "Nova descrição", "35,00", "25", false);

        assertEquals(original.getId(), editado.getId());
        Servico doBanco = repo.buscarPorId(original.getId()).orElseThrow();
        assertEquals("Nova descrição", doBanco.getDescricao());
        assertEquals(new BigDecimal("35.00"), doBanco.getPreco());
        assertEquals(25, doBanco.getDuracaoMinutos());
        assertFalse(doBanco.isAtivo());
    }

    @Test
    void edicaoNaoPodeUsarNomeDeOutroServico() {
        service.salvar(null, "Corte de Unhas", null, "20", "10", true);
        Servico outro = service.salvar(null, nomeUnico(), null, "20", "10", true);
        assertThrows(IllegalArgumentException.class,
            () -> service.salvar(outro.getId(), "corte de unhas", null, "20", "10", true));
    }

    @Test
    void alternarAtivoInverteStatus() {
        Servico s = service.salvar(null, nomeUnico(), null, "10", "10", true);
        assertFalse(service.alternarAtivo(s.getId()).isAtivo());
        assertTrue(service.alternarAtivo(s.getId()).isAtivo());
    }

    @Test
    void listaEmOrdemAlfabetica() {
        service.salvar(null, "zzz Último", null, "10", "10", true);
        service.salvar(null, "aaa Primeiro", null, "10", "10", false);

        List<Servico> lista = service.listar();
        assertEquals("aaa Primeiro", lista.get(0).getNome(), "inativos também aparecem na listagem");
        assertEquals("zzz Último", lista.get(lista.size() - 1).getNome());
    }
}
