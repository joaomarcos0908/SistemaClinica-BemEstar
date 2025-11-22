package clinica.servicos;

import clinica.Modulo.financeiro.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class GerenciadorFinanceiro {
    private List<Pagamento> pagamentos;
    private List<TaxaCancelamento> taxas;
    private static final String ARQUIVO_PAGAMENTOS = "dados/pagamentos.csv";
    private static final String ARQUIVO_TAXAS = "dados/taxas.csv";
    private static final String DIRETORIO_RELATORIOS = "relatorios/";

    public GerenciadorFinanceiro() {
        this.pagamentos = new ArrayList<>();
        this.taxas = new ArrayList<>();
        criarDiretorio();
        carregarDados();
    }

    private void criarDiretorio() {
        File dir = new File("dados");
        if (!dir.exists()) dir.mkdir();
        File dirRel = new File(DIRETORIO_RELATORIOS);
        if (!dirRel.exists()) dirRel.mkdirs();
    }

    public Pagamento criarPagamento(String consultaId, double valorBase, String tipoPaciente) {
        Pagamento pagamento = new Pagamento(consultaId, valorBase, tipoPaciente);
        pagamentos.add(pagamento);
        salvarPagamentos();
        return pagamento;
    }

    public boolean registrarPagamento(String pagamentoId, Pagamento.FormaPagamento forma) {
        Pagamento pagamento = buscarPagamentoPorId(pagamentoId);
        if (pagamento != null) {
            boolean sucesso = pagamento.registrarPagamento(forma);
            if (sucesso) salvarPagamentos();
            return sucesso;
        }
        return false;
    }

    public TaxaCancelamento criarTaxaCancelamento(String consultaId, double valorOriginal,
                                                  LocalDateTime dataConsulta,
                                                  LocalDateTime dataCancelamento) {
        if (TaxaCancelamento.deveAplicarTaxa(dataConsulta, dataCancelamento)) {
            TaxaCancelamento taxa = new TaxaCancelamento(consultaId, valorOriginal);
            taxas.add(taxa);
            salvarTaxas();
            return taxa;
        }
        return null;
    }

    public Pagamento gerarCobrancaTaxa(String taxaId, String tipoPaciente) {
        TaxaCancelamento taxa = buscarTaxaPorId(taxaId);
        if (taxa != null && !taxa.isCobrado()) {
            Pagamento cobranca = taxa.gerarCobranca(tipoPaciente);
            pagamentos.add(cobranca);
            taxa.marcarComoCobrado();
            salvarPagamentos();
            salvarTaxas();
            return cobranca;
        }
        return null;
    }

    public Pagamento buscarPagamentoPorId(String id) {
        if (id == null) return null;
        return pagamentos.stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst()
                .orElse(null);
    }

    public Pagamento buscarPagamentoPorConsulta(String consultaId) {
        if (consultaId == null) return null;
        return pagamentos.stream()
                .filter(p -> consultaId.equals(p.getConsultaId()))
                .findFirst()
                .orElse(null);
    }

    public TaxaCancelamento buscarTaxaPorId(String id) {
        if (id == null) return null;
        return taxas.stream()
                .filter(t -> id.equals(t.getId()))
                .findFirst()
                .orElse(null);
    }

    public List<Pagamento> listarPagamentos() {
        return new ArrayList<>(pagamentos);
    }

    public List<Pagamento> listarPagamentosPorStatus(Pagamento.StatusPagamento status) {
        return pagamentos.stream()
                .filter(p -> p.getStatusPagamento() == status)
                .collect(Collectors.toList());
    }

    public List<Pagamento> listarPagamentosPorTipo(String tipo) {
        if (tipo == null) return new ArrayList<>();
        return pagamentos.stream()
                .filter(p -> tipo.equalsIgnoreCase(p.getTipoPaciente()))
                .collect(Collectors.toList());
    }

    public List<Pagamento> listarPagamentosPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return pagamentos.stream()
                .filter(p -> p.getDataPagamento() != null)
                .filter(p -> !p.getDataPagamento().isBefore(inicio) &&
                        !p.getDataPagamento().isAfter(fim))
                .collect(Collectors.toList());
    }

    public Map<String, Double> calcularFaturamentoPorTipo() {
        Map<String, Double> faturamento = new HashMap<>();
        for (Pagamento pag : pagamentos) {
            if (pag.getStatusPagamento() == Pagamento.StatusPagamento.PAGO) {
                String tipo = pag.getTipoPaciente() != null ? pag.getTipoPaciente() : "PARTICULAR";
                faturamento.put(tipo, faturamento.getOrDefault(tipo, 0.0) + pag.getValorFinal());
            }
        }
        return faturamento;
    }

    public double calcularFaturamentoTotal() {
        return pagamentos.stream()
                .filter(p -> p.getStatusPagamento() == Pagamento.StatusPagamento.PAGO)
                .mapToDouble(Pagamento::getValorFinal)
                .sum();
    }

    public double calcularTotalTaxas() {
        return taxas.stream()
                .filter(TaxaCancelamento::isCobrado)
                .mapToDouble(TaxaCancelamento::getValorTaxa)
                .sum();
    }

    public List<TaxaCancelamento> listarTaxas() {
        return new ArrayList<>(taxas);
    }

    public String gerarRelatorioFinanceiroGeral() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        sb.append("╔═══════════════════════════════════════════════════════════╗\n");
        sb.append("║          RELATÓRIO FINANCEIRO GERAL                       ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════╝\n\n");
        sb.append("Data: ").append(LocalDateTime.now().format(formatter)).append("\n\n");
        double faturamentoTotal = calcularFaturamentoTotal();
        sb.append("───────────────────────────────────────────────────────────\n");
        sb.append("FATURAMENTO TOTAL: R$ ").append(String.format("%.2f", faturamentoTotal)).append("\n");
        sb.append("───────────────────────────────────────────────────────────\n\n");
        sb.append("FATURAMENTO POR TIPO DE PACIENTE:\n");
        Map<String, Double> faturamentoPorTipo = calcularFaturamentoPorTipo();
        for (Map.Entry<String, Double> entry : faturamentoPorTipo.entrySet()) {
            double percentual = faturamentoTotal > 0 ? (entry.getValue() / faturamentoTotal) * 100 : 0;
            sb.append(String.format("  • %s: R$ %.2f (%.1f%%)\n",
                    entry.getKey(), entry.getValue(), percentual));
        }
        sb.append("\n");
        sb.append("STATUS DOS PAGAMENTOS:\n");
        Map<Pagamento.StatusPagamento, Long> statusCount = pagamentos.stream()
                .collect(Collectors.groupingBy(Pagamento::getStatusPagamento, Collectors.counting()));
        for (Map.Entry<Pagamento.StatusPagamento, Long> entry : statusCount.entrySet()) {
            sb.append(String.format("  • %s: %d pagamentos\n",
                    entry.getKey(), entry.getValue()));
        }
        sb.append("\n");
        double totalTaxas = calcularTotalTaxas();
        long numTaxas = taxas.stream().filter(TaxaCancelamento::isCobrado).count();
        sb.append("TAXAS DE CANCELAMENTO:\n");
        sb.append(String.format("  • Total cobrado: R$ %.2f\n", totalTaxas));
        sb.append(String.format("  • Número de taxas: %d\n", numTaxas));
        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════════════\n");
        return sb.toString();
    }

    public String gerarRelatorioFaturamentoPorTipo() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════════════════════════════════════════════╗\n");
        sb.append("║       RELATÓRIO DE FATURAMENTO POR TIPO DE PACIENTE      ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════╝\n\n");
        Map<String, Double> faturamento = calcularFaturamentoPorTipo();
        double total = faturamento.values().stream().mapToDouble(Double::doubleValue).sum();
        double particular = faturamento.getOrDefault("PARTICULAR", 0.0);
        sb.append("PARTICULAR (sem desconto):\n");
        sb.append(String.format("  Faturamento: R$ %.2f\n", particular));
        if (total > 0) sb.append(String.format("  Percentual: %.1f%%\n", (particular/total)*100));
        long qtdParticular = listarPagamentosPorTipo("PARTICULAR").stream()
                .filter(p -> p.getStatusPagamento() == Pagamento.StatusPagamento.PAGO)
                .count();
        sb.append(String.format("  Quantidade: %d pagamentos\n\n", qtdParticular));
        double convenio = faturamento.getOrDefault("CONVENIO", 0.0);
        sb.append("CONVÊNIO (20% desconto):\n");
        sb.append(String.format("  Faturamento: R$ %.2f\n", convenio));
        if (total > 0) sb.append(String.format("  Percentual: %.1f%%\n", (convenio/total)*100));
        long qtdConvenio = listarPagamentosPorTipo("CONVENIO").stream()
                .filter(p -> p.getStatusPagamento() == Pagamento.StatusPagamento.PAGO)
                .count();
        sb.append(String.format("  Quantidade: %d pagamentos\n\n", qtdConvenio));
        double vip = faturamento.getOrDefault("VIP", 0.0);
        sb.append("VIP (30% desconto):\n");
        sb.append(String.format("  Faturamento: R$ %.2f\n", vip));
        if (total > 0) sb.append(String.format("  Percentual: %.1f%%\n", (vip/total)*100));
        long qtdVip = listarPagamentosPorTipo("VIP").stream()
                .filter(p -> p.getStatusPagamento() == Pagamento.StatusPagamento.PAGO)
                .count();
        sb.append(String.format("  Quantidade: %d pagamentos\n\n", qtdVip));
        sb.append("───────────────────────────────────────────────────────────\n");
        sb.append(String.format("TOTAL GERAL: R$ %.2f\n", total));
        sb.append("═══════════════════════════════════════════════════════════\n");
        return sb.toString();
    }

    public String gerarRelatorioCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append(gerarRelatorioFinanceiroGeral()).append("\n\n");
        sb.append(gerarRelatorioFaturamentoPorTipo()).append("\n\n");
        return sb.toString();
    }

    public boolean salvarRelatorio(String nomeArquivo, String conteudo) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = LocalDateTime.now().format(formatter);
            String nomeCompleto = DIRETORIO_RELATORIOS + nomeArquivo + "_" + timestamp + ".txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(nomeCompleto))) {
                writer.print(conteudo);
            }
            System.out.println("Relatório salvo em: " + nomeCompleto);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar relatório: " + e.getMessage());
            return false;
        }
    }

    public void salvarPagamentos() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_PAGAMENTOS))) {
            writer.println("id,consultaId,valorBase,valorFinal,tipoPaciente,statusPagamento,formaPagamento,dataPagamento");
            for (Pagamento pag : pagamentos) writer.println(pag.toCSV());
        } catch (IOException e) {
            System.err.println("Erro ao salvar pagamentos: " + e.getMessage());
        }
    }

    public void salvarTaxas() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_TAXAS))) {
            writer.println("id,consultaId,valorOriginal,valorTaxa,dataGeracao,cobrado");
            for (TaxaCancelamento taxa : taxas) writer.println(taxa.toCSV());
        } catch (IOException e) {
            System.err.println("Erro ao salvar taxas: " + e.getMessage());
        }
    }

    public void carregarDados() {
        carregarPagamentos();
        carregarTaxas();
    }

    private void carregarPagamentos() {
        File arquivo = new File(ARQUIVO_PAGAMENTOS);
        if (!arquivo.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha = reader.readLine();
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",", -1);
                if (dados.length >= 8) {
                    String id = safe(dados[0]);
                    String consultaId = safe(dados[1]);
                    double valorBase = parseDoubleSafe(dados[2]);
                    double valorFinal = parseDoubleSafe(dados[3]);
                    String tipo = safe(dados[4]);
                    String status = safe(dados[5]);
                    String forma = safe(dados[6]);
                    String data = safe(dados[7]);
                    Pagamento pag = new Pagamento(
                            id, consultaId, valorBase, valorFinal, tipo, status, forma, data
                    );
                    pagamentos.add(pag);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar pagamentos: " + e.getMessage());
        }
    }

    private void carregarTaxas() {
        File arquivo = new File(ARQUIVO_TAXAS);
        if (!arquivo.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha = reader.readLine();
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",", -1);
                if (dados.length >= 6) {
                    String id = safe(dados[0]);
                    String consultaId = safe(dados[1]);
                    double valorOriginal = parseDoubleSafe(dados[2]);
                    double valorTaxa = parseDoubleSafe(dados[3]);
                    String dataGeracao = safe(dados[4]);
                    boolean cobrado = Boolean.parseBoolean(safe(dados[5]));
                    TaxaCancelamento taxa = new TaxaCancelamento(id, consultaId, valorOriginal, valorTaxa, dataGeracao, cobrado);
                    taxas.add(taxa);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar taxas: " + e.getMessage());
        }
    }

    private String safe(String s) { return s == null || s.equals("null") ? "" : s.trim(); }
    private double parseDoubleSafe(String s) { try { return Double.parseDouble(s); } catch (Exception e) { return 0.0; } }

    public boolean realizarBackup() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = LocalDateTime.now().format(formatter);
            File backupDir = new File("dados/backup");
            if (!backupDir.exists()) backupDir.mkdirs();
            File pagOriginal = new File(ARQUIVO_PAGAMENTOS);
            if (pagOriginal.exists()) copiarArquivo(pagOriginal, new File("dados/backup/pagamentos_" + timestamp + ".csv"));
            File taxaOriginal = new File(ARQUIVO_TAXAS);
            if (taxaOriginal.exists()) copiarArquivo(taxaOriginal, new File("dados/backup/taxas_" + timestamp + ".csv"));
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
            while ((linha = reader.readLine()) != null) writer.println(linha);
        }
    }
}
