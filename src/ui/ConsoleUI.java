package ui;

import algorithm.MergeSort;
import model.Book;
import service.LibraryService;

import java.time.Year;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    private LibraryService libraryService;
    private Scanner scanner;

    public ConsoleUI(LibraryService libraryService) {
        this.libraryService = libraryService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;

        System.out.println("\n╔════════════════════════════╗");
        System.out.println("║   SISTEMA DE BIBLIOTECA    ║");
        System.out.println("╚════════════════════════════╝");

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
                    System.out.println("\nOpção inválida! Por favor, escolha entre 1 e 7.");
            }

            if (running) {
                waitForEnter();
            }
        }

        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                    MENU PRINCIPAL");
        System.out.println("=".repeat(50));
        System.out.println("  1. Adicionar um livro");
        System.out.println("  2. Remover um livro");
        System.out.println("  3. Atualizar um livro");
        System.out.println("  4. Buscar livro por título");
        System.out.println("  5. Listar todos os livros");
        System.out.println("  6. Ver estatísticas");
        System.out.println("  7. Sair");
        System.out.println("=".repeat(50));
    }

    private void addBook() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("           ADICIONAR NOVO LIVRO");
        System.out.println("─".repeat(50));

        String title = getStringInput("Digite o título do livro: ");
        String author = getStringInput("Digite o autor do livro: ");
        Integer year = getYearInput("Digite o ano de publicação (ou deixe em branco): ", false);

        try {
            String isbn = libraryService.addBook(title, author, year);
            System.out.println("\nLivro adicionado com sucesso!");
            System.out.println("ISBN gerado: " + isbn);
            System.out.println("Salve este ISBN para futuras operações de atualização ou remoção.");
        } catch (IllegalArgumentException e) {
            System.out.println("\nErro: " + e.getMessage());
        }
    }

    private void removeBook() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("              REMOVER LIVRO");
        System.out.println("─".repeat(50));

        String isbn = getStringInput("Digite o ISBN do livro a ser removido: ");

        Book book = libraryService.findBookByIsbn(isbn);
        if (book != null) {
            System.out.println("\nLivro encontrado:");
            System.out.println("  " + book);

            String confirm = getStringInput("\nTem certeza que deseja remover este livro? (s/n): ");

            if (confirm.equalsIgnoreCase("s") || confirm.equalsIgnoreCase("sim")) {
                boolean removed = libraryService.removeBook(isbn);
                if (removed) {
                    System.out.println("\nLivro removido com sucesso!");
                } else {
                    System.out.println("\nErro ao remover o livro.");
                }
            } else {
                System.out.println("\nOperação cancelada.");
            }
        } else {
            System.out.println("\nLivro não encontrado com o ISBN informado.");
        }
    }

    private void updateBook() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("             ATUALIZAR LIVRO");
        System.out.println("─".repeat(50));

        String isbn = getStringInput("Digite o ISBN do livro a ser atualizado: ");

        Book book = libraryService.findBookByIsbn(isbn);
        if (book != null) {
            System.out.println("\nLivro encontrado:");
            System.out.println("  " + book);
            System.out.println("\nDeixe em branco para manter o valor atual.");

            String newTitle = getStringInput("Novo título: ");
            String newAuthor = getStringInput("Novo autor: ");
            Integer newYear = getYearInput("Novo ano (ou -1 para remover o ano): ", true);

            if (newTitle.isEmpty() && newAuthor.isEmpty() && newYear == null) {
                System.out.println("\nNenhuma alteração foi feita.");
                return;
            }

            try {
                boolean updated = libraryService.updateBook(isbn, newTitle, newAuthor, newYear);

                if (updated) {
                    System.out.println("\nLivro atualizado com sucesso!");
                    Book updatedBook = libraryService.findBookByIsbn(isbn);
                    System.out.println("  " + updatedBook);
                } else {
                    System.out.println("\nErro ao atualizar o livro.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("\nErro: " + e.getMessage());
            }
        } else {
            System.out.println("\nLivro não encontrado com o ISBN informado.");
        }
    }

    private void searchBook() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("          BUSCAR LIVRO POR TÍTULO");
        System.out.println("─".repeat(50));

        String title = getStringInput("Digite o título exato do livro: ");

        List<Book> results = libraryService.searchBooksByTitle(title);

        if (results.isEmpty()) {
            System.out.println("\nNenhum livro encontrado.");
        } else {
            System.out.println("\n" + results.size() + " livro(s) encontrado(s):");
            System.out.println("─".repeat(50));
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i));
            }
        }
    }

    private void listAllBooks() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("          TODOS OS LIVROS DA BIBLIOTECA");
        System.out.println("─".repeat(50));

        if (libraryService.getTotalBooks() == 0) {
            System.out.println("\nA biblioteca está vazia.");
            return;
        }

        System.out.println("\nComo deseja ordenar a listagem?");
        System.out.println("  1. Por Título (A-Z)");
        System.out.println("  2. Por Autor (A-Z)");
        System.out.println("  3. Por Ano (mais antigo primeiro)");
        System.out.println("─".repeat(50));

        int sortChoice;
        MergeSort.SortBy sortBy;
        String sortLabel;

        while (true) {
            sortChoice = getIntInput("Escolha uma opção (1-3): ");

            if (sortChoice >= 1 && sortChoice <= 3) {
                break;
            }

            System.out.println("Opção inválida! Por favor, escolha entre 1 e 3.");
        }

        switch (sortChoice) {
            case 1:
                sortBy = MergeSort.SortBy.TITLE;
                sortLabel = "Título";
                break;
            case 2:
                sortBy = MergeSort.SortBy.AUTHOR;
                sortLabel = "Autor";
                break;
            case 3:
                sortBy = MergeSort.SortBy.YEAR;
                sortLabel = "Ano";
                break;
            default:
                sortBy = MergeSort.SortBy.TITLE;
                sortLabel = "Título";
        }

        List<Book> allBooks = libraryService.listAllBooksSortedBy(sortBy);

        System.out.println("\nOrdenado por: " + sortLabel);
        System.out.println("Total: " + allBooks.size() + " livro(s)");
        System.out.println("─".repeat(50));

        for (int i = 0; i < allBooks.size(); i++) {
            System.out.println((i + 1) + ". " + allBooks.get(i));
        }
    }

    private void showStatistics() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println(libraryService.getStatistics());
        System.out.println("─".repeat(50));
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido.");
            }
        }
    }

    private void waitForEnter() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }

    private Integer getYearInput(String prompt, boolean allowRemove) {
        int currentYear = Year.now().getValue();

        while (true) {
            String input = getStringInput(prompt);

            if (input.isEmpty()) {
                return null;
            }

            try {
                int year = Integer.parseInt(input);

                if (allowRemove && year == -1) {
                    return -1;
                }

                if (year < 0 || year > currentYear) {
                    if (allowRemove) {
                        System.out.println("Ano inválido! Digite um ano entre 0 e " + currentYear + ", ou -1 para remover. Tente novamente ou deixe em branco.");
                    } else {
                        System.out.println("Ano inválido! O ano deve estar entre 0 e " + currentYear + ". Tente novamente ou deixe em branco.");
                    }
                    continue;
                }

                return year;
            } catch (NumberFormatException e) {
                if (allowRemove) {
                    System.out.println("Por favor, digite um número válido, -1 para remover, ou deixe em branco.");
                } else {
                    System.out.println("Por favor, digite um número válido ou deixe em branco.");
                }
            }
        }
    }
}
