package clinica.app;

import clinica.servicos.GerenciadorFinanceiro;

import java.io.File;

public class Main {
    private static MenuView view = new MenuView();
    private static CadastroController cadastroCtrl = new CadastroController(view);
    private static AgendamentoController agendamentoCtrl;
    private static FinanceiroController financeiroCtrl;
    private static GerenciadorFinanceiro gerFinanceiro;

    public static void main(String[] args) {
        inicializar();

        int op;
        do {
            view.menuPrincipal();
            op = view.lerInt("Opção: ");

            switch (op) {
                case 1:
                    cadastroCtrl.executar();
                    atualizarConexoes();
                    break;
                case 2:
                    agendamentoCtrl.executar();
                    break;
                case 3:
                    financeiroCtrl.executar();
                    break;
                case 0:
                    finalizar();
                    break;
                default:
                    view.erro("Opção inválida!");
            }
        } while (op != 0);
    }

    private static void atualizarConexoes() {
        agendamentoCtrl.setRepositoriosCadastro(
                cadastroCtrl.getGerenteLogado(),
                cadastroCtrl.getRepoPaciente()
        );
        financeiroCtrl.setRepoConsulta(agendamentoCtrl.getRepoConsulta());
    }

    private static void inicializar() {
        File pastaData = new File("dados");
        if (!pastaData.exists()) {
            pastaData.mkdir();
        }

        gerFinanceiro = new GerenciadorFinanceiro();

        cadastroCtrl.carregarDados();
        agendamentoCtrl = new AgendamentoController(view);
        agendamentoCtrl.carregarDados();

        financeiroCtrl = new FinanceiroController(view, gerFinanceiro);
        financeiroCtrl.setRepoConsulta(agendamentoCtrl.getRepoConsulta());
        agendamentoCtrl.setRepositoriosCadastro(
                cadastroCtrl.getGerenteLogado(),
                cadastroCtrl.getRepoPaciente()
        );

        view.sucesso("Sistema iniciado com sucesso!");
        view.info("Clínica Bem Estar - Sistema de Gestão");
    }

    private static void finalizar() {
        view.salvando();
        cadastroCtrl.salvarDados();
        agendamentoCtrl.salvarDados();
        financeiroCtrl.salvarDados();
        view.sucesso("Todos os dados foram salvos!");
        view.encerrar();
    }
}
