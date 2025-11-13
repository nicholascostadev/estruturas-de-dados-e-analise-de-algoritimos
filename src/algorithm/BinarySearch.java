package algorithm;

import model.Book;
import java.util.ArrayList;
import java.util.List;
import java.text.Normalizer;

public class BinarySearch {

    /**
     * Busca livros por título usando busca binária
     * IMPORTANTE: A lista deve estar ordenada por título antes de usar este método
     *
     * @param books       Lista de livros ordenada por título
     * @param searchTitle Título a ser buscado (busca parcial)
     * @return Lista de livros que contêm o título buscado
     */
    public static List<Book> search(List<Book> books, String searchTitle) {
        List<Book> results = new ArrayList<>();

        if (books == null || books.isEmpty() || searchTitle == null || searchTitle.trim().isEmpty()) {
            return results;
        }

        String searchLower = searchTitle.toLowerCase().trim();

        // Encontra a primeira ocorrência usando busca binária
        int index = binarySearchFirst(books, searchLower);

        // Se encontrou, coleta todos os livros que correspondem à busca
        if (index != -1) {
            // Procura para trás
            int left = index;
            while (left >= 0 && bookContainsTitle(books.get(left), searchLower)) {
                results.add(0, books.get(left));
                left--;
            }

            // Procura para frente (se não adicionou o index ainda)
            if (results.isEmpty() || !results.get(results.size() - 1).equals(books.get(index))) {
                results.add(books.get(index));
            }

            int right = index + 1;
            while (right < books.size() && bookContainsTitle(books.get(right), searchLower)) {
                results.add(books.get(right));
                right++;
            }
        }

        return results;
    }

    /**
     * Busca binária que retorna o índice de uma ocorrência (não necessariamente a
     * primeira)
     * de um livro cujo título contém o texto buscado
     */
    private static int binarySearchFirst(List<Book> books, String searchLower) {
        int left = 0;
        int right = books.size() - 1;
        int result = -1;

        while (left <= right) {
            int middle = left + (right - left) / 2;
            String bookTitle = books.get(middle).getTitulo().toLowerCase();

            // Se o livro contém o título buscado
            if (bookContainsTitle(books.get(middle), searchLower)) {
                result = middle;
                // Continua buscando à esquerda para ver se há mais ocorrências
                right = middle - 1;
            }
            // Se o título do livro atual é maior que o buscado, busca à esquerda
            else if (bookTitle.compareTo(searchLower) > 0) {
                right = middle - 1;
            }
            // Se o título do livro atual é menor que o buscado, busca à direita
            else {
                left = middle + 1;
            }
        }

        return result;
    }

    /**
     * Verifica se o título do livro contém o texto buscado (com normalização)
     */
    private static boolean bookContainsTitle(Book book, String searchLower) {
        if (book == null || book.getTitulo() == null) {
            return false;
        }
        String normalizedTitle = normalizeText(book.getTitulo());
        String normalizedSearch = normalizeText(searchLower);
        return normalizedTitle.contains(normalizedSearch);
    }

    /**
     * Verifica se o título do livro é um match exato com o texto buscado (com
     * normalização)
     */
    private static boolean isExactMatch(Book book, String searchLower) {
        if (book == null || book.getTitulo() == null) {
            return false;
        }
        String normalizedTitle = normalizeText(book.getTitulo());
        String normalizedSearch = normalizeText(searchLower);
        return normalizedTitle.equals(normalizedSearch);
    }

    /**
     * Normaliza texto removendo acentos e convertendo para lowercase
     */
    private static String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        // Remove acentos usando NFD (Normalization Form Decomposed)
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        // Remove marcas diacríticas (acentos)
        normalized = normalized.replaceAll("\\p{M}", "");
        return normalized.toLowerCase();
    }

    /**
     * Busca exata por título (retorna apenas correspondências exatas)
     *
     * @param books      Lista de livros ordenada por título
     * @param exactTitle Título exato a ser buscado
     * @return O livro com o título exato, ou null se não encontrado
     */
    public static Book searchExact(List<Book> books, String exactTitle) {
        if (books == null || books.isEmpty() || exactTitle == null) {
            return null;
        }

        int left = 0;
        int right = books.size() - 1;
        String searchLower = exactTitle.toLowerCase().trim();

        while (left <= right) {
            int middle = left + (right - left) / 2;
            String bookTitle = books.get(middle).getTitulo().toLowerCase();

            int comparison = bookTitle.compareTo(searchLower);

            if (comparison == 0) {
                return books.get(middle);
            } else if (comparison > 0) {
                right = middle - 1;
            } else {
                left = middle + 1;
            }
        }

        return null;
    }

    /**
     * Busca livros similares usando distância de Levenshtein simplificada
     * Retorna os livros mais parecidos com o termo buscado
     *
     * @param books       Lista de livros
     * @param searchTitle Título buscado
     * @param maxResults  Número máximo de resultados a retornar
     * @return Lista de livros similares ordenados por similaridade
     */
    public static List<Book> searchSimilar(List<Book> books, String searchTitle, int maxResults) {
        if (books == null || books.isEmpty() || searchTitle == null || searchTitle.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String normalizedSearch = normalizeText(searchTitle.trim());
        List<BookSimilarity> similarities = new ArrayList<>();

        // Calcula similaridade para cada livro
        for (Book book : books) {
            String normalizedTitle = normalizeText(book.getTitulo());
            int similarity = calculateSimilarity(normalizedSearch, normalizedTitle);

            // Apenas adiciona se houver alguma similaridade
            if (similarity > 0) {
                similarities.add(new BookSimilarity(book, similarity));
            }
        }

        // Ordena por similaridade (maior primeiro) usando algoritmo de ordenação
        // customizado
        sortBySimilarity(similarities);

        // Retorna os top N resultados
        List<Book> results = new ArrayList<>();
        int limit = Math.min(maxResults, similarities.size());
        for (int i = 0; i < limit; i++) {
            results.add(similarities.get(i).book);
        }

        return results;
    }

    /**
     * Calcula similaridade entre duas strings
     * Retorna um score baseado em:
     * - Se contém a substring completa (score alto)
     * - Quantas palavras em comum (score médio)
     * - Distância de edição simplificada (score baixo)
     */
    private static int calculateSimilarity(String search, String title) {
        // Correspondência exata
        if (title.equals(search)) {
            return 1000;
        }

        // Contém a substring completa
        if (title.contains(search)) {
            return 500 + (100 - Math.abs(title.length() - search.length()));
        }

        // Verifica palavras em comum
        String[] searchWords = search.split("\\s+");
        String[] titleWords = title.split("\\s+");
        int commonWords = 0;

        for (String searchWord : searchWords) {
            for (String titleWord : titleWords) {
                if (searchWord.length() > 2 && titleWord.contains(searchWord)) {
                    commonWords++;
                    break;
                } else if (titleWord.length() > 2 && searchWord.contains(titleWord)) {
                    commonWords++;
                    break;
                }
            }
        }

        if (commonWords > 0) {
            return 100 * commonWords;
        }

        // Similaridade de caracteres (muito básica)
        int commonChars = 0;
        for (char c : search.toCharArray()) {
            if (title.indexOf(c) >= 0) {
                commonChars++;
            }
        }

        return commonChars * 10 / search.length();
    }

    /**
     * Ordena lista de BookSimilarity por score (bubble sort simplificado)
     */
    private static void sortBySimilarity(List<BookSimilarity> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j).similarity < list.get(j + 1).similarity) {
                    // Troca
                    BookSimilarity temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * Classe auxiliar para armazenar livro e sua pontuação de similaridade
     */
    private static class BookSimilarity {
        Book book;
        int similarity;

        BookSimilarity(Book book, int similarity) {
            this.book = book;
            this.similarity = similarity;
        }
    }
}
