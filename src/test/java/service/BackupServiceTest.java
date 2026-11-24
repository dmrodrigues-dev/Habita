
package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import util.ArmazenamentoException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.*;

class BackupServiceTest {

    @TempDir
    Path pastaTemporaria;

    private Path pastaDados;
    private Path pastaBackups;
    private BackupService service;

    @BeforeEach
    void preparar() throws IOException {

        pastaDados = pastaTemporaria.resolve("data");
        pastaBackups = pastaTemporaria.resolve("backups");

        Files.createDirectories(pastaDados);

        service = new BackupService(
                pastaDados,
                pastaBackups,
                arquivo -> "link-do-backup"
        );
    }

    @Test
    void deveCriarArquivoZip() throws IOException {

        Files.writeString(
                pastaDados.resolve("moradores.csv"),
                "id,nome\n1,João"
        );

        Path backup = service.criarBackupLocal();

        assertNotNull(backup);
        assertTrue(Files.isRegularFile(backup));
        assertTrue(backup.toString().endsWith(".zip"));

        try (ZipFile zip = new ZipFile(backup.toFile())) {
            assertTrue(zip.size() > 0);
        }
    }

    @Test
    void deveIncluirTodosOsArquivosDaPastaDados() throws IOException {

        Files.writeString(
                pastaDados.resolve("moradores.csv"),
                "id,nome\n1,João"
        );

        Files.writeString(
                pastaDados.resolve("apartamentos.csv"),
                "id,numero\n1,101"
        );

        Path subpasta = pastaDados.resolve("historico");
        Files.createDirectories(subpasta);

        Files.writeString(
                subpasta.resolve("acessos.csv"),
                "id,morador\n1,1"
        );

        Path backup = service.criarBackupLocal();

        try (ZipFile zip = new ZipFile(backup.toFile())) {
            assertNotNull(zip.getEntry("moradores.csv"));
            assertNotNull(zip.getEntry("apartamentos.csv"));
            assertNotNull(zip.getEntry("historico/acessos.csv"));
        }
    }

    @Test
    void deveCriarPastaBackupsQuandoNaoExistir() throws IOException {

        Files.writeString(
                pastaDados.resolve("moradores.csv"),
                "id,nome\n1,João"
        );

        assertFalse(Files.exists(pastaBackups));

        service.criarBackupLocal();

        assertTrue(Files.isDirectory(pastaBackups));
        assertEquals(1, Files.list(pastaBackups).count());
    }

    @Test
    void deveGerarNomeComDataEHorario() throws IOException {

        Files.writeString(
                pastaDados.resolve("moradores.csv"),
                "id,nome\n1,João"
        );

        Path backup = service.criarBackupLocal();

        String nomeArquivo = backup.getFileName().toString();

        assertTrue(nomeArquivo.matches(
                "backup_\\d{4}-\\d{2}-\\d{2}_\\d{6}\\.zip"
        ));
    }

    @Test
    void deveLancarExcecaoQuandoPastaDadosNaoExistir() {
        Path pastaInexistente = pastaTemporaria.resolve("dados-inexistentes");

        service = new BackupService(
                pastaInexistente,
                pastaBackups,
                arquivo -> "link-do-backup"
        );

        assertThrows(IllegalStateException.class, () -> service.criarBackupLocal());
    }

    @Test
    void deveCriarZipValidoQuandoPastaDadosEstiverVazia() throws IOException {
        Path backup = service.criarBackupLocal();

        assertNotNull(backup);
        assertTrue(Files.exists(backup));

        try (ZipFile zip = new ZipFile(backup.toFile())) {
            assertEquals(0, zip.size());
        }
    }

    @Test
    void deveLancarExcecaoQuandoDestinoBackupForInvalido() throws IOException {
        Path arquivo = pastaTemporaria.resolve("destino");
        Files.createFile(arquivo);

        Path destinoInvalido = arquivo.resolve("backups");

        service = new BackupService(
                pastaDados,
                destinoInvalido,
                arquivoBackup -> "link-do-backup"
        );

        assertThrows(IllegalStateException.class, () -> service.criarBackupLocal());
    }

    @Test
    void deveCriarBackupLocalEEnviarParaNuvem() throws IOException {
        Files.writeString(
                pastaDados.resolve("moradores.csv"),
                "id,nome\n1,João"
        );

        Path[] arquivoEnviado = new Path[1];

        service = new BackupService(
                pastaDados,
                pastaBackups,
                arquivo -> {
                    arquivoEnviado[0] = arquivo;
                    return "https://drive.google.com/backup";
                }
        );

        String link = service.criarBackupEEnviarParaNuvem();

        assertEquals("https://drive.google.com/backup", link);
        assertNotNull(arquivoEnviado[0]);
        assertTrue(Files.isRegularFile(arquivoEnviado[0]));
    }

    @Test
    void deveManterBackupLocalQuandoEnvioParaNuvemFalhar() throws IOException {
        Files.writeString(
                pastaDados.resolve("moradores.csv"),
                "id,nome\n1,João"
        );

        service = new BackupService(
                pastaDados,
                pastaBackups,
                arquivo -> {
                    throw new ArmazenamentoException("Falha no envio.");
                }
        );

        assertThrows(
                ArmazenamentoException.class,
                () -> service.criarBackupEEnviarParaNuvem()
        );

        assertTrue(Files.isDirectory(pastaBackups));

        try (var arquivos = Files.list(pastaBackups)) {
            assertEquals(1, arquivos.count());
        }
    }


}
