package service;

import algorithm.BinarySearch;
import algorithm.MergeSort;
import model.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LibraryService {

    private List<Book> books;

    public LibraryService() {
        this.books = new ArrayList<>();
    }

    public LibraryService(List<Book> initialBooks) {
        this.books = new ArrayList<>(initialBooks);
        sortBooks();
    }

    public String addBook(String titulo, String autor) {
        return addBook(titulo, autor, null);
    }

    public String addBook(String titulo, String autor, Integer ano) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título não pode ser vazio");
        }

        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("Autor não pode ser vazio");
        }

        // Gera ISBN único
        String isbn = generateUniqueIsbn();

        Book newBook = new Book(titulo.trim(), autor.trim(), isbn, ano);
        books.add(newBook);

        // Reordena a lista
        sortBooks();

        return isbn;
    }

    public boolean removeBook(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }

        boolean removed = books.removeIf(book -> book.getIsbn().equals(isbn.trim()));

        return removed;
    }

    public boolean updateBook(String isbn, String novoTitulo, String novoAutor) {
        return updateBook(isbn, novoTitulo, novoAutor, null);
    }

    public boolean updateBook(String isbn, String novoTitulo, String novoAutor, Integer novoAno) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }

        Book book = findBookByIsbn(isbn.trim());

        if (book == null) {
            return false;
        }

        boolean updated = false;

        if (novoTitulo != null && !novoTitulo.trim().isEmpty()) {
            book.setTitulo(novoTitulo.trim());
            updated = true;
        }

        if (novoAutor != null && !novoAutor.trim().isEmpty()) {
            book.setAutor(novoAutor.trim());
            updated = true;
        }

        if (novoAno != null) {
            if (novoAno == -1) {
                book.setAno(null); // Remove o ano
            } else {
                book.setAno(novoAno);
            }
            updated = true;
        }

        if (updated) {
            sortBooks();
        }

        return updated;
    }

    public List<Book> searchBooksByTitle(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return BinarySearch.search(books, titulo.trim());
    }

    public Book findBookByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return null;
        }

        for (Book book : books) {
            if (book.getIsbn().equals(isbn.trim())) {
                return book;
            }
        }

        return null;
    }

    public List<Book> listAllBooks() {
        return new ArrayList<>(books);
    }

    public List<Book> listAllBooksSortedBy(MergeSort.SortBy sortBy) {
        List<Book> sortedBooks = new ArrayList<>(books);
        MergeSort.sortBy(sortedBooks, sortBy);
        return sortedBooks;
    }

    public int getTotalBooks() {
        return books.size();
    }

    public boolean isbnExists(String isbn) {
        return findBookByIsbn(isbn) != null;
    }

    private void sortBooks() {
        MergeSort.sort(books);
    }

    private String generateUniqueIsbn() {
        String isbn;

        do {
            // Gera um ISBN-like baseado em UUID (primeiros 13 caracteres numéricos)
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            StringBuilder isbnBuilder = new StringBuilder();

            for (char c : uuid.toCharArray()) {
                if (Character.isDigit(c)) {
                    isbnBuilder.append(c);
                    if (isbnBuilder.length() == 13) {
                        break;
                    }
                }
            }

            // Se não conseguiu 13 dígitos, completa com números aleatórios
            while (isbnBuilder.length() < 13) {
                isbnBuilder.append((int) (Math.random() * 10));
            }

            isbn = isbnBuilder.toString();

        } while (isbnExists(isbn)); // Garante que o ISBN é único

        return isbn;
    }

    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== ESTATÍSTICAS DA BIBLIOTECA ===\n");
        stats.append("Total de livros: ").append(getTotalBooks()).append("\n");

        if (!books.isEmpty()) {
            stats.append("Primeiro livro (alfabeticamente): ").append(books.get(0).getTitulo()).append("\n");
            stats.append("Último livro (alfabeticamente): ").append(books.get(books.size() - 1).getTitulo()).append("\n");

            // Encontra o autor com mais livros
            AuthorCount topAuthor = getAuthorWithMostBooks();
            if (topAuthor != null) {
                stats.append("Autor com mais livros: ").append(topAuthor.author)
                     .append(" (").append(topAuthor.count).append(" livros)\n");
            }
        }

        return stats.toString();
    }

    private AuthorCount getAuthorWithMostBooks() {
        if (books.isEmpty()) {
            return null;
        }

        Map<String, Integer> authorCounts = new HashMap<>();

        for (Book book : books) {
            String author = book.getAutor();
            authorCounts.put(author, authorCounts.getOrDefault(author, 0) + 1);
        }

        int maxCount = 0;
        String topAuthor = null;

        for (Map.Entry<String, Integer> entry : authorCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                topAuthor = entry.getKey();
            }
        }

        return new AuthorCount(topAuthor, maxCount);
    }

    private static class AuthorCount {
        String author;
        int count;

        AuthorCount(String author, int count) {
            this.author = author;
            this.count = count;
        }
    }
}
