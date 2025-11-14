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
}




