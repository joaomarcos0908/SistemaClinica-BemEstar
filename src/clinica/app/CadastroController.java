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
                case 4: removerMedico(); break;
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
            view.info("Data de Nascimento:");
            LocalDate data = view.lerData();
            String email = view.lerStr("Email: ");
            String tel = view.lerStr("Telefone: ");
            String end = view.lerStr("Endereço: ");
            String crm = view.lerStr("CRM: ");
            double valor = view.lerDouble("Valor Consulta Base (R$): ");

            List<Especialidade> especialidades = selecionarEspecialidades();

            Medico m = new Medico(nome, cpf, data, email, tel, end,
                    false, false, false, false, false, crm,
                    especialidades, valor);
            gerente.adicionarMedico(m);
            view.sucesso("Médico cadastrado com sucesso!");
        } catch (Exception e) {
            view.erro(e.getMessage());
        }
    }

    private List<Especialidade> selecionarEspecialidades() {
        List<Especialidade> selecionadas = new ArrayList<>();
        view.info("Especialidades disponíveis:");
        Especialidade[] todas = Especialidade.values();
        for (int i = 0; i < todas.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, todas[i].getNome());
        }
        view.info("Digite os números separados por vírgula (ex: 1,3,5) ou 0 para nenhuma:");
        String input = view.lerStr("Especialidades: ");
        if (!input.equals("0") && !input.isEmpty()) {
            String[] nums = input.split(",");
            for (String num : nums) {
                try {
                    int idx = Integer.parseInt(num.trim()) - 1;
                    if (idx >= 0 && idx < todas.length) {
                        selecionadas.add(todas[idx]);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return selecionadas;
    }

    private void listarMedicos() {
        view.titulo("Lista de Médicos");
        List<Medico> medicos = gerente.getMedicos();
        if (medicos.isEmpty()) { view.info("Nenhum médico cadastrado."); return; }
        System.out.printf("%-15s %-25s %-12s%n", "CRM", "NOME", "VALOR");
        System.out.println("─".repeat(55));
        for (Medico m : medicos) {
            System.out.printf("%-15s %-25s R$ %.2f%n", m.getCrm(), m.getNome(), m.getValorConsultaBase());
        }
        System.out.println("Total: " + medicos.size());
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

    private void removerMedico() {
        String crm = view.lerStr("CRM do Médico: ");
        for (Medico m : gerente.getMedicos()) {
            if (m.getCrm().equalsIgnoreCase(crm)) {
                gerente.removerMedico(m);
                view.sucesso("Médico removido!");
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
                case 4: removerPaciente(); break;
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

            view.info("Tipo de Paciente: 1-EMERGÊNCIA, 2-PRIORIDADE, 3-ELETIVO");
            int tipoPac = view.lerInt("Tipo: ");
            TipoPaciente tp = TipoPaciente.values()[Math.min(tipoPac - 1, 2)];

            view.info("Tipo de Atendimento: 1-CONVÊNIO, 2-PARTICULAR");
            int tipoAt = view.lerInt("Tipo: ");
            TipoAtendimento ta = tipoAt == 1 ? TipoAtendimento.CONVENIO : TipoAtendimento.PARTICULAR;

            Paciente p = new Paciente(nome, cpf, data, email, tel, end,
                    gestante, espectro, pcd, lactante, crianca, tp, null, ta);
            repoPaciente.adicionarPaciente(p);
            view.sucesso("Paciente cadastrado com sucesso!");
        } catch (Exception e) {
            view.erro(e.getMessage());
        }
    }

    private void listarPacientes() {
        view.titulo("Lista de Pacientes");
        List<Paciente> pacientes = repoPaciente.listarPacientes();
        if (pacientes.isEmpty()) { view.info("Nenhum paciente cadastrado."); return; }
        System.out.printf("%-15s %-25s %-15s%n", "CPF", "NOME", "ATENDIMENTO");
        System.out.println("─".repeat(60));
        for (Paciente p : pacientes) {
            System.out.printf("%-15s %-25s %-15s%n", p.getCpf(), p.getNome(), p.getTipoAtendimento());
        }
        System.out.println("Total: " + pacientes.size());
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
                System.out.println("Idade: " + p.getIdade() + " anos");
                System.out.println("Tipo Paciente: " + p.getTipoPaciente());
                System.out.println("Tipo Atendimento: " + p.getTipoAtendimento());
                System.out.println("Prioridade: " + (p.isPrioridade() ? "Sim" : "Não"));
                return;
            }
        }
        view.erro("Paciente não encontrado!");
    }

    private void removerPaciente() {
        String cpf = view.lerStr("CPF do Paciente: ");
        for (Paciente p : repoPaciente.listarPacientes()) {
            if (p.getCpf().equals(cpf)) {
                repoPaciente.removerPaciente(p);
                view.sucesso("Paciente removido!");
                return;
            }
        }
        view.erro("Paciente não encontrado!");
    }

    public void carregarDados() {
        try {
            gerente.carregarMedicosCSV(ARQ_MEDICOS);
            repoPaciente.carregarPacientesCSV(ARQ_PACIENTES);
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