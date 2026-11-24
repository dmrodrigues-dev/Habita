
package service;

import util.ArmazenamentoNuvem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupService {

    private final Path pastaDados;
    private final Path pastaBackups;
    private final ArmazenamentoNuvem armazenamentoNuvem;

    public BackupService(
            Path pastaDados,
            Path pastaBackups,
            ArmazenamentoNuvem armazenamentoNuvem
    ) {
        this.pastaDados = pastaDados;
        this.pastaBackups = pastaBackups;
        this.armazenamentoNuvem = armazenamentoNuvem;
    }

    // Cria arquivo compactado .zip mas não envia para nuvem
    public Path criarBackupLocal() {

        try {
            Files.createDirectories(pastaBackups);

            String dataHora = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss")
            );

            Path arquivoBackup = pastaBackups.resolve(
                    "backup_" + dataHora + ".zip"
            );

            try (ZipOutputStream zip = new ZipOutputStream(
                    Files.newOutputStream(arquivoBackup)
            )) {
                try (var arquivos = Files.walk(pastaDados)) {
                    arquivos
                            .filter(Files::isRegularFile)
                            .forEach(arquivo -> {
                                try {
                                    String nomeArquivo = pastaDados
                                            .relativize(arquivo)
                                            .toString()
                                            .replace("\\", "/");

                                    zip.putNextEntry(new ZipEntry(nomeArquivo));
                                    Files.copy(arquivo, zip);
                                    zip.closeEntry();

                                } catch (IOException e) {
                                    throw new IllegalStateException(
                                            "Erro ao adicionar arquivo ao backup.", e
                                    );
                                }
                            });
                }
            }

            return arquivoBackup;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao criar backup local.", e
            );
        }
    }

    // Cria arquivo .zip e envia para nuvem
    public String criarBackupEEnviarParaNuvem() {
        Path arquivoBackup = criarBackupLocal();

        return armazenamentoNuvem.enviarParaNuvem(arquivoBackup);
    }
}
