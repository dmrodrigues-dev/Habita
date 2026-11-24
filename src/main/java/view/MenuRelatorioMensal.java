
package view;

import model.RelatorioMensal;
import repository.CobrancaRepository;
import repository.ManutencaoRepository;
import repository.RegistroVisitaRepository;
import repository.ReservaRepository;
import service.RelatorioMensalService;

import java.time.YearMonth;
import java.util.Scanner;

public class MenuRelatorioMensal {

    private final Scanner scanner;
    private final RelatorioMensalService service;

    public MenuRelatorioMensal(Scanner scanner) {
        this.scanner = scanner;

        this.service = new RelatorioMensalService(
                new CobrancaRepository(),
                new ManutencaoRepository(),
                new ReservaRepository(),
                new RegistroVisitaRepository()
        );
    }

    public void exibir() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n========== RELATORIOS ==========");
            System.out.println("1 - Gerar relatorio mensal");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opcao: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());

                switch (opcao) {
                    case 1 -> gerar();
                    case 0 -> System.out.println("Voltando...");
                    default -> System.out.println("Opcao invalida.");
                }

            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void gerar() {
        System.out.print("Mes do relatorio (AAAA-MM): ");
        YearMonth mes = YearMonth.parse(scanner.nextLine().trim());

        RelatorioMensal relatorio = service.gerarRelatorioMensal(mes);

        System.out.println("\n========== RESUMO DO RELATORIO ==========");
        System.out.println("Mes: " + relatorio.mes());
        System.out.println("Arrecadacao: R$ " + relatorio.arrecadacao());
        System.out.println("Cobrancas pagas: " + relatorio.cobrancasPagas());
        System.out.println("Inadimplencia: R$ " + relatorio.inadimplencia());
        System.out.println("Cobrancas em atraso: " + relatorio.cobrancasEmAtraso());
        System.out.println("Gastos: R$ " + relatorio.gastos());
        System.out.println("Saldo: R$ " + relatorio.saldo());
        System.out.println("Chamados concluidos: " + relatorio.chamadosConcluidos().size());
        System.out.println("Chamados abertos no mes: " + relatorio.chamadosAbertos());
        System.out.println("Reservas: " + relatorio.totalReservas());
        System.out.println("Visitas: " + relatorio.totalVisitas());

        service.transformarRelatorioEmPdf(relatorio);

        System.out.println(
                "\nPDF gerado em: relatorios/relatorio_" + mes + ".pdf"
        );
    }
}
