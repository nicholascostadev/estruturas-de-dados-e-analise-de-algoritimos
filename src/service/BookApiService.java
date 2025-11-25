package service;

import model.Book;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookApiService {

    private static final String CSV_FILE_PATH = "base-books.csv";
    private static final int TITLE_INDEX = 0;
    private static final int AUTHOR_INDEX = 1;
    private static final int ISBN_INDEX = 2;
    private static final int YEAR_INDEX = 3;

    public static List<Book> loadBooksFromCsv() {
        List<Book> books = new ArrayList<>();

        System.out.println("Carregando livros do arquivo " + CSV_FILE_PATH + "...");

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                Book book = parseCsvLine(line);
                if (book != null) {
                    books.add(book);
                }
            }

            System.out.println("Total de " + books.size() + " livros carregados.");

        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo CSV: " + e.getMessage());
            System.err.println("Certifique-se que o arquivo " + CSV_FILE_PATH + " existe no diretório do projeto.");
        }

        return books;
    }

    private static Book parseCsvLine(String line) {
        try {
            List<String> fields = new ArrayList<>();
            StringBuilder currentField = new StringBuilder();
            boolean inQuotes = false;

            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);

                if (c == '"') {
                    if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        currentField.append('"');
                        i++;
                    } else {
                        inQuotes = !inQuotes;
                    }
                } else if (c == ',' && !inQuotes) {
                    fields.add(currentField.toString());
                    currentField = new StringBuilder();
                } else {
                    currentField.append(c);
                }
            }
            fields.add(currentField.toString());

            if (fields.size() < 4) {
                return null;
            }

            String title = fields.get(TITLE_INDEX).trim();
            String author = fields.get(AUTHOR_INDEX).trim();
            String isbn = fields.get(ISBN_INDEX).trim();
            String yearStr = fields.get(YEAR_INDEX).trim();

            Integer year = null;
            if (!yearStr.isEmpty()) {
                try {
                    year = Integer.parseInt(yearStr);
                } catch (NumberFormatException e) {
                    System.err.println("Erro ao converter ano para inteiro: " + e.getMessage());
                }
            }

            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
                return null;
            }

            return new Book(title, author, isbn, year);

        } catch (Exception e) {
            System.err.println("Erro ao fazer parse da linha CSV: " + e.getMessage());
            return null;
        }
    }
}
