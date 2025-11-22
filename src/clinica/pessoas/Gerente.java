package clinica.pessoas;
import java.io.*;
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
    public void salvarMedicosCSV(String caminho) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminho))) {

            for (Medico m : medicos) {

                StringBuilder esp = new StringBuilder();
                for (int i = 0; i < m.getEspecialidades().size(); i++) {
                    esp.append(m.getEspecialidades().get(i).name());
                    if (i < m.getEspecialidades().size() - 1) {
                        esp.append(";");
                    }
                }

                String linha = m.getNome() + "," +
                        m.getCpf() + "," +
                        m.getDataNascimento() + "," +
                        m.getEmail() + "," +
                        m.getNumTelefone() + "," +
                        m.getEndereco() + "," +
                        m.getCrm() + "," +
                        esp + "," +
                        m.getValorConsultaBase();

                bw.write(linha);
                bw.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar médicos.", e);
        }
    }

    public void carregarMedicosCSV(String caminho) {
        File arquivo = new File(caminho);
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(",");

                String nome = partes[0];
                String cpf = partes[1];
                LocalDate dataNascimento = LocalDate.parse(partes[2]);
                String email = partes[3];
                String telefone = partes[4];
                String endereco = partes[5];
                String crm = partes[6];

                List<Especialidade> especialidades = new ArrayList<>();
                if (!partes[7].isEmpty()) {
                    String[] esp = partes[7].split(";");
                    for (String e : esp) {
                        especialidades.add(Especialidade.valueOf(e));
                    }
                }

                double valorConsulta = Double.parseDouble(partes[8]);

                Medico medico = new Medico(
                        nome,
                        cpf,
                        dataNascimento,
                        email,
                        telefone,
                        endereco,
                        false, false, false, false, false,
                        crm,
                        especialidades,
                        valorConsulta
                );

                adicionarMedico(medico);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar médicos.", e);
        }
    }


}
