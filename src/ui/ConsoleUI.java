package ui;

import algorithm.MergeSort;
import model.Book;
import service.LibraryService;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    private LibraryService libraryService;
    private Scanner scanner;

    public ConsoleUI(LibraryService libraryService) {
        this.libraryService = libraryService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Inicia o loop principal da interface
     */
    public void start() {
        boolean running = true;

        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE GERENCIAMENTO DE BIBLIOTECA  ║");
        System.out.println("╚════════════════════════════════════════════╝");

        while (running) {
            displayMenu();
            int choice = getIntInput("Escolha uma opção: ");

            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    removeBook();
                    break;
                case 3:
                    updateBook();
                    break;
                case 4:
                    searchBook();
                    break;
                case 5:
                    listAllBooks();
                    break;
                case 6:
                    showStatistics();
                    break;
                case 7:
                    running = false;
                    System.out.println("\nEncerrando o sistema... Até logo!");
                    break;
                default:
                    System.out.println("\n❌ Opção inválida! Por favor, escolha entre 1 e 7.");
            }

            if (running) {
                waitForEnter();
            }
        }

        scanner.close();
    }

    /**
     * Exibe o menu principal
     */
    private void displayMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                    MENU PRINCIPAL");
        System.out.println("=".repeat(50));
        System.out.println("  1. 📚 Adicionar um livro");
        System.out.println("  2. 🗑️  Remover um livro");
        System.out.println("  3. ✏️  Atualizar um livro");
        System.out.println("  4. 🔍 Buscar livro por título");
        System.out.println("  5. 📖 Listar todos os livros");
        System.out.println("  6. 📊 Ver estatísticas");
        System.out.println("  7. 🚪 Sair");
        System.out.println("=".repeat(50));
    }

    /**
     * Adiciona um novo livro
     */
    private void addBook() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("           ADICIONAR NOVO LIVRO");
        System.out.println("─".repeat(50));

        String titulo = getStringInput("Digite o título do livro: ");
        String autor = getStringInput("Digite o autor do livro: ");
        String anoStr = getStringInput("Digite o ano de publicação (ou deixe em branco): ");

        Integer ano = null;
        if (!anoStr.isEmpty()) {
            try {
                ano = Integer.parseInt(anoStr);
                if (ano < 0 || ano > 2100) {
                    System.out.println("\n⚠️  Ano inválido! Livro será adicionado sem ano.");
                    ano = null;
                }
            } catch (NumberFormatException e) {
                System.out.println("\n⚠️  Ano inválido! Livro será adicionado sem ano.");
            }
        }

        try {
            String isbn = libraryService.addBook(titulo, autor, ano);
            System.out.println("\n✅ Livro adicionado com sucesso!");
            System.out.println("📌 ISBN gerado: " + isbn);
            System.out.println("💡 Salve este ISBN para futuras operações de atualização ou remoção.");
        } catch (IllegalArgumentException e) {
            System.out.println("\n❌ Erro: " + e.getMessage());
        }
    }

    /**
     * Remove um livro
     */
    private void removeBook() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("              REMOVER LIVRO");
        System.out.println("─".repeat(50));

        String isbn = getStringInput("Digite o ISBN do livro a ser removido: ");

        // Mostra o livro antes de remover
        Book book = libraryService.findBookByIsbn(isbn);
        if (book != null) {
            System.out.println("\nLivro encontrado:");
            System.out.println("  " + book);

            String confirm = getStringInput("\nTem certeza que deseja remover este livro? (s/n): ");

            if (confirm.equalsIgnoreCase("s") || confirm.equalsIgnoreCase("sim")) {
                boolean removed = libraryService.removeBook(isbn);
                if (removed) {
                    System.out.println("\n✅ Livro removido com sucesso!");
                } else {
                    System.out.println("\n❌ Erro ao remover o livro.");
                }
            } else {
                System.out.println("\n↩️  Operação cancelada.");
            }
        } else {
            System.out.println("\n❌ Livro não encontrado com o ISBN informado.");
        }
    }

    /**
     * Atualiza um livro existente
     */
    private void updateBook() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("             ATUALIZAR LIVRO");
        System.out.println("─".repeat(50));

        String isbn = getStringInput("Digite o ISBN do livro a ser atualizado: ");

        Book book = libraryService.findBookByIsbn(isbn);
        if (book != null) {
            System.out.println("\nLivro encontrado:");
            System.out.println("  " + book);
            System.out.println("\n💡 Deixe em branco para manter o valor atual.");

            String novoTitulo = getStringInput("Novo título: ");
            String novoAutor = getStringInput("Novo autor: ");
            String anoStr = getStringInput("Novo ano (ou -1 para remover o ano): ");

            Integer novoAno = null;
            if (!anoStr.isEmpty()) {
                try {
                    novoAno = Integer.parseInt(anoStr);
                    if (novoAno != -1 && (novoAno < 0 || novoAno > 2100)) {
                        System.out.println("\n⚠️  Ano inválido! O ano não será alterado.");
                        novoAno = null;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("\n⚠️  Ano inválido! O ano não será alterado.");
                }
            }

            if (novoTitulo.isEmpty() && novoAutor.isEmpty() && novoAno == null) {
                System.out.println("\n↩️  Nenhuma alteração foi feita.");
                return;
            }

            boolean updated = libraryService.updateBook(isbn, novoTitulo, novoAutor, novoAno);

            if (updated) {
                System.out.println("\n✅ Livro atualizado com sucesso!");
                Book updatedBook = libraryService.findBookByIsbn(isbn);
                System.out.println("  " + updatedBook);
            } else {
                System.out.println("\n❌ Erro ao atualizar o livro.");
            }
        } else {
            System.out.println("\n❌ Livro não encontrado com o ISBN informado.");
        }
    }

    /**
     * Busca livros por título
     */
    private void searchBook() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("          BUSCAR LIVRO POR TÍTULO");
        System.out.println("─".repeat(50));

        String titulo = getStringInput("Digite o título ou parte do título: ");

        List<Book> results = libraryService.searchBooksByTitle(titulo);

        if (results.isEmpty()) {
            System.out.println("\n❌ Nenhum livro encontrado com esse título.");

            // Busca livros similares para sugerir
            System.out.println("\n🔍 Buscando livros similares...");
            List<Book> similarBooks = libraryService.searchSimilarBooks(titulo, 5);

            if (!similarBooks.isEmpty()) {
                System.out.println("\n💡 Você quis dizer algum destes?");
                System.out.println("─".repeat(50));
                for (int i = 0; i < similarBooks.size(); i++) {
                    System.out.println((i + 1) + ". " + similarBooks.get(i));
                }
            } else {
                System.out.println("\n😔 Nenhum livro similar foi encontrado.");
            }
        } else {
            System.out.println("\n✅ " + results.size() + " livro(s) encontrado(s):");
            System.out.println("─".repeat(50));
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i));
            }
        }
    }

    /**
     * Lista todos os livros
     */
    private void listAllBooks() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("          TODOS OS LIVROS DA BIBLIOTECA");
        System.out.println("─".repeat(50));

        if (libraryService.getTotalBooks() == 0) {
            System.out.println("\n📭 A biblioteca está vazia.");
            return;
        }

        // Submenu de ordenação
        System.out.println("\nComo deseja ordenar a listagem?");
        System.out.println("  1. Por Título (A-Z)");
        System.out.println("  2. Por Autor (A-Z)");
        System.out.println("  3. Por Ano (mais antigo primeiro)");
        System.out.println("─".repeat(50));

        int sortChoice;
        MergeSort.SortBy sortBy;
        String sortLabel;

        // Valida a opção até que seja válida
        while (true) {
            sortChoice = getIntInput("Escolha uma opção (1-3): ");

            if (sortChoice >= 1 && sortChoice <= 3) {
                break;
            }

            System.out.println("❌ Opção inválida! Por favor, escolha entre 1 e 3.");
        }

        switch (sortChoice) {
            case 1:
                sortBy = MergeSort.SortBy.TITULO;
                sortLabel = "Título";
                break;
            case 2:
                sortBy = MergeSort.SortBy.AUTOR;
                sortLabel = "Autor";
                break;
            case 3:
                sortBy = MergeSort.SortBy.ANO;
                sortLabel = "Ano";
                break;
            default:
                sortBy = MergeSort.SortBy.TITULO;
                sortLabel = "Título";
        }

        List<Book> allBooks = libraryService.listAllBooksSortedBy(sortBy);

        System.out.println("\n✅ Ordenado por: " + sortLabel);
        System.out.println("Total: " + allBooks.size() + " livro(s)");
        System.out.println("─".repeat(50));

        for (int i = 0; i < allBooks.size(); i++) {
            System.out.println((i + 1) + ". " + allBooks.get(i));
        }
    }

    /**
     * Mostra estatísticas da biblioteca
     */
    private void showStatistics() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println(libraryService.getStatistics());
        System.out.println("─".repeat(50));
    }

    /**
     * Obtém entrada de string do usuário
     */
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Obtém entrada de inteiro do usuário
     */
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor, digite um número válido.");
            }
        }
    }

    /**
     * Aguarda o usuário pressionar Enter
     */
    private void waitForEnter() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}
