package algorithm;

import model.Book;
import java.util.ArrayList;
import java.util.List;

public class BinarySearch {

    public static List<Book> search(List<Book> books, String searchTitle) {
        List<Book> results = new ArrayList<>();

        if (books == null || books.isEmpty() || searchTitle == null || searchTitle.trim().isEmpty()) {
            return results;
        }

        String searchLower = searchTitle.toLowerCase().trim();

        int insertionPoint = binarySearchInsertionPoint(books, searchLower);
        boolean isExactMatch = insertionPoint < books.size() &&
            books.get(insertionPoint).getTitulo().toLowerCase().equals(searchLower);

        if (isExactMatch) {
            int left = insertionPoint;
            while (left >= 0 && books.get(left).getTitulo().toLowerCase().equals(searchLower)) {
                results.add(0, books.get(left));
                left--;
            }

            if (results.isEmpty() || !results.get(results.size() - 1).equals(books.get(insertionPoint))) {
                results.add(books.get(insertionPoint));
            }

            int right = insertionPoint + 1;
            while (right < books.size() && books.get(right).getTitulo().toLowerCase().equals(searchLower)) {
                results.add(books.get(right));
                right++;
            }
        } else {
            // No exact match - return closest books
            int start = Math.max(0, insertionPoint - 5);
            int end = Math.min(books.size(), insertionPoint + 5);

            for (int i = start; i < end; i++) {
                results.add(books.get(i));
            }
        }

        return results;
    }

    private static int binarySearchInsertionPoint(List<Book> books, String searchLower) {
        int left = 0;
        int right = books.size() - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;
            String bookTitle = books.get(middle).getTitulo().toLowerCase();
            int comparison = bookTitle.compareTo(searchLower);

            if (comparison == 0) {
                return middle;
            } else if (comparison > 0) {
                right = middle - 1;
            } else {
                left = middle + 1;
            }
        }

        return left;
    }

    public static List<Book> getClosestBooks(List<Book> books, String searchTitle) {
        List<Book> results = new ArrayList<>();

        if (books == null || books.isEmpty() || searchTitle == null || searchTitle.trim().isEmpty()) {
            return results;
        }

        String searchLower = searchTitle.toLowerCase().trim();
        int insertionPoint = binarySearchInsertionPoint(books, searchLower);

        int start = Math.max(0, insertionPoint - 5);
        int end = Math.min(books.size(), insertionPoint + 5);

        for (int i = start; i < end; i++) {
            results.add(books.get(i));
        }

        return results;
    }
}
