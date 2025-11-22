package clinica.sistema;

import clinica.repositorio.RepositorioHorario;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Consulta {
    private int id;
    private int idPaciente;
    private int idMedico;
    private int idEspecialidade;
    private int idHorario;
    private StatusConsulta status;
    private boolean emergencial;
    private LocalDate dataDeAgendamento;
    private LocalDate dataDeCancelamento;
    private String motivoCancelamento;

    public Consulta(LocalDate dataDeAgendamento, LocalDate dataDeCancelamento, int id,
                    int idEspecialidade, StatusConsulta status, boolean emergencial,
                    int idMedico, int idPaciente) {
        this.dataDeAgendamento = dataDeAgendamento;
        this.dataDeCancelamento = dataDeCancelamento;
        this.id = id;
        this.idEspecialidade = idEspecialidade;
        this.status = status != null ? status : StatusConsulta.AGENDADA;
        this.emergencial = emergencial;
        this.idMedico = idMedico;
        this.idPaciente = idPaciente;
        this.motivoCancelamento = null;
    }

    public Consulta(int id, int idPaciente, int idMedico, int idEspecialidade,
                    int idHorario, LocalDate dataDeAgendamento, LocalDate dataDeCancelamento,
                    StatusConsulta status, boolean emergencial, String motivoCancelamento) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.idEspecialidade = idEspecialidade;
        this.idHorario = idHorario;
        this.dataDeAgendamento = dataDeAgendamento;
        this.dataDeCancelamento = dataDeCancelamento;
        this.status = status != null ? status : StatusConsulta.AGENDADA;
        this.emergencial = emergencial;
        this.motivoCancelamento = motivoCancelamento;
    }

    public LocalDate getDataDeAgendamento() { return dataDeAgendamento; }
    public void setDataDeAgendamento(LocalDate dataDeAgendamento) { this.dataDeAgendamento = dataDeAgendamento; }
    public LocalDate getDataDeCancelamento() { return dataDeCancelamento; }
    public void setDataDeCancelamento(LocalDate dataDeCancelamento) { this.dataDeCancelamento = dataDeCancelamento; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdEspecialidade() { return idEspecialidade; }
    public void setIdEspecialidade(int idEspecialidade) { this.idEspecialidade = idEspecialidade; }
    public boolean isEmergencial() { return emergencial; }
    public void setEmergencial(boolean emergencial) { this.emergencial = emergencial; }
    public int getIdHorario() { return idHorario; }
    public void setIdHorario(int idHorario) { this.idHorario = idHorario; }
    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }
    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }
    public StatusConsulta getStatus() { return status; }
    public void setStatus(StatusConsulta status) { this.status = status; }
    public String getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(String motivoCancelamento) { this.motivoCancelamento = motivoCancelamento; }

    public boolean agendarConsulta(RepositorioHorario repositorio) {
        Horario horario = repositorio.buscarPorID(idHorario);
        if (horario == null) return false;
        if (!horario.isDisponivel() && !emergencial) return false;
        if (emergencial) {
            this.status = StatusConsulta.EMERGENCIAL;
        } else {
            horario.ocupado();
            this.status = StatusConsulta.CONFIRMADA;
        }
        return true;
    }

    public boolean cancelarConsulta(RepositorioHorario repositorio, String motivo) {
        Horario horario = repositorio.buscarPorID(idHorario);
        if (horario == null) return false;
        this.motivoCancelamento = motivo;
        Cancelamento cancelamento = new Cancelamento(this.id, motivo, LocalDateTime.now());
        cancelamento.calcularMultaCancelamento(horario);
        this.dataDeCancelamento = LocalDate.now();
        this.status = StatusConsulta.CANCELADA;
        horario.liberar();
        return true;
    }

    public String toCSV() {
        String motivoEscapado = motivoCancelamento != null ? motivoCancelamento.replace(",", ";") : "";
        return String.format("%d,%d,%d,%d,%d,%s,%s,%s,%b,%s",
                id,
                idPaciente,
                idMedico,
                idEspecialidade,
                idHorario,
                dataDeAgendamento != null ? dataDeAgendamento.toString() : "",
                dataDeCancelamento != null ? dataDeCancelamento.toString() : "",
                status != null ? status.name() : "",
                emergencial,
                motivoEscapado
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Consulta #").append(id).append("\n");
        sb.append("  Paciente: ").append(idPaciente).append("\n");
        sb.append("  Médico: ").append(idMedico).append("\n");
        sb.append("  Especialidade: ").append(idEspecialidade).append("\n");
        sb.append("  Horário: ").append(idHorario).append("\n");
        sb.append("  Data Agendamento: ").append(dataDeAgendamento).append("\n");
        sb.append("  Status: ").append(status).append("\n");
        sb.append("  Emergencial: ").append(emergencial ? "Sim" : "Não").append("\n");
        if (dataDeCancelamento != null) {
            sb.append("  Data Cancelamento: ").append(dataDeCancelamento).append("\n");
            sb.append("  Motivo: ").append(motivoCancelamento != null ? motivoCancelamento : "Não informado").append("\n");
        }
        return sb.toString();
    }
}
