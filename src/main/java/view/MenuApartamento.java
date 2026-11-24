
package view;

import model.Apartamento;
import repository.ApartamentoRepository;
import service.ApartamentoService;

import java.util.List;
import java.util.Scanner;

public class MenuApartamento {

    private final Scanner scanner;
    private final ApartamentoService service;

    public MenuApartamento(Scanner scanner) {
        this.scanner = scanner;

        this.service = new ApartamentoService(
                new ApartamentoRepository()
        );
    }

    public void exibir() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n========== APARTAMENTOS ==========");
            System.out.println("1 - Cadastrar apartamento");
            System.out.println("2 - Listar apartamentos");
            System.out.println("3 - Buscar apartamento");
            System.out.println("4 - Remover apartamento");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opcao: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());

                switch (opcao) {
                    case 1 -> cadastrar();
                    case 2 -> listar();
                    case 3 -> buscar();
                    case 4 -> remover();
                    case 0 -> System.out.println("Voltando...");
                    default -> System.out.println("Opcao invalida.");
                }

            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void cadastrar() {
        String torre = ler("Torre");
        String numero = ler("Numero do apartamento");

        service.cadastrarApartamento(torre, numero);

        System.out.println("Apartamento cadastrado!");
    }

    private void listar() {
        List<Apartamento> apartamentos = service.listarTodos();

        if (apartamentos.isEmpty()) {
            System.out.println("Nenhum apartamento cadastrado.");
            return;
        }

        apartamentos.forEach(this::mostrar);
    }

    private void buscar() {
        String torre = ler("Torre");
        String numero = ler("Numero do apartamento");

        mostrar(service.buscarPorNumeroTorre(torre, numero));
    }

    private void remover() {
        String torre = ler("Torre");
        String numero = ler("Numero do apartamento");

        service.removerApartamento(torre, numero);

        System.out.println("Apartamento removido!");
    }

    private void mostrar(Apartamento apartamento) {
        System.out.println(
                "ID: " + apartamento.getId()
                        + " | Torre: " + apartamento.getTorre()
                        + " | Apartamento: " + apartamento.getNumero()
        );
    }

    private String ler(String campo) {
        System.out.print(campo + ": ");
        return scanner.nextLine().trim();
    }
}
