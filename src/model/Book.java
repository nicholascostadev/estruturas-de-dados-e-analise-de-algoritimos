package model;

import java.util.Objects;

public class Book {
    private String title;
    private String author;
    private String isbn;
    private Integer year;

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.year = null;
    }

    public Book(String title, String author, String isbn, Integer year) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public Integer getYear() {
        return year;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public String toString() {
        String yearStr = (year != null) ? " | Ano: " + year : "";
        return String.format("ISBN: %s | Título: %s | Autor: %s%s", isbn, title, author, yearStr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}
