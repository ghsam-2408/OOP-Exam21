import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryManager {
    private static final String CSV_FILE = "books.csv";

    public LibraryManager() {
        createCSVFileIfNotExists();
    }

    private void createCSVFileIfNotExists() {
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            try {
                // Create a new CSV file and write the headers
                file.createNewFile();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE))) {
                    bw.write("ISBN,Title,Author,Publisher,Year");
                    bw.newLine();
                }
            } catch (IOException e) {
                logError("Error creating CSV file: " + e.getMessage());
            }
        }
    }

    public List<LibraryBook> loadBooks() {
        List<LibraryBook> books = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            // Skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                LibraryBook book = new LibraryBook(data[0], data[1], data[2], data[3], data[4]);
                books.add(book);
            }
        } catch (IOException e) {
            logError("Error loading books: " + e.getMessage());
        }
        return books;
    }

    public void saveBooks(List<LibraryBook> books) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE))) {
            // Write the header first
            bw.write("ISBN,Title,Author,Publisher,Year");
            bw.newLine();
            for (LibraryBook book : books) {
                bw.write(String.join(",", book.toStringArray()));
                bw.newLine();
            }
        } catch (IOException e) {
            logError("Error saving books: " + e.getMessage());
        }
    }

    public void logError(String message) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("error_log.txt", true))) {
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            bw.write(timestamp + " - " + message);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}