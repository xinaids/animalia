package ifrs.ibiruba.animalia.util;

import java.util.List;
import java.util.stream.Stream;

public final class ValidacaoUtil {

    private static final String REGEX_EMAIL = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    private ValidacaoUtil() {}

    /** Retorna true se todos os campos forem não-nulos e não-em-branco. */
    public static boolean todosPreenchidos(String... campos) {
        return Stream.of(campos).allMatch(c -> c != null && !c.isBlank());
    }

    /** Retorna lista de mensagens de erro para o cadastro. Vazia = válido. */
    public static List<String> validarCadastro(
            String nome, String email, String senha, String confirmacao) {

        return Stream.of(
            (nome == null || nome.isBlank())          ? "O nome é obrigatório."                 : null,
            (email == null || !email.matches(REGEX_EMAIL)) ? "Informe um e-mail válido."        : null,
            (senha == null || senha.length() < 8)     ? "A senha deve ter no mínimo 8 caracteres." : null,
            (confirmacao == null || !confirmacao.equals(senha)) ? "As senhas não conferem."     : null
        ).filter(msg -> msg != null).toList();
    }

    /** Retorna lista de mensagens para redefinição de senha. Vazia = válido. */
    public static List<String> validarNovaSenha(String novaSenha, String confirmacao) {
        return Stream.of(
            (novaSenha == null || novaSenha.length() < 8)         ? "A senha deve ter no mínimo 8 caracteres." : null,
            (confirmacao == null || !confirmacao.equals(novaSenha)) ? "As senhas não conferem."                : null
        ).filter(msg -> msg != null).toList();
    }

    public static boolean emailValido(String email) {
        return email != null && email.matches(REGEX_EMAIL);
    }
}
