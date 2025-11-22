package clinica.pessoas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Paciente extends Pessoa {
    private boolean isGestante;
    private boolean hasEspectroAutista;
    private boolean isPcd;
    private boolean isLactante;
    private boolean hasCriancaColo;
    private TipoPaciente tipoPaciente;
    private List<String> historicoIds;
    private TipoAtendimento tipoAtendimento;

    public Paciente(String nome, String cpf, LocalDate dataNascimento, String email, String numTelefone,
                    String endereco, boolean isGestante, boolean hasEspectroAutista, boolean isPcd, boolean isLactante,
                    boolean hasCriancaColo, TipoPaciente tipoPaciente, List<String> historicoIds, TipoAtendimento tipoAtendimento) {
        super(nome, cpf, dataNascimento, email, numTelefone, endereco);
        this.isGestante = isGestante;
        this.hasEspectroAutista = hasEspectroAutista;
        this.isPcd = isPcd;
        this.isLactante = isLactante;
        this.hasCriancaColo = hasCriancaColo;
        setTipoPaciente(tipoPaciente);
        setHistoricoIds(historicoIds);
        setTipoAtendimento(tipoAtendimento);
    }

    public TipoPaciente getTipoPaciente() {
        return tipoPaciente;
    }

    public void setTipoPaciente(TipoPaciente tipoPaciente) {
        if (tipoPaciente == TipoPaciente.PRIORIDADE && !isPrioridade()) {
            throw new IllegalArgumentException("clinica.pessoas.Paciente não possui os requisitos para um atendimento prioritário.");
        }
        this.tipoPaciente = tipoPaciente;
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

    public TipoAtendimento getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(TipoAtendimento tipoAtendimento) {
        this.tipoAtendimento = tipoAtendimento;
    }

    public boolean isGestante() {
        return isGestante;
    }

    public void setGestante(boolean gestante) {
        isGestante = gestante;
    }

    public boolean hasEspectroAutista() {
        return hasEspectroAutista;
    }

    public void setHasEspectroAutista(boolean hasEspectroAutista) {
        this.hasEspectroAutista = hasEspectroAutista;
    }

    public boolean isPcd() {
        return isPcd;
    }

    public void setPcd(boolean pcd) {
        isPcd = pcd;
    }

    public boolean isLactante() {
        return isLactante;
    }

    public void setLactante(boolean lactante) {
        isLactante = lactante;
    }

    public boolean hasCriancaColo() {
        return hasCriancaColo;
    }

    public void setHasCriancaColo(boolean hasCriancaColo) {
        this.hasCriancaColo = hasCriancaColo;
    }

    public Boolean isAcompanhanteNecessario() {
        return getIdade() < 12;
    }

    public Boolean isPrioridade() {
        return getIdade() >= 60 || isGestante() || isPcd() || isLactante() || hasEspectroAutista() || hasCriancaColo();
    }
}



