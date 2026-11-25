# Habita

O Habita é um sistema de gerenciamento de condomínios desenvolvido em Java para a disciplina de Programação Orientada a Objetos. O projeto foi desenvolvido em grupo e utiliza o Condomínio Vista Alegre como cenário de aplicação.

O sistema funciona pelo terminal, com um menu principal que reúne os módulos desenvolvidos pela equipe.

## Funcionalidades

- **Apartamentos:** cadastro, listagem, busca e remoção de apartamentos.
- **Moradores:** cadastro, consulta por CPF, listagem, edição e remoção.
- **Controle de acesso:** registro de entrada e saída de moradores, consulta de moradores presentes e histórico de acesso.
- **Visitantes:** cadastro, autorização de visitas, registro de entrada e saída e consulta de históricos.
- **Reservas:** consulta de áreas comuns, cadastro, busca, cancelamento e conclusão de reservas.
- **Manutenção:** cadastro e consulta de chamados, início e conclusão de atendimentos, histórico por área e total de gastos.
- **Financeiro:** geração de taxas condominiais, registro de pagamentos e consulta de receitas, inadimplência e despesas.
- **Relatórios mensais:** geração de relatórios em PDF com informações financeiras, manutenção, reservas e visitantes.
- **Backup:** compactação dos arquivos de dados em ZIP e envio ao Google Drive mediante autorização do usuário.

## Tecnologias utilizadas

- Java 21
- Maven — gerenciamento de dependências e compilação
- OpenCSV — leitura e gravação de arquivos CSV
- JUnit Jupiter — testes automatizados
- iText — geração de relatórios em PDF
- Google Drive API e OAuth 2.0 — envio de backups
- Git e GitHub — controle de versões

## Estrutura do projeto

```text
Habita/
├── data/                  # Arquivos de dados em CSV
├── docs/                  # Documentação e guias de uso
├── src/
│   ├── main/java/
│   │   ├── Main.java      # Inicialização do sistema
│   │   ├── enums/         # Tipos e estados
│   │   ├── model/         # Classes do domínio
│   │   ├── repository/    # Acesso aos dados
│   │   ├── service/       # Regras de negócio
│   │   ├── util/          # Backup, PDF e outras funções auxiliares
│   │   └── view/          # Menus do terminal
│   └── test/java/         # Testes automatizados
├── pom.xml
└── README.md
```

O código foi separado em camadas para distinguir os modelos, as regras de negócio, o armazenamento e a interação com o usuário. O projeto utiliza conceitos de orientação a objetos, como encapsulamento, herança e interfaces.

## Como executar

### Requisitos

- JDK 21
- Maven (ou o Maven integrado ao IntelliJ IDEA)
- IntelliJ IDEA ou outra IDE compatível com Java

### Pelo IntelliJ IDEA

1. Baixe ou clone o repositório e abra a pasta do projeto no IntelliJ IDEA.
2. Importe o arquivo `pom.xml` como projeto Maven e aguarde o carregamento das dependências.
3. Configure o SDK do projeto para Java 21.
4. Abra `src/main/java/Main.java` e execute o método `main`.
5. Escolha uma opção no menu apresentado no console.

Use a pasta raiz do projeto como diretório de trabalho para que os caminhos relativos dos arquivos funcionem corretamente.

### Menu principal

```text
1 - Apartamentos
2 - Moradores
3 - Controle de acesso de moradores
4 - Visitantes
5 - Reservas
6 - Manutencao
7 - Financeiro
8 - Relatorios mensais
9 - Backup
0 - Encerrar sistema
```

## Armazenamento dos dados

Os registros são armazenados em arquivos CSV na pasta `data/`. Os relatórios mensais em PDF são gerados na pasta `relatorios/`, e os backups locais são gravados na pasta `backups/`.

A classe `util.SeedDados` permite popular os dados com registros fictícios para demonstração. Atenção: ela substitui os CSVs atuais. Não a execute sobre dados que deseja preservar sem antes conferir e guardar uma cópia.

## Backup no Google Drive

Para utilizar o envio ao Google Drive, é necessário configurar as credenciais do Google:

1. Crie ou selecione um projeto no [Google Cloud Console](https://console.cloud.google.com/).
2. Ative a Google Drive API.
3. Configure a tela de consentimento OAuth e, se necessário, adicione sua conta como usuário de teste.
4. Crie uma credencial OAuth 2.0 do tipo aplicativo para computador.
5. Baixe o arquivo JSON da credencial e salve-o como `credentials/credentials.json` na raiz do projeto.
6. Execute o sistema, selecione a opção `9 - Backup` e depois a opção `1`.
7. Autorize o acesso à conta Google quando solicitado no navegador.

O sistema cria um ZIP com os arquivos da pasta `data/`, salva uma cópia em `backups/` e tenta enviá-lo à pasta `Backup Vista Alegre` no Google Drive. Caso o envio falhe após a criação do ZIP, a cópia local é mantida.

Os tokens de autorização ficam em `credentials/tokens/`. A pasta `credentials/` está no `.gitignore` e não deve ser enviada ao GitHub. O backup é acionado pelo menu; não há agendamento automático nem restauração pelo menu nesta versão.

## Testes

Os testes automatizados estão em `src/test/java` e incluem classes de modelo e serviços, como manutenção, reservas, cobranças, controle de acesso, relatórios e backup.

Para executar os testes, utilize o comando abaixo na raiz do projeto:

```bash
mvn test
```

No IntelliJ IDEA, também é possível executar a tarefa `test` em Maven > Lifecycle.

## Desenvolvimento em grupo

O projeto foi dividido em módulos, desenvolvidos pelos integrantes e reunidos no menu principal. O GitHub foi utilizado para compartilhar o código e integrar as contribuições da equipe. O módulo de manutenção, por exemplo, foi desenvolvido em uma branch separada e incorporado por meio de um Pull Request.

Os guias de uso e os documentos da atividade estão disponíveis na pasta `docs/`.
