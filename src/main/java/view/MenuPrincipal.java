package view;

import java.util.Scanner;

public class MenuPrincipal {

    public MenuPrincipal() {
        Scanner scanner = new Scanner(System.in);

        int opcao = -1;

        while (opcao != 0) {

            System.out.println("\n======================================");
            System.out.println("                HABITA");
            System.out.println("       CONDOMINIO VISTA ALEGRE");
            System.out.println("======================================");
            System.out.println("1 - Apartamentos");
            System.out.println("2 - Moradores");
            System.out.println("3 - Controle de acesso de moradores");
            System.out.println("4 - Visitantes");
            System.out.println("5 - Reservas");
            System.out.println("6 - Manutencao");
            System.out.println("7 - Financeiro");
            System.out.println("8 - Relatorios mensais");
            System.out.println("9 - Backup");
            System.out.println("0 - Encerrar sistema");
            System.out.println("======================================");
            System.out.print("Escolha uma opcao: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());

                switch (opcao) {

                    case 1 -> new MenuApartamento(scanner).exibir();

                    case 2 -> new MenuMorador(scanner).exibir();

                    case 3 -> new MenuControleAcessoMorador(scanner).exibir();

                    case 4 -> new MenuVisitante(scanner).exibir();

                    case 5 -> new MenuReserva(scanner).exibir();

                    case 6 -> new MenuManutencao(scanner).exibir();

                    case 7 -> new MenuCobranca(scanner).exibir();

                    case 8 -> new MenuRelatorioMensal(scanner).exibir();

                    case 9 -> new MenuBackup(scanner).exibir();

                    case 0 -> System.out.println(
                            "Encerrando o sistema. Ate mais!"
                    );

                    default -> System.out.println("Opcao invalida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Digite uma opcao numerica valida.");

            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }

        // Não fechar o Scanner antes de encerrar o programa.
        scanner.close();
    }
}
