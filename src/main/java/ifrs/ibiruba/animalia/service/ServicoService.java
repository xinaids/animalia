package ifrs.ibiruba.animalia.service;

import ifrs.ibiruba.animalia.model.Servico;
import ifrs.ibiruba.animalia.repository.ServicoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ServicoService {

    static final int        NOME_MAX       = 255;
    static final int        DESCRICAO_MAX  = 1000;
    static final int        DURACAO_MAX    = 24 * 60;
    static final BigDecimal PRECO_MAX      = new BigDecimal("99999999.99");

    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    public List<Servico> listar() {
        return servicoRepository.listarOrdenadoPorNome();
    }

    /**
     * Valida e salva um serviço (novo se {@code id} for null, edição caso contrário).
     * Preço aceita "89,90", "89.90" ou "1.234,56"; duração em minutos.
     *
     * @throws IllegalArgumentException com todas as mensagens de validação.
     */
    public Servico salvar(Long id, String nome, String descricao,
                          String precoTexto, String duracaoTexto, boolean ativo) {
        List<String> erros = new ArrayList<>();

        String nomeLimpo = nome == null ? "" : nome.trim();
        String descricaoLimpa = (descricao == null || descricao.isBlank()) ? null : descricao.trim();

        if (nomeLimpo.isEmpty()) {
            erros.add("O nome é obrigatório.");
        } else if (nomeLimpo.length() > NOME_MAX) {
            erros.add("O nome deve ter no máximo " + NOME_MAX + " caracteres.");
        }

        if (descricaoLimpa != null && descricaoLimpa.length() > DESCRICAO_MAX) {
            erros.add("A descrição deve ter no máximo " + DESCRICAO_MAX + " caracteres.");
        }

        BigDecimal preco = converterPreco(precoTexto);
        if (preco == null) {
            erros.add("Informe um preço válido (ex.: 59,90).");
        } else if (preco.signum() <= 0) {
            erros.add("O preço deve ser maior que zero.");
        } else if (preco.compareTo(PRECO_MAX) > 0) {
            erros.add("O preço informado é muito alto.");
        }

        Integer duracao = converterInteiro(duracaoTexto);
        if (duracao == null || duracao <= 0) {
            erros.add("Informe a duração em minutos (número inteiro maior que zero).");
        } else if (duracao > DURACAO_MAX) {
            erros.add("A duração não pode passar de " + DURACAO_MAX + " minutos.");
        }

        if (!nomeLimpo.isEmpty() && servicoRepository.existePorNome(nomeLimpo, id)) {
            erros.add("Já existe um serviço com este nome.");
        }

        if (!erros.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", erros));
        }

        // Na edição, parte da entidade do banco e altera só os campos do formulário.
        Servico servico = (id == null)
            ? new Servico()
            : servicoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado. Ele pode ter sido excluído."));

        servico.setNome(nomeLimpo);
        servico.setDescricao(descricaoLimpa);
        servico.setPreco(preco);
        servico.setDuracaoMinutos(duracao);
        servico.setAtivo(ativo);

        return servicoRepository.salvar(servico);
    }

    /** Ativa um serviço inativo ou desativa um ativo. */
    public Servico alternarAtivo(Long id) {
        Servico servico = servicoRepository.buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado. Ele pode ter sido excluído."));
        servico.setAtivo(!servico.isAtivo());
        return servicoRepository.salvar(servico);
    }

    /** Converte texto em preço com 2 casas; retorna null se inválido. */
    static BigDecimal converterPreco(String texto) {
        if (texto == null || texto.isBlank()) return null;
        String s = texto.trim().replace("R$", "").replace(" ", "");
        if (s.contains(",")) {
            // Formato brasileiro: ponto = milhar, vírgula = decimal
            s = s.replace(".", "").replace(",", ".");
        }
        if (!s.matches("\\d+(\\.\\d{1,2})?")) return null;
        return new BigDecimal(s).setScale(2, RoundingMode.UNNECESSARY);
    }

    static Integer converterInteiro(String texto) {
        if (texto == null || !texto.trim().matches("\\d{1,9}")) return null;
        return Integer.parseInt(texto.trim());
    }
}
