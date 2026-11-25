package ifrs.ibiruba.animalia;

import org.junit.jupiter.api.Test;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica, via XML + reflexão, que cada .fxml tem os fx:id e handlers
 * correspondentes nos controllers, sem abrir a interface gráfica.
 */
class FxmlVerificadorTest {

    private static final String[] FXMLS = {
        "login.fxml", "cadastro.fxml", "recuperacao.fxml", "dashboard.fxml", "construcao.fxml"
    };

    @Test
    void todosOsFxmlsExistem() {
        for (String fxml : FXMLS) {
            URL url = getClass().getResource("/ifrs/ibiruba/animalia/view/" + fxml);
            assertNotNull(url, "FXML não encontrado no classpath: " + fxml);
        }
    }

    @Test
    void todosOsFxIdsTêmCampoCorrespondente() throws Exception {
        List<String> falhas = new ArrayList<>();

        for (String fxml : FXMLS) {
            InputStream is = getClass().getResourceAsStream("/ifrs/ibiruba/animalia/view/" + fxml);
            if (is == null) continue;

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(is);

            String controllerNome = doc.getDocumentElement().getAttributeNS(
                "http://javafx.com/fxml", "controller");

            if (controllerNome == null || controllerNome.isBlank()) {
                falhas.add(fxml + ": sem fx:controller");
                continue;
            }

            Class<?> controller;
            try {
                controller = Class.forName(controllerNome);
            } catch (ClassNotFoundException e) {
                falhas.add(fxml + ": controller não encontrado — " + controllerNome);
                continue;
            }

            // Verifica fx:id
            List<String> ids = coletarAtributos(doc, "id", "http://javafx.com/fxml");
            for (String id : ids) {
                if (!temCampoFxml(controller, id)) {
                    falhas.add(fxml + ": fx:id='" + id + "' sem campo @FXML em " + controllerNome);
                }
            }

            // Verifica onAction e outros handlers (atributos que começam com "on" ou são "handler")
            List<String> handlers = coletarAtributosOnAction(doc);
            for (String handler : handlers) {
                // handler está no formato "#nomeMetodo"
                String nomeMetodo = handler.startsWith("#") ? handler.substring(1) : handler;
                if (!temMetodoFxml(controller, nomeMetodo)) {
                    falhas.add(fxml + ": handler='" + handler + "' sem método @FXML em " + controllerNome);
                }
            }
        }

        assertTrue(falhas.isEmpty(),
            "Problemas nos FXMLs:\n" + String.join("\n", falhas));
    }

    private List<String> coletarAtributos(Document doc, String atributo, String ns) {
        List<String> resultado = new ArrayList<>();
        coletarAtributosRecursivo(doc.getDocumentElement(), atributo, ns, resultado);
        return resultado;
    }

    private void coletarAtributosRecursivo(Element el, String atributo, String ns,
                                            List<String> resultado) {
        String valor = el.getAttributeNS(ns, atributo);
        if (valor != null && !valor.isBlank()) {
            resultado.add(valor);
        }
        NodeList filhos = el.getChildNodes();
        for (int i = 0; i < filhos.getLength(); i++) {
            Node n = filhos.item(i);
            if (n instanceof Element filho) {
                coletarAtributosRecursivo(filho, atributo, ns, resultado);
            }
        }
    }

    private List<String> coletarAtributosOnAction(Document doc) {
        List<String> resultado = new ArrayList<>();
        coletarHandlersRecursivo(doc.getDocumentElement(), resultado);
        return resultado;
    }

    private void coletarHandlersRecursivo(Element el, List<String> resultado) {
        NamedNodeMap attrs = el.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node a = attrs.item(i);
            String nome = a.getLocalName();
            if (nome != null && nome.startsWith("on")) {
                String valor = a.getNodeValue();
                if (valor != null && valor.startsWith("#")) {
                    resultado.add(valor);
                }
            }
        }
        NodeList filhos = el.getChildNodes();
        for (int i = 0; i < filhos.getLength(); i++) {
            Node n = filhos.item(i);
            if (n instanceof Element filho) {
                coletarHandlersRecursivo(filho, resultado);
            }
        }
    }

    private boolean temCampoFxml(Class<?> classe, String nome) {
        Class<?> atual = classe;
        while (atual != null && atual != Object.class) {
            for (Field f : atual.getDeclaredFields()) {
                if (f.getName().equals(nome) &&
                    f.isAnnotationPresent(javafx.fxml.FXML.class)) {
                    return true;
                }
            }
            atual = atual.getSuperclass();
        }
        return false;
    }

    private boolean temMetodoFxml(Class<?> classe, String nome) {
        Class<?> atual = classe;
        while (atual != null && atual != Object.class) {
            for (Method m : atual.getDeclaredMethods()) {
                if (m.getName().equals(nome) &&
                    m.isAnnotationPresent(javafx.fxml.FXML.class)) {
                    return true;
                }
            }
            atual = atual.getSuperclass();
        }
        return false;
    }
}
