package ifrs.ibiruba.animalia.repository;

import ifrs.ibiruba.animalia.model.Agendamento;
import ifrs.ibiruba.animalia.model.StatusAgendamento;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AgendamentoRepository extends BaseRepository<Agendamento, Long> {

    public AgendamentoRepository() {
        super(Agendamento.class);
    }

    /** Conta agendamentos com status AGENDADO cuja dataHora seja no dia informado. */
    public long contarAgendadosHoje() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicio = hoje.atStartOfDay();
        LocalDateTime fim    = hoje.plusDays(1).atStartOfDay();

        EntityManager em = abrirEm();
        try {
            return em.createQuery(
                    "select count(a) from Agendamento a " +
                    "where a.status = :status and a.dataHora >= :inicio and a.dataHora < :fim",
                    Long.class)
                .setParameter("status", StatusAgendamento.AGENDADO)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getSingleResult();
        } finally {
            em.close();
        }
    }
}
