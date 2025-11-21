package Modulo.financeiro;

public class PrecoVIP implements CalculadoraPreco {

    private static final double DESCONTO = 0.30;

    @Override
    public double calcularValor(double valorBase) {
        return valorBase * (1 - DESCONTO);
    }

    @Override
    public String getTipo() {
        return "VIP";
    }

    @Override
    public double getDesconto() {
        return DESCONTO * 100; // Retorna em percentual
    }

    @Override
    public String toString() {
        return "VIP (30% de desconto)";
    }
}