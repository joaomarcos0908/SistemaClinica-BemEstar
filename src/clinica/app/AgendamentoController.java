package clinica.app;

import clinica.repositorio.RepositorioConsulta;
import clinica.repositorio.RepositorioHorario;
import clinica.sistema.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AgendamentoController {
    private MenuView view;
    private RepositorioHorario repoHorario;
    private RepositorioConsulta repoConsulta;

    private static final String ARQ_HORARIOS = "dados/horarios.csv";
    private static final String ARQ_CONSULTAS = "dados/consultas.csv";

    public AgendamentoController(MenuView view) {
        this.view = view;
        this.repoHorario = new RepositorioHorario();
        this.repoConsulta = new RepositorioConsulta();
    }

    public void executar() {
        int op;
        do {
            view.menuAgendamentos();
            op = view.lerInt("Opção: ");
            switch (op) {
                case 1: menuHorarios(); break;
                case 2: menuConsultas(); break;
                case 3: menuCancelamentos(); break;
                case 0: view.voltando(); break;
                default: view.erro("Opção inválida!");
            }
        } while (op != 0);
    }

    private void menuHorarios() {
        int op;
        do {
            view.menuHorarios();
            op = view.lerInt("Opção: ");
            switch (op) {
                case 1: cadastrarHorario(); break;
                case 2: view.tabelaHorarios(repoHorario.listarQuadroDeHorarios()); break;
                case 3: listarDisponiveis(); break;
                case 4: buscarHorario(); break;
                case 5: removerHorario(); break;
                case 0: view.voltando(); break;
                default: view.erro("Opção inválida!");
            }
        } while (op != 0);
    }

    private void cadastrarHorario() {
        view.titulo("Cadastrar Horário");
        try {
            int id = view.lerInt("ID do Horário: ");
            if (repoHorario.buscarPorID(id) != null) {
                view.erro("ID já existe!");
                return;
            }
            int idMedico = view.lerInt("ID do Médico: ");

            view.info("Hora de INÍCIO (dd/MM/yyyy HH:mm):");
            LocalDateTime inicio = view.lerDataHora();

            view.info("Hora de FIM (dd/MM/yyyy HH:mm):");
            LocalDateTime fim = view.lerDataHora();

            String tipo = view.lerStr("Tipo (NORMAL/EMERGENCIAL): ").toUpperCase();
            if (!tipo.equals("EMERGENCIAL")) tipo = "NORMAL";

            Horario h = new Horario(id, inicio, fim, idMedico, true, tipo);
            repoHorario.adicionar(h);
            view.sucesso("Horário cadastrado! Duração: " + h.duracaoHoraTrabalho() + " minutos");
        } catch (Exception e) {
            view.erro(e.getMessage());
        }
    }

    private void listarDisponiveis() {
        view.titulo("Horários Disponíveis");
        List<Horario> horarios = repoHorario.listarQuadroDeHorarios();
        int count = 0;
        System.out.printf("%-5s %-18s %-18s %-8s%n", "ID", "INÍCIO", "FIM", "MÉDICO");
        System.out.println("─".repeat(55));
        for (Horario h : horarios) {
            if (h.isDisponivel()) {
                System.out.printf("%-5d %-18s %-18s %-8d%n",
                        h.getId(), h.getHoraInicio().format(MenuView.DTF),
                        h.getHoraFim().format(MenuView.DTF), h.getIdMedico());
                count++;
            }
        }
        if (count == 0) view.info("Nenhum horário disponível.");
        else System.out.println("Total disponíveis: " + count);
    }

    private void buscarHorario() {
        int id = view.lerInt("ID do Horário: ");
        Horario h = repoHorario.buscarPorID(id);
        if (h == null) {
            view.erro("Horário não encontrado!");
            return;
        }
        view.titulo("Horário Encontrado");
        System.out.println("ID: " + h.getId());
        System.out.println("Início: " + h.getHoraInicio().format(MenuView.DTF));
        System.out.println("Fim: " + h.getHoraFim().format(MenuView.DTF));
        System.out.println("Duração: " + h.duracaoHoraTrabalho() + " minutos");
        System.out.println("Médico ID: " + h.getIdMedico());
        System.out.println("Disponível: " + (h.isDisponivel() ? "Sim" : "Não"));
        System.out.println("Tipo: " + h.getTipo());
    }

    private void removerHorario() {
        int id = view.lerInt("ID do Horário: ");
        if (repoHorario.remover(id)) view.sucesso("Horário removido!");
        else view.erro("Horário não encontrado!");
    }

    private void menuConsultas() {
        int op;
        do {
            view.menuConsultas();
            op = view.lerInt("Opção: ");
            switch (op) {
                case 1: agendarConsulta(false); break;
                case 2: agendarConsulta(true); break;
                case 3: view.tabelaConsultas(repoConsulta.listarConsultas()); break;
                case 4: buscarConsulta(); break;
                case 5: confirmarConsulta(); break;
                case 6: realizarConsulta(); break;
                case 0: view.voltando(); break;
                default: view.erro("Opção inválida!");
            }
        } while (op != 0);
    }

    private void agendarConsulta(boolean emergencial) {
        view.titulo(emergencial ? "Agendar Consulta Emergencial" : "Agendar Consulta");
        try {
            int id = view.lerInt("ID da Consulta: ");
            int idPaciente = view.lerInt("ID do Paciente: ");
            int idMedico = view.lerInt("ID do Médico: ");
            int idEspecialidade = view.lerInt("ID da Especialidade: ");
            int idHorario = view.lerInt("ID do Horário: ");

            Consulta c = new Consulta(LocalDate.now(), null, id, idEspecialidade,
                    StatusConsulta.AGENDADA, emergencial, idMedico, idPaciente);
            c.setIdHorario(idHorario);

            if (c.agendarConsulta(repoHorario)) {
                repoConsulta.adicionar(c);
                view.sucesso("Consulta agendada com sucesso!");
                System.out.println("ID: " + c.getId() + " | Status: " + c.getStatus());
            }
        } catch (Exception e) {
            view.erro(e.getMessage());
        }
    }

    private void buscarConsulta() {
        int id = view.lerInt("ID da Consulta: ");
        for (Consulta c : repoConsulta.listarConsultas()) {
            if (c.getId() == id) {
                view.titulo("Consulta Encontrada");
                System.out.println("ID: " + c.getId());
                System.out.println("Paciente ID: " + c.getIdPaciente());
                System.out.println("Médico ID: " + c.getIdMedico());
                System.out.println("Especialidade ID: " + c.getIdEspecialidade());
                System.out.println("Data Agendamento: " + c.getDataDeAgendamento().format(MenuView.DF));
                System.out.println("Emergencial: " + (c.isEmergencial() ? "Sim" : "Não"));
                System.out.println("Status: " + c.getStatus());
                return;
            }
        }
        view.erro("Consulta não encontrada!");
    }

    private void confirmarConsulta() {
        int id = view.lerInt("ID da Consulta: ");
        for (Consulta c : repoConsulta.listarConsultas()) {
            if (c.getId() == id) {
                if (c.getStatus() == StatusConsulta.AGENDADA) {
                    c.setStatus(StatusConsulta.CONFIRMADA);
                    view.sucesso("Consulta confirmada!");
                } else {
                    view.erro("Não é possível confirmar. Status atual: " + c.getStatus());
                }
                return;
            }
        }
        view.erro("Consulta não encontrada!");
    }

    private void realizarConsulta() {
        int id = view.lerInt("ID da Consulta: ");
        for (Consulta c : repoConsulta.listarConsultas()) {
            if (c.getId() == id) {
                if (c.getStatus() == StatusConsulta.CONFIRMADA || c.getStatus() == StatusConsulta.EMERGENCIAL) {
                    c.setStatus(StatusConsulta.REALIZADA);
                    view.sucesso("Consulta marcada como realizada!");
                } else {
                    view.erro("Não é possível realizar. Status atual: " + c.getStatus());
                }
                return;
            }
        }
        view.erro("Consulta não encontrada!");
    }


    private void menuCancelamentos() {
        int op;
        do {
            view.menuCancelamentos();
            op = view.lerInt("Opção: ");
            switch (op) {
                case 1: cancelarConsulta(); break;
                case 2: listarCanceladas(); break;
                case 0: view.voltando(); break;
                default: view.erro("Opção inválida!");
            }
        } while (op != 0);
    }

    private void cancelarConsulta() {
        int id = view.lerInt("ID da Consulta: ");
        for (Consulta c : repoConsulta.listarConsultas()) {
            if (c.getId() == id) {
                if (c.getStatus() == StatusConsulta.CANCELADA) {
                    view.erro("Consulta já está cancelada!");
                    return;
                }
                if (c.getStatus() == StatusConsulta.REALIZADA) {
                    view.erro("Não é possível cancelar consulta já realizada!");
                    return;
                }
                String motivo = view.lerStr("Motivo do cancelamento: ");
                c.cancelarConsulta(repoHorario, motivo);
                view.sucesso("Consulta cancelada!");
                return;
            }
        }
        view.erro("Consulta não encontrada!");
    }

    private void listarCanceladas() {
        view.titulo("Consultas Canceladas");
        int count = 0;
        for (Consulta c : repoConsulta.listarConsultas()) {
            if (c.getStatus() == StatusConsulta.CANCELADA) {
                System.out.printf("ID: %d | Paciente: %d | Data Cancelamento: %s%n",
                        c.getId(), c.getIdPaciente(),
                        c.getDataDeCancelamento() != null ? c.getDataDeCancelamento().format(MenuView.DF) : "N/A");
                count++;
            }
        }
        if (count == 0) view.info("Nenhuma consulta cancelada.");
        else System.out.println("Total canceladas: " + count);
    }


    public void carregarDados() {
        repoHorario.carregarCSV(ARQ_HORARIOS);
        repoConsulta.carregarCSV(ARQ_CONSULTAS);
    }

    public void salvarDados() {
        repoHorario.salvarCSV(ARQ_HORARIOS);
        repoConsulta.salvarCSV(ARQ_CONSULTAS);
    }


    public RepositorioHorario getRepoHorario() { return repoHorario; }
    public RepositorioConsulta getRepoConsulta() { return repoConsulta; }
}
