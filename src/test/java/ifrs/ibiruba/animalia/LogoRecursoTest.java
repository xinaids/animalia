package ifrs.ibiruba.animalia;

import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class LogoRecursoTest {

    private static final String CAMINHO = "/ifrs/ibiruba/animalia/imagens/animalia-logo.png";

    @Test
    void logoExisteNoClasspath() throws Exception {
        try (InputStream is = getClass().getResourceAsStream(CAMINHO)) {
            assertNotNull(is, "Recurso não encontrado no classpath: " + CAMINHO);
        }
    }

    @Test
    void logoEhPngValido() throws Exception {
        byte[] magicEsperado = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        try (InputStream is = getClass().getResourceAsStream(CAMINHO)) {
            assertNotNull(is, "Recurso não encontrado: " + CAMINHO);
            byte[] cabecalho = is.readNBytes(8);
            assertArrayEquals(magicEsperado, cabecalho, "Arquivo não é um PNG válido");
        }
    }
}
