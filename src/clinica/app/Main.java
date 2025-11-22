package clinica.app;

import java.io.File;

public class Main {
    private static MenuView view = new MenuView();
    private static CadastroController cadastroCtrl = new CadastroController(view);
    private static AgendamentoController agendamentoCtrl = new AgendamentoController(view);
    private static FinanceiroController financeiroCtrl = new FinanceiroController(view);

    public static void main(String[] args) {
        inicializar();

        int op;
        do {
            view.menuPrincipal();
            op = view.lerInt("Opção: ");

            switch (op) {
                case 1: cadastroCtrl.executar(); break;
                case 2: agendamentoCtrl.executar(); break;
                case 3: financeiroCtrl.executar(); break;
                case 0: finalizar(); break;
                default: view.erro("Opção inválida!");
            }
        } while (op != 0);
    }

    private static void inicializar() {

        File pastaData = new File("dados");
        if (!pastaData.exists()) {
            pastaData.mkdir();
        }


        cadastroCtrl.carregarDados();
        agendamentoCtrl.carregarDados();


        financeiroCtrl.setRepoConsulta(agendamentoCtrl.getRepoConsulta());

        view.sucesso("Sistema iniciado com sucesso!");
        view.info("Clínica Bem Estar - Sistema de Gestão");
    }

    private static void finalizar() {
        view.salvando();
        cadastroCtrl.salvarDados();
        agendamentoCtrl.salvarDados();
        view.sucesso("Todos os dados foram salvos!");
        view.encerrar();
    }
}