package clinica.pessoas;

public enum Especialidade {

    CARDIOLOGIA("Cardiologia"),
    DERMATOLOGIA("Dermatologia"),
    PEDIATRIA("Pediatria"),
    ORTOPEDIA("Ortopedia"),
    NEUROLOGIA("Neurologia"),
    OFTALMOLOGIA("Oftalmologia"),
    CLINICA_GERAL("Clínica Geral"),
    NENHUMA("Não possui");

    private final String nome;

    Especialidade(String nome) {
        this.nome = nome;
    }

    public String getNome(){
        return nome;
    }
}