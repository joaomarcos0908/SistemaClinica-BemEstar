import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Horario {
    private int id;
    private LocalDate data;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFim;
    private int idMedico;
    private boolean disponivel;
    private String tipo;

    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Horario(int id,LocalDate data, boolean disponivel, LocalDateTime horaFim, LocalDateTime horaInicio, int idMedico, String tipo) {
        if (Duration.between(horaInicio, horaFim).toMinutes() <= 0) {
            throw new IllegalArgumentException("Hora inicio maior que Hora fim");
        }
        this.id=id;
        this.data = data;
        this.disponivel = disponivel;
        this.horaFim = horaFim;
        this.horaInicio = horaInicio;
        this.idMedico = idMedico;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalDateTime getHoraFim() {
        if (Duration.between(horaInicio, horaFim).toMinutes() <= 0) {
            throw new IllegalArgumentException("Hora inicio maior que Hora fim");
        }
        return horaFim;
    }

    public void setHoraFim(LocalDateTime horaFim) {
        this.horaFim = horaFim;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalDateTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public int getIdMedico() {
        return idMedico;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isDisponivel() {
        return disponivel;
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
