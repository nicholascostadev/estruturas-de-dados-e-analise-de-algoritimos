package service;

import algorithm.BinarySearch;
import algorithm.MergeSort;
import model.Book;

import java.time.Year;
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

    public String addBook(String title, String author) {
        return addBook(title, author, null);
    }

    public String addBook(String title, String author, Integer year) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Título não pode ser vazio");
        }

        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Autor não pode ser vazio");
        }

        if (year != null && (year < 0 || year > Year.now().getValue())) {
            throw new IllegalArgumentException("Ano inválido. Deve estar entre 0 e " + Year.now().getValue());
        }

        String isbn = generateUniqueIsbn();
        Book newBook = new Book(title.trim(), author.trim(), isbn, year);
        books.add(newBook);

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

    public boolean updateBook(String isbn, String newTitle, String newAuthor) {
        return updateBook(isbn, newTitle, newAuthor, null);
    }

    public boolean updateBook(String isbn, String newTitle, String newAuthor, Integer newYear) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }

        Book book = findBookByIsbn(isbn.trim());

        if (book == null) {
            return false;
        }

        boolean updated = false;

        if (newTitle != null && !newTitle.trim().isEmpty()) {
            book.setTitle(newTitle.trim());
            updated = true;
        }

        if (newAuthor != null && !newAuthor.trim().isEmpty()) {
            book.setAuthor(newAuthor.trim());
            updated = true;
        }

        if (newYear != null) {
            if (newYear == -1) {
                book.setYear(null);
            } else {
                if (newYear < 0 || newYear > Year.now().getValue()) {
                    throw new IllegalArgumentException("Ano inválido. Deve estar entre 0 e " + Year.now().getValue());
                }
                book.setYear(newYear);
            }
            updated = true;
        }

        if (updated) {
            sortBooks();
        }

        return updated;
    }

    public List<Book> searchBooksByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return BinarySearch.search(books, title.trim());
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
        MergeSort.sortBy(books, MergeSort.SortBy.TITLE);
    }

    private String generateUniqueIsbn() {
        String isbn;

        do {
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

            while (isbnBuilder.length() < 13) {
                isbnBuilder.append((int) (Math.random() * 10));
            }

            isbn = isbnBuilder.toString();

        } while (isbnExists(isbn));

        return isbn;
    }

    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== ESTATÍSTICAS DA BIBLIOTECA ===\n");
        stats.append("Total de livros: ").append(getTotalBooks()).append("\n");

        if (!books.isEmpty()) {
            stats.append("Primeiro livro (alfabeticamente): ").append(books.get(0).getTitle()).append("\n");
            stats.append("Último livro (alfabeticamente): ").append(books.get(books.size() - 1).getTitle())
                    .append("\n");

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
            String author = book.getAuthor();
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
