import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Paciente extends  Pessoa {
    private TipoPaciente tipoPaciente;
    private List<String> historicoIds;
    private TipoAtendimento tipoAtendimento;

    public Paciente(String nome, String cpf, LocalDate dataNascimento, String email, String numTelefone,
                    String endereco, boolean isGestante, boolean hasEspectroAutista, boolean isPcd, boolean isLactante,
                    boolean hasCriancaColo, TipoPaciente tipoPaciente, List<String> historicoIds, TipoAtendimento tipoAtendimento) {
        super(nome, cpf, dataNascimento, email, numTelefone, endereco, isGestante, hasEspectroAutista, isPcd,
                isLactante, hasCriancaColo);
        setTipoPaciente(tipoPaciente);
        setHistoricoIds(historicoIds);
        setTipoAtendimento(tipoAtendimento);
    }

    public TipoPaciente getTipoPaciente() {
        return tipoPaciente;
    }

    public void setTipoPaciente(TipoPaciente tipoPaciente) {
        if (tipoPaciente == tipoPaciente.PRIORIDADE && !isPrioridade()) {
            throw new IllegalArgumentException("Paciente não possui os requisitos para um atendimento prioritário.");
        }
        this.tipoPaciente = tipoPaciente;
    }
    public static TipoPaciente obterTipoPacientePorNumero(int opcao) {
        switch (opcao) {
            case 1:
                return TipoPaciente.EMERGENCIA;
            case 2:
                return TipoPaciente.PRIORIDADE;
            case 3:
                return TipoPaciente.ELETIVO;
            default:
                throw new IllegalArgumentException("Tipo de paciente inválido. Escolha 1, 2 ou 3.");
        }
    }

    public List<String> getHistoricoIds() {
        return new ArrayList<>(historicoIds);
    }

    public void setHistoricoIds(List<String> historicoIds) {
        if (historicoIds == null) {
            this.historicoIds = new ArrayList<>();
        } else {
            this.historicoIds = new ArrayList<>(historicoIds);
        }
    }

    public void adicionarHistoricoId(String id) {
        if (id != null && !id.trim().isEmpty()) {
            this.historicoIds.add(id);
        }
    }

    public void verHistoricoId() {
        if (this.historicoIds.isEmpty()) {
            System.out.println("Histórico vazio!");
        } else {
            for (String id : historicoIds) {
                System.out.println(id);
            }
        }
    }

    public TipoAtendimento getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(TipoAtendimento tipoAtendimento) {
        this.tipoAtendimento = tipoAtendimento;
    }
    public static TipoAtendimento obterTipoAtendimentoPorNumero(int opcao) {
        switch (opcao) {
            case 1:
                return TipoAtendimento.CONVENIO;
            case 2:
                return TipoAtendimento.PARTICULAR;
            default:
                throw new IllegalArgumentException("Tipo de atendimento inválido. Escolha 1 ou 2.");
        }
    }


    public Boolean isAcompanhanteNecessario() {
        return getIdade() < 12;
    }

    public Boolean isPrioridade() {
        return getIdade() >= 60 || isGestante() || isPcd() || isLactante() || hasEspectroAutista() || hasCriancaColo();
    }
}



