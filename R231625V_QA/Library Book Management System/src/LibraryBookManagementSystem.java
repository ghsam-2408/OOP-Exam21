
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LibraryBookManagementSystem extends JFrame {
    private JPanel mainPanel;
    private JTextField isbntextField1;
    private JTextField titletextField2;
    private JTextField authortextField3;
    private JTextField yeartextField5;
    private JLabel isbnLabel;
    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel publisherLabel;
    private JLabel yearLabel;
    private JButton addButton;
    private JButton viewButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JTable table12;
    private JTextField publishertextField1;
    private DefaultTableModel tableModel;
    private LibraryManager libraryManager;
    private List<LibraryBook> books;

    public LibraryBookManagementSystem() {
        libraryManager = new LibraryManager();
        books = libraryManager.loadBooks();

        // Initialize the table model with column names (headings)
        String[] columnNames = {"ISBN", "Title", "Author", "Publisher", "Year"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table12.setModel(tableModel);
        loadBooksIntoTable();

        // Add action listeners for buttons
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadBooksIntoTable();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBook();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
            }
        });

        // Add a selection listener to the table
        table12.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = table12.getSelectedRow();
                    if (selectedRow != -1) {
                        populateFields(selectedRow);
                    }
                }
            }
        });
    }

    private void loadBooksIntoTable() {
        tableModel.setRowCount(0); // Clear existing rows
        for (LibraryBook book : books) {
            tableModel.addRow(book.toStringArray());
        }
    }

    private void addBook() {
        String isbn = isbntextField1.getText();
        String title = titletextField2.getText();
        String author = authortextField3.getText();
        String publisher = publishertextField1.getText();
        String year = yeartextField5.getText();

        // Validate input
        if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || publisher.isEmpty() || year.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        LibraryBook newBook = new LibraryBook(isbn, title, author, publisher, year);
        books.add(newBook);
        libraryManager.saveBooks(books);
        loadBooksIntoTable();
        clearFields();
    }

    private void updateBook() {
        int selectedRow = table12.getSelectedRow();
        if (selectedRow != -1) {
            String isbn = isbntextField1.getText();
            String title = titletextField2.getText();
            String author = authortextField3.getText();
            String publisher = publishertextField1.getText();
            String year = yeartextField5.getText();

            // Validate input
            if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || publisher.isEmpty() || year.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.");
                return;
            }

            // Update the selected book
            LibraryBook updatedBook = new LibraryBook(isbn, title, author, publisher, year);
            books.set(selectedRow, updatedBook);
            libraryManager.saveBooks(books);
            loadBooksIntoTable();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to update.");
        }
    }

    private void deleteBook() {
        int selectedRow = table12.getSelectedRow();
        if (selectedRow != -1) {
            books.remove(selectedRow);
            libraryManager.saveBooks(books);
            loadBooksIntoTable();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
        }
    }

    private void clearFields() {
        isbntextField1.setText("");
        titletextField2.setText("");
        authortextField3.setText("");
        publishertextField1.setText("");
        yeartextField5.setText("");
    }

    private void populateFields(int selectedRow) {
        LibraryBook book = books.get(selectedRow);
        isbntextField1.setText(book.getIsbn());
        titletextField2.setText(book.getTitle());
        authortextField3.setText(book.getAuthor());
        publishertextField1.setText(book.getPublisher());
        yeartextField5.setText(book.getYear());
    }

    public JPanel getMainPanel(){
        return mainPanel;
    }

//    public static void main(String[] args) {
//        LibraryBookManagementSystem lib = new LibraryBookManagementSystem();
//        lib.setContentPane(lib.mainPanel);
//        lib.setVisible(true);
//        lib.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        lib.setMinimumSize(new Dimension(500, 500));
//        lib.setTitle("LIBRARY BOOK MANAGEMENT SYSTEM");
//        lib.setLocationRelativeTo(null);
//    }
}