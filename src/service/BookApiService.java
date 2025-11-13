package service;

import model.Book;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookApiService {

    private static final String API_BASE_URL = "https://openlibrary.org/search.json";
    private static final int MAX_BOOKS = 2000;

    /**
     * Busca livros da Open Library API
     * 
     * @return Lista de livros
     */
    public static List<Book> fetchBooksInPortuguese() {
        List<Book> books = new ArrayList<>();

        // Lista de autores pra buscar os livros
        String[] authors = {
                "Clarice Lispector",
                "Freida McFadden",
                "Gergely Orosz",
                "Stephen King",
                "Agatha Christie",
                "Ernest Hemingway",
                "Mark Twain",
                "Jane Austen",
                "William Shakespeare",
                "Leo Tolstoy",
                "Anton Chekhov",
                "Virginia Woolf",
                "J.K. Rowling",
        };

        System.out.println("Buscando livros...");

        for (String author : authors) {
            if (books.size() >= MAX_BOOKS) {
                break;
            }

            try {
                List<Book> authorBooks = fetchBooksByAuthor(author);
                for (Book book : authorBooks) {
                    if (books.size() >= MAX_BOOKS) {
                        break;
                    }
                    books.add(book);
                }
            } catch (Exception e) {
                System.err.println("Erro ao buscar livros de " + author + ": " + e.getMessage());
            }
        }

        System.out.println("Total de " + books.size() + " livros carregados.");
        return books;
    }

    /**
     * Busca livros por autor
     */
    private static List<Book> fetchBooksByAuthor(String author) throws Exception {
        List<Book> books = new ArrayList<>();

        String query = URLEncoder.encode(author, StandardCharsets.UTF_8.toString());
        String urlString = API_BASE_URL + "?author=" + query + "&limit=50";

        String jsonResponse = makeHttpRequest(urlString);

        if (jsonResponse != null) {
            books = parseJsonResponse(jsonResponse);
        }

        return books;
    }

    /**
     * Faz requisição HTTP GET
     */
    private static String makeHttpRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } else {
            System.err.println("Erro HTTP: " + responseCode);
            return null;
        }
    }

    /**
     * Faz parsing do JSON usando biblioteca org.json
     * Extrai os campos necessários: title, author_name e first_publish_year
     */
    private static List<Book> parseJsonResponse(String jsonString) {
        List<Book> books = new ArrayList<>();

        try {
            JSONObject jsonResponse = new JSONObject(jsonString);
            JSONArray docs = jsonResponse.optJSONArray("docs");

            if (docs == null) {
                return books;
            }

            for (int i = 0; i < docs.length(); i++) {
                JSONObject bookObj = docs.getJSONObject(i);

                // Extrai título
                String title = bookObj.optString("title", null);
                if (title == null || title.isEmpty()) {
                    continue;
                }

                // Extrai autor (pega o primeiro do array)
                JSONArray authorArray = bookObj.optJSONArray("author_name");
                String author = null;
                if (authorArray != null && authorArray.length() > 0) {
                    author = authorArray.getString(0);
                }

                if (author == null || author.isEmpty()) {
                    continue;
                }

                // Extrai ano de publicação
                Integer year = null;
                if (bookObj.has("first_publish_year")) {
                    year = bookObj.optInt("first_publish_year", -1);
                    if (year == -1) {
                        year = null;
                    }
                }

                // Gera ISBN único (usando UUID)
                String isbn = generateIsbn();
                books.add(new Book(title, author, isbn, year));
            }

        } catch (Exception e) {
            System.err.println("Erro ao fazer parse do JSON: " + e.getMessage());
        }

        return books;
    }

    /**
     * Gera um ISBN único usando UUID
     */
    private static String generateIsbn() {
        // Gera um ISBN-like baseado em UUID (primeiros 13 caracteres numéricos)
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        StringBuilder isbn = new StringBuilder();

        for (char c : uuid.toCharArray()) {
            if (Character.isDigit(c)) {
                isbn.append(c);
                if (isbn.length() == 13) {
                    break;
                }
            }
        }

        // Se não conseguiu 13 dígitos, completa com zeros
        while (isbn.length() < 13) {
            isbn.append("0");
        }

        return isbn.toString();
    }
}
