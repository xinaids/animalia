package ifrs.ibiruba.animalia.service;

import ifrs.ibiruba.animalia.model.Usuario;
import ifrs.ibiruba.animalia.repository.UsuarioRepository;
import ifrs.ibiruba.animalia.util.SenhaUtil;
import ifrs.ibiruba.animalia.util.ValidacaoUtil;

import java.util.List;

public class CadastroService {

    private final UsuarioRepository usuarioRepository;

    public CadastroService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Valida e registra um novo usuário.
     *
     * @return o código de recuperação em texto puro — deve ser exibido uma única vez.
     * @throws IllegalArgumentException se qualquer validação falhar.
     */
    public String registrar(String nome, String email, String senha, String confirmacao) {
        List<String> erros = ValidacaoUtil.validarCadastro(nome, email, senha, confirmacao);
        if (!erros.isEmpty()) {
            throw new IllegalArgumentException(String.join(" ", erros));
        }

        if (usuarioRepository.existePorEmail(email)) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }

        String salt      = SenhaUtil.gerarSalt();
        String senhaHash = SenhaUtil.hash(senha, salt);

        String codigo             = SenhaUtil.gerarCodigoRecuperacao();
        String saltRecuperacao    = SenhaUtil.gerarSalt();
        String codigoHash         = SenhaUtil.hash(codigo, saltRecuperacao);

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email.toLowerCase());
        usuario.setSenhaHash(senhaHash);
        usuario.setSalt(salt);
        usuario.setCodigoRecuperacaoHash(codigoHash);
        usuario.setSaltRecuperacao(saltRecuperacao);

        usuarioRepository.salvar(usuario);

        return codigo;
    }
}
