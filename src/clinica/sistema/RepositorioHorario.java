package clinica.sistema;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepositorioHorario {
    List<Horario> repositorio = new ArrayList<>();

    public void adicionar(Horario h) {
        repositorio.add(h);
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
                if (p.length >= 6) {
                    int id = Integer.parseInt(p[0]);
                    LocalDateTime horaInicio = LocalDateTime.parse(p[1]);
                    LocalDateTime horaFim =LocalDateTime.parse(p[2]);
                    int idMedico  = Integer.parseInt(p[3]);
                    boolean disponivel = Boolean.parseBoolean(p[4]);
                    String tipo = p[5];

                    repositorio.add(new Horario(id,horaInicio,horaFim,idMedico,disponivel,tipo));
                }
            }
            System.out.println(" Horários carregados de " + nomeArquivo);
        } catch (IOException e) {
            System.out.println(" Arquivo de horários não encontrado, iniciando vazio.");
        }
    }

}





