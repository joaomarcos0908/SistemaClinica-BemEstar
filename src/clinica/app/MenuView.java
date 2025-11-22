package clinica.app;

import clinica.sistema.*;
import clinica.pessoas.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class MenuView {
    private Scanner scanner;
    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public MenuView() {
        this.scanner = new Scanner(System.in);
    }
    public void menuPrincipal() {
        System.out.println("╔═════════════════════════════════════════════════╗");
        System.out.println("║      SISTEMA CLÍNICA BEM ESTAR                  ║");
        System.out.println("╠═════════════════════════════════════════════════╣");
        System.out.println("║  1. Cadastros (Médicos/Pacientes)               ║");
        System.out.println("║  2. Agendamentos (Horários/Consultas)           ║");
        System.out.println("║  3. Financeiro (Pagamentos/Relatórios)          ║");
        System.out.println("║  0. Sair e Salvar                               ║");
        System.out.println("╚═════════════════════════════════════════════════╝");
    }
    public void menuCadastros() {
        System.out.println("┌─────────────────────────────────────┐");
        System.out.println("│         MÓDULO DE CADASTROS         │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│  1. Gerenciar Médicos               │");
        System.out.println("│  2. Gerenciar Pacientes             │");
        System.out.println("│  0. Voltar                          │");
        System.out.println("└─────────────────────────────────────┘");
    }
    public void menuMedicos() {
        System.out.println("┌─────────────────────────────────────┐");
        System.out.println("│        GERENCIAR MÉDICOS            │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│  1. Cadastrar Médico                │");
        System.out.println("│  2. Listar Médicos                  │");
        System.out.println("│  3. Buscar Médico por CRM           │");
        System.out.println("│  4. Remover Médico                  │");
        System.out.println("│  0. Voltar                          │");
        System.out.println("└─────────────────────────────────────┘");
    }
    public void menuPacientes() {
        System.out.println("┌─────────────────────────────────────┐");
        System.out.println("│       GERENCIAR PACIENTES           │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│  1. Cadastrar Paciente              │");
        System.out.println("│  2. Listar Pacientes                │");
        System.out.println("│  3. Buscar Paciente por CPF         │");
        System.out.println("│  4. Remover Paciente                │");
        System.out.println("│  0. Voltar                          │");
        System.out.println("└─────────────────────────────────────┘");
    }
    public void menuAgendamentos() {
        System.out.println("┌─────────────────────────────────────┐");
        System.out.println("│       MÓDULO DE AGENDAMENTOS        │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│  1. Gerenciar Horários              │");
        System.out.println("│  2. Gerenciar Consultas             │");
        System.out.println("│  3. Cancelamentos                   │");
        System.out.println("│  0. Voltar                          │");
        System.out.println("└─────────────────────────────────────┘");
    }
    public void menuHorarios() {
        System.out.println("┌─────────────────────────────────────┐");
        System.out.println("│        GERENCIAR HORÁRIOS           │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│  1. Cadastrar Horário               │");
        System.out.println("│  2. Listar Todos os Horários        │");
        System.out.println("│  3. Listar Horários Disponíveis     │");
        System.out.println("│  4. Buscar Horário por ID           │");
        System.out.println("│  5. Remover Horário                 │");
        System.out.println("│  0. Voltar                          │");
        System.out.println("└─────────────────────────────────────┘");
    }
    public void menuConsultas() {
        System.out.println("┌─────────────────────────────────────┐");
        System.out.println("│        GERENCIAR CONSULTAS          │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│  1. Agendar Consulta                │");
        System.out.println("│  2. Agendar Emergencial             │");
        System.out.println("│  3. Listar Consultas                │");
        System.out.println("│  4. Buscar Consulta por ID          │");
        System.out.println("│  5. Confirmar Consulta              │");
        System.out.println("│  6. Marcar como Realizada           │");
        System.out.println("│  0. Voltar                          │");
        System.out.println("└─────────────────────────────────────┘");
    }

    public void menuCancelamentos() {
        System.out.println("┌─────────────────────────────────────┐");
        System.out.println("│          CANCELAMENTOS              │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│  1. Cancelar Consulta               │");
        System.out.println("│  2. Listar Canceladas               │");
        System.out.println("│  0. Voltar                          │");
        System.out.println("└─────────────────────────────────────┘");
    }


    public void menuFinanceiro() {
        System.out.println("┌─────────────────────────────────────┐");
        System.out.println("│        MÓDULO FINANCEIRO            │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│  1. Pagamentos                      │");
        System.out.println("│  2. Relatórios                      │");
        System.out.println("│  0. Voltar                          │");
        System.out.println("└─────────────────────────────────────┘");
    }

    public void menuPagamentos() {
        System.out.println("┌─────────────────────────────────────┐");
        System.out.println("│           PAGAMENTOS                │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│  1. Registrar Pagamento             │");
        System.out.println("│  2. Listar Pagamentos               │");
        System.out.println("│  3. Pagamentos Pendentes            │");
        System.out.println("│  0. Voltar                          │");
        System.out.println("└─────────────────────────────────────┘");
    }

    public void menuRelatorios() {
        System.out.println("┌─────────────────────────────────────┐");
        System.out.println("│           RELATÓRIOS                │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│  1. Relatório de Consultas          │");
        System.out.println("│  2. Relatório Financeiro            │");
        System.out.println("│  0. Voltar                          │");
        System.out.println("└─────────────────────────────────────┘");
    }


    public void tabelaHorarios(List<Horario> lista) {
        System.out.println("\n═══════════════════ HORÁRIOS ═══════════════════");
        if (lista.isEmpty()) { info("Nenhum horário cadastrado."); return; }
        System.out.printf("%-5s %-18s %-18s %-8s %-10s%n", "ID", "INÍCIO", "FIM", "MÉDICO", "DISPON.");
        System.out.println("─".repeat(65));
        for (Horario h : lista) {
            System.out.printf("%-5d %-18s %-18s %-8d %-10s%n",
                    h.getId(), h.getHoraInicio().format(DTF), h.getHoraFim().format(DTF),
                    h.getIdMedico(), h.isDisponivel() ? "Sim" : "Não");
        }
        System.out.println("Total: " + lista.size());
    }

    public void tabelaConsultas(List<Consulta> lista) {
        System.out.println("\n═══════════════════ CONSULTAS ═══════════════════");
        if (lista.isEmpty()) { info("Nenhuma consulta cadastrada."); return; }
        System.out.printf("%-5s %-10s %-8s %-12s %-15s%n", "ID", "PACIENTE", "MÉDICO", "EMERGÊNCIA", "STATUS");
        System.out.println("─".repeat(55));
        for (Consulta c : lista) {
            System.out.printf("%-5d %-10d %-8d %-12s %-15s%n",
                    c.getId(), c.getIdPaciente(), c.getIdMedico(),
                    c.isEmergencial() ? "Sim" : "Não", c.getStatus());
        }
        System.out.println("Total: " + lista.size());
    }
    public void sucesso(String msg) { System.out.println(msg); }
    public void erro(String msg) { System.out.println(msg); }
    public void info(String msg) { System.out.println( msg); }
    public void titulo(String t) { System.out.println("--- " + t.toUpperCase() + " ---"); }
    public void voltando() { System.out.println("Voltando..."); }
    public void salvando() { System.out.println("Salvando dados..."); }
    public void encerrar() { System.out.println(" Sistema encerrado. Até logo!"); }


    public int lerInt(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) { erro("Digite um número válido!"); }
        }
    }

    public double lerDouble(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) { erro("Digite um valor válido!"); }
        }
    }

    public String lerStr(String msg) {
        System.out.print(msg);
        return scanner.nextLine().trim();
    }

    public boolean lerBool(String msg) {
        System.out.print(msg + " (S/N): ");
        String r = scanner.nextLine().trim().toUpperCase();
        return r.equals("S") || r.equals("SIM");
    }

    public LocalDate lerData() {
        while (true) {
            try {
                String input = lerStr("Data (dd/MM/yyyy): ");
                return LocalDate.parse(input, DF);
            } catch (DateTimeParseException e) { erro("Formato inválido! Use dd/MM/yyyy"); }
        }
    }

    public LocalDateTime lerDataHora() {
        while (true) {
            try {
                String input = lerStr("Data e Hora (dd/MM/yyyy HH:mm): ");
                return LocalDateTime.parse(input, DTF);
            } catch (DateTimeParseException e) { erro("Formato inválido! Use dd/MM/yyyy HH:mm"); }
        }
    }
}