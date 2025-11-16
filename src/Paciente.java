import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Paciente extends  Pessoa {
    private String tipoPaciente;
    private List<String> historicoIds;
    private String tipoAtendimento;

    public Paciente(String nome, String cpf, LocalDate dataNascimento, String email, String numTelefone,
                    String endereco, boolean isGestante, boolean hasEspectroAutista, boolean isPcd, boolean isLactante,
                    boolean hasCriancaColo, int tipoPaciente, List<String> historicoIds, int tipoAtendimento) {
        super(nome, cpf, dataNascimento, email, numTelefone, endereco, isGestante, hasEspectroAutista, isPcd,
                isLactante, hasCriancaColo);
        setTipoPaciente(tipoPaciente);
        setHistoricoIds(historicoIds);
        setTipoAtendimento(tipoAtendimento);
    }

    public String getTipoPaciente() {
        return tipoPaciente;
    }

    public void setTipoPaciente(int tipoPaciente) {
        switch (tipoPaciente) {
            case 1:
                this.tipoPaciente = "Emergência";
                break;
            case 2:
                if (isPrioridade()) {
                    this.tipoPaciente = "Prioridade";
                } else {
                    throw new IllegalArgumentException("Você não preenche os requisitos para ter um " +
                            "atendimento prioritário.");
                }
                break;
            case 3:
                this.tipoPaciente = "Eletivo";
                break;
            default:
                throw new IllegalArgumentException("Tipo de paciente inválido.");
        }
    }

    public String getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(int tipoAtendimento) {
        switch (tipoAtendimento) {
            case 1:
                this.tipoAtendimento = "Convênio";
                break;
            case 2:
                this.tipoAtendimento = "Particular";
                break;
            default:
                throw new IllegalArgumentException("Tipo de atendimento inválido.");
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
    public void adicionarHistoricoId(String id){
        if(id != null && !id.trim().isEmpty()){
            this.historicoIds.add(id);
        }
    }
    public void verHistoricoId(){
        if(this.historicoIds.isEmpty()){
            System.out.println("Histórico vazio!");
        } else {
            for (String id : historicoIds) {
                System.out.println(id);
            }
        }
    }

    public Boolean isAcompNecessario() {
        return getIdade() < 12;
    }

    public Boolean isPrioridade() {
        return getIdade() >= 60 || isGestante() || isPcd() || isLactante() || hasEspectroAutista() || hasCriancaColo();
    }



}
