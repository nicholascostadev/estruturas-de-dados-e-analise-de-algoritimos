import model.Book;
import service.BookApiService;
import service.LibraryService;
import ui.ConsoleUI;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║  BEM-VINDO AO SISTEMA DE GERENCIAMENTO DE BIBLIOTECA     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();

        // Carrega livros da API
        System.out.println("🌐 Conectando à Open Library API...");
        List<Book> initialBooks = BookApiService.fetchBooksInPortuguese();

        if (initialBooks.isEmpty()) {
            System.out.println("⚠️  Aviso: Não foi possível carregar livros da API.");
            System.out.println("   Iniciando com biblioteca vazia.");
        } else {
            System.out.println("✅ " + initialBooks.size() + " livros carregados com sucesso!");
        }

        // Inicializa o serviço de biblioteca
        LibraryService libraryService = new LibraryService(initialBooks);

        // Inicia a interface do usuário
        ConsoleUI consoleUI = new ConsoleUI(libraryService);
        consoleUI.start();
    }
}
