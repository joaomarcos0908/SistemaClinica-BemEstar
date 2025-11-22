package clinica.Modulo.financeiro;

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

    public enum StatusPagamento { PENDENTE, PAGO, CANCELADO, REEMBOLSADO }
    public enum FormaPagamento { DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO, PIX, CONVENIO }

    public Pagamento(String consultaId, double valorBase, String tipoPaciente) {
        this.id = gerarId();
        this.consultaId = consultaId != null ? consultaId : "";
        this.valorBase = valorBase;
        this.tipoPaciente = tipoPaciente != null ? tipoPaciente : "PARTICULAR";
        this.statusPagamento = StatusPagamento.PENDENTE;
        this.calculadora = obterCalculadora(this.tipoPaciente);
        this.valorFinal = calcularValorConsulta();
    }

    public Pagamento(String id, String consultaId, double valorBase, double valorFinal,
                     String tipoPaciente, String statusPagamento, String formaPagamento,
                     String dataPagamento) {
        this.id = id != null ? id : gerarId();
        this.consultaId = consultaId != null ? consultaId : "";
        this.valorBase = valorBase;
        this.tipoPaciente = tipoPaciente != null && !tipoPaciente.isEmpty() ? tipoPaciente : "PARTICULAR";
        this.calculadora = obterCalculadora(this.tipoPaciente);
        this.valorFinal = valorFinal > 0 ? valorFinal : calcularValorConsulta();

        try {
            this.statusPagamento = statusPagamento != null && !statusPagamento.isEmpty()
                    ? StatusPagamento.valueOf(statusPagamento) : StatusPagamento.PENDENTE;
        } catch (Exception e) {
            this.statusPagamento = StatusPagamento.PENDENTE;
        }

        try {
            this.formaPagamento = formaPagamento != null && !formaPagamento.isEmpty()
                    && !formaPagamento.equals("null") ? FormaPagamento.valueOf(formaPagamento) : null;
        } catch (Exception e) {
            this.formaPagamento = null;
        }

        try {
            this.dataPagamento = dataPagamento != null && !dataPagamento.isEmpty() && !dataPagamento.equals("null")
                    ? LocalDateTime.parse(dataPagamento, DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
        } catch (Exception e) {
            this.dataPagamento = null;
        }
    }

    private String gerarId() { return "PAG" + System.currentTimeMillis(); }

    private CalculadoraPreco obterCalculadora(String tipo) {
        if (tipo == null) return new PrecoParticular();
        switch (tipo.toUpperCase()) {
            case "CONVENIO": return new PrecoConvenio();
            case "VIP": return new PrecoVIP();
            default: return new PrecoParticular();
        }
    }

    public double calcularValorConsulta() { return calculadora.calcularValor(valorBase); }

    public boolean registrarPagamento(FormaPagamento forma) {
        if (this.statusPagamento == StatusPagamento.PAGO) return false;
        this.formaPagamento = forma;
        this.dataPagamento = LocalDateTime.now();
        this.statusPagamento = StatusPagamento.PAGO;
        return true;
    }

    public void cancelarPagamento() { this.statusPagamento = StatusPagamento.CANCELADO; }

    public void reembolsar() {
        if (this.statusPagamento == StatusPagamento.PAGO) this.statusPagamento = StatusPagamento.REEMBOLSADO;
    }

    public String getId() { return id; }
    public String getConsultaId() { return consultaId; }
    public double getValorBase() { return valorBase; }
    public double getValorFinal() { return valorFinal; }
    public String getTipoPaciente() { return tipoPaciente; }
    public StatusPagamento getStatusPagamento() { return statusPagamento; }
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public CalculadoraPreco getCalculadora() { return calculadora; }

    public String toCSV() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return String.format("%s,%s,%.2f,%.2f,%s,%s,%s,%s",
                id,
                consultaId,
                valorBase,
                valorFinal,
                tipoPaciente,
                statusPagamento != null ? statusPagamento.name() : "",
                formaPagamento != null ? formaPagamento.name() : "",
                dataPagamento != null ? dataPagamento.format(formatter) : ""
        );
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format(
                "Pagamento ID: %s\nclinica.sistema.Consulta: %s\nValor Base: R$ %.2f\nValor Final: R$ %.2f\nTipo: %s (%.0f%% desconto)\nStatus: %s\nForma: %s\nData: %s",
                id, consultaId, valorBase, valorFinal,
                tipoPaciente, calculadora.getDesconto(),
                statusPagamento,
                formaPagamento != null ? formaPagamento : "N/A",
                dataPagamento != null ? dataPagamento.format(formatter) : "N/A"
        );
    }
}
