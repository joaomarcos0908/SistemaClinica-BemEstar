package clinica.Modulo.financeiro;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TaxaCancelamento {
    private static final double PERCENTUAL_TAXA = 0.50; // 50% do valor
    private static final int HORAS_MINIMAS_CANCELAMENTO = 24;

    private String id;
    private String consultaId;
    private double valorOriginal;
    private double valorTaxa;
    private LocalDateTime dataGeracao;
    private boolean cobrado;

    public TaxaCancelamento(String consultaId, double valorOriginal) {
        this.id = gerarId();
        this.consultaId = consultaId;
        this.valorOriginal = valorOriginal;
        this.valorTaxa = calcularTaxa();
        this.dataGeracao = LocalDateTime.now();
        this.cobrado = false;
    }


    public TaxaCancelamento(String id, String consultaId, double valorOriginal,
                            double valorTaxa, String dataGeracao, boolean cobrado) {
        this.id = id;
        this.consultaId = consultaId;
        this.valorOriginal = valorOriginal;
        this.valorTaxa = valorTaxa;
        this.dataGeracao = LocalDateTime.parse(dataGeracao);
        this.cobrado = cobrado;
    }

    private String gerarId() {
        return "TAXA" + System.currentTimeMillis();
    }

    public static boolean deveAplicarTaxa(LocalDateTime dataConsulta, LocalDateTime dataCancelamento) {
        if (dataConsulta == null || dataCancelamento == null) {
            return false;
        }

        long horasAteConsulta = ChronoUnit.HOURS.between(dataCancelamento, dataConsulta);
        return horasAteConsulta < HORAS_MINIMAS_CANCELAMENTO;
    }

    private double calcularTaxa() {
        return valorOriginal * PERCENTUAL_TAXA;
    }

    public Pagamento gerarCobranca(String tipoPaciente) {
        Pagamento cobranca = new Pagamento(consultaId, valorTaxa, tipoPaciente);
        this.cobrado = true;
        return cobranca;
    }

    public void marcarComoCobrado() {
        this.cobrado = true;
    }

    public String getId() {
        return id;
    }

    public String getConsultaId() {
        return consultaId;
    }

    public double getValorOriginal() {
        return valorOriginal;
    }

    public double getValorTaxa() {
        return valorTaxa;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public boolean isCobrado() {
        return cobrado;
    }

    public static double getPercentualTaxa() {
        return PERCENTUAL_TAXA * 100;
    }

    public String toCSV() {
        return String.format("%s,%s,%.2f,%.2f,%s,%b",
                id, consultaId, valorOriginal, valorTaxa,
                dataGeracao.toString(), cobrado
        );
    }

    @Override
    public String toString() {
        return String.format(
                "Taxa de clinica.sistema.Cancelamento\n" +
                        "ID: %s\n" +
                        "clinica.sistema.Consulta: %s\n" +
                        "Valor Original: R$ %.2f\n" +
                        "Valor da Taxa (50%%): R$ %.2f\n" +
                        "Status: %s",
                id, consultaId, valorOriginal, valorTaxa,
                cobrado ? "Cobrado" : "Pendente"
        );
    }
}