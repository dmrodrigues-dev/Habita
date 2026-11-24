
package view;

import model.Cobranca;
import model.Morador;
import repository.CobrancaRepository;
import repository.MoradorRepository;
import service.CobrancaService;
import service.ManutencaoService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MenuCobranca {

    private final Scanner scanner;
    private final CobrancaRepository cobrancaRepository;
    private final MoradorRepository moradorRepository;
    private final CobrancaService service;

    public MenuCobranca(Scanner scanner) {
        this.scanner = scanner;

        this.cobrancaRepository = new CobrancaRepository();
        this.moradorRepository = new MoradorRepository();

        this.service = new CobrancaService(
                cobrancaRepository,
                new ManutencaoService()
        );
    }

    public void exibir() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n========== FINANCEIRO ==========");
            System.out.println("1 - Gerar taxa condominial");
            System.out.println("2 - Listar cobrancas");
            System.out.println("3 - Registrar pagamento");
            System.out.println("4 - Consultar receita");
            System.out.println("5 - Consultar inadimplencia");
            System.out.println("6 - Consultar despesas");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opcao: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());

                switch (opcao) {
                    case 1 -> gerarTaxa();
                    case 2 -> listar();
                    case 3 -> registrarPagamento();
                    case 4 -> System.out.println(
                            "Receita: R$ " + service.calcularReceita());
                    case 5 -> System.out.println(
                            "Inadimplencia: R$ " + service.calcularInadimplencia());
                    case 6 -> System.out.println(
                            "Despesas: R$ " + service.calcularDespesas());
                    case 0 -> System.out.println("Voltando...");
                    default -> System.out.println("Opcao invalida.");
                }

            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void gerarTaxa() {
        String cpf = ler("CPF do morador");

        Morador morador = moradorRepository.buscarCpf(cpf);

        if (morador == null) {
            throw new IllegalArgumentException("Morador nao encontrado.");
        }

        LocalDate vencimento = LocalDate.parse(
                ler("Vencimento (AAAA-MM-DD)")
        );

        service.gerarTaxaCondominial(morador, vencimento);

        System.out.println("Taxa condominial gerada!");
    }

    private void listar() {
        List<Cobranca> cobrancas = cobrancaRepository.listarTudo();

        if (cobrancas.isEmpty()) {
            System.out.println("Nenhuma cobranca cadastrada.");
            return;
        }

        for (Cobranca cobranca : cobrancas) {
            mostrar(cobranca);
        }
    }

    private void registrarPagamento() {
        Long id = Long.parseLong(ler("ID da cobranca"));

        Cobranca cobranca = cobrancaRepository.buscarPorId(id);

        if (cobranca == null) {
            throw new IllegalArgumentException("Cobranca nao encontrada.");
        }

        service.registrarPagamento(cobranca);

        System.out.println(
                "Pagamento registrado. Valor pago: R$ " + cobranca.getValorPago()
        );
    }

    private void mostrar(Cobranca cobranca) {
        Morador morador = moradorRepository.buscarPorId(
                cobranca.getMoradorId()
        );

        System.out.println("\n-----------------------------");
        System.out.println("ID: " + cobranca.getId());
        System.out.println("Morador: " +
                (morador != null ? morador.getNome() : "ID " + cobranca.getMoradorId()));
        System.out.println("Tipo: " + cobranca.getTipo());
        System.out.println("Valor original: R$ " + cobranca.getValor());
        System.out.println("Vencimento: " + cobranca.getVencimento());
        System.out.println("Status: " + cobranca.getStatus());
        System.out.println("Data do pagamento: " + cobranca.getDataPagamento());
        System.out.println("Valor pago: " + cobranca.getValorPago());
        System.out.println("-----------------------------");
    }

    private String ler(String campo) {
        System.out.print(campo + ": ");
        return scanner.nextLine().trim();
    }
}
