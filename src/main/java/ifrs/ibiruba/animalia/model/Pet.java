package ifrs.ibiruba.animalia.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(length = 100)
    private String especie;

    @Column(length = 100)
    private String raca;

    @Column
    private LocalDate dataNascimento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    // Sem cascade/orphanRemoval: a exclusão do pet é bloqueada enquanto houver agendamentos.
    @OneToMany(mappedBy = "pet")
    private List<Agendamento> agendamentos = new ArrayList<>();

    public Long getId()                      { return id; }
    public String getNome()                  { return nome; }
    public String getEspecie()               { return especie; }
    public String getRaca()                  { return raca; }
    public LocalDate getDataNascimento()     { return dataNascimento; }
    public Tutor getTutor()                  { return tutor; }
    public List<Agendamento> getAgendamentos() { return agendamentos; }

    public void setNome(String nome)                       { this.nome = nome; }
    public void setEspecie(String especie)                 { this.especie = especie; }
    public void setRaca(String raca)                       { this.raca = raca; }
    public void setDataNascimento(LocalDate d)             { this.dataNascimento = d; }
    public void setTutor(Tutor tutor)                      { this.tutor = tutor; }
    public void setAgendamentos(List<Agendamento> lista)   { this.agendamentos = lista; }
}
