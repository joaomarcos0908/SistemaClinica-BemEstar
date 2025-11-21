package Modulo.financeiro;

public class PrecoConvenio implements CalculadoraPreco {

    private static final double DESCONTO = 0.20;

    @Override
    public double calcularValor(double valorBase) {
        return valorBase * (1 - DESCONTO);
    }

    @Override
    public String getTipo() {
        return "CONVENIO";
    }

    @Override
    public double getDesconto() {
        return DESCONTO * 100; // Retorna em percentual
    }

    @Override
    public String toString() {
        return "Convênio (20% de desconto)";
    }
}