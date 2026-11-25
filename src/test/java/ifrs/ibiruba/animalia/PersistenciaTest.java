package ifrs.ibiruba.animalia;

import ifrs.ibiruba.animalia.model.*;
import ifrs.ibiruba.animalia.persistencia.Persistencia;
import ifrs.ibiruba.animalia.persistencia.RepositorioException;
import ifrs.ibiruba.animalia.repository.*;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PersistenciaTest {

    private static Persistencia persistencia;
    private static UsuarioRepository    usuarioRepo;
    private static TutorRepository      tutorRepo;
    private static PetRepository        petRepo;
    private static ServicoRepository    servicoRepo;
    private static AgendamentoRepository agendamentoRepo;

    @BeforeAll
    static void configurar() {
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", "jdbc:sqlite::memory:");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        props.put("hibernate.show_sql", "false");

        persistencia = new Persistencia(props);
        // Injeta o EMF nos repositórios via Persistencia singleton de teste
        // Para testes isolados, os repos usam getInstance() do singleton.
        // Aqui usamos uma abordagem: substituir a instância singleton temporariamente.
        setPersistenciaSingleton(persistencia);

        usuarioRepo     = new UsuarioRepository();
        tutorRepo       = new TutorRepository();
        petRepo         = new PetRepository();
        servicoRepo     = new ServicoRepository();
        agendamentoRepo = new AgendamentoRepository();
    }

    @AfterAll
    static void fechar() {
        if (persistencia != null) persistencia.fechar();
        // Limpa singleton para não afetar outros testes
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

    // ─── Usuario ───────────────────────────────────────────────

    @Test
    void testSalvarEBuscarUsuario() {
        Usuario u = novoUsuario("Ana Silva", "ana@test.com");
        Usuario salvo = usuarioRepo.salvar(u);

        assertNotNull(salvo.getId());
        Optional<Usuario> encontrado = usuarioRepo.buscarPorId(salvo.getId());
        assertTrue(encontrado.isPresent());
        assertEquals("ana@test.com", encontrado.get().getEmail());
    }

    @Test
    void testBuscarUsuarioPorEmail() {
        Usuario u = novoUsuario("Carlos", "carlos@test.com");
        usuarioRepo.salvar(u);

        assertTrue(usuarioRepo.buscarPorEmail("carlos@test.com").isPresent());
        assertFalse(usuarioRepo.buscarPorEmail("inexistente@test.com").isPresent());
    }

    @Test
    void testListarUsuarios() {
        usuarioRepo.salvar(novoUsuario("Usuario1", "u1@test.com"));
        usuarioRepo.salvar(novoUsuario("Usuario2", "u2@test.com"));
        List<Usuario> lista = usuarioRepo.listar();
        assertTrue(lista.size() >= 2);
    }

    @Test
    void testExcluirUsuario() {
        Usuario u = usuarioRepo.salvar(novoUsuario("Excluir", "excluir@test.com"));
        Long id = u.getId();
        usuarioRepo.excluir(id);
        assertFalse(usuarioRepo.buscarPorId(id).isPresent());
    }

    @Test
    void testEmailUnico() {
        usuarioRepo.salvar(novoUsuario("Primeiro", "unico@test.com"));
        // Segundo com mesmo email deve lançar RepositorioException
        assertThrows(RepositorioException.class,
            () -> usuarioRepo.salvar(novoUsuario("Segundo", "unico@test.com")));
    }

    // ─── Tutor ─────────────────────────────────────────────────

    @Test
    void testCRUDTutor() {
        Tutor t = new Tutor();
        t.setNome("Roberto Souza");
        t.setCpf("123.456.789-00");
        t.setTelefone("(51) 99999-0001");
        t.setEmail("roberto@test.com");
        Tutor salvo = tutorRepo.salvar(t);
        assertNotNull(salvo.getId());

        Optional<Tutor> encontrado = tutorRepo.buscarPorId(salvo.getId());
        assertTrue(encontrado.isPresent());
        assertEquals("Roberto Souza", encontrado.get().getNome());
    }

    // ─── Pet ───────────────────────────────────────────────────

    @Test
    void testCRUDPet() {
        Tutor tutor = new Tutor();
        tutor.setNome("Tutor Pet");
        tutor.setCpf("111.222.333-44");
        Tutor tutorSalvo = tutorRepo.salvar(tutor);

        Pet pet = new Pet();
        pet.setNome("Rex");
        pet.setEspecie("Cachorro");
        pet.setRaca("Labrador");
        pet.setDataNascimento(LocalDate.of(2020, 3, 15));
        pet.setTutor(tutorSalvo);
        Pet salvo = petRepo.salvar(pet);

        assertNotNull(salvo.getId());
        assertEquals("Rex", petRepo.buscarPorId(salvo.getId()).orElseThrow().getNome());
    }

    // ─── Servico ───────────────────────────────────────────────

    @Test
    void testCRUDServico() {
        Servico s = new Servico();
        s.setNome("Banho Premium");
        s.setDescricao("Banho completo com hidratação");
        s.setPreco(new BigDecimal("89.90"));
        s.setDuracaoMinutos(60);
        s.setAtivo(true);
        Servico salvo = servicoRepo.salvar(s);

        assertNotNull(salvo.getId());
        assertTrue(servicoRepo.contarAtivos() >= 1);
    }

    // ─── Agendamento ───────────────────────────────────────────

    @Test
    void testCRUDAgendamento() {
        Tutor tutor = new Tutor();
        tutor.setNome("Tutor Agend");
        tutor.setCpf("999.888.777-66");
        Tutor tutorSalvo = tutorRepo.salvar(tutor);

        Pet pet = new Pet();
        pet.setNome("Luna");
        pet.setEspecie("Gato");
        pet.setTutor(tutorSalvo);
        Pet petSalvo = petRepo.salvar(pet);

        Agendamento a = new Agendamento();
        a.setPet(petSalvo);
        a.setDataHora(LocalDateTime.now().plusDays(1));
        a.setStatus(StatusAgendamento.AGENDADO);
        a.setValorTotal(new BigDecimal("80.00"));
        Agendamento salvo = agendamentoRepo.salvar(a);

        assertNotNull(salvo.getId());
        assertEquals(StatusAgendamento.AGENDADO,
            agendamentoRepo.buscarPorId(salvo.getId()).orElseThrow().getStatus());
    }

    // ─── Helpers ───────────────────────────────────────────────

    private Usuario novoUsuario(String nome, String email) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setSenhaHash("hashFake");
        u.setSalt("saltFake");
        return u;
    }
}
