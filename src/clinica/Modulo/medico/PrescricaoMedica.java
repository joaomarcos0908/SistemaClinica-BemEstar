package clinica.Modulo.medico;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PrescricaoMedica {
    private String id;
    private String consultaId;
    private String pacienteId;
    private String medicoId;
    private List<String> medicamentos;
    private String posologia;
    private String orientacoes;
    private LocalDate validade;
    private LocalDateTime dataEmissao;

    public PrescricaoMedica(String consultaId, String pacienteId, String medicoId) {
        this.id = gerarId();
        this.consultaId = consultaId;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.medicamentos = new ArrayList<>();
        this.posologia = "";
        this.orientacoes = "";
        this.dataEmissao = LocalDateTime.now();
        this.validade = LocalDate.now().plusMonths(6); // Validade padrão de 6 meses
    }

    public PrescricaoMedica(String id, String consultaId, String pacienteId, String medicoId,
                            String medicamentos, String posologia, String orientacoes,
                            String validade, String dataEmissao) {
        this.id = id;
        this.consultaId = consultaId;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.medicamentos = new ArrayList<>();
        if (medicamentos != null && !medicamentos.isEmpty()) {
            String[] meds = medicamentos.split(";");
            for (String med : meds) {
                this.medicamentos.add(med.trim());
            }
        }
        this.posologia = posologia;
        this.orientacoes = orientacoes;
        this.validade = LocalDate.parse(validade);
        this.dataEmissao = LocalDateTime.parse(dataEmissao);
    }

    private String gerarId() {
        return "PRESC" + System.currentTimeMillis();
    }

    public void adicionarMedicamento(String medicamento) {
        this.medicamentos.add(medicamento);
    }

    public boolean removerMedicamento(String medicamento) {
        return this.medicamentos.remove(medicamento);
    }

    public void definirPosologia(String posologia) {
        this.posologia = posologia;
    }

    public void adicionarOrientacoes(String orientacoes) {
        this.orientacoes = orientacoes;
    }

    public void definirValidade(LocalDate validade) {
        this.validade = validade;
    }

    public boolean isValida() {
        return LocalDate.now().isBefore(validade) || LocalDate.now().isEqual(validade);
    }

    public String gerarPrescricaoTexto(String nomePaciente, String nomeMedico, String crm) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════════════════════════════════════════════╗\n");
        sb.append("║           PRESCRIÇÃO MÉDICA ELETRÔNICA                    ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════╝\n\n");

        sb.append("Prescrição Nº: ").append(id).append("\n");
        sb.append("Data de Emissão: ").append(dataEmissao.format(dateTimeFormatter)).append("\n");
        sb.append("Válida até: ").append(validade.format(dateFormatter)).append("\n\n");

        sb.append("─────────────────────────────────────────────────────────────\n");
        sb.append("PACIENTE: ").append(nomePaciente).append("\n");
        sb.append("MÉDICO: Dr(a). ").append(nomeMedico).append("\n");
        sb.append("CRM: ").append(crm).append("\n");
        sb.append("─────────────────────────────────────────────────────────────\n\n");

        sb.append("MEDICAMENTOS PRESCRITOS:\n");
        if (medicamentos.isEmpty()) {
            sb.append("  • Nenhum medicamento prescrito\n");
        } else {
            for (int i = 0; i < medicamentos.size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(medicamentos.get(i)).append("\n");
            }
        }
        sb.append("\n");

        if (!posologia.isEmpty()) {
            sb.append("POSOLOGIA:\n");
            sb.append("  ").append(posologia).append("\n\n");
        }

        if (!orientacoes.isEmpty()) {
            sb.append("ORIENTAÇÕES:\n");
            sb.append("  ").append(orientacoes).append("\n\n");
        }

        sb.append("─────────────────────────────────────────────────────────────\n");
        sb.append("Status: ").append(isValida() ? "VÁLIDA" : "VENCIDA").append("\n");
        sb.append("─────────────────────────────────────────────────────────────\n");

        return sb.toString();
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

    public List<String> getMedicamentos() {
        return new ArrayList<>(medicamentos);
    }

    public String getPosologia() {
        return posologia;
    }

    public void setPosologia(String posologia) {
        this.posologia = posologia;
    }

    public String getOrientacoes() {
        return orientacoes;
    }

    public void setOrientacoes(String orientacoes) {
        this.orientacoes = orientacoes;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public LocalDateTime getDataEmissao() {
        return dataEmissao;
    }

    public String toCSV() {
        String medsJoined = String.join(";", medicamentos);

        String posoEscaped = posologia.replace(",", " -").replace("\n", " ");
        String orientEscaped = orientacoes.replace(",", " -").replace("\n", " ");

        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                id, consultaId, pacienteId, medicoId,
                medsJoined, posoEscaped, orientEscaped,
                validade.toString(), dataEmissao.toString()
        );
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format(
                "Prescrição %s\n" +
                        "Emitida em: %s\n" +
                        "Válida até: %s\n" +
                        "Medicamentos: %d\n" +
                        "Status: %s",
                id,
                dataEmissao.format(formatter),
                validade.format(formatter),
                medicamentos.size(),
                isValida() ? "Válida" : "Vencida"
        );
    }
}