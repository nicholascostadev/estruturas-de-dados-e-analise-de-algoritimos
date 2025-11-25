package algorithm;

import model.Book;
import java.util.List;

public class MergeSort {

    public enum SortBy {
        TITLE,
        AUTHOR,
        YEAR
    }

    public static void sortBy(List<Book> books, SortBy sortBy) {
        if (books == null || books.size() <= 1) {
            return;
        }

        merge(books, 0, books.size() - 1, sortBy);
    }

    public static void merge(List<Book> books, int start, int end, SortBy sortBy) {
        int middle;
        if (start < end) {
            middle = (start + end) / 2;
            merge(books, start, middle, sortBy);
            merge(books, middle + 1, end, sortBy);
            intercalate(books, start, end, middle, sortBy);
        }
    }

    public static void intercalate(List<Book> books, int start, int end, int middle, SortBy sortBy) {
        int freePos, startVector1, startVector2, i;
        Book aux[] = new Book[books.size()];

        startVector1 = start;
        startVector2 = middle + 1;
        freePos = start;

        while (startVector1 <= middle && startVector2 <= end) {
            if (compareBooks(books.get(startVector1), books.get(startVector2), sortBy) <= 0) {
                aux[freePos] = books.get(startVector1);
                startVector1 = startVector1 + 1;
            } else {
                aux[freePos] = books.get(startVector2);
                startVector2 = startVector2 + 1;
            }
            freePos = freePos + 1;
        }

        for (i = startVector1; i <= middle; i++) {
            aux[freePos] = books.get(i);
            freePos = freePos + 1;
        }

        for (i = startVector2; i <= end; i++) {
            aux[freePos] = books.get(i);
            freePos = freePos + 1;
        }

        for (i = start; i <= end; i++) {
            books.set(i, aux[i]);
        }
    }

    private static int compareBooks(Book book1, Book book2, SortBy sortBy) {
        switch (sortBy) {
            case TITLE:
                return compareTitles(book1.getTitle(), book2.getTitle());
            case AUTHOR:
                return compareAuthors(book1.getAuthor(), book2.getAuthor());
            case YEAR:
                return compareYears(book1.getYear(), book2.getYear());
            default:
                return compareTitles(book1.getTitle(), book2.getTitle());
        }
    }

    private static int compareTitles(String title1, String title2) {
        if (title1 == null && title2 == null)
            return 0;
        if (title1 == null)
            return -1;
        if (title2 == null)
            return 1;

        return title1.toLowerCase().compareTo(title2.toLowerCase());
    }

    private static int compareAuthors(String author1, String author2) {
        if (author1 == null && author2 == null)
            return 0;
        if (author1 == null)
            return -1;
        if (author2 == null)
            return 1;

        return author1.toLowerCase().compareTo(author2.toLowerCase());
    }

    private static int compareYears(Integer year1, Integer year2) {
        if (year1 == null && year2 == null)
            return 0;
        if (year1 == null)
            return 1;
        if (year2 == null)
            return -1;

        return year1.compareTo(year2);
    }
}
