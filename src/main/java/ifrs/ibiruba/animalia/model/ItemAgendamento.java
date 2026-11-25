package ifrs.ibiruba.animalia.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_agendamento")
public class ItemAgendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agendamento_id", nullable = false)
    private Agendamento agendamento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoCobrado;

    public Long getId()                 { return id; }
    public Agendamento getAgendamento() { return agendamento; }
    public Servico getServico()         { return servico; }
    public BigDecimal getPrecoCobrado() { return precoCobrado; }

    public void setAgendamento(Agendamento a) { this.agendamento = a; }
    public void setServico(Servico s)          { this.servico = s; }
    public void setPrecoCobrado(BigDecimal p)  { this.precoCobrado = p; }
}
