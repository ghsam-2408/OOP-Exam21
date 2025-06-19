public class LibraryBook {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String year;

    public LibraryBook(String isbn, String title, String author, String publisher, String year) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
    }

    // Getters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getYear() { return year; }

    // Convert to String array for table representation
    public String[] toStringArray() {
        return new String[]{isbn, title, author, publisher, year};
    }
}