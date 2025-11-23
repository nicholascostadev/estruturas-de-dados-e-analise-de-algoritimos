package service;

import model.Book;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookApiService {

    private static final String CSV_FILE_PATH = "base-books.csv";

    public static List<Book> loadBooksFromCsv() {
        List<Book> books = new ArrayList<>();

        System.out.println("Carregando livros do arquivo " + CSV_FILE_PATH + "...");

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Parse CSV line
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

    /**
     * Faz parsing de uma linha CSV
     * Handles both simple comma-separated values and quoted fields
     */
    private static Book parseCsvLine(String line) {
        try {
            List<String> fields = new ArrayList<>();
            StringBuilder currentField = new StringBuilder();
            boolean inQuotes = false;

            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);

                if (c == '"') {
                    // Check if it's an escaped quote (two consecutive quotes)
                    if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        currentField.append('"');
                        i++; // Skip next quote
                    } else {
                        inQuotes = !inQuotes;
                    }
                } else if (c == ',' && !inQuotes) {
                    // End of field
                    fields.add(currentField.toString());
                    currentField = new StringBuilder();
                } else {
                    currentField.append(c);
                }
            }
            // Add last field
            fields.add(currentField.toString());

            // Ensure we have at least 4 fields (titulo, autor, isbn, ano)
            if (fields.size() < 4) {
                return null;
            }

            String titulo = fields.get(0).trim();
            String autor = fields.get(1).trim();
            String isbn = fields.get(2).trim();
            String anoStr = fields.get(3).trim();

            // Parse year (can be empty)
            Integer ano = null;
            if (!anoStr.isEmpty()) {
                try {
                    ano = Integer.parseInt(anoStr);
                } catch (NumberFormatException e) {
                    // Keep ano as null if parsing fails
                }
            }

            // Validate required fields
            if (titulo.isEmpty() || autor.isEmpty() || isbn.isEmpty()) {
                return null;
            }

            return new Book(titulo, autor, isbn, ano);

        } catch (Exception e) {
            System.err.println("Erro ao fazer parse da linha CSV: " + e.getMessage());
            return null;
        }
    }
}
