
package view;

import model.Morador;
import model.RegistroAcessoMorador;
import repository.MoradorRepository;
import repository.RegistroAcessoMoradorRepository;
import service.ControleAcessoMoradorService;

import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class MenuControleAcessoMorador {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Scanner scanner;
    private final ControleAcessoMoradorService service;
    private final MoradorRepository moradorRepository;

    public MenuControleAcessoMorador(Scanner scanner) {
        this.scanner = scanner;

        this.moradorRepository = new MoradorRepository();

        this.service = new ControleAcessoMoradorService(
                moradorRepository,
                new RegistroAcessoMoradorRepository(),
                Clock.systemDefaultZone()
        );
    }

    public void exibir() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n========== ACESSO DE MORADORES ==========");
            System.out.println("1 - Registrar entrada");
            System.out.println("2 - Registrar saida");
            System.out.println("3 - Listar moradores presentes");
            System.out.println("4 - Consultar historico por CPF");
            System.out.println("5 - Listar historico completo");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opcao: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());

                switch (opcao) {
                    case 1 -> registrarEntrada();
                    case 2 -> registrarSaida();
                    case 3 -> listarPresentes();
                    case 4 -> historicoMorador();
                    case 5 -> listarHistorico();
                    case 0 -> System.out.println("Voltando...");
                    default -> System.out.println("Opcao invalida.");
                }

            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void registrarEntrada() {
        RegistroAcessoMorador registro =
                service.registrarEntrada(ler("CPF do morador"));

        System.out.println(
                "Entrada registrada: " + registro.getEntrada().format(FORMATO)
        );
    }

    private void registrarSaida() {
        RegistroAcessoMorador registro =
                service.registrarSaida(ler("CPF do morador"));

        System.out.println(
                "Saida registrada: " + registro.getSaida().format(FORMATO)
        );
    }

    private void listarPresentes() {
        List<RegistroAcessoMorador> registros = service.listaPresentes();

        if (registros.isEmpty()) {
            System.out.println("Nenhum morador presente.");
            return;
        }

        mostrarRegistros(registros);
    }

    private void historicoMorador() {
        String cpf = ler("CPF do morador");
        mostrarRegistros(service.historicoMorador(cpf));
    }

    private void listarHistorico() {
        mostrarRegistros(service.listarTodos());
    }

    private void mostrarRegistros(List<RegistroAcessoMorador> registros) {
        if (registros.isEmpty()) {
            System.out.println("Nenhum registro encontrado.");
            return;
        }

        for (RegistroAcessoMorador registro : registros) {
            Morador morador = moradorRepository.buscarPorId(
                    registro.getIdMorador()
            );

            System.out.println("\n-----------------------------");
            System.out.println("Registro: " + registro.getId());
            System.out.println("Morador: " +
                    (morador != null ? morador.getNome() : "ID " + registro.getIdMorador()));
            System.out.println("Entrada: " + registro.getEntrada().format(FORMATO));
            System.out.println("Saida: " +
                    (registro.getSaida() == null
                            ? "Ainda no condominio"
                            : registro.getSaida().format(FORMATO)));
            System.out.println("-----------------------------");
        }
    }

    private String ler(String campo) {
        System.out.print(campo + ": ");
        return scanner.nextLine().trim();
    }
}
