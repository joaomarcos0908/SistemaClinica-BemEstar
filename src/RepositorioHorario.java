import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepositorioHorario {
    List<Horario> repositorio = new ArrayList<>();

    public Horario adicionar(int id, LocalDate data, boolean disponivel, LocalDateTime horaFim, LocalDateTime horaInicio, int idMedico, String tipo) {

        Horario horario = new Horario(id, data, disponivel, horaFim, horaInicio, idMedico, tipo);

        repositorio.add(horario);
        return horario;
    }

    public Horario buscarPorID(int id) {
        for (Horario h : repositorio) {
            if (h.getId() == id) {
                return h;
            }
            }
        return null;
    }
public List<Horario> listarQuadroDeHorarios(){
        return repositorio;
}

    public boolean remover(int id) {
        for (Horario h : repositorio) {
            if (h.getId()==id){
        boolean removido=repositorio.remove(h);
        return true;
            }
        }
        return false;
    }
    public void salvarCSV(String nomeArquivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nomeArquivo))) {
            bw.write("id,data,disponivel,horaInicio,horaFim,idMedico,tipo");
            bw.newLine();

            for (Horario h : repositorio) {
                bw.write(
                        h.getId() + "," +
                                h.getData() + "," +
                                h.isDisponivel() + "," +
                                h.getHoraInicio() + "," +
                                h.getHoraFim() + "," +
                                h.getIdMedico() + "," +
                                h.getTipo()
                );
                bw.newLine();
            }
            System.out.println(" Horários salvos em " + nomeArquivo);
        } catch (IOException e) {
            System.out.println(" Erro ao salvar horários: " + e.getMessage());
        }
    }

    public void carregarCSV(String nomeArquivo) {
        repositorio.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            br.readLine();
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(",");
                if (p.length >= 7) {
                    int id = Integer.parseInt(p[0]);
                    LocalDate data = LocalDate.parse(p[1]);
                    boolean disponivel = Boolean.parseBoolean(p[2]);
                    LocalDateTime horaInicio = LocalDateTime.parse(p[3]);
                    LocalDateTime horaFim = LocalDateTime.parse(p[4]);
                    int idMedico = Integer.parseInt(p[5]);
                    String tipo = p[6];
                    repositorio.add(new Horario(id, data, disponivel, horaFim, horaInicio, idMedico, tipo));
                }
            }
            System.out.println(" Horários carregados de " + nomeArquivo);
        } catch (IOException e) {
            System.out.println("Arquivo de horários não encontrado, iniciando vazio.");
        }
    }
}





