package algorithm;

import model.Book;
import java.util.ArrayList;
import java.util.List;
import java.text.Normalizer;

public class BinarySearch {

    public static List<Book> search(List<Book> books, String searchTitle) {
        List<Book> results = new ArrayList<>();

        if (books == null || books.isEmpty() || searchTitle == null || searchTitle.trim().isEmpty()) {
            return results;
        }

        String searchNormalized = normalize(searchTitle.trim());

        int insertionPoint = binarySearchInsertionPoint(books, searchNormalized);
        boolean isExactMatch = insertionPoint < books.size() &&
                normalize(books.get(insertionPoint).getTitle()).equals(searchNormalized);

        if (isExactMatch) {
            return findAllWithSameTitle(books, insertionPoint, searchNormalized);
        } else {
            return findNearbyBooks(books, insertionPoint, 5);
        }
    }

    private static List<Book> findAllWithSameTitle(List<Book> books, int insertionPoint, String searchNormalized) {
        List<Book> results = new ArrayList<>();

        int left = insertionPoint;
        while (left >= 0 && normalize(books.get(left).getTitle()).equals(searchNormalized)) {
            results.add(0, books.get(left));
            left--;
        }

        if (results.isEmpty() || !results.get(results.size() - 1).equals(books.get(insertionPoint))) {
            results.add(books.get(insertionPoint));
        }

        int right = insertionPoint + 1;
        while (right < books.size() && normalize(books.get(right).getTitle()).equals(searchNormalized)) {
            results.add(books.get(right));
            right++;
        }

        return results;
    }

    private static List<Book> findNearbyBooks(List<Book> books, int insertionPoint, int range) {
        List<Book> results = new ArrayList<>();

        int start = Math.max(0, insertionPoint - range);
        int end = Math.min(books.size(), insertionPoint + range);

        for (int i = start; i < end; i++) {
            results.add(books.get(i));
        }

        return results;
    }

    private static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);

        normalized = normalized.replaceAll("\\p{M}", "");

        return normalized.toLowerCase();
    }

    private static int binarySearchInsertionPoint(List<Book> books, String searchNormalized) {
        int left = 0;
        int right = books.size() - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;
            String bookTitle = normalize(books.get(middle).getTitle());
            int comparison = bookTitle.compareTo(searchNormalized);

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
}
