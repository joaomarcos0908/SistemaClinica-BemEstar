package clinica.app;

import clinica.Modulo.financeiro.*;
import clinica.repositorio.RepositorioConsulta;
import clinica.servicos.GerenciadorFinanceiro;
import clinica.sistema.*;

import java.util.List;
import java.util.Map;

public class FinanceiroController {
    private MenuView view;
    private GerenciadorFinanceiro gerenciador;
    private RepositorioConsulta repoConsulta;

    public FinanceiroController(MenuView view) {
        this.view = view;
        this.gerenciador = new GerenciadorFinanceiro();
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
                case 2: listarPagamentos(); break;
                case 3: listarPendentes(); break;
                case 0: view.voltando(); break;
                default: view.erro("Opção inválida!");
            }
        } while (op != 0);
    }

    private void registrarPagamento() {
        view.titulo("Registrar Pagamento");
        try {
            String idConsulta = view.lerStr("ID da Consulta: ");
            double valorBase = view.lerDouble("Valor Base (R$): ");

            view.info("Tipo do Paciente:");
            view.info("  1. PARTICULAR (sem desconto)");
            view.info("  2. CONVÊNIO (20% desconto)");
            view.info("  3. VIP (30% desconto)");
            int tipoOp = view.lerInt("Tipo: ");
            String tipo = tipoOp == 2 ? "CONVENIO" : tipoOp == 3 ? "VIP" : "PARTICULAR";

            Pagamento pag = gerenciador.criarPagamento(idConsulta, valorBase, tipo);

            view.info("Forma de Pagamento:");
            view.info("  1. DINHEIRO");
            view.info("  2. CARTÃO CRÉDITO");
            view.info("  3. CARTÃO DÉBITO");
            view.info("  4. PIX");
            view.info("  5. CONVÊNIO");
            int formaOp = view.lerInt("Forma: ");
            Pagamento.FormaPagamento forma = Pagamento.FormaPagamento.values()[Math.min(formaOp - 1, 4)];

            gerenciador.registrarPagamento(pag.getId(), forma);

            view.sucesso("Pagamento registrado com sucesso!");
            System.out.println("─".repeat(40));
            System.out.printf("Valor Base:    R$ %.2f%n", pag.getValorBase());
            System.out.printf("Desconto:      %.0f%%%n", pag.getCalculadora().getDesconto());
            System.out.printf("Valor Final:   R$ %.2f%n", pag.getValorFinal());
            System.out.println("─".repeat(40));
        } catch (Exception e) {
            view.erro(e.getMessage());
        }
    }

    private void listarPagamentos() {
        view.titulo("Lista de Pagamentos");
        List<Pagamento> pagamentos = gerenciador.listarPagamentos();
        if (pagamentos.isEmpty()) { view.info("Nenhum pagamento registrado."); return; }

        System.out.printf("%-15s %-12s %-12s %-12s%n", "ID", "VALOR", "TIPO", "STATUS");
        System.out.println("─".repeat(55));
        for (Pagamento p : pagamentos) {
            System.out.printf("%-15s R$ %-10.2f %-12s %-12s%n",
                    p.getId(), p.getValorFinal(), p.getTipoPaciente(), p.getStatusPagamento());
        }
        System.out.println("Total: " + pagamentos.size());
    }

    private void listarPendentes() {
        view.titulo("Pagamentos Pendentes");
        List<Pagamento> pendentes = gerenciador.listarPagamentosPorStatus(Pagamento.StatusPagamento.PENDENTE);
        if (pendentes.isEmpty()) { view.info("Nenhum pagamento pendente."); return; }

        double total = 0;
        System.out.printf("%-15s %-12s %-15s%n", "ID", "VALOR", "CONSULTA");
        System.out.println("─".repeat(45));
        for (Pagamento p : pendentes) {
            System.out.printf("%-15s R$ %-10.2f %-15s%n", p.getId(), p.getValorFinal(), p.getConsultaId());
            total += p.getValorFinal();
        }
        System.out.println("─".repeat(45));
        System.out.printf("Total Pendente: R$ %.2f%n", total);
    }

    private void menuRelatorios() {
        int op;
        do {
            view.menuRelatorios();
            op = view.lerInt("Opção: ");
            switch (op) {
                case 1: relatorioConsultas(); break;
                case 2: relatorioFinanceiro(); break;
                case 0: view.voltando(); break;
                default: view.erro("Opção inválida!");
            }
        } while (op != 0);
    }

    private void relatorioConsultas() {
        view.titulo("Relatório de Consultas");
        if (repoConsulta == null) { view.erro("Módulo de consultas não conectado!"); return; }

        List<Consulta> consultas = repoConsulta.listarConsultas();
        int agendadas = 0, confirmadas = 0, realizadas = 0, canceladas = 0, emergenciais = 0;

        for (Consulta c : consultas) {
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
        Map<String, Double> porTipo = gerenciador.calcularFaturamentoPorTipo();

        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║      RELATÓRIO FINANCEIRO              ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.printf("║  Faturamento Total:   R$ %-14.2f ║%n", totalGeral);
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  Por Tipo de Paciente:                 ║");
        System.out.printf("║    PARTICULAR:        R$ %-14.2f ║%n", porTipo.getOrDefault("PARTICULAR", 0.0));
        System.out.printf("║    CONVÊNIO:          R$ %-14.2f ║%n", porTipo.getOrDefault("CONVENIO", 0.0));
        System.out.printf("║    VIP:               R$ %-14.2f ║%n", porTipo.getOrDefault("VIP", 0.0));
        System.out.println("╠════════════════════════════════════════╣");
        System.out.printf("║  Taxas de Cancelamento: R$ %-12.2f ║%n", totalTaxas);
        System.out.println("╚════════════════════════════════════════╝");
    }

    public GerenciadorFinanceiro getGerenciador() { return gerenciador; }
}