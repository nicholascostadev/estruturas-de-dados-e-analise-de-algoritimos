import model.Book;
import service.BookApiService;
import service.LibraryService;
import ui.ConsoleUI;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Book> initialBooks = BookApiService.loadBooksFromCsv();

        if (initialBooks.isEmpty()) {
            System.out.println("Aviso: Não foi possível carregar livros do arquivo.");
            System.out.println("   Iniciando com biblioteca vazia.");
        } else {
            System.out.println(initialBooks.size() + " livros carregados com sucesso!");
        }

        LibraryService libraryService = new LibraryService(initialBooks);
        ConsoleUI consoleUI = new ConsoleUI(libraryService);
        consoleUI.start();
    }
}
