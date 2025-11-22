package clinica.repositorio;

import clinica.sistema.Consulta;
import clinica.sistema.StatusConsulta;

import java.io.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RepositorioConsulta {

List<Consulta> consultas=new ArrayList<>();
    public void adicionar(Consulta c){
  consultas.add(c);
    }
public List<Consulta> listarConsultas(){
        return consultas;
}

public  boolean remover(int id){
 for(Consulta c: consultas){
     if(c.getId()== id){
         consultas.remove(c);
         return true;
     }
 }
 return false;
}
public Boolean procurarConsultaPorId(int id){
        for(Consulta c: consultas){
            if(c.getId()==id){
                System.out.println(consultas.get(c.getId()));
                return true;
            }
        }
        return false;
}
    public void salvarCSV(String nomeArquivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nomeArquivo))) {
            bw.write("id,idPaciente,idMedico,idEspecialidade,idHorario,status,emergencial,dataAgendamento,dataCancelamento");
            bw.newLine();
            for (Consulta c : consultas) {
                bw.write(c.getId() + "," +
                        c.getIdPaciente() + "," +
                        c.getIdMedico() + "," +
                        c.getIdEspecialidade() + "," +
                        c.getIdHorario() + "," +
                        c.getStatus() + "," +
                        c.isEmergencial() + "," +
                        (c.getDataDeAgendamento() != null ? c.getDataDeAgendamento() : "") + "," +
                        (c.getDataDeCancelamento() != null ? c.getDataDeCancelamento() : ""));
                bw.newLine();
            }
            System.out.println(" Consultas salvas em " + nomeArquivo);
        } catch (IOException e) {
            System.out.println(" Erro ao salvar consultas: " + e.getMessage());
        }
    }

    public void carregarCSV(String nomeArquivo) {
        consultas.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            br.readLine();
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(",");
                if (p.length >= 9) {
                    LocalDate dataAgendamento = p[7].isEmpty() ? LocalDate.now() : LocalDate.parse(p[7]);
                    LocalDate dataCancelamento = p[8].isEmpty() ? null : LocalDate.parse(p[8]);

                    Consulta c = new Consulta(
                            dataAgendamento,
                            dataCancelamento,
                            Integer.parseInt(p[0]),
                            Integer.parseInt(p[3]),
                            StatusConsulta.valueOf(p[5]),
                            Boolean.parseBoolean(p[6]),
                            Integer.parseInt(p[2]),
                            Integer.parseInt(p[1])
                    );
                    c.setIdHorario(Integer.parseInt(p[4]));
                    consultas.add(c);
                }
            }
            System.out.println(" Consultas carregadas de " + nomeArquivo);
        } catch (IOException e) {
            System.out.println("" +
                    " Arquivo de consultas não encontrado, iniciando vazio.");
        }
    }
}

