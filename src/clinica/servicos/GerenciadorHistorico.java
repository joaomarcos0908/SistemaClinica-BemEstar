package clinica.servicos;

import clinica.Modulo.medico.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class GerenciadorHistorico {
    private List<HistoricoConsulta> historicos;
    private List<PrescricaoMedica> prescricoes;
    private static final String ARQUIVO_HISTORICOS = "dados/historicos.csv";
    private static final String ARQUIVO_PRESCRICOES = "dados/prescricoes.csv";

    public GerenciadorHistorico() {
        this.historicos = new ArrayList<>();
        this.prescricoes = new ArrayList<>();
        criarDiretorio();
        carregarDados();
    }

    private void criarDiretorio() {
        File dir = new File("dados");
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public HistoricoConsulta criarHistorico(String consultaId, String pacienteId,
                                            String medicoId, LocalDateTime dataConsulta,
                                            String especialidade) {
        HistoricoConsulta historico = new HistoricoConsulta(
                consultaId, pacienteId, medicoId, dataConsulta, especialidade
        );
        historicos.add(historico);
        salvarHistoricos();
        return historico;
    }

    public boolean atualizarHistorico(String historicoId, String diagnostico,
                                      String sintomas, String observacoes) {
        HistoricoConsulta historico = buscarHistoricoPorId(historicoId);
        if (historico != null) {
            if (diagnostico != null) historico.setDiagnostico(diagnostico);
            if (sintomas != null) historico.setSintomas(sintomas);
            if (observacoes != null) historico.setObservacoes(observacoes);
            salvarHistoricos();
            return true;
        }
        return false;
    }

    public HistoricoConsulta buscarHistoricoPorId(String id) {
        return historicos.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public HistoricoConsulta buscarHistoricoPorConsulta(String consultaId) {
        return historicos.stream()
                .filter(h -> h.getConsultaId().equals(consultaId))
                .findFirst()
                .orElse(null);
    }

    public List<HistoricoConsulta> listarHistoricoPaciente(String pacienteId) {
        return historicos.stream()
                .filter(h -> h.getPacienteId().equals(pacienteId))
                .sorted((h1, h2) -> h2.getDataConsulta().compareTo(h1.getDataConsulta()))
                .collect(Collectors.toList());
    }

    public List<HistoricoConsulta> listarHistoricoMedico(String medicoId) {
        return historicos.stream()
                .filter(h -> h.getMedicoId().equals(medicoId))
                .sorted((h1, h2) -> h2.getDataConsulta().compareTo(h1.getDataConsulta()))
                .collect(Collectors.toList());
    }

    public List<HistoricoConsulta> listarHistoricosPorPeriodo(LocalDateTime inicio,
                                                              LocalDateTime fim) {
        return historicos.stream()
                .filter(h -> !h.getDataConsulta().isBefore(inicio) &&
                        !h.getDataConsulta().isAfter(fim))
                .sorted((h1, h2) -> h2.getDataConsulta().compareTo(h1.getDataConsulta()))
                .collect(Collectors.toList());
    }

    public PrescricaoMedica criarPrescricao(String consultaId, String pacienteId,
                                            String medicoId) {
        PrescricaoMedica prescricao = new PrescricaoMedica(consultaId, pacienteId, medicoId);
        prescricoes.add(prescricao);
        salvarPrescricoes();
        return prescricao;
    }

    public boolean adicionarMedicamentoPrescricao(String prescricaoId, String medicamento) {
        PrescricaoMedica prescricao = buscarPrescricaoPorId(prescricaoId);
        if (prescricao != null) {
            prescricao.adicionarMedicamento(medicamento);
            salvarPrescricoes();
            return true;
        }
        return false;
    }

    public boolean atualizarPrescricao(String prescricaoId, String posologia,
                                       String orientacoes, LocalDate validade) {
        PrescricaoMedica prescricao = buscarPrescricaoPorId(prescricaoId);
        if (prescricao != null) {
            if (posologia != null) prescricao.setPosologia(posologia);
            if (orientacoes != null) prescricao.setOrientacoes(orientacoes);
            if (validade != null) prescricao.definirValidade(validade);
            salvarPrescricoes();
            return true;
        }
        return false;
    }

    public PrescricaoMedica buscarPrescricaoPorId(String id) {
        return prescricoes.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public PrescricaoMedica buscarPrescricaoPorConsulta(String consultaId) {
        return prescricoes.stream()
                .filter(p -> p.getConsultaId().equals(consultaId))
                .findFirst()
                .orElse(null);
    }

    public List<PrescricaoMedica> listarPrescricoesPaciente(String pacienteId) {
        return prescricoes.stream()
                .filter(p -> p.getPacienteId().equals(pacienteId))
                .sorted((p1, p2) -> p2.getDataEmissao().compareTo(p1.getDataEmissao()))
                .collect(Collectors.toList());
    }

    public List<PrescricaoMedica> listarPrescricoesValidas(String pacienteId) {
        return prescricoes.stream()
                .filter(p -> p.getPacienteId().equals(pacienteId))
                .filter(PrescricaoMedica::isValida)
                .sorted((p1, p2) -> p2.getDataEmissao().compareTo(p1.getDataEmissao()))
                .collect(Collectors.toList());
    }

    public List<PrescricaoMedica> listarPrescricoes() {
        return new ArrayList<>(prescricoes);
    }

    public String gerarRelatorioCompletoPaciente(String pacienteId, String nomePaciente) {
        List<HistoricoConsulta> hists = listarHistoricoPaciente(pacienteId);
        List<PrescricaoMedica> prescs = listarPrescricoesPaciente(pacienteId);

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("    RELATÓRIO MÉDICO COMPLETO\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        sb.append("Paciente: ").append(nomePaciente).append("\n");
        sb.append("ID: ").append(pacienteId).append("\n");
        sb.append("Data do Relatório: ").append(LocalDate.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");

        sb.append("───────────────────────────────────────────────────────\n");
        sb.append("HISTÓRICO DE CONSULTAS (").append(hists.size()).append(" consultas)\n");
        sb.append("───────────────────────────────────────────────────────\n\n");

        if (hists.isEmpty()) {
            sb.append("Nenhuma consulta registrada.\n\n");
        } else {
            for (HistoricoConsulta hist : hists) {
                sb.append(hist.toString()).append("\n\n");
            }
        }

        sb.append("───────────────────────────────────────────────────────\n");
        sb.append("PRESCRIÇÕES MÉDICAS (").append(prescs.size()).append(" prescrições)\n");
        sb.append("───────────────────────────────────────────────────────\n\n");

        if (prescs.isEmpty()) {
            sb.append("Nenhuma prescrição registrada.\n\n");
        } else {
            for (PrescricaoMedica presc : prescs) {
                sb.append(presc.toString()).append("\n\n");
            }
        }

        sb.append("═══════════════════════════════════════════════════════\n");

        return sb.toString();
    }


    private void salvarHistoricos() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_HISTORICOS))) {
            writer.println("id,consultaId,pacienteId,medicoId,dataConsulta,diagnostico,observacoes,sintomas,especialidade");
            for (HistoricoConsulta hist : historicos) {
                writer.println(hist.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar históricos: " + e.getMessage());
        }
    }

    private void salvarPrescricoes() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_PRESCRICOES))) {
            writer.println("id,consultaId,pacienteId,medicoId,medicamentos,posologia,orientacoes,validade,dataEmissao");
            for (PrescricaoMedica presc : prescricoes) {
                writer.println(presc.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar prescrições: " + e.getMessage());
        }
    }

    private void carregarDados() {
        carregarHistoricos();
        carregarPrescricoes();
    }

    private void carregarHistoricos() {
        File arquivo = new File(ARQUIVO_HISTORICOS);
        if (!arquivo.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha = reader.readLine(); // Pula cabeçalho

            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",", 9);
                if (dados.length >= 9) {
                    HistoricoConsulta hist = new HistoricoConsulta(
                            dados[0], dados[1], dados[2], dados[3],
                            dados[4], dados[5], dados[6], dados[7], dados[8]
                    );
                    historicos.add(hist);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar históricos: " + e.getMessage());
        }
    }

    private void carregarPrescricoes() {
        File arquivo = new File(ARQUIVO_PRESCRICOES);
        if (!arquivo.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha = reader.readLine(); // Pula cabeçalho

            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",", 9);
                if (dados.length >= 9) {
                    PrescricaoMedica presc = new PrescricaoMedica(
                            dados[0], dados[1], dados[2], dados[3],
                            dados[4], dados[5], dados[6], dados[7], dados[8]
                    );
                    prescricoes.add(presc);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar prescrições: " + e.getMessage());
        }
    }

    public boolean realizarBackup() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = LocalDateTime.now().format(formatter);

            File backupDir = new File("dados/backup");
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            File histOriginal = new File(ARQUIVO_HISTORICOS);
            if (histOriginal.exists()) {
                File histBackup = new File("dados/backup/historicos_" + timestamp + ".csv");
                copiarArquivo(histOriginal, histBackup);
            }

            File prescOriginal = new File(ARQUIVO_PRESCRICOES);
            if (prescOriginal.exists()) {
                File prescBackup = new File("dados/backup/prescricoes_" + timestamp + ".csv");
                copiarArquivo(prescOriginal, prescBackup);
            }

            return true;
        } catch (Exception e) {
            System.err.println("Erro ao realizar backup: " + e.getMessage());
            return false;
        }
    }

    private void copiarArquivo(File origem, File destino) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(origem));
             PrintWriter writer = new PrintWriter(new FileWriter(destino))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                writer.println(linha);
            }
        }
    }
}