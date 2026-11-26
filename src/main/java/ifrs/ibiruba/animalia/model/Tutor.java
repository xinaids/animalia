package ifrs.ibiruba.animalia.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tutores")
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(unique = true, length = 14)
    private String cpf;

    @Column(length = 20)
    private String telefone;

    @Column(length = 255)
    private String email;

    // Sem cascade/orphanRemoval: a exclusão do tutor é bloqueada enquanto houver pets vinculados.
    @OneToMany(mappedBy = "tutor")
    private List<Pet> pets = new ArrayList<>();

    public Long getId()          { return id; }
    public String getNome()      { return nome; }
    public String getCpf()       { return cpf; }
    public String getTelefone()  { return telefone; }
    public String getEmail()     { return email; }
    public List<Pet> getPets()   { return pets; }

    public void setNome(String nome)       { this.nome = nome; }
    public void setCpf(String cpf)         { this.cpf = cpf; }
    public void setTelefone(String t)      { this.telefone = t; }
    public void setEmail(String email)     { this.email = email; }
    public void setPets(List<Pet> pets)    { this.pets = pets; }
}
