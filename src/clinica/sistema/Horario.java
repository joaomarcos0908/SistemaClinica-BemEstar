package clinica.sistema;

import java.time.Duration;
import java.time.LocalDateTime;


public class Horario {
    private int id;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFim;
    private int idMedico;
    private boolean disponivel;
    private String tipo;


public Horario(int id, LocalDateTime horaInicio, LocalDateTime horaFim, int idMedico, boolean disponivel,String tipo){
    if (horaInicio == null || horaFim == null) {
        throw new IllegalArgumentException("horaInicio e horaFim não podem ser nulos");
    }
    if (horaFim.isBefore(horaInicio) || horaFim.isEqual(horaInicio)) {
        throw new IllegalArgumentException("horaFim deve ser posterior a horaInicio");
    }
    this.id=id;
    this.horaInicio=horaInicio;
    this.horaFim=horaFim;
    this.idMedico=idMedico;
    this.disponivel=disponivel;
    this.tipo=tipo;
}



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalDateTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalDateTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalDateTime horaFim) {
        this.horaFim = horaFim;
    }

    public int getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void ocupado() {
        this.disponivel = false;
    }

    public void liberar() {
        this.disponivel = true;
    }

    public long duracaoHoraTrabalho() {
        return Duration.between(horaInicio, horaFim).toMinutes();
    }

}
