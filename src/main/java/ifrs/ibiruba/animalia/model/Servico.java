package ifrs.ibiruba.animalia.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column
    private int duracaoMinutos;

    @Column(nullable = false)
    private boolean ativo = true;

    public Long getId()              { return id; }
    public String getNome()          { return nome; }
    public String getDescricao()     { return descricao; }
    /** O SQLite não preserva a escala decimal (35.00 volta como 35); normaliza para 2 casas. */
    public BigDecimal getPreco()     { return preco == null ? null : preco.setScale(2, RoundingMode.HALF_UP); }
    public int getDuracaoMinutos()   { return duracaoMinutos; }
    public boolean isAtivo()         { return ativo; }

    public void setNome(String nome)             { this.nome = nome; }
    public void setDescricao(String descricao)   { this.descricao = descricao; }
    public void setPreco(BigDecimal preco)        { this.preco = preco; }
    public void setDuracaoMinutos(int d)          { this.duracaoMinutos = d; }
    public void setAtivo(boolean ativo)           { this.ativo = ativo; }
}
