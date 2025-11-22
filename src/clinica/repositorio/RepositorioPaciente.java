package clinica.repositorio;

import clinica.pessoas.Paciente;
import clinica.pessoas.TipoAtendimento;
import clinica.pessoas.TipoPaciente;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class RepositorioPaciente {
    private Queue<Paciente> pacientes = new ArrayDeque<>();

    public void adicionarPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("Paciente não pode ser nulo.");
        } else {
            pacientes.add(paciente);
        }
    }

    public void removerPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("Paciente não pode ser nulo.");
        }
        if (pacientes.contains(paciente)) {
            pacientes.remove(paciente);
        } else {
            throw new IllegalArgumentException(paciente.getNome() + " não está na lista.");
        }
    }

    public List<Paciente> listarPacientes() {
        return new ArrayList<>(pacientes);
    }

    public void salvarPacientesCSV(String caminho) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminho))) {

            for (Paciente p : pacientes) {

                String historico = String.join(";", p.getHistoricoIds());

                String linha = p.getNome() + "," +
                        p.getCpf() + "," +
                        p.getDataNascimento() + "," +
                        p.getEmail() + "," +
                        p.getNumTelefone() + "," +
                        p.getEndereco() + "," +
                        p.isGestante() + "," +
                        p.hasEspectroAutista() + "," +
                        p.isPcd() + "," +
                        p.isLactante() + "," +
                        p.hasCriancaColo() + "," +
                        p.getTipoPaciente().name() + "," +
                        historico + "," +
                        p.getTipoAtendimento().name();

                bw.write(linha);
                bw.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar pacientes.", e);
        }
    }
    public void carregarPacientesCSV(String caminho) {
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

                boolean gestante = Boolean.parseBoolean(partes[6]);
                boolean espectro = Boolean.parseBoolean(partes[7]);
                boolean pcd = Boolean.parseBoolean(partes[8]);
                boolean lactante = Boolean.parseBoolean(partes[9]);
                boolean criancaColo = Boolean.parseBoolean(partes[10]);

                TipoPaciente tipoPaciente = TipoPaciente.valueOf(partes[11]);

                List<String> historico = new ArrayList<>();
                if (!partes[12].isEmpty()) {
                    String[] ids = partes[12].split(";");
                    historico.addAll(Arrays.asList(ids));
                }

                TipoAtendimento tipoAtendimento = TipoAtendimento.valueOf(partes[13]);

                Paciente paciente = new Paciente(
                        nome,
                        cpf,
                        dataNascimento,
                        email,
                        telefone,
                        endereco,
                        gestante,
                        espectro,
                        pcd,
                        lactante,
                        criancaColo,
                        tipoPaciente,
                        historico,
                        tipoAtendimento
                );

                adicionarPaciente(paciente);
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar pacientes.", e);
        }
    }

}

