package clinica.sistema;

import java.time.Duration;
import java.time.LocalDateTime;

public class Cancelamento {
    private int idConsulta;
    private LocalDateTime dataCancelamento;
    private String motivo;
    private double taxaAplicada;

    public Cancelamento(int idConsulta, String motivo,LocalDateTime dataCancelamento) {
        this.idConsulta = idConsulta;
        this.motivo = motivo;
        this.dataCancelamento=dataCancelamento;
    }

    public LocalDateTime getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(LocalDateTime dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public int getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }


    public void calcularMultaCancelamento(Horario agendado){
        if(agendado==null){
            throw new IllegalArgumentException("Horário da consulta não foi encontrado");
        }
        long periodoCancelamento = Duration.between(dataCancelamento, agendado.getHoraInicio()).toHours();
        if(periodoCancelamento<24){
         this.taxaAplicada= 0.5;
            System.out.println("clinica.sistema.Cancelamento  em menos de 24 horas, terá que pagar 50% do valor da consulta.");

        }else{
            this.taxaAplicada = 0.0;
            System.out.println("sem taxas para pagar");
        }
    }

    public String toString() {
        return "clinica.sistema.Cancelamento da consulta " + idConsulta +
                " em " + dataCancelamento +
                " | Motivo: " + motivo +
                " | Taxa aplicada: " + (taxaAplicada * 100) + "%";
    }
}
