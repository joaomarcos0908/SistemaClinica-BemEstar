package clinica.app;

import clinica.repositorio.RepositorioConsulta;
import clinica.repositorio.RepositorioHorario;
import clinica.sistema.*;
import clinica.pessoas.*;
import clinica.repositorio.RepositorioPaciente;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AgendamentoController {
    private MenuView view;
    private RepositorioHorario repoHorario;
    private RepositorioConsulta repoConsulta;

    private Gerente gerente;
    private RepositorioPaciente repoPaciente;

    private static final String ARQ_HORARIOS = "dados/horarios.csv";
    private static final String ARQ_CONSULTAS = "dados/consultas.csv";

    public AgendamentoController(MenuView view) {
        this.view = view;
        this.repoHorario = new RepositorioHorario();
        this.repoConsulta = new RepositorioConsulta();
        this.gerente = null;
        this.repoPaciente = null;
    }


    public void setRepositoriosCadastro(Gerente gerente, RepositorioPaciente repoPaciente) {
        this.gerente = gerente;
        this.repoPaciente = repoPaciente;
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

    // ========== HORÁRIOS ==========
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


            for (Consulta c : repoConsulta.listarConsultas()) {
                if (c.getId() == id) {
                    view.erro("Já existe uma consulta com este ID!");
                    return;
                }
            }


            int idPaciente = lerEValidarPaciente();
            if (idPaciente == -1) return;


            int idMedico = lerEValidarMedico();
            if (idMedico == -1) return;


            int idEspecialidade = lerEValidarEspecialidade(idMedico);
            if (idEspecialidade == -1) return;


            int idHorario = lerEValidarHorario(idMedico);
            if (idHorario == -1) return;

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

    private int lerEValidarPaciente() {
        // Se não tem repositório conectado, apenas pede o ID
        if (repoPaciente == null) {
            view.info("⚠️ Validação de paciente não disponível.");
            return view.lerInt("ID do Paciente: ");
        }

        List<Paciente> pacientes = repoPaciente.listarPacientes();
        if (pacientes.isEmpty()) {
            view.erro("Nenhum paciente cadastrado! Cadastre um paciente primeiro.");
            return -1;
        }


        view.info("Pacientes cadastrados:");
        for (int i = 0; i < pacientes.size(); i++) {
            Paciente p = pacientes.get(i);
            System.out.printf("  %d. %s (CPF: %s)%n", i + 1, p.getNome(), p.getCpf());
        }

        int escolha = view.lerInt("Escolha o número do paciente: ");
        if (escolha < 1 || escolha > pacientes.size()) {
            view.erro("Opção inválida!");
            return -1;
        }


        Paciente selecionado = pacientes.get(escolha - 1);
        view.sucesso("Paciente selecionado: " + selecionado.getNome());
        return escolha;
    }

    private int lerEValidarMedico() {
        if (gerente == null) {
            view.info(" Validação de médico não disponível.");
            return view.lerInt("ID do Médico: ");
        }

        List<Medico> medicos = gerente.getMedicos();
        if (medicos.isEmpty()) {
            view.erro("Nenhum médico cadastrado! O gerente deve cadastrar médicos primeiro.");
            return -1;
        }


        view.info("Médicos cadastrados:");
        for (int i = 0; i < medicos.size(); i++) {
            Medico m = medicos.get(i);
            System.out.printf("  %d. Dr(a). %s - CRM: %s%n", i + 1, m.getNome(), m.getCrm());
        }

        int escolha = view.lerInt("Escolha o número do médico: ");
        if (escolha < 1 || escolha > medicos.size()) {
            view.erro("Opção inválida!");
            return -1;
        }

        Medico selecionado = medicos.get(escolha - 1);
        view.sucesso("Médico selecionado: Dr(a). " + selecionado.getNome());
        return escolha;  // Usando índice como ID
    }

    private int lerEValidarEspecialidade(int idMedico) {

        if (gerente == null) {
            view.info(" Validação de especialidade não disponível.");
            return view.lerInt("ID da Especialidade: ");
        }

        List<Medico> medicos = gerente.getMedicos();
        if (idMedico < 1 || idMedico > medicos.size()) {
            return view.lerInt("ID da Especialidade: ");
        }

        Medico medico = medicos.get(idMedico - 1);
        List<Especialidade> especialidades = medico.getEspecialidades();

        if (especialidades.isEmpty()) {
            view.info("Médico não possui especialidades cadastradas. Usando especialidade padrão.");
            return 0;
        }


        view.info("Especialidades do Dr(a). " + medico.getNome() + ":");
        for (int i = 0; i < especialidades.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, especialidades.get(i).getNome());
        }

        int escolha = view.lerInt("Escolha a especialidade: ");
        if (escolha < 1 || escolha > especialidades.size()) {
            view.erro("Opção inválida! Usando primeira especialidade.");
            return 1;
        }

        view.sucesso("Especialidade: " + especialidades.get(escolha - 1).getNome());
        return escolha;
    }

    private int lerEValidarHorario(int idMedico) {
        List<Horario> horarios = repoHorario.listarQuadroDeHorarios();

        if (horarios.isEmpty()) {
            view.erro("Nenhum horário cadastrado! Cadastre horários primeiro.");
            return -1;
        }


        view.info("Horários disponíveis:");
        int count = 0;
        for (Horario h : horarios) {
            if (h.isDisponivel()) {
                System.out.printf("  ID: %d | %s - %s | Médico ID: %d%n",
                        h.getId(),
                        h.getHoraInicio().format(MenuView.DTF),
                        h.getHoraFim().format(MenuView.DTF),
                        h.getIdMedico());
                count++;
            }
        }

        if (count == 0) {
            view.erro("Nenhum horário disponível!");
            return -1;
        }

        int idHorario = view.lerInt("Digite o ID do horário: ");
        Horario horarioSelecionado = repoHorario.buscarPorID(idHorario);

        if (horarioSelecionado == null) {
            view.erro("Horário não encontrado!");
            return -1;
        }

        if (!horarioSelecionado.isDisponivel()) {
            view.erro("Este horário não está disponível!");
            return -1;
        }

        view.sucesso("Horário selecionado: " +
                horarioSelecionado.getHoraInicio().format(MenuView.DTF) + " - " +
                horarioSelecionado.getHoraFim().format(MenuView.DTF));
        return idHorario;
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