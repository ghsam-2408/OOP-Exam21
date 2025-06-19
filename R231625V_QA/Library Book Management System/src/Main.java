import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        LibraryBookManagementSystem lib = new LibraryBookManagementSystem();
        lib.setContentPane(lib.getMainPanel());
        lib.setVisible(true);
        lib.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        lib.setMinimumSize(new Dimension(500, 500));
        lib.setTitle("LIBRARY BOOK MANAGEMENT SYSTEM");
        lib.setLocationRelativeTo(null);
    }
}