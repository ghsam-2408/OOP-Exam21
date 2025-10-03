import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private Connection conn;

    public CustomerDAO(Connection conn) throws SQLException {
        this.conn = conn;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS customers (
                id INT AUTO_INCREMENT PRIMARY KEY,
                customer_id VARCHAR(50) UNIQUE NOT NULL,
                full_name VARCHAR(100) NOT NULL,
                contact_info VARCHAR(200),
                customer_type VARCHAR(20) NOT NULL,
                national_identity VARCHAR(50),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public void insertCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (customer_id, full_name, contact_info, customer_type, national_identity) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, customer.getCustomerId());
            pst.setString(2, customer.getFullName());
            pst.setString(3, customer.getContactInfo());
            pst.setString(4, customer instanceof CorporateCustomer ? "corporate" : "individual");

            // Handle national identity for individual customers
            if (customer instanceof IndividualCustomer) {
                pst.setString(5, ((IndividualCustomer) customer).getNationalIdentity());
            } else {
                pst.setString(5, null);
            }

            pst.executeUpdate();

            ResultSet keys = pst.getGeneratedKeys();
            if (keys.next()) {
                customer.setId(keys.getInt(1));
            }
        }
    }

    public Customer selectCustomerById(String customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, customerId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("full_name");
                String contactInfo = rs.getString("contact_info");
                String customerType = rs.getString("customer_type");

                if ("corporate".equals(customerType)) {
                    CorporateCustomer customer = new CorporateCustomer(customerId, fullName, contactInfo);
                    customer.setId(rs.getInt("id"));
                    return customer;
                } else {
                    // For individual customers, you might need to fetch additional data
                    IndividualCustomer customer = new IndividualCustomer(customerId, fullName, contactInfo, "");
                    customer.setId(rs.getInt("id"));
                    return customer;
                }
            }
        }
        return null;
    }

    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET full_name = ?, contact_info = ? WHERE customer_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, customer.getFullName());
            pst.setString(2, customer.getContactInfo());
            pst.setString(3, customer.getCustomerId());
            pst.executeUpdate();
        }
    }

    public void deleteCustomer(String customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, customerId);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Customer not found with ID: " + customerId);
            }
        }
    }

    public String getNextCustomerId() throws SQLException {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(customer_id, 9) AS UNSIGNED)), 0) + 1 FROM customers WHERE customer_id LIKE 'customer%'";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                int nextId = rs.getInt(1);
                return "customer" + nextId;
            }
        }
        return "customer1"; // fallback if no customers exist
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY id";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String customerId = rs.getString("customer_id");
                String fullName = rs.getString("full_name");
                String contactInfo = rs.getString("contact_info");
                String customerType = rs.getString("customer_type");
                String nationalIdentity = rs.getString("national_identity");

                Customer customer;
                if ("corporate".equals(customerType)) {
                    customer = new CorporateCustomer(customerId, fullName, contactInfo);
                } else {
                    customer = new IndividualCustomer(customerId, fullName, contactInfo, nationalIdentity != null ? nationalIdentity : "");
                }
                customer.setId(rs.getInt("id"));
                customers.add(customer);
            }
        }
        return customers;
    }
}
