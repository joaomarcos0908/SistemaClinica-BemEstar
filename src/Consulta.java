import java.time.LocalDate;
import java.time.LocalDateTime;


public class Consulta {
    private int id;
    private int idPaciente;
    private int idMedico;
    private int idEspecialidade;
    private int idHorario;
    StatusConsulta status;
    private boolean emergencial;
    LocalDate dataDeAgendamento;
    LocalDate dataDeCancelamento;

    public Consulta(LocalDate dataDeAgendamento, LocalDate dataDeCancelamento, int id, int idEspecialidade, StatusConsulta status, boolean emergencial, int idMedico, int idPaciente) {
        this.dataDeAgendamento = dataDeAgendamento;
        this.dataDeCancelamento = dataDeCancelamento;
        this.id = id;
        this.idEspecialidade = idEspecialidade;
        this.status =status!= null ? status:StatusConsulta.AGENDADA;
        this.emergencial = emergencial;
        this.idMedico = idMedico;
        this.idPaciente = idPaciente;
    }

    public LocalDate getDataDeAgendamento() {
        return dataDeAgendamento;
    }

    public void setDataDeAgendamento(LocalDate dataDeAgendamento) {
        this.dataDeAgendamento = dataDeAgendamento;
    }

    public LocalDate getDataDeCancelamento() {
        return dataDeCancelamento;
    }

    public void setDataDeCancelamento(LocalDate dataDeCancelamento) {
        this.dataDeCancelamento = dataDeCancelamento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEspecialidade() {
        return idEspecialidade;
    }

    public void setIdEspecialidade(int idEspecialidade) {
        this.idEspecialidade = idEspecialidade;
    }

    public boolean isEmergencial() {
        return emergencial;
    }

    public void setEmergencial(boolean emergencial) {
        this.emergencial = emergencial;
    }

    public int getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(int idHorario) {
        this.idHorario = idHorario;
    }

    public int getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public StatusConsulta getStatus() {
        return status;
    }

    public void setStatus(StatusConsulta status) {
        this.status = status;
    }

    public boolean agendarConsulta(RepositorioHorario repositorio) {

        Horario horario = repositorio.buscarPorID(idHorario);

        if (horario == null) {
            System.out.println("Horário não encontrado.");
            return false;
        }
        if (!horario.isDisponivel() && !emergencial) {
            System.out.println("O horário não está disponível.Por favor, procure outro.");
            return false;
        }
        if (emergencial) {
            this.status = StatusConsulta.EMERGENCIAL;
            System.out.println("Consulta emergencial encaixada para o paciente de ID : " + idPaciente);
        } else {
            horario.ocupado();
            this.status = StatusConsulta.CONFIRMADA;
        }
        return true;
    }

    public boolean cancelarConsulta(RepositorioHorario repositorio, String motivo) {
        Horario horario = repositorio.buscarPorID(idHorario);
        if (horario == null) {
            System.out.println("Horário não encontrado.");
            return false;
        }

        Cancelamento cancelamento = new Cancelamento(this.id, motivo, LocalDateTime.now());
        cancelamento.calcularMultaCancelamento(horario);
        this.dataDeCancelamento = LocalDate.now();
        this.status = StatusConsulta.CANCELADA;
        horario.liberar();
        System.out.println("Consulta Cancelada com sucesso, pelo motivo :" + motivo);

        return true;
    }
}
