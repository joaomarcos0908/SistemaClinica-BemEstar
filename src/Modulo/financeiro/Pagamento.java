package Modulo.financeiro;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Pagamento {
    private String id;
    private String consultaId;
    private double valorBase;
    private double valorFinal;
    private String tipoPaciente;
    private StatusPagamento statusPagamento;
    private FormaPagamento formaPagamento;
    private LocalDateTime dataPagamento;
    private CalculadoraPreco calculadora;

    public enum StatusPagamento {
        PENDENTE, PAGO, CANCELADO, REEMBOLSADO
    }

    public enum FormaPagamento {
        DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO, PIX, CONVENIO
    }

    public Pagamento(String consultaId, double valorBase, String tipoPaciente) {
        this.id = gerarId();
        this.consultaId = consultaId;
        this.valorBase = valorBase;
        this.tipoPaciente = tipoPaciente;
        this.statusPagamento = StatusPagamento.PENDENTE;
        this.calculadora = obterCalculadora(tipoPaciente);
        this.valorFinal = calcularValorConsulta();
    }

    public Pagamento(String id, String consultaId, double valorBase, double valorFinal,
                     String tipoPaciente, String statusPagamento, String formaPagamento,
                     String dataPagamento) {
        this.id = id;
        this.consultaId = consultaId;
        this.valorBase = valorBase;
        this.valorFinal = valorFinal;
        this.tipoPaciente = tipoPaciente;
        this.statusPagamento = StatusPagamento.valueOf(statusPagamento);
        this.formaPagamento = formaPagamento != null && !formaPagamento.equals("null")
                ? FormaPagamento.valueOf(formaPagamento) : null;
        this.dataPagamento = dataPagamento != null && !dataPagamento.equals("null")
                ? LocalDateTime.parse(dataPagamento) : null;
        this.calculadora = obterCalculadora(tipoPaciente);
    }

    private String gerarId() {
        return "PAG" + System.currentTimeMillis();
    }

    private CalculadoraPreco obterCalculadora(String tipo) {
        switch (tipo.toUpperCase()) {
            case "CONVENIO":
                return new PrecoConvenio();
            case "VIP":
                return new PrecoVIP();
            default:
                return new PrecoParticular();
        }
    }

    public double calcularValorConsulta() {
        return calculadora.calcularValor(valorBase);
    }

    public boolean registrarPagamento(FormaPagamento forma) {
        if (this.statusPagamento == StatusPagamento.PAGO) {
            return false;
        }

        this.formaPagamento = forma;
        this.dataPagamento = LocalDateTime.now();
        this.statusPagamento = StatusPagamento.PAGO;
        return true;
    }

    public void cancelarPagamento() {
        this.statusPagamento = StatusPagamento.CANCELADO;
    }

    public void reembolsar() {
        if (this.statusPagamento == StatusPagamento.PAGO) {
            this.statusPagamento = StatusPagamento.REEMBOLSADO;
        }
    }

    public String getId() {
        return id;
    }

    public String getConsultaId() {
        return consultaId;
    }

    public double getValorBase() {
        return valorBase;
    }

    public double getValorFinal() {
        return valorFinal;
    }

    public String getTipoPaciente() {
        return tipoPaciente;
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public CalculadoraPreco getCalculadora() {
        return calculadora;
    }

    public String toCSV() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return String.format("%s,%s,%.2f,%.2f,%s,%s,%s,%s",
                id,
                consultaId,
                valorBase,
                valorFinal,
                tipoPaciente,
                statusPagamento,
                formaPagamento != null ? formaPagamento : "null",
                dataPagamento != null ? dataPagamento.format(formatter) : "null"
        );
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format(
                "Pagamento ID: %s\n" +
                        "Consulta: %s\n" +
                        "Valor Base: R$ %.2f\n" +
                        "Valor Final: R$ %.2f\n" +
                        "Tipo: %s (%.0f%% desconto)\n" +
                        "Status: %s\n" +
                        "Forma: %s\n" +
                        "Data: %s",
                id, consultaId, valorBase, valorFinal,
                tipoPaciente, calculadora.getDesconto(),
                statusPagamento,
                formaPagamento != null ? formaPagamento : "N/A",
                dataPagamento != null ? dataPagamento.format(formatter) : "N/A"
        );
    }
}