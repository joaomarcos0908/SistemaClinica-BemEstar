package clinica.pessoas;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;

public class Gerente extends Pessoa {
    public Gerente(String nome, String cpf, LocalDate dataNascimento, String email, String numTelefone, String endereco) {
        super(nome, cpf, dataNascimento, email, numTelefone, endereco);
    }
    private List<Medico> medicos = new ArrayList<>();

    public void adicionarMedico(Medico medico){
        if(medico == null){
            throw new IllegalArgumentException("Médico não pode ser nulo.");
        }
        if(!medicos.contains(medico)){
            medicos.add(medico);
        } else {
            throw new IllegalArgumentException(medico.getNome() +  " já foi adicionado.");
        }
    }
    public void removerMedico(Medico medico){
        if(medico == null){
            throw new IllegalArgumentException("Médico não pode ser nulo.");
        }
         if (medicos.contains(medico)){
            medicos.remove(medico);
        } else {
            throw new IllegalArgumentException(medico.getNome() + " não está na lista.");
        }
    }

    public List<Medico> getMedicos(){
        return new ArrayList<>(medicos);
    }
}
