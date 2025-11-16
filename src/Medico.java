import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Medico extends Pessoa{
    private String crm;
    private List<String> especialidades;
    private double valorConsultaBase;

    public Medico(String nome, String cpf, LocalDate dataNascimento, String email, String numTelefone, String endereco,
                  boolean isGestante, boolean hasEspectroAutista, boolean isPcd, boolean isLactante,
                  boolean hasCriancaColo, String crm, List<String> especialidades, double valorConsultaBase) {
        super(nome, cpf, dataNascimento, email, numTelefone, endereco, isGestante, hasEspectroAutista, isPcd,
                isLactante, hasCriancaColo);
        setCrm(crm);
        if(especialidades == null){
            this.especialidades = new ArrayList<>();
        } else {
            this.especialidades = new ArrayList<>(especialidades);
        }
        setValorConsultaBase(valorConsultaBase);
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        if(crm == null || crm.trim().isEmpty()){
            throw new IllegalArgumentException("Insira um CRM.");
        }
        this.crm = crm;
    }

    List<String> getEspecialidadesInternas() {
        return especialidades;
    }

    public List<String> getEspecialidades() {
        return new ArrayList<>(especialidades);
    }

    public double getValorConsultaBase() {
        return valorConsultaBase;
    }

    public void setValorConsultaBase(double valorConsultaBase) {
        if(valorConsultaBase <= 0){
            throw new IllegalArgumentException("Insira um valor válido.");
        }
        this.valorConsultaBase = valorConsultaBase;
    }
}
