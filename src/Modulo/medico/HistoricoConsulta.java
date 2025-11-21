package Modulo.medico;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistoricoConsulta {
    private String id;
    private String consultaId;
    private String pacienteId;
    private String medicoId;
    private LocalDateTime dataConsulta;
    private String diagnostico;
    private String observacoes;
    private String sintomas;
    private String especialidade;

    public HistoricoConsulta(String consultaId, String pacienteId, String medicoId,
                             LocalDateTime dataConsulta, String especialidade) {
        this.id = gerarId();
        this.consultaId = consultaId;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.dataConsulta = dataConsulta;
        this.especialidade = especialidade;
        this.diagnostico = "";
        this.observacoes = "";
        this.sintomas = "";
    }

    public HistoricoConsulta(String id, String consultaId, String pacienteId,
                             String medicoId, String dataConsulta, String diagnostico,
                             String observacoes, String sintomas, String especialidade) {
        this.id = id;
        this.consultaId = consultaId;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.dataConsulta = LocalDateTime.parse(dataConsulta);
        this.diagnostico = diagnostico;
        this.observacoes = observacoes;
        this.sintomas = sintomas;
        this.especialidade = especialidade;
    }

    private String gerarId() {
        return "HIST" + System.currentTimeMillis();
    }

    public void adicionarDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public void adicionarObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public void adicionarSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getId() {
        return id;
    }

    public String getConsultaId() {
        return consultaId;
    }

    public String getPacienteId() {
        return pacienteId;
    }

    public String getMedicoId() {
        return medicoId;
    }

    public LocalDateTime getDataConsulta() {
        return dataConsulta;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public String toCSV() {
        String diagEscaped = diagnostico.replace(",", ";").replace("\n", " ");
        String obsEscaped = observacoes.replace(",", ";").replace("\n", " ");
        String sintEscaped = sintomas.replace(",", ";").replace("\n", " ");

        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                id, consultaId, pacienteId, medicoId,
                dataConsulta.toString(),
                diagEscaped, obsEscaped, sintEscaped, especialidade
        );
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format(
                "=== HISTÓRICO DE CONSULTA ===\n" +
                        "ID: %s\n" +
                        "Data: %s\n" +
                        "Especialidade: %s\n" +
                        "Sintomas: %s\n" +
                        "Diagnóstico: %s\n" +
                        "Observações: %s\n" +
                        "=============================",
                id,
                dataConsulta.format(formatter),
                especialidade,
                sintomas.isEmpty() ? "Não informado" : sintomas,
                diagnostico.isEmpty() ? "Não informado" : diagnostico,
                observacoes.isEmpty() ? "Nenhuma" : observacoes
        );
    }
}