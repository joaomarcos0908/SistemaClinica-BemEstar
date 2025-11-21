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

    public GerenciadorFinanceiro() {
        this.pagamentos = new ArrayList<>();
        this.taxas = new ArrayList<>();
        criarDiretorio();
        carregarDados();
    }

    private void criarDiretorio() {
        File dir = new File("dados");
        if (!dir.exists()) {
            dir.mkdir();
        }
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
            if (sucesso) {
                salvarPagamentos();
            }
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
            salvarPagamentos();
            salvarTaxas();
            return cobranca;
        }
        return null;
    }

    public Pagamento buscarPagamentoPorId(String id) {
        return pagamentos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Pagamento buscarPagamentoPorConsulta(String consultaId) {
        return pagamentos.stream()
                .filter(p -> p.getConsultaId().equals(consultaId))
                .findFirst()
                .orElse(null);
    }

    public TaxaCancelamento buscarTaxaPorId(String id) {
        return taxas.stream()
                .filter(t -> t.getId().equals(id))
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
        return pagamentos.stream()
                .filter(p -> p.getTipoPaciente().equalsIgnoreCase(tipo))
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
                String tipo = pag.getTipoPaciente();
                faturamento.put(tipo,
                        faturamento.getOrDefault(tipo, 0.0) + pag.getValorFinal());
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

    private void salvarPagamentos() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_PAGAMENTOS))) {
            writer.println("id,consultaId,valorBase,valorFinal,tipoPaciente,statusPagamento,formaPagamento,dataPagamento");
            for (Pagamento pag : pagamentos) {
                writer.println(pag.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar pagamentos: " + e.getMessage());
        }
    }

    private void salvarTaxas() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_TAXAS))) {
            writer.println("id,consultaId,valorOriginal,valorTaxa,dataGeracao,cobrado");
            for (TaxaCancelamento taxa : taxas) {
                writer.println(taxa.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar taxas: " + e.getMessage());
        }
    }

    private void carregarDados() {
        carregarPagamentos();
        carregarTaxas();
    }

    private void carregarPagamentos() {
        File arquivo = new File(ARQUIVO_PAGAMENTOS);
        if (!arquivo.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha = reader.readLine(); // Pula cabeçalho

            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length >= 8) {
                    Pagamento pag = new Pagamento(
                            dados[0], dados[1],
                            Double.parseDouble(dados[2]),
                            Double.parseDouble(dados[3]),
                            dados[4], dados[5], dados[6], dados[7]
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
            String linha = reader.readLine(); // Pula cabeçalho

            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length >= 6) {
                    TaxaCancelamento taxa = new TaxaCancelamento(
                            dados[0], dados[1],
                            Double.parseDouble(dados[2]),
                            Double.parseDouble(dados[3]),
                            dados[4],
                            Boolean.parseBoolean(dados[5])
                    );
                    taxas.add(taxa);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar taxas: " + e.getMessage());
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

            // Backup pagamentos
            File pagOriginal = new File(ARQUIVO_PAGAMENTOS);
            if (pagOriginal.exists()) {
                File pagBackup = new File("dados/backup/pagamentos_" + timestamp + ".csv");
                copiarArquivo(pagOriginal, pagBackup);
            }

            // Backup taxas
            File taxaOriginal = new File(ARQUIVO_TAXAS);
            if (taxaOriginal.exists()) {
                File taxaBackup = new File("dados/backup/taxas_" + timestamp + ".csv");
                copiarArquivo(taxaOriginal, taxaBackup);
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