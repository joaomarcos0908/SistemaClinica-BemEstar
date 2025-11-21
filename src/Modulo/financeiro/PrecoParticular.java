package Modulo.financeiro;


public class PrecoParticular implements CalculadoraPreco {

    @Override
    public double calcularValor(double valorBase) {
        return valorBase;
    }

    @Override
    public String getTipo() {
        return "PARTICULAR";
    }

    @Override
    public double getDesconto() {
        return 0.0;
    }

    @Override
    public String toString() {
        return "Particular (sem desconto)";
    }
}