package ifrs.ibiruba.animalia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String senhaHash;

    @Column(nullable = false)
    private String salt;

    @Column
    private String codigoRecuperacaoHash;

    @Column
    private String saltRecuperacao;

    public Long getId()                       { return id; }
    public String getNome()                   { return nome; }
    public String getEmail()                  { return email; }
    public String getSenhaHash()              { return senhaHash; }
    public String getSalt()                   { return salt; }
    public String getCodigoRecuperacaoHash()  { return codigoRecuperacaoHash; }
    public String getSaltRecuperacao()        { return saltRecuperacao; }

    public void setNome(String nome)                             { this.nome = nome; }
    public void setEmail(String email)                           { this.email = email; }
    public void setSenhaHash(String senhaHash)                   { this.senhaHash = senhaHash; }
    public void setSalt(String salt)                             { this.salt = salt; }
    public void setCodigoRecuperacaoHash(String h)               { this.codigoRecuperacaoHash = h; }
    public void setSaltRecuperacao(String s)                     { this.saltRecuperacao = s; }
}
