package clinica.app;

import clinica.pessoas.*;
import clinica.repositorio.RepositorioPaciente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CadastroController {
    private MenuView view;
    private Gerente gerente;
    private RepositorioPaciente repoPaciente;

    private static final String ARQ_MEDICOS = "dados/medicos.csv";
    private static final String ARQ_PACIENTES = "dados/pacientes.csv";

    public CadastroController(MenuView view) {
        this.view = view;
        this.gerente = new Gerente("Admin", "000.000.000-00", LocalDate.of(1990, 1, 1),
                "admin@clinica.com", "00000000000", "Clínica Bem Estar");
        this.repoPaciente = new RepositorioPaciente();
    }

    public void executar() {
        int op;
        do {
            view.menuCadastros();
            op = view.lerInt("Opção: ");
            switch (op) {
                case 1: menuMedicos(); break;
                case 2: menuPacientes(); break;
                case 0: view.voltando(); break;
                default: view.erro("Opção inválida!");
            }
        } while (op != 0);
    }

    private void menuMedicos() {
        int op;
        do {
            view.menuMedicos();
            op = view.lerInt("Opção: ");
            switch (op) {
                case 1: cadastrarMedico(); break;
                case 2: listarMedicos(); break;
                case 3: buscarMedico(); break;
                case 4: editarMedico(); break;
                case 5: removerMedico(); break;
                case 0: view.voltando(); break;
                default: view.erro("Opção inválida!");
            }
        } while (op != 0);
    }

    private void cadastrarMedico() {
        view.titulo("Cadastrar Médico");
        try {
            String nome = view.lerStr("Nome: ");

            String cpf = view.lerStr("CPF: ");
            if (!validarCPF(cpf)) return;

            view.info("Data de Nascimento:");
            LocalDate data = view.lerData();

            String email = view.lerStr("Email: ");
            String tel = view.lerStr("Telefone: ");
            String end = view.lerStr("Endereço: ");

            String crm = view.lerStr("CRM: ");

            for (Medico existente : gerente.getMedicos()) {
                if (existente.getCrm().equalsIgnoreCase(crm)) {
                    view.erro("CRM já cadastrado!");
                    return;
                }
            }

            double valor = view.lerDouble("Valor Consulta Base (R$): ");
            if (valor <= 0) {
                view.erro("Valor deve ser maior que zero!");
                return;
            }

            List<Especialidade> especialidades = selecionarEspecialidades();
            if (especialidades.isEmpty()) {
                view.erro("Médico deve ter pelo menos uma especialidade!");
                return;
            }

            Medico m = new Medico(nome, cpf, data, email, tel, end,
                    false, false, false, false, false, crm,
                    especialidades, valor);
            gerente.adicionarMedico(m);
            salvarDados();
            view.sucesso("Médico cadastrado com sucesso!");
        } catch (Exception e) {
            view.erro("Erro ao cadastrar: " + e.getMessage());
        }
    }

    private List<Especialidade> selecionarEspecialidades() {
        List<Especialidade> selecionadas = new ArrayList<>();
        view.info("Especialidades disponíveis:");
        Especialidade[] todas = Especialidade.values();
        for (int i = 0; i < todas.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, todas[i].getNome());
        }
        view.info("Digite os números separados por vírgula (ex: 1,3,5) ou 0 para cancelar:");
        String input = view.lerStr("Especialidades: ");

        if (input.equals("0") || input.isEmpty()) {
            return selecionadas; // Lista vazia
        }

        String[] nums = input.split(",");
        for (String num : nums) {
            try {
                int idx = Integer.parseInt(num.trim()) - 1;
                if (idx >= 0 && idx < todas.length) {
                    if (!selecionadas.contains(todas[idx])) {
                        selecionadas.add(todas[idx]);
                    }
                } else {
                    view.erro("Número " + (idx+1) + " está fora do intervalo!");
                }
            } catch (NumberFormatException e) {
                view.erro("'" + num.trim() + "' não é um número válido! Ignorando...");
            }
        }

        return selecionadas;
    }

    private void listarMedicos() {
        view.titulo("Lista de Médicos");
        List<Medico> medicos = gerente.getMedicos();
        if (medicos.isEmpty()) {
            view.info("Nenhum médico cadastrado.");
            return;
        }
        System.out.printf("%-15s %-25s %-12s %-20s%n", "CRM", "NOME", "VALOR", "ESPECIALIDADES");
        System.out.println("─".repeat(75));
        for (Medico m : medicos) {
            String especialidades = m.getEspecialidades().isEmpty() ? "Nenhuma" :
                    String.join(", ", m.getEspecialidades().stream()
                            .map(Especialidade::getNome).toArray(String[]::new));
            System.out.printf("%-15s %-25s R$ %-9.2f %-20s%n",
                    m.getCrm(),
                    m.getNome().length() > 25 ? m.getNome().substring(0, 22) + "..." : m.getNome(),
                    m.getValorConsultaBase(),
                    especialidades.length() > 20 ? especialidades.substring(0, 17) + "..." : especialidades);
        }
        System.out.println("\nTotal: " + medicos.size() + " médicos");
    }

    private void buscarMedico() {
        String crm = view.lerStr("CRM do Médico: ");
        for (Medico m : gerente.getMedicos()) {
            if (m.getCrm().equalsIgnoreCase(crm)) {
                view.titulo("Médico Encontrado");
                System.out.println("Nome: " + m.getNome());
                System.out.println("CRM: " + m.getCrm());
                System.out.println("CPF: " + m.getCpf());
                System.out.println("Email: " + m.getEmail());
                System.out.println("Telefone: " + m.getNumTelefone());
                System.out.println("Endereço: " + m.getEndereco());
                System.out.println("Valor Consulta: R$ " + String.format("%.2f", m.getValorConsultaBase()));
                System.out.print("Especialidades: ");
                if (m.getEspecialidades().isEmpty()) {
                    System.out.println("Nenhuma");
                } else {
                    for (Especialidade e : m.getEspecialidades()) {
                        System.out.print(e.getNome() + " ");
                    }
                    System.out.println();
                }
                return;
            }
        }
        view.erro("Médico não encontrado!");
    }


    private void editarMedico() {
        view.titulo("Editar Médico");
        String crm = view.lerStr("CRM do Médico: ");

        for (Medico m : gerente.getMedicos()) {
            if (m.getCrm().equalsIgnoreCase(crm)) {
                view.info("Médico encontrado: " + m.getNome());
                view.info("Deixe em branco para manter o valor atual");

                String novoTel = view.lerStr("Novo Telefone [" + m.getNumTelefone() + "]: ");
                if (!novoTel.isEmpty()) {
                    m.setNumTelefone(novoTel);
                }

                String novoEmail = view.lerStr("Novo Email [" + m.getEmail() + "]: ");
                if (!novoEmail.isEmpty()) {
                    m.setEmail(novoEmail);
                }

                String novoEnd = view.lerStr("Novo Endereço [" + m.getEndereco() + "]: ");
                if (!novoEnd.isEmpty()) {
                    m.setEndereco(novoEnd);
                }

                String novoValorStr = view.lerStr("Novo Valor Consulta [R$ " +
                        String.format("%.2f", m.getValorConsultaBase()) + "]: ");
                if (!novoValorStr.isEmpty()) {
                    try {
                        double novoValor = Double.parseDouble(novoValorStr);
                        if (novoValor > 0) {
                            m.setValorConsultaBase(novoValor);
                        }
                    } catch (NumberFormatException e) {
                        view.erro("Valor inválido! Mantendo valor anterior.");
                    }
                }

                view.info("Nota: Para alterar especialidades, recadastre o médico.");

                salvarDados();
                view.sucesso("Médico atualizado com sucesso!");
                return;
            }
        }
        view.erro("Médico não encontrado!");
    }

    private void removerMedico() {
        view.titulo("Remover Médico");
        String crm = view.lerStr("CRM do Médico: ");

        for (Medico m : gerente.getMedicos()) {
            if (m.getCrm().equalsIgnoreCase(crm)) {
                view.info("Médico encontrado:");
                System.out.println("  Nome: " + m.getNome());
                System.out.println("  CRM: " + crm);
                System.out.println("  Especialidades: " + m.getEspecialidades().size());

                if (view.lerBool("Confirma a remoção deste médico?")) {
                    gerente.removerMedico(m);
                    salvarDados(); // CORRIGIDO: Salva automaticamente
                    view.sucesso("Médico removido com sucesso!");
                } else {
                    view.info("Operação cancelada.");
                }
                return;
            }
        }
        view.erro("Médico não encontrado!");
    }

    private void menuPacientes() {
        int op;
        do {
            view.menuPacientes();
            op = view.lerInt("Opção: ");
            switch (op) {
                case 1: cadastrarPaciente(); break;
                case 2: listarPacientes(); break;
                case 3: buscarPaciente(); break;
                case 4: editarPaciente(); break;  // NOVO
                case 5: removerPaciente(); break; // Mudou de 4 para 5
                case 0: view.voltando(); break;
                default: view.erro("Opção inválida!");
            }
        } while (op != 0);
    }

    private void cadastrarPaciente() {
        view.titulo("Cadastrar Paciente");
        try {
            String nome = view.lerStr("Nome: ");

            String cpf = view.lerStr("CPF: ");
            if (!validarCPF(cpf)) return;


            for (Paciente existente : repoPaciente.listarPacientes()) {
                if (existente.getCpf().equals(cpf)) {
                    view.erro("CPF já cadastrado!");
                    return;
                }
            }

            view.info("Data de Nascimento:");
            LocalDate data = view.lerData();

            String email = view.lerStr("Email: ");
            String tel = view.lerStr("Telefone: ");
            String end = view.lerStr("Endereço: ");

            boolean gestante = view.lerBool("É gestante?");
            boolean espectro = view.lerBool("Possui espectro autista?");
            boolean pcd = view.lerBool("É PCD?");
            boolean lactante = view.lerBool("É lactante?");
            boolean crianca = view.lerBool("Possui criança de colo?");

            view.info("Tipo de Paciente:");
            view.info("  1 - EMERGÊNCIA");
            view.info("  2 - PRIORIDADE");
            view.info("  3 - ELETIVO");
            int tipoPac = view.lerInt("Tipo: ");


            TipoPaciente tp;
            if (tipoPac < 1 || tipoPac > 3) {
                view.erro("Tipo inválido! Usando ELETIVO como padrão.");
                tp = TipoPaciente.ELETIVO;
            } else {
                tp = TipoPaciente.values()[tipoPac - 1];
            }

            view.info("Tipo de Atendimento:");
            view.info("  1 - CONVÊNIO");
            view.info("  2 - PARTICULAR");
            int tipoAt = view.lerInt("Tipo: ");


            TipoAtendimento ta;
            if (tipoAt == 1) {
                ta = TipoAtendimento.CONVENIO;
            } else if (tipoAt == 2) {
                ta = TipoAtendimento.PARTICULAR;
            } else {
                view.erro("Tipo inválido! Usando PARTICULAR como padrão.");
                ta = TipoAtendimento.PARTICULAR;
            }

            Paciente p = new Paciente(nome, cpf, data, email, tel, end,
                    gestante, espectro, pcd, lactante, crianca, tp, null, ta);
            repoPaciente.adicionarPaciente(p);
            salvarDados(); // CORRIGIDO: Salva automaticamente
            view.sucesso("Paciente cadastrado com sucesso!");
        } catch (Exception e) {
            view.erro("Erro ao cadastrar: " + e.getMessage());
        }
    }

    private void listarPacientes() {
        view.titulo("Lista de Pacientes");
        List<Paciente> pacientes = repoPaciente.listarPacientes();
        if (pacientes.isEmpty()) {
            view.info("Nenhum paciente cadastrado.");
            return;
        }
        System.out.printf("%-15s %-25s %-15s %-12s%n", "CPF", "NOME", "ATENDIMENTO", "TIPO");
        System.out.println("─".repeat(70));
        for (Paciente p : pacientes) {
            System.out.printf("%-15s %-25s %-15s %-12s%n",
                    p.getCpf(),
                    p.getNome().length() > 25 ? p.getNome().substring(0, 22) + "..." : p.getNome(),
                    p.getTipoAtendimento(),
                    p.getTipoPaciente());
        }
        System.out.println("\nTotal: " + pacientes.size() + " pacientes");
    }

    private void buscarPaciente() {
        String cpf = view.lerStr("CPF do Paciente: ");
        for (Paciente p : repoPaciente.listarPacientes()) {
            if (p.getCpf().equals(cpf)) {
                view.titulo("Paciente Encontrado");
                System.out.println("Nome: " + p.getNome());
                System.out.println("CPF: " + p.getCpf());
                System.out.println("Email: " + p.getEmail());
                System.out.println("Telefone: " + p.getNumTelefone());
                System.out.println("Endereço: " + p.getEndereco());
                System.out.println("Idade: " + p.getIdade() + " anos");
                System.out.println("Tipo Paciente: " + p.getTipoPaciente());
                System.out.println("Tipo Atendimento: " + p.getTipoAtendimento());
                System.out.println("Prioridade: " + (p.isPrioridade() ? "Sim" : "Não"));
                System.out.println("Gestante: " + (p.isGestante() ? "Sim" : "Não"));
                System.out.println("PCD: " + (p.isPcd() ? "Sim" : "Não"));
                return;
            }
        }
        view.erro("Paciente não encontrado!");
    }


    private void editarPaciente() {
        view.titulo("Editar Paciente");
        String cpf = view.lerStr("CPF do Paciente: ");

        for (Paciente p : repoPaciente.listarPacientes()) {
            if (p.getCpf().equals(cpf)) {
                view.info("Paciente encontrado: " + p.getNome());
                view.info("Deixe em branco para manter o valor atual");

                String novoTel = view.lerStr("Novo Telefone [" + p.getNumTelefone() + "]: ");
                if (!novoTel.isEmpty()) {
                    p.setNumTelefone(novoTel);
                }

                String novoEmail = view.lerStr("Novo Email [" + p.getEmail() + "]: ");
                if (!novoEmail.isEmpty()) {
                    p.setEmail(novoEmail);
                }

                String novoEnd = view.lerStr("Novo Endereço [" + p.getEndereco() + "]: ");
                if (!novoEnd.isEmpty()) {
                    p.setEndereco(novoEnd);
                }

                salvarDados();
                view.sucesso("Paciente atualizado com sucesso!");
                return;
            }
        }
        view.erro("Paciente não encontrado!");
    }

    private void removerPaciente() {
        view.titulo("Remover Paciente");
        String cpf = view.lerStr("CPF do Paciente: ");

        for (Paciente p : repoPaciente.listarPacientes()) {
            if (p.getCpf().equals(cpf)) {
                view.info("Paciente encontrado:");
                System.out.println("  Nome: " + p.getNome());
                System.out.println("  CPF: " + cpf);
                System.out.println("  Tipo: " + p.getTipoPaciente());


                if (view.lerBool("Confirma a remoção deste paciente?")) {
                    repoPaciente.removerPaciente(p);
                    salvarDados(); // CORRIGIDO: Salva automaticamente
                    view.sucesso("Paciente removido com sucesso!");
                } else {
                    view.info("Operação cancelada.");
                }
                return;
            }
        }
        view.erro("Paciente não encontrado!");
    }


    private boolean validarCPF(String cpf) {
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");

        if (cpfLimpo.length() != 11) {
            view.erro("CPF deve ter 11 dígitos!");
            return false;
        }


        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            view.erro("CPF inválido!");
            return false;
        }

        return true;
    }

    public void carregarDados() {
        try {
            gerente.carregarMedicosCSV(ARQ_MEDICOS);
            repoPaciente.carregarPacientesCSV(ARQ_PACIENTES);
            view.sucesso("Dados carregados com sucesso!");
        } catch (Exception e) {
            view.info("Iniciando com dados vazios.");
        }
    }

    public void salvarDados() {
        try {
            gerente.salvarMedicosCSV(ARQ_MEDICOS);
            repoPaciente.salvarPacientesCSV(ARQ_PACIENTES);
        } catch (Exception e) {
            view.erro("Erro ao salvar dados: " + e.getMessage());
        }
    }

    public Gerente getGerenteLogado() { return gerente; }
    public RepositorioPaciente getRepoPaciente() { return repoPaciente; }
}