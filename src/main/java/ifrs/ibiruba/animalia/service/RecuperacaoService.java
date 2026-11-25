package ifrs.ibiruba.animalia.service;

import ifrs.ibiruba.animalia.model.Usuario;
import ifrs.ibiruba.animalia.repository.UsuarioRepository;
import ifrs.ibiruba.animalia.util.SenhaUtil;
import ifrs.ibiruba.animalia.util.ValidacaoUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecuperacaoService {

    public static final int LIMITE_TENTATIVAS = 5;
    private static final String MSG_INVALIDO  = "E-mail ou código inválidos.";
    private static final String MSG_BLOQUEADO =
        "Muitas tentativas falhas. Reinicie o programa para tentar novamente.";

    private final UsuarioRepository usuarioRepository;
    // Rastreamento simples em memória: email (lowercase) → número de tentativas falhas
    private final Map<String, Integer> tentativas = new HashMap<>();

    public RecuperacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Redefine a senha usando o código de recuperação.
     *
     * @return novo código de recuperação em texto puro — deve ser exibido uma única vez.
     * @throws IllegalArgumentException para erros de validação ou credenciais inválidas.
     * @throws IllegalStateException se o limite de tentativas for atingido.
     */
    public String redefinir(String email, String codigoFornecido,
                             String novaSenha, String confirmacao) {

        String chave = email == null ? "" : email.toLowerCase();

        if (tentativas.getOrDefault(chave, 0) >= LIMITE_TENTATIVAS) {
            throw new IllegalStateException(MSG_BLOQUEADO);
        }

        // Busca o usuário sem revelar se ele existe
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
            .orElse(null);

        boolean credenciaisValidas = usuario != null
            && usuario.getCodigoRecuperacaoHash() != null
            && usuario.getSaltRecuperacao()        != null
            && SenhaUtil.verificar(codigoFornecido,
                                   usuario.getSaltRecuperacao(),
                                   usuario.getCodigoRecuperacaoHash());

        if (!credenciaisValidas) {
            int novasTentativas = tentativas.getOrDefault(chave, 0) + 1;
            tentativas.put(chave, novasTentativas);
            if (novasTentativas >= LIMITE_TENTATIVAS) {
                throw new IllegalStateException(MSG_BLOQUEADO);
            }
            throw new IllegalArgumentException(MSG_INVALIDO);
        }

        List<String> erros = ValidacaoUtil.validarNovaSenha(novaSenha, confirmacao);
        if (!erros.isEmpty()) {
            throw new IllegalArgumentException(String.join(" ", erros));
        }

        // Gera nova senha
        String novoSalt     = SenhaUtil.gerarSalt();
        String novoHash     = SenhaUtil.hash(novaSenha, novoSalt);

        // Gera novo código de recuperação
        String novoCodigo       = SenhaUtil.gerarCodigoRecuperacao();
        String novoSaltCodigo   = SenhaUtil.gerarSalt();
        String novoHashCodigo   = SenhaUtil.hash(novoCodigo, novoSaltCodigo);

        usuario.setSenhaHash(novoHash);
        usuario.setSalt(novoSalt);
        usuario.setCodigoRecuperacaoHash(novoHashCodigo);
        usuario.setSaltRecuperacao(novoSaltCodigo);
        usuarioRepository.salvar(usuario);

        tentativas.remove(chave);
        return novoCodigo;
    }

    /** Exposto para testes: lê o número de tentativas do e-mail. */
    public int getTentativas(String email) {
        return tentativas.getOrDefault(email.toLowerCase(), 0);
    }
}
