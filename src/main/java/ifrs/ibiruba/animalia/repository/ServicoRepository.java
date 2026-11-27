package ifrs.ibiruba.animalia.repository;

import ifrs.ibiruba.animalia.model.Servico;
import jakarta.persistence.EntityManager;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class ServicoRepository extends BaseRepository<Servico, Long> {

    public ServicoRepository() {
        super(Servico.class);
    }

    @Override
    protected Optional<String> motivoBloqueioExclusao(EntityManager em, Long id) {
        long usos = contarVinculos(em,
            "select count(i) from ItemAgendamento i where i.servico.id = :id", id);
        if (usos > 0) {
            return Optional.of("Este serviço já foi utilizado em " + usos
                + (usos == 1 ? " agendamento" : " agendamentos")
                + ". Desative o serviço em vez de excluí-lo.");
        }
        return Optional.empty();
    }

    /*
     * Observação: o lower() do SQLite só converte letras ASCII ("Ê" ≠ "ê"), por isso
     * a ordenação e a comparação de nomes são feitas em Java com regras do português.
     */
    private static final Locale PT_BR = Locale.of("pt", "BR");

    /** Todos os serviços (ativos e inativos), em ordem alfabética. */
    public List<Servico> listarOrdenadoPorNome() {
        List<Servico> lista = listar();
        lista.sort(Comparator.comparing(Servico::getNome, comparadorNomes()));
        return lista;
    }

    /** Apenas serviços ativos, em ordem alfabética (para seleção em agendamentos). */
    public List<Servico> listarAtivos() {
        List<Servico> lista = listarOrdenadoPorNome();
        lista.removeIf(s -> !s.isAtivo());
        return lista;
    }

    /**
     * Verifica se já existe serviço com o nome informado (sem diferenciar maiúsculas,
     * inclusive em letras acentuadas).
     *
     * @param idIgnorado id do próprio serviço em edição (ou null em um cadastro novo)
     */
    public boolean existePorNome(String nome, Long idIgnorado) {
        String alvo = nome.trim().toLowerCase(PT_BR);
        EntityManager em = abrirEm();
        try {
            return em.createQuery("select s.id, s.nome from Servico s", Object[].class)
                     .getResultStream()
                     .anyMatch(linha -> !Objects.equals(linha[0], idIgnorado)
                                     && ((String) linha[1]).trim().toLowerCase(PT_BR).equals(alvo));
        } finally {
            em.close();
        }
    }

    /** Ordem alfabética do português, ignorando maiúsculas e acentos. */
    public static Comparator<String> comparadorNomes() {
        Collator collator = Collator.getInstance(PT_BR);
        collator.setStrength(Collator.SECONDARY);
        return collator::compare;
    }

    /** Conta apenas os serviços com ativo=true. */
    public long contarAtivos() {
        EntityManager em = abrirEm();
        try {
            return em.createQuery("select count(s) from Servico s where s.ativo = true", Long.class)
                     .getSingleResult();
        } finally {
            em.close();
        }
    }
}
