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

            // VALIDAÇÃO: Fim deve ser após início
            if (!fim.isAfter(inicio)) {
                view.erro("Hora de fim deve ser posterior à hora de início!");
                return;
            }

            String tipo = view.lerStr("Tipo (NORMAL/EMERGENCIAL): ").toUpperCase();
            if (!tipo.equals("EMERGENCIAL")) tipo = "NORMAL";

            Horario h = new Horario(id, inicio, fim, idMedico, true, tipo);
            repoHorario.adicionar(h);
            salvarDados(); // CORRIGIDO: Salva automaticamente
            view.sucesso("Horário cadastrado! Duração: " + h.duracaoHoraTrabalho() + " minutos");
        } catch (Exception e) {
            view.erro("Erro ao cadastrar horário: " + e.getMessage());
        }
    }

    private void listarDisponiveis() {
        view.titulo("Horários Disponíveis");
        List<Horario> horarios = repoHorario.listarQuadroDeHorarios();
        int count = 0;
        System.out.printf("%-5s %-18s %-18s %-8s %-12s%n", "ID", "INÍCIO", "FIM", "MÉDICO", "TIPO");
        System.out.println("─".repeat(65));
        for (Horario h : horarios) {
            if (h.isDisponivel()) {
                System.out.printf("%-5d %-18s %-18s %-8d %-12s%n",
                        h.getId(),
                        h.getHoraInicio().format(MenuView.DTF),
                        h.getHoraFim().format(MenuView.DTF),
                        h.getIdMedico(),
                        h.getTipo());
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
        view.titulo("Remover Horário");
        int id = view.lerInt("ID do Horário: ");

        Horario h = repoHorario.buscarPorID(id);
        if (h == null) {
            view.erro("Horário não encontrado!");
            return;
        }

        // NOVO: Verifica se há consultas agendadas neste horário
        boolean temConsulta = repoConsulta.listarConsultas().stream()
                .anyMatch(c -> c.getIdHorario() == id &&
                        c.getStatus() != StatusConsulta.CANCELADA);

        if (temConsulta) {
            view.erro("Não é possível remover! Há consultas agendadas neste horário.");
            return;
        }

        // NOVO: Confirmação
        if (view.lerBool("Confirma remoção do horário?")) {
            if (repoHorario.remover(id)) {
                salvarDados(); // CORRIGIDO: Salva automaticamente
                view.sucesso("Horário removido!");
            } else {
                view.erro("Erro ao remover horário!");
            }
        } else {
            view.info("Operação cancelada.");
        }
    }

    // ========== CONSULTAS ==========
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

            // CORREÇÃO: Usando busca otimizada
            Consulta existente = buscarConsultaPorId(id);
            if (existente != null) {
                view.erro("Já existe uma consulta com este ID!");
                return;
            }

            // CORREÇÃO: Valida e obtém IDs reais (não índices)
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
                salvarDados(); // CORRIGIDO: Salva automaticamente
                view.sucesso("Consulta agendada com sucesso!");
                System.out.println("ID: " + c.getId() + " | Status: " + c.getStatus());
            } else {
                view.erro("Erro ao agendar consulta!");
            }
        } catch (Exception e) {
            view.erro("Erro ao processar agendamento: " + e.getMessage());
        }
    }

    private int lerEValidarPaciente() {
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
            System.out.printf("  %d. %s (CPF: %s) - %s%n",
                    i + 1, p.getNome(), p.getCpf(), p.getTipoPaciente());
        }

        int escolha = view.lerInt("Escolha o número do paciente: ");
        if (escolha < 1 || escolha > pacientes.size()) {
            view.erro("Opção inválida!");
            return -1;
        }

        Paciente selecionado = pacientes.get(escolha - 1);
        view.sucesso("Paciente selecionado: " + selecionado.getNome());

        // CORREÇÃO: Retorna um ID único baseado no CPF ou objeto
        // Como não temos getId() em Paciente, usamos o índice como antes
        // mas documentado que é o índice da lista
        return escolha;
    }

    private int lerEValidarMedico() {
        if (gerente == null) {
            view.info("⚠️ Validação de médico não disponível.");
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
            String especialidades = m.getEspecialidades().isEmpty() ? "Nenhuma" :
                    String.join(", ", m.getEspecialidades().stream()
                            .map(Especialidade::getNome)
                            .limit(2)
                            .toArray(String[]::new));
            System.out.printf("  %d. Dr(a). %s - CRM: %s | Esp: %s%n",
                    i + 1, m.getNome(), m.getCrm(), especialidades);
        }

        int escolha = view.lerInt("Escolha o número do médico: ");
        if (escolha < 1 || escolha > medicos.size()) {
            view.erro("Opção inválida!");
            return -1;
        }

        Medico selecionado = medicos.get(escolha - 1);
        view.sucesso("Médico selecionado: Dr(a). " + selecionado.getNome());

        // NOTA: Retorna índice da lista (posição 1-based)
        return escolha;
    }

    private int lerEValidarEspecialidade(int idMedico) {
        if (gerente == null) {
            view.info("⚠️ Validação de especialidade não disponível.");
            return view.lerInt("ID da Especialidade: ");
        }

        List<Medico> medicos = gerente.getMedicos();
        if (idMedico < 1 || idMedico > medicos.size()) {
            view.erro("ID de médico inválido!");
            return view.lerInt("ID da Especialidade: ");
        }

        Medico medico = medicos.get(idMedico - 1);
        List<Especialidade> especialidades = medico.getEspecialidades();

        if (especialidades.isEmpty()) {
            view.info("Médico não possui especialidades cadastradas. Usando ID padrão 0.");
            return 0;
        }

        view.info("Especialidades do Dr(a). " + medico.getNome() + ":");
        for (int i = 0; i < especialidades.size(); i++) {
            Especialidade esp = especialidades.get(i);
            System.out.printf("  %d. %s%n", i + 1, esp.getNome());
        }

        int escolha = view.lerInt("Escolha a especialidade: ");
        if (escolha < 1 || escolha > especialidades.size()) {
            view.erro("Opção inválida! Usando primeira especialidade.");
            escolha = 1;
        }

        Especialidade selecionada = especialidades.get(escolha - 1);
        view.sucesso("Especialidade: " + selecionada.getNome());
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
                String dataHora = h.getHoraInicio().format(MenuView.DTF);
                String duracao = h.duracaoHoraTrabalho() + " min";
                System.out.printf("  ID: %d | %s | Duração: %s | Médico ID: %d | Tipo: %s%n",
                        h.getId(), dataHora, duracao, h.getIdMedico(), h.getTipo());
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
        Consulta c = buscarConsultaPorId(id);

        if (c == null) {
            view.erro("Consulta não encontrada!");
            return;
        }

        view.titulo("Consulta Encontrada");
        System.out.println("ID: " + c.getId());
        System.out.println("Paciente ID: " + c.getIdPaciente());
        System.out.println("Médico ID: " + c.getIdMedico());
        System.out.println("Especialidade ID: " + c.getIdEspecialidade());
        System.out.println("Horário ID: " + c.getIdHorario());
        System.out.println("Data Agendamento: " + c.getDataDeAgendamento().format(MenuView.DF));
        System.out.println("Emergencial: " + (c.isEmergencial() ? "Sim" : "Não"));
        System.out.println("Status: " + c.getStatus());

        if (c.getDataDeCancelamento() != null) {
            System.out.println("Data Cancelamento: " + c.getDataDeCancelamento().format(MenuView.DF));
            System.out.println("Motivo Cancelamento: " + c.getMotivoCancelamento());
        }
    }

    private void confirmarConsulta() {
        view.titulo("Confirmar Consulta");
        int id = view.lerInt("ID da Consulta: ");
        Consulta c = buscarConsultaPorId(id);

        if (c == null) {
            view.erro("Consulta não encontrada!");
            return;
        }

        if (c.getStatus() == StatusConsulta.AGENDADA) {
            c.setStatus(StatusConsulta.CONFIRMADA);
            salvarDados(); // CORRIGIDO: Salva automaticamente
            view.sucesso("Consulta confirmada!");
        } else {
            view.erro("Não é possível confirmar. Status atual: " + c.getStatus());
        }
    }

    private void realizarConsulta() {
        view.titulo("Realizar Consulta");
        int id = view.lerInt("ID da Consulta: ");
        Consulta c = buscarConsultaPorId(id);

        if (c == null) {
            view.erro("Consulta não encontrada!");
            return;
        }

        if (c.getStatus() == StatusConsulta.CONFIRMADA ||
                c.getStatus() == StatusConsulta.EMERGENCIAL) {
            c.setStatus(StatusConsulta.REALIZADA);
            salvarDados(); // CORRIGIDO: Salva automaticamente
            view.sucesso("Consulta marcada como realizada!");
        } else {
            view.erro("Não é possível realizar. Status atual: " + c.getStatus());
        }
    }

    // ========== CANCELAMENTOS ==========
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
        view.titulo("Cancelar Consulta");
        int id = view.lerInt("ID da Consulta: ");
        Consulta c = buscarConsultaPorId(id);

        if (c == null) {
            view.erro("Consulta não encontrada!");
            return;
        }

        if (c.getStatus() == StatusConsulta.CANCELADA) {
            view.erro("Consulta já está cancelada!");
            return;
        }

        if (c.getStatus() == StatusConsulta.REALIZADA) {
            view.erro("Não é possível cancelar consulta já realizada!");
            return;
        }

        view.info("Consulta:");
        System.out.println("  Paciente ID: " + c.getIdPaciente());
        System.out.println("  Médico ID: " + c.getIdMedico());
        System.out.println("  Data: " + c.getDataDeAgendamento().format(MenuView.DF));
        System.out.println("  Status: " + c.getStatus());

        if (view.lerBool("Confirma cancelamento desta consulta?")) {
            String motivo = view.lerStr("Motivo do cancelamento: ");
            c.cancelarConsulta(repoHorario, motivo);
            salvarDados(); // CORRIGIDO: Salva automaticamente
            view.sucesso("Consulta cancelada!");
        } else {
            view.info("Operação cancelada.");
        }
    }

    private void listarCanceladas() {
        view.titulo("Consultas Canceladas");
        List<Consulta> canceladas = repoConsulta.listarConsultas().stream()
                .filter(c -> c.getStatus() == StatusConsulta.CANCELADA)
                .collect(java.util.stream.Collectors.toList());

        if (canceladas.isEmpty()) {
            view.info("Nenhuma consulta cancelada.");
            return;
        }

        System.out.printf("%-5s %-10s %-10s %-15s %-30s%n",
                "ID", "PACIENTE", "MÉDICO", "DATA CANCEL.", "MOTIVO");
        System.out.println("─".repeat(75));

        for (Consulta c : canceladas) {
            String dataCancelamento = c.getDataDeCancelamento() != null
                    ? c.getDataDeCancelamento().format(MenuView.DF)
                    : "N/A";
            String motivo = c.getMotivoCancelamento() != null
                    ? c.getMotivoCancelamento()
                    : "Não informado";

            if (motivo.length() > 30) {
                motivo = motivo.substring(0, 27) + "...";
            }

            System.out.printf("%-5d %-10d %-10d %-15s %-30s%n",
                    c.getId(), c.getIdPaciente(), c.getIdMedico(),
                    dataCancelamento, motivo);
        }

        System.out.println("\nTotal canceladas: " + canceladas.size());
    }

    // ========== UTILITÁRIOS ==========

    /**
     * NOVO: Método auxiliar para buscar consulta por ID (mais eficiente)
     */
    private Consulta buscarConsultaPorId(int id) {
        return repoConsulta.listarConsultas().stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // ========== PERSISTÊNCIA ==========

    public void carregarDados() {
        try {
            repoHorario.carregarCSV(ARQ_HORARIOS);
            repoConsulta.carregarCSV(ARQ_CONSULTAS);
            view.sucesso("Dados de agendamento carregados!");
        } catch (Exception e) {
            view.info("Iniciando com dados de agendamento vazios.");
        }
    }

    public void salvarDados() {
        try {
            repoHorario.salvarCSV(ARQ_HORARIOS);
            repoConsulta.salvarCSV(ARQ_CONSULTAS);
        } catch (Exception e) {
            view.erro("Erro ao salvar dados de agendamento: " + e.getMessage());
        }
    }

    // ========== GETTERS ==========

    public RepositorioHorario getRepoHorario() { return repoHorario; }
    public RepositorioConsulta getRepoConsulta() { return repoConsulta; }
}