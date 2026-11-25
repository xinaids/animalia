package ifrs.ibiruba.animalia.util;

import ifrs.ibiruba.animalia.model.Usuario;

public class Sessao {

    private static Sessao instancia;

    private Usuario usuarioLogado;

    private Sessao() {}

    public static synchronized Sessao getInstance() {
        if (instancia == null) {
            instancia = new Sessao();
        }
        return instancia;
    }

    public void iniciar(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public void encerrar() {
        this.usuarioLogado = null;
    }

    public Usuario getUsuario() {
        return usuarioLogado;
    }

    public boolean estaAtiva() {
        return usuarioLogado != null;
    }
}
