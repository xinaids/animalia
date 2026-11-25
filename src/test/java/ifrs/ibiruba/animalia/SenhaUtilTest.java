package ifrs.ibiruba.animalia;

import ifrs.ibiruba.animalia.util.SenhaUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SenhaUtilTest {

    @Test
    void testHashVerificacao() {
        String salt = SenhaUtil.gerarSalt();
        String hash = SenhaUtil.hash("minhaSenha123", salt);

        assertTrue(SenhaUtil.verificar("minhaSenha123", salt, hash),
            "Deve verificar corretamente com a senha certa");
        assertFalse(SenhaUtil.verificar("senhaErrada", salt, hash),
            "Não deve verificar com senha errada");
    }

    @Test
    void testSaltsDiferentes() {
        String senha = "minhaSenha123";
        String salt1 = SenhaUtil.gerarSalt();
        String salt2 = SenhaUtil.gerarSalt();

        assertNotEquals(salt1, salt2, "Salts gerados devem ser diferentes");
        assertNotEquals(SenhaUtil.hash(senha, salt1), SenhaUtil.hash(senha, salt2),
            "O mesmo texto com salts diferentes deve gerar hashes diferentes");
    }

    @Test
    void testTempoConstante() {
        String salt = SenhaUtil.gerarSalt();
        String hash = SenhaUtil.hash("texto", salt);

        // Não deve lançar exceção ao comparar strings de tamanhos diferentes
        assertDoesNotThrow(() -> SenhaUtil.verificar("texto_mais_longo_que_o_esperado", salt, hash));
        assertDoesNotThrow(() -> SenhaUtil.verificar("", salt, hash));
    }

    @Test
    void testGerarCodigoRecuperacao() {
        String codigo = SenhaUtil.gerarCodigoRecuperacao();

        assertNotNull(codigo);
        assertEquals(8, codigo.length(), "Código deve ter exatamente 8 caracteres");
        assertTrue(codigo.matches("[A-Za-z0-9]+"), "Código deve ser alfanumérico");
    }

    @Test
    void testHashNaoEhVazioOuNulo() {
        String salt = SenhaUtil.gerarSalt();
        String hash = SenhaUtil.hash("qualquerTexto", salt);

        assertNotNull(hash);
        assertFalse(hash.isBlank());
    }
}
