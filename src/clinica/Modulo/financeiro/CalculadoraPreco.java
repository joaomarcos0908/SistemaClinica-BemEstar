package clinica.Modulo.financeiro;

public interface CalculadoraPreco {

    double calcularValor(double valorBase);

    String getTipo();

    double getDesconto();
}