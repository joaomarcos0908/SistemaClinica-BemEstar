package clinica.app;

import clinica.Modulo.financeiro.Pagamento;
import clinica.repositorio.RepositorioConsulta;
import clinica.servicos.GerenciadorFinanceiro;

import java.util.List;

public class FinanceiroController {
    private MenuView view;
    private GerenciadorFinanceiro gerenciador;
    private RepositorioConsulta repoConsulta;

    public FinanceiroController(MenuView view, GerenciadorFinanceiro gerenciador) {
        this.view = view;
        this.gerenciador = gerenciador;
    }

    public void setRepoConsulta(RepositorioConsulta repo) {
        this.repoConsulta = repo;
    }

    public void executar() {
        int op;
        do {
            view.menuFinanceiro();
            op = view.lerInt("Opção: ");
            switch (op) {
                case 1: menuPagamentos(); break;
                case 2: menuRelatorios(); break;
                case 0: view.voltando(); break;
                default: view.erro("Opção inválida!");
            }
        } while (op != 0);
    }

    private void menuPagamentos() {
        int op;
        do {
            view.menuPagamentos();
            op = view.lerInt("Opção: ");
            switch (op) {
                case 1: registrarPagamento(); break;
                case 2: consultarPagamento(); break;
                case 3: listarPagamentos(); break;
                case 4: listarPendentes(); break;
                case 5: cancelarPagamento(); break;
                case 0: view.voltando(); break;
                default: view.erro("Opção inválida!");
            }
        } while (op != 0);
    }

    private void registrarPagamento() {
        view.titulo("Registrar Pagamento");
        try {
            String idConsulta = view.lerStr("ID da Consulta: ");
            if (idConsulta == null || idConsulta.isEmpty()) {
                view.erro("ID da consulta inválido.");
                return;
            }

            Pagamento pagExistente = gerenciador.buscarPagamentoPorConsulta(idConsulta);
            if (pagExistente != null) {
                view.erro("Já existe um pagamento para esta consulta!");
                view.info("ID do Pagamento: " + pagExistente.getId());
                view.info("Status: " + pagExistente.getStatusPagamento());
                return;
            }

            double valorBase = view.lerDouble("Valor Base (R$): ");
            if (valorBase <= 0) {
                view.erro("Valor deve ser maior que zero!");
                return;
            }

            view.info("Tipo do Paciente:");
            view.info("  1. PARTICULAR (sem desconto)");
            view.info("  2. CONVÊNIO (20% desconto)");
            view.info("  3. VIP (30% desconto)");
            int tipoOp = view.lerInt("Tipo: ");
            String tipo;
            if (tipoOp == 1) tipo = "PARTICULAR";
            else if (tipoOp == 2) tipo = "CONVENIO";
            else if (tipoOp == 3) tipo = "VIP";
            else {
                view.erro("Tipo inválido! Usando PARTICULAR como padrão.");
                tipo = "PARTICULAR";
            }

            Pagamento pag = gerenciador.criarPagamento(idConsulta, valorBase, tipo);

            view.info("Forma de Pagamento:");
            view.info("  1. DINHEIRO");
            view.info("  2. CARTÃO CRÉDITO");
            view.info("  3. CARTÃO DÉBITO");
            view.info("  4. PIX");
            view.info("  5. CONVÊNIO");
            int formaOp = view.lerInt("Forma: ");

            Pagamento.FormaPagamento forma;
            if (formaOp < 1 || formaOp > 5) {
                view.erro("Forma inválida! Usando DINHEIRO como padrão.");
                forma = Pagamento.FormaPagamento.DINHEIRO;
            } else {
                forma = Pagamento.FormaPagamento.values()[formaOp - 1];
            }

            boolean sucesso = gerenciador.registrarPagamento(pag.getId(), forma);

            if (sucesso) {
                view.sucesso("Pagamento registrado com sucesso!");
                System.out.println("─".repeat(40));
                System.out.println("ID: " + pag.getId());
                System.out.printf("Valor Base:    R$ %.2f%n", pag.getValorBase());
                System.out.printf("Desconto:      %.0f%%%n", pag.getCalculadora().getDesconto());
                System.out.printf("Valor Final:   R$ %.2f%n", pag.getValorFinal());
                System.out.println("Forma:         " + forma);
                System.out.println("─".repeat(40));
            } else {
                view.erro("Erro ao registrar pagamento!");
            }
        } catch (Exception e) {
            view.erro("Erro ao processar pagamento: " + e.getMessage());
        }
    }

    private void consultarPagamento() {
        view.titulo("Consultar Pagamento");
        try {
            String id = view.lerStr("ID do Pagamento: ");
            Pagamento pag = gerenciador.buscarPagamentoPorId(id);
            if (pag == null) {
                view.erro("Pagamento não encontrado!");
                return;
            }

            System.out.println("\n" + "═".repeat(50));
            System.out.println("             DETALHES DO PAGAMENTO");
            System.out.println("═".repeat(50));
            System.out.println("ID:               " + pag.getId());
            System.out.println("Consulta:         " + pag.getConsultaId());
            System.out.printf("Valor Base:       R$ %.2f%n", pag.getValorBase());
            System.out.printf("Desconto:         %.0f%%%n", pag.getCalculadora().getDesconto());
            System.out.printf("Valor Final:      R$ %.2f%n", pag.getValorFinal());
            System.out.println("Tipo Paciente:    " + pag.getTipoPaciente());
            System.out.println("Status:           " + pag.getStatusPagamento());
            System.out.println("Forma Pagamento:  " +
                    (pag.getFormaPagamento() != null ? pag.getFormaPagamento() : "N/A"));

            if (pag.getDataPagamento() != null) {
                System.out.println("Data Pagamento:   " + pag.getDataPagamento().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            System.out.println("═".repeat(50));

        } catch (Exception e) {
            view.erro("Erro ao consultar: " + e.getMessage());
        }
    }

    private void listarPagamentos() {
        view.titulo("Lista de Pagamentos");
        view.info("Filtrar por:");
        view.info("  1. Todos");
        view.info("  2. Por Status");
        view.info("  3. Por Tipo de Paciente");
        int filtro = view.lerInt("Opção: ");

        List<Pagamento> pagamentos;

        switch (filtro) {
            case 2:
                view.info("Status: 1-PENDENTE 2-PAGO 3-CANCELADO 4-REEMBOLSADO");
                int statusOp = view.lerInt("Status: ");
                if (statusOp >= 1 && statusOp <= 4) {
                    Pagamento.StatusPagamento status = Pagamento.StatusPagamento.values()[statusOp - 1];
                    pagamentos = gerenciador.listarPagamentosPorStatus(status);
                } else {
                    view.erro("Status inválido! Mostrando todos.");
                    pagamentos = gerenciador.listarPagamentos();
                }
                break;
            case 3:
                view.info("Tipo: 1-PARTICULAR 2-CONVENIO 3-VIP");
                int tipoOp = view.lerInt("Tipo: ");
                String tipo = tipoOp == 1 ? "PARTICULAR" : tipoOp == 2 ? "CONVENIO" : "VIP";
                pagamentos = gerenciador.listarPagamentosPorTipo(tipo);
                break;
            default:
                pagamentos = gerenciador.listarPagamentos();
        }

        if (pagamentos.isEmpty()) {
            view.info("Nenhum pagamento encontrado.");
            return;
        }

        System.out.printf("%-18s %-15s %-12s %-12s %-15s%n",
                "ID", "CONSULTA", "VALOR", "TIPO", "STATUS");
        System.out.println("─".repeat(75));

        double total = 0;
        for (Pagamento p : pagamentos) {
            System.out.printf("%-18s %-15s R$ %-9.2f %-12s %-15s%n",
                    p.getId().length() > 18 ? p.getId().substring(0, 15) + "..." : p.getId(),
                    p.getConsultaId().length() > 15 ? p.getConsultaId().substring(0, 12) + "..." : p.getConsultaId(),
                    p.getValorFinal(),
                    p.getTipoPaciente(),
                    p.getStatusPagamento());

            if (p.getStatusPagamento() == Pagamento.StatusPagamento.PAGO) {
                total += p.getValorFinal();
            }
        }

        System.out.println("─".repeat(75));
        System.out.println("Total de pagamentos: " + pagamentos.size());
        System.out.printf("Total pago: R$ %.2f%n", total);
    }

    private void listarPendentes() {
        view.titulo("Pagamentos Pendentes");
        List<Pagamento> pendentes = gerenciador.listarPagamentosPorStatus(
                Pagamento.StatusPagamento.PENDENTE);

        if (pendentes.isEmpty()) {
            view.info("Nenhum pagamento pendente.");
            return;
        }

        double total = 0;
        System.out.printf("%-18s %-12s %-15s%n", "ID", "VALOR", "CONSULTA");
        System.out.println("─".repeat(50));
        for (Pagamento p : pendentes) {
            System.out.printf("%-18s R$ %-9.2f %-15s%n",
                    p.getId().length() > 18 ? p.getId().substring(0, 15) + "..." : p.getId(),
                    p.getValorFinal(),
                    p.getConsultaId());
            total += p.getValorFinal();
        }
        System.out.println("─".repeat(50));
        System.out.printf("Total Pendente: R$ %.2f (%d pagamentos)%n", total, pendentes.size());
    }

    private void cancelarPagamento() {
        view.titulo("Cancelar/Reembolsar Pagamento");
        try {
            String id = view.lerStr("ID do Pagamento: ");
            Pagamento pag = gerenciador.buscarPagamentoPorId(id);
            if (pag == null) {
                view.erro("Pagamento não encontrado!");
                return;
            }

            view.info("Pagamento encontrado:");
            System.out.println("  Consulta: " + pag.getConsultaId());
            System.out.printf("  Valor: R$ %.2f%n", pag.getValorFinal());
            System.out.println("  Status: " + pag.getStatusPagamento());

            if (pag.getStatusPagamento() == Pagamento.StatusPagamento.PAGO) {
                if (view.lerBool("Este pagamento foi realizado. Deseja reembolsar?")) {
                    pag.reembolsar();
                    gerenciador.salvarPagamentos();
                    view.sucesso("Pagamento reembolsado!");
                }
            } else if (pag.getStatusPagamento() == Pagamento.StatusPagamento.PENDENTE) {
                if (view.lerBool("Confirma cancelamento deste pagamento?")) {
                    pag.cancelarPagamento();
                    gerenciador.salvarPagamentos();
                    view.sucesso("Pagamento cancelado!");
                }
            } else {
                view.erro("Este pagamento não pode ser modificado!");
            }

        } catch (Exception e) {
            view.erro("Erro: " + e.getMessage());
        }
    }

    private void menuRelatorios() {
        int op;
        do {
            view.menuRelatorios();
            op = view.lerInt("Opção: ");
            switch (op) {
                case 1: relatorioConsultas(); break;
                case 2: relatorioFinanceiro(); break;
                case 3: relatorioDetalhado(); break;
                case 4: exportarRelatorio(); break;
                case 0: view.voltando(); break;
                default: view.erro("Opção inválida!");
            }
        } while (op != 0);
    }

    private void relatorioConsultas() {
        view.titulo("Relatório de Consultas");
        if (repoConsulta == null) {
            view.erro("Módulo de consultas não conectado!");
            return;
        }

        List<clinica.sistema.Consulta> consultas = repoConsulta.listarConsultas();
        int agendadas = 0, confirmadas = 0, realizadas = 0, canceladas = 0, emergenciais = 0;

        for (clinica.sistema.Consulta c : consultas) {
            switch (c.getStatus()) {
                case AGENDADA: agendadas++; break;
                case CONFIRMADA: confirmadas++; break;
                case REALIZADA: realizadas++; break;
                case CANCELADA: canceladas++; break;
                case EMERGENCIAL: emergenciais++; break;
            }
        }

        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║      RELATÓRIO DE CONSULTAS            ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.printf("║  Total de Consultas:     %-14d ║%n", consultas.size());
        System.out.println("╠════════════════════════════════════════╣");
        System.out.printf("║  Agendadas:              %-14d ║%n", agendadas);
        System.out.printf("║  Confirmadas:            %-14d ║%n", confirmadas);
        System.out.printf("║  Realizadas:             %-14d ║%n", realizadas);
        System.out.printf("║  Canceladas:             %-14d ║%n", canceladas);
        System.out.printf("║  Emergenciais:           %-14d ║%n", emergenciais);
        System.out.println("╚════════════════════════════════════════╝");
    }

    private void relatorioFinanceiro() {
        view.titulo("Relatório Financeiro");

        double totalGeral = gerenciador.calcularFaturamentoTotal();
        double totalTaxas = gerenciador.calcularTotalTaxas();
        var porTipo = gerenciador.calcularFaturamentoPorTipo();

        List<Pagamento> todos = gerenciador.listarPagamentos();
        long pagos = todos.stream()
                .filter(p -> p.getStatusPagamento() == Pagamento.StatusPagamento.PAGO)
                .count();
        long pendentes = todos.stream()
                .filter(p -> p.getStatusPagamento() == Pagamento.StatusPagamento.PENDENTE)
                .count();

        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║      RELATÓRIO FINANCEIRO              ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.printf("║  Total de Pagamentos:        %-10d ║%n", todos.size());
        System.out.printf("║  Pagamentos Realizados:      %-10d ║%n", pagos);
        System.out.printf("║  Pagamentos Pendentes:       %-10d ║%n", pendentes);
        System.out.println("╠════════════════════════════════════════╣");
        System.out.printf("║  Faturamento Total:   R$ %-14.2f ║%n", totalGeral);
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  Por Tipo de Paciente:                 ║");
        System.out.printf("║    PARTICULAR:        R$ %-14.2f ║%n",
                porTipo.getOrDefault("PARTICULAR", 0.0));
        System.out.printf("║    CONVÊNIO:          R$ %-14.2f ║%n",
                porTipo.getOrDefault("CONVENIO", 0.0));
        System.out.printf("║    VIP:               R$ %-14.2f ║%n",
                porTipo.getOrDefault("VIP", 0.0));
        System.out.println("╠════════════════════════════════════════╣");
        System.out.printf("║  Taxas de Cancelamento: R$ %-12.2f ║%n", totalTaxas);
        System.out.println("╚════════════════════════════════════════╝");
    }

    private void relatorioDetalhado() {
        view.titulo("Relatório Detalhado");
        String relatorio = gerenciador.gerarRelatorioFinanceiroGeral();
        System.out.println(relatorio);
        if (view.lerBool("Deseja salvar este relatório?")) {
            boolean sucesso = gerenciador.salvarRelatorio("financeiro_detalhado", relatorio);
            if (sucesso) view.sucesso("Relatório salvo na pasta 'relatorios/'");
            else view.erro("Erro ao salvar relatório!");
        }
    }

    private void exportarRelatorio() {
        view.titulo("Exportar Relatório");
        view.info("1. Financeiro Geral");
        view.info("2. Por Tipo de Paciente");
        view.info("3. Completo do Sistema");

        int op = view.lerInt("Tipo: ");
        String relatorio = "";
        String nomeArquivo = "";

        switch (op) {
            case 1:
                relatorio = gerenciador.gerarRelatorioFinanceiroGeral();
                nomeArquivo = "financeiro_geral";
                break;
            case 2:
                relatorio = gerenciador.gerarRelatorioFaturamentoPorTipo();
                nomeArquivo = "faturamento_tipo";
                break;
            case 3:
                relatorio = gerenciador.gerarRelatorioCompleto();
                nomeArquivo = "completo";
                break;
            default:
                view.erro("Opção inválida!");
                return;
        }

        boolean sucesso = gerenciador.salvarRelatorio(nomeArquivo, relatorio);
        if (sucesso) view.sucesso("Relatório exportado para a pasta 'relatorios/'");
        else view.erro("Erro ao exportar relatório!");
    }

    public GerenciadorFinanceiro getGerenciador() { return gerenciador; }

    public void salvarDados() {
        gerenciador.salvarPagamentos();
        gerenciador.salvarTaxas();
    }
}
