
package view;

import model.Morador;
import repository.ApartamentoRepository;
import repository.MoradorRepository;
import service.MoradorService;

import java.util.List;
import java.util.Scanner;

public class MenuMorador {

    private final Scanner scanner;
    private final MoradorService service;
    private final ApartamentoRepository apartamentoRepository;

    public MenuMorador(Scanner scanner) {
        this.scanner = scanner;

        this.apartamentoRepository = new ApartamentoRepository();

        this.service = new MoradorService(
                apartamentoRepository,
                new MoradorRepository()
        );
    }

    public void exibir() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n========== MORADORES ==========");
            System.out.println("1 - Cadastrar morador");
            System.out.println("2 - Listar moradores");
            System.out.println("3 - Buscar morador por CPF");
            System.out.println("4 - Editar morador");
            System.out.println("5 - Remover morador");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opcao: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());

                switch (opcao) {
                    case 1 -> cadastrar();
                    case 2 -> listar();
                    case 3 -> buscar();
                    case 4 -> editar();
                    case 5 -> remover();
                    case 0 -> System.out.println("Voltando...");
                    default -> System.out.println("Opcao invalida.");
                }

            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void cadastrar() {
        String nome = ler("Nome");
        String cpf = ler("CPF");
        Long apartamentoId = lerLong("ID do apartamento");
        int pets = lerInt("Quantidade de pets");
        int vagas = lerInt("Quantidade de vagas de garagem");

        service.cadastrarMorador(nome, cpf, apartamentoId, pets, vagas);

        System.out.println("Morador cadastrado!");
    }

    private void listar() {
        List<Morador> moradores = service.listarTodos();

        if (moradores.isEmpty()) {
            System.out.println("Nenhum morador cadastrado.");
            return;
        }

        moradores.forEach(this::mostrar);
    }

    private void buscar() {
        mostrar(service.buscarPorCpf(ler("CPF")));
    }

    private void editar() {
        String nome = ler("Nome");
        String cpf = ler("CPF");
        Long apartamentoId = lerLong("ID do apartamento");
        int pets = lerInt("Quantidade de pets");
        int vagas = lerInt("Quantidade de vagas de garagem");

        service.editarMorador(nome, cpf, apartamentoId, pets, vagas);

        System.out.println("Morador atualizado!");
    }

    private void remover() {
        service.removerMorador(ler("CPF"));
        System.out.println("Morador removido!");
    }

    private void mostrar(Morador morador) {
        var apartamento = apartamentoRepository.buscarPorId(
                morador.getApartamentoId()
        );

        System.out.println("\n-----------------------------");
        System.out.println("ID: " + morador.getId());
        System.out.println("Nome: " + morador.getNome());
        System.out.println("CPF: " + morador.getCpf());
        System.out.println("Apartamento: " +
                (apartamento != null
                        ? apartamento.getTorre() + " - " + apartamento.getNumero()
                        : "ID " + morador.getApartamentoId()));
        System.out.println("Pets: " + morador.getQtdPets());
        System.out.println("Vagas: " + morador.getVagasGaragem());
    }

    private String ler(String campo) {
        System.out.print(campo + ": ");
        return scanner.nextLine().trim();
    }

    private Long lerLong(String campo) {
        return Long.parseLong(ler(campo));
    }

    private int lerInt(String campo) {
        return Integer.parseInt(ler(campo));
    }
}
