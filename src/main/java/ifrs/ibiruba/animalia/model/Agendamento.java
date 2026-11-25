package ifrs.ibiruba.animalia.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAgendamento status = StatusAgendamento.AGENDADO;

    @Column(length = 1000)
    private String observacoes;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "agendamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemAgendamento> itens = new ArrayList<>();

    public Long getId()                         { return id; }
    public Pet getPet()                         { return pet; }
    public LocalDateTime getDataHora()          { return dataHora; }
    public StatusAgendamento getStatus()        { return status; }
    public String getObservacoes()              { return observacoes; }
    public BigDecimal getValorTotal()           { return valorTotal; }
    public List<ItemAgendamento> getItens()     { return itens; }

    public void setPet(Pet pet)                         { this.pet = pet; }
    public void setDataHora(LocalDateTime d)            { this.dataHora = d; }
    public void setStatus(StatusAgendamento s)          { this.status = s; }
    public void setObservacoes(String obs)               { this.observacoes = obs; }
    public void setValorTotal(BigDecimal v)              { this.valorTotal = v; }
    public void setItens(List<ItemAgendamento> itens)    { this.itens = itens; }
}
