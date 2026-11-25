package ifrs.ibiruba.animalia.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public final class SenhaUtil {

    private static final int ITERACOES    = 310_000;
    private static final int TAMANHO_HASH = 32; // bytes
    private static final int TAMANHO_SALT = 16; // bytes
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String CHARS_CODIGO = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private SenhaUtil() {}

    /** Gera um salt aleatório (16 bytes) em Base64. */
    public static String gerarSalt() {
        byte[] bytes = new byte[TAMANHO_SALT];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /** Deriva um hash PBKDF2 do texto usando o salt fornecido. Retorna Base64. */
    public static String hash(String texto, String salt) {
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            PBEKeySpec spec = new PBEKeySpec(
                texto.toCharArray(), saltBytes, ITERACOES, TAMANHO_HASH * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITMO);
            byte[] hashBytes = factory.generateSecret(spec).getEncoded();
            spec.clearPassword();
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Erro ao gerar hash: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica o texto contra salt+hash usando comparação em tempo constante.
     * Nunca revela qual dos dois estava errado.
     */
    public static boolean verificar(String texto, String salt, String hashEsperado) {
        String hashCalculado = hash(texto, salt);
        byte[] b1 = Base64.getDecoder().decode(hashCalculado);
        byte[] b2 = Base64.getDecoder().decode(hashEsperado);
        return MessageDigest.isEqual(b1, b2);
    }

    /** Gera um código de recuperação de 8 caracteres alfanuméricos. */
    public static String gerarCodigoRecuperacao() {
        SecureRandom rng = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(CHARS_CODIGO.charAt(rng.nextInt(CHARS_CODIGO.length())));
        }
        return sb.toString();
    }
}
