package ifrs.ibiruba.animalia;

import ifrs.ibiruba.animalia.model.*;
import ifrs.ibiruba.animalia.persistencia.ExclusaoBloqueadaException;
import ifrs.ibiruba.animalia.persistencia.Persistencia;
import ifrs.ibiruba.animalia.repository.*;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Garante que não há exclusão em cascata: registros com vínculos são bloqueados. */
class ExclusaoTest {

    private static Persistencia persistencia;
    private static TutorRepository       tutorRepo;
    private static PetRepository         petRepo;
    private static ServicoRepository     servicoRepo;
    private static AgendamentoRepository agendamentoRepo;
    private static int sequencia = 0;

    @BeforeAll
    static void configurar() {
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", "jdbc:sqlite::memory:");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        persistencia = new Persistencia(props);
        setPersistenciaSingleton(persistencia);

        tutorRepo       = new TutorRepository();
        petRepo         = new PetRepository();
        servicoRepo     = new ServicoRepository();
        agendamentoRepo = new AgendamentoRepository();
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

    @Test
    void tutorSemPetsPodeSerExcluido() {
        Tutor t = novoTutor();
        assertTrue(tutorRepo.verificarExclusao(t.getId()).isEmpty());
        tutorRepo.excluir(t.getId());
        assertFalse(tutorRepo.buscarPorId(t.getId()).isPresent());
    }

    @Test
    void tutorComPetNaoPodeSerExcluido() {
        Tutor t = novoTutor();
        Pet p = novoPet(t);

        assertTrue(tutorRepo.verificarExclusao(t.getId()).isPresent());
        var ex = assertThrows(ExclusaoBloqueadaException.class, () -> tutorRepo.excluir(t.getId()));
        assertTrue(ex.getMessage().contains("1 pet vinculado"));

        assertTrue(tutorRepo.buscarPorId(t.getId()).isPresent(), "tutor deve continuar no banco");
        assertTrue(petRepo.buscarPorId(p.getId()).isPresent(), "pet não pode ser apagado em cascata");
    }

    @Test
    void petComAgendamentoNaoPodeSerExcluido() {
        Pet p = novoPet(novoTutor());
        Agendamento a = novoAgendamento(p, null);

        assertThrows(ExclusaoBloqueadaException.class, () -> petRepo.excluir(p.getId()));
        assertTrue(petRepo.buscarPorId(p.getId()).isPresent());
        assertTrue(agendamentoRepo.buscarPorId(a.getId()).isPresent(), "histórico deve ser preservado");
    }

    @Test
    void servicoUtilizadoNaoPodeSerExcluido() {
        Servico s = novoServico();
        novoAgendamento(novoPet(novoTutor()), s);

        var ex = assertThrows(ExclusaoBloqueadaException.class, () -> servicoRepo.excluir(s.getId()));
        assertTrue(ex.getMessage().contains("Desative"));
        assertTrue(servicoRepo.buscarPorId(s.getId()).isPresent());
    }

    @Test
    void servicoNaoUtilizadoPodeSerExcluido() {
        Servico s = novoServico();
        servicoRepo.excluir(s.getId());
        assertFalse(servicoRepo.buscarPorId(s.getId()).isPresent());
    }

    // ─── Helpers ───────────────────────────────────────────────

    private Tutor novoTutor() {
        Tutor t = new Tutor();
        t.setNome("Tutor " + (++sequencia));
        t.setCpf(String.format("000.000.%03d-00", sequencia));
        return tutorRepo.salvar(t);
    }

    private Pet novoPet(Tutor tutor) {
        Pet p = new Pet();
        p.setNome("Pet " + (++sequencia));
        p.setTutor(tutor);
        return petRepo.salvar(p);
    }

    private Servico novoServico() {
        Servico s = new Servico();
        s.setNome("Serviço " + (++sequencia));
        s.setPreco(new BigDecimal("50.00"));
        s.setDuracaoMinutos(30);
        return servicoRepo.salvar(s);
    }

    private Agendamento novoAgendamento(Pet pet, Servico servico) {
        Agendamento a = new Agendamento();
        a.setPet(pet);
        a.setDataHora(LocalDateTime.now().plusDays(1));
        if (servico != null) {
            ItemAgendamento item = new ItemAgendamento();
            item.setAgendamento(a);
            item.setServico(servico);
            item.setPrecoCobrado(servico.getPreco());
            a.getItens().add(item);
            a.setValorTotal(servico.getPreco());
        }
        return agendamentoRepo.salvar(a);
    }
}
