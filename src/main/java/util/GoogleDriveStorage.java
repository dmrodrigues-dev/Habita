package util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.io.Reader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.List;

public class GoogleDriveStorage implements ArmazenamentoNuvem {

    private static final String NOME_APP = "Habita";
    private static final String PASTA_NO_DRIVE = "Backup Vista Alegre";
    private static final String MIME_PASTA = "application/vnd.google-apps.folder";
    private static final List<String> ESCOPOS = List.of(DriveScopes.DRIVE_FILE);
    private static final JsonFactory JSON = GsonFactory.getDefaultInstance();

    private final Path arquivoCredenciais;
    private final Path pastaTokens;
    private Drive servico; // criado no primeiro uso

    public GoogleDriveStorage() {
        this(Path.of("credentials", "credentials.json"), Path.of("credentials", "tokens"));
    }

    public GoogleDriveStorage(Path arquivoCredenciais, Path pastaTokens) {
        this.arquivoCredenciais = arquivoCredenciais;
        this.pastaTokens = pastaTokens;
    }

    /** Útil para o menu avisar "Drive não configurado" sem tentar autenticar. */
    public boolean credenciaisDisponiveis() {
        return Files.isRegularFile(arquivoCredenciais);
    }

    @Override
    public String enviarParaNuvem(Path arquivo) {
        if (arquivo == null || !Files.isRegularFile(arquivo)) {
            throw new ArmazenamentoException("Arquivo de backup não encontrado: " + arquivo);
        }

        try {
            Drive drive = obterServico();

            String idPasta = obterPastaBackup(drive);

            File metadados = new File()
                    .setName(arquivo.getFileName().toString())
                    .setParents(List.of(idPasta));

            FileContent conteudo = new FileContent("application/zip", arquivo.toFile());

            File enviado = drive.files()
                    .create(metadados, conteudo)
                    .setFields("id, name, webViewLink")
                    .execute();

            return enviado.getWebViewLink();

        } catch (GeneralSecurityException | IOException e) {
            throw tratarErro(e);
        }
    }

    // ---------- autenticação ----------

    private Drive obterServico() throws GeneralSecurityException, IOException {
        if (servico == null) {
            if (!credenciaisDisponiveis()) {
                throw new ArmazenamentoException("Credenciais do Google não encontradas em "
                        + arquivoCredenciais + ". Veja a seção 'Backup no Google Drive' do README.");
            }
            NetHttpTransport transporte = GoogleNetHttpTransport.newTrustedTransport();
            Credential credencial = autorizar(transporte);
            servico = new Drive.Builder(transporte, JSON, credencial)
                    .setApplicationName(NOME_APP)
                    .build();
        }
        return servico;
    }

    private Credential autorizar(NetHttpTransport transporte) throws IOException {
        GoogleClientSecrets segredos;
        try (Reader leitor = Files.newBufferedReader(arquivoCredenciais)) {
            segredos = GoogleClientSecrets.load(JSON, leitor);
        } catch (IllegalArgumentException e) {
            throw new ArmazenamentoException(
                    "O arquivo " + arquivoCredenciais + " não é um credentials.json válido.", e);
        }

        GoogleAuthorizationCodeFlow fluxo = new GoogleAuthorizationCodeFlow.Builder(
                transporte, JSON, segredos, ESCOPOS)
                .setDataStoreFactory(new FileDataStoreFactory(pastaTokens.toFile()))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receptor = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(fluxo, receptor).authorize("user");
    }

    private ArmazenamentoException tratarErro(Exception e) {
        if (e instanceof TokenResponseException) {
            return new ArmazenamentoException("A autorização do Google expirou ou foi revogada. "
                    + "Apague a pasta " + pastaTokens + " e tente de novo.", e);
        }
        if (e instanceof GoogleJsonResponseException) {
            return new ArmazenamentoException("Erro do Google Drive (HTTP " + ((GoogleJsonResponseException) e).getStatusCode() + ").", e);
        }
        if (e instanceof UnknownHostException || e instanceof ConnectException
                || e instanceof SocketTimeoutException) {
            return new ArmazenamentoException("Sem conexão com a internet. O backup local foi mantido.", e);
        }
        return new ArmazenamentoException("Falha ao enviarParaNuvem para o Google Drive: " + e.getMessage(), e);
    }

    private String obterPastaBackup(Drive drive) throws IOException {
        FileList resultado = drive.files()
                .list()
                .setQ("name = '" + PASTA_NO_DRIVE + "'"
                        + " and mimeType = '" + MIME_PASTA + "'"
                        + " and trashed = false")
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute();

        if (!resultado.getFiles().isEmpty()) {
            return resultado.getFiles().get(0).getId();
        }

        File pasta = new File()
                .setName(PASTA_NO_DRIVE)
                .setMimeType(MIME_PASTA);

        File criada = drive.files()
                .create(pasta)
                .setFields("id")
                .execute();

        return criada.getId();
    }
}