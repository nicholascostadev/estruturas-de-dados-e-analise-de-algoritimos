package service;

import algorithm.BinarySearch;
import algorithm.MergeSort;
import model.Book;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * Adiciona um novo livro à biblioteca
     * @param titulo Título do livro
     * @param autor Autor do livro
     * @return ISBN gerado para o livro
     * @throws IllegalArgumentException se título ou autor forem vazios
     */
    public String addBook(String titulo, String autor) {
        return addBook(titulo, autor, null);
    }

    /**
     * Adiciona um novo livro à biblioteca com ano
     * @param titulo Título do livro
     * @param autor Autor do livro
     * @param ano Ano de publicação (pode ser null)
     * @return ISBN gerado para o livro
     * @throws IllegalArgumentException se título ou autor forem vazios
     */
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

    /**
     * Remove um livro pelo ISBN
     * @param isbn ISBN do livro a ser removido
     * @return true se o livro foi removido, false se não foi encontrado
     */
    public boolean removeBook(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }

        boolean removed = books.removeIf(book -> book.getIsbn().equals(isbn.trim()));

        if (removed) {
            // Não é necessário reordenar após remoção
        }

        return removed;
    }

    /**
     * Atualiza um livro existente
     * @param isbn ISBN do livro a ser atualizado
     * @param novoTitulo Novo título (ou null para manter o atual)
     * @param novoAutor Novo autor (ou null para manter o atual)
     * @return true se o livro foi atualizado, false se não foi encontrado
     */
    public boolean updateBook(String isbn, String novoTitulo, String novoAutor) {
        return updateBook(isbn, novoTitulo, novoAutor, null);
    }

    /**
     * Atualiza um livro existente com todos os campos
     * @param isbn ISBN do livro a ser atualizado
     * @param novoTitulo Novo título (ou null para manter o atual)
     * @param novoAutor Novo autor (ou null para manter o atual)
     * @param novoAno Novo ano (ou null para manter o atual, use -1 para remover)
     * @return true se o livro foi atualizado, false se não foi encontrado
     */
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
            // Reordena a lista após atualização (pois o título pode ter mudado)
            sortBooks();
        }

        return updated;
    }

    /**
     * Busca livros por título usando busca binária
     * @param titulo Título ou parte do título a ser buscado
     * @return Lista de livros encontrados
     */
    public List<Book> searchBooksByTitle(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return BinarySearch.search(books, titulo.trim());
    }

    /**
     * Busca livros similares ao título fornecido
     * Usado quando a busca normal não retorna resultados
     * @param titulo Título a ser buscado
     * @param maxResults Número máximo de sugestões
     * @return Lista de livros similares
     */
    public List<Book> searchSimilarBooks(String titulo, int maxResults) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return BinarySearch.searchSimilar(books, titulo.trim(), maxResults);
    }

    /**
     * Busca um livro pelo ISBN (busca linear, pois ISBN não é usado para ordenação)
     * @param isbn ISBN do livro
     * @return O livro encontrado ou null
     */
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

    /**
     * Lista todos os livros (ordenados por título)
     * @return Lista de todos os livros
     */
    public List<Book> listAllBooks() {
        return new ArrayList<>(books);
    }

    /**
     * Lista todos os livros ordenados por critério específico
     * @param sortBy Critério de ordenação (TITULO, AUTOR ou ANO)
     * @return Lista de todos os livros ordenada
     */
    public List<Book> listAllBooksSortedBy(MergeSort.SortBy sortBy) {
        List<Book> sortedBooks = new ArrayList<>(books);
        MergeSort.sortBy(sortedBooks, sortBy);
        return sortedBooks;
    }

    /**
     * Retorna o número total de livros na biblioteca
     * @return Total de livros
     */
    public int getTotalBooks() {
        return books.size();
    }

    /**
     * Verifica se um ISBN já existe na biblioteca
     * @param isbn ISBN a ser verificado
     * @return true se o ISBN já existe
     */
    public boolean isbnExists(String isbn) {
        return findBookByIsbn(isbn) != null;
    }

    /**
     * Ordena os livros por título usando Merge Sort
     */
    private void sortBooks() {
        MergeSort.sort(books);
    }

    /**
     * Gera um ISBN único que não existe na biblioteca
     * @return ISBN único
     */
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

    /**
     * Retorna estatísticas da biblioteca
     * @return String com estatísticas formatadas
     */
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== ESTATÍSTICAS DA BIBLIOTECA ===\n");
        stats.append("Total de livros: ").append(getTotalBooks()).append("\n");

        if (!books.isEmpty()) {
            stats.append("Primeiro livro (alfabeticamente): ").append(books.get(0).getTitulo()).append("\n");
            stats.append("Último livro (alfabeticamente): ").append(books.get(books.size() - 1).getTitulo()).append("\n");
        }

        return stats.toString();
    }
}
