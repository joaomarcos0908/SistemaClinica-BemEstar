package clinica.repositorio;

import clinica.pessoas.Paciente;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class RepositorioPaciente {
    private Queue<Paciente> pacientes = new ArrayDeque<>();

    public void adicionarPaciente(Paciente paciente){
        if(paciente == null){
            throw new IllegalArgumentException("Paciente não pode ser nulo.");
        } else {
            pacientes.add(paciente);
        }
    }
    public void removerPaciente(Paciente paciente){
        if(paciente == null){
            throw new IllegalArgumentException("Paciente não pode ser nulo.");
        }
        if (pacientes.contains(paciente)){
            pacientes.remove(paciente);
        } else{
            throw new IllegalArgumentException(paciente.getNome() + " não está na lista.");
        }
    }
    public List<Paciente> listarPacientes(){
        return new ArrayList<>(pacientes);
    }
}
