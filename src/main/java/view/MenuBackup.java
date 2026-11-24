
package view;

import service.BackupService;
import util.ArmazenamentoException;
import util.GoogleDriveStorage;

import java.nio.file.Path;
import java.util.Scanner;

public class MenuBackup {

    private final Scanner scanner;
    private final BackupService service;

    public MenuBackup(Scanner scanner) {
        this.scanner = scanner;

        this.service = new BackupService(
                Path.of("data"),
                Path.of("backups"),
                new GoogleDriveStorage()
        );
    }

    public void exibir() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n========== BACKUP ==========");
            System.out.println("1 - Criar backup e enviar para o Google Drive");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opcao: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());

                switch (opcao) {
                    case 1 -> realizarBackup();
                    case 0 -> System.out.println("Voltando...");
                    default -> System.out.println("Opcao invalida.");
                }

            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void realizarBackup() {
        try {
            String link = service.criarBackupEEnviarParaNuvem();

            System.out.println("\nBackup realizado com sucesso!");
            System.out.println("Copia local: pasta backups/");
            System.out.println("Google Drive: " + link);

        } catch (ArmazenamentoException e) {
            System.out.println("\nNao foi possivel enviar o backup para o Google Drive.");
            System.out.println("O arquivo local foi preservado na pasta backups/");
            System.out.println("Detalhes: " + e.getMessage());

        } catch (IllegalStateException e) {
            System.out.println("\nNao foi possivel criar o backup local.");
            System.out.println("Detalhes: " + e.getMessage());
        }
    }
}
