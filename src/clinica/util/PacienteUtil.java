package clinica.util;

import clinica.pessoas.TipoAtendimento;
import clinica.pessoas.TipoPaciente;

import java.util.List;

public class PacienteUtil {
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
    public static void verHistoricoId(String nomePaciente, List<String> historicoIds) {
        System.out.println("Histórico de: " + nomePaciente);
        if (historicoIds == null || historicoIds.isEmpty() ) {
            System.out.println("Histórico vazio!");
        } else {
            for (String id : historicoIds) {
                System.out.println(id);
            }
        }
    }

}
