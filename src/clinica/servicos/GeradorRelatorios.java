package clinica.servicos;

import clinica.Modulo.financeiro.*;
import clinica.Modulo.medico.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class GeradorRelatorios {
    private GerenciadorFinanceiro gerFinanceiro;
    private GerenciadorHistorico gerHistorico;
    private static final String DIRETORIO_RELATORIOS = "relatorios/";

    public GeradorRelatorios(GerenciadorFinanceiro gerFinanceiro,
                             GerenciadorHistorico gerHistorico) {
        this.gerFinanceiro = gerFinanceiro;
        this.gerHistorico = gerHistorico;
        criarDiretorio();
    }

    private void criarDiretorio() {
        File dir = new File(DIRETORIO_RELATORIOS);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public String gerarRelatorioFinanceiroGeral() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        sb.append("╔═══════════════════════════════════════════════════════════╗\n");
        sb.append("║          RELATÓRIO FINANCEIRO GERAL                       ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════╝\n\n");
        sb.append("Data: ").append(LocalDateTime.now().format(formatter)).append("\n\n");

        // Faturamento total
        double faturamentoTotal = gerFinanceiro.calcularFaturamentoTotal();
        sb.append("───────────────────────────────────────────────────────────\n");
        sb.append("FATURAMENTO TOTAL: R$ ").append(String.format("%.2f", faturamentoTotal)).append("\n");
        sb.append("───────────────────────────────────────────────────────────\n\n");

        // Faturamento por tipo de paciente
        sb.append("FATURAMENTO POR TIPO DE PACIENTE:\n");
        Map<String, Double> faturamentoPorTipo = gerFinanceiro.calcularFaturamentoPorTipo();
        for (Map.Entry<String, Double> entry : faturamentoPorTipo.entrySet()) {
            double percentual = (entry.getValue() / faturamentoTotal) * 100;
            sb.append(String.format("  • %s: R$ %.2f (%.1f%%)\n",
                    entry.getKey(), entry.getValue(), percentual));
        }
        sb.append("\n");

        sb.append("STATUS DOS PAGAMENTOS:\n");
        Map<Pagamento.StatusPagamento, Long> statusCount = gerFinanceiro.listarPagamentos()
                .stream()
                .collect(Collectors.groupingBy(Pagamento::getStatusPagamento, Collectors.counting()));

        for (Map.Entry<Pagamento.StatusPagamento, Long> entry : statusCount.entrySet()) {
            sb.append(String.format("  • %s: %d pagamentos\n",
                    entry.getKey(), entry.getValue()));
        }
        sb.append("\n");

        double totalTaxas = gerFinanceiro.calcularTotalTaxas();
        long numTaxas = gerFinanceiro.listarTaxas().stream()
                .filter(TaxaCancelamento::isCobrado)
                .count();
        sb.append("TAXAS DE CANCELAMENTO:\n");
        sb.append(String.format("  • Total cobrado: R$ %.2f\n", totalTaxas));
        sb.append(String.format("  • Número de taxas: %d\n", numTaxas));
        sb.append("\n");

        sb.append("═══════════════════════════════════════════════════════════\n");

        return sb.toString();
    }

    public String gerarRelatorioFinanceiroPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        sb.append("╔═══════════════════════════════════════════════════════════╗\n");
        sb.append("║          RELATÓRIO FINANCEIRO POR PERÍODO                 ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════╝\n\n");
        sb.append("Período: ").append(inicio.format(formatter))
                .append(" a ").append(fim.format(formatter)).append("\n\n");

        List<Pagamento> pagamentosPeriodo = gerFinanceiro.listarPagamentosPorPeriodo(inicio, fim);

        double totalPeriodo = pagamentosPeriodo.stream()
                .filter(p -> p.getStatusPagamento() == Pagamento.StatusPagamento.PAGO)
                .mapToDouble(Pagamento::getValorFinal)
                .sum();

        sb.append("Total do período: R$ ").append(String.format("%.2f", totalPeriodo)).append("\n");
        sb.append("Número de pagamentos: ").append(pagamentosPeriodo.size()).append("\n\n");

        sb.append("───────────────────────────────────────────────────────────\n");
        sb.append("DETALHAMENTO:\n");
        sb.append("───────────────────────────────────────────────────────────\n\n");

        for (Pagamento pag : pagamentosPeriodo) {
            sb.append(String.format("Data: %s\n",
                    pag.getDataPagamento().format(formatterHora)));
            sb.append(String.format("Tipo: %s | Valor: R$ %.2f | Status: %s\n",
                    pag.getTipoPaciente(), pag.getValorFinal(), pag.getStatusPagamento()));
            sb.append("---\n");
        }

        sb.append("\n═══════════════════════════════════════════════════════════\n");

        return sb.toString();
    }

    public String gerarRelatorioFaturamentoPorTipo() {
        StringBuilder sb = new StringBuilder();

        sb.append("╔═══════════════════════════════════════════════════════════╗\n");
        sb.append("║       RELATÓRIO DE FATURAMENTO POR TIPO DE PACIENTE      ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════╝\n\n");

        Map<String, Double> faturamento = gerFinanceiro.calcularFaturamentoPorTipo();
        double total = faturamento.values().stream().mapToDouble(Double::doubleValue).sum();

        sb.append("PARTICULAR (sem desconto):\n");
        double particular = faturamento.getOrDefault("PARTICULAR", 0.0);
        sb.append(String.format("  Faturamento: R$ %.2f\n", particular));
        sb.append(String.format("  Percentual: %.1f%%\n", (particular/total)*100));

        long qtdParticular = gerFinanceiro.listarPagamentosPorTipo("PARTICULAR").stream()
                .filter(p -> p.getStatusPagamento() == Pagamento.StatusPagamento.PAGO)
                .count();
        sb.append(String.format("  Quantidade: %d pagamentos\n\n", qtdParticular));

        sb.append("CONVÊNIO (20% desconto):\n");
        double convenio = faturamento.getOrDefault("CONVENIO", 0.0);
        sb.append(String.format("  Faturamento: R$ %.2f\n", convenio));
        sb.append(String.format("  Percentual: %.1f%%\n", (convenio/total)*100));

        long qtdConvenio = gerFinanceiro.listarPagamentosPorTipo("CONVENIO").stream()
                .filter(p -> p.getStatusPagamento() == Pagamento.StatusPagamento.PAGO)
                .count();
        sb.append(String.format("  Quantidade: %d pagamentos\n\n", qtdConvenio));

        sb.append("VIP (30% desconto):\n");
        double vip = faturamento.getOrDefault("VIP", 0.0);
        sb.append(String.format("  Faturamento: R$ %.2f\n", vip));
        sb.append(String.format("  Percentual: %.1f%%\n", (vip/total)*100));

        long qtdVip = gerFinanceiro.listarPagamentosPorTipo("VIP").stream()
                .filter(p -> p.getStatusPagamento() == Pagamento.StatusPagamento.PAGO)
                .count();
        sb.append(String.format("  Quantidade: %d pagamentos\n\n", qtdVip));

        sb.append("───────────────────────────────────────────────────────────\n");
        sb.append(String.format("TOTAL GERAL: R$ %.2f\n", total));
        sb.append("═══════════════════════════════════════════════════════════\n");

        return sb.toString();
    }

    public String gerarRelatorioConsultasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        sb.append("╔═══════════════════════════════════════════════════════════╗\n");
        sb.append("║          RELATÓRIO DE CONSULTAS POR PERÍODO               ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════╝\n\n");
        sb.append("Período: ").append(inicio.format(formatter))
                .append(" a ").append(fim.format(formatter)).append("\n\n");

        List<HistoricoConsulta> consultas = gerHistorico.listarHistoricosPorPeriodo(inicio, fim);

        sb.append("Total de consultas: ").append(consultas.size()).append("\n\n");

        Map<String, Long> porEspecialidade = consultas.stream()
                .collect(Collectors.groupingBy(HistoricoConsulta::getEspecialidade, Collectors.counting()));

        sb.append("POR ESPECIALIDADE:\n");
        for (Map.Entry<String, Long> entry : porEspecialidade.entrySet()) {
            sb.append(String.format("  • %s: %d consultas\n", entry.getKey(), entry.getValue()));
        }
        sb.append("\n");

        sb.append("═══════════════════════════════════════════════════════════\n");

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

    public String gerarRelatorioCompleto() {
        StringBuilder sb = new StringBuilder();

        sb.append("╔═══════════════════════════════════════════════════════════╗\n");
        sb.append("║          RELATÓRIO COMPLETO DO SISTEMA                    ║\n");
        sb.append("║              Clínica Bem Estar                            ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════╝\n\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        sb.append("Gerado em: ").append(LocalDateTime.now().format(formatter)).append("\n\n");

        sb.append(gerarRelatorioFinanceiroGeral()).append("\n\n");
        sb.append(gerarRelatorioFaturamentoPorTipo()).append("\n\n");

        return sb.toString();
    }
}