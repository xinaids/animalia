package ifrs.ibiruba.animalia.service;

import ifrs.ibiruba.animalia.model.Usuario;
import ifrs.ibiruba.animalia.repository.UsuarioRepository;
import ifrs.ibiruba.animalia.util.SenhaUtil;

public class LoginService {

    // Mensagem genérica: não revela se foi o e-mail ou a senha que falhou
    private static final String MSG_INVALIDO = "E-mail ou senha inválidos.";

    private final UsuarioRepository usuarioRepository;

    public LoginService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Autentica o usuário.
     *
     * @return o Usuario autenticado.
     * @throws IllegalArgumentException com mensagem genérica em qualquer falha.
     */
    public Usuario autenticar(String email, String senha) {
        if (email == null || senha == null) {
            throw new IllegalArgumentException(MSG_INVALIDO);
        }

        Usuario usuario = usuarioRepository.buscarPorEmail(email)
            .orElseThrow(() -> new IllegalArgumentException(MSG_INVALIDO));

        if (!SenhaUtil.verificar(senha, usuario.getSalt(), usuario.getSenhaHash())) {
            throw new IllegalArgumentException(MSG_INVALIDO);
        }

        return usuario;
    }
}
