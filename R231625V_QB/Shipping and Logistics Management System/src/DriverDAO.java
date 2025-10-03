import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {
    private Connection conn;

    public DriverDAO(Connection conn) throws SQLException {
        this.conn = conn;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS drivers (
                id INT AUTO_INCREMENT PRIMARY KEY,
                driver_id VARCHAR(50) UNIQUE NOT NULL,
                driver_name VARCHAR(100) NOT NULL,
                license_id VARCHAR(50) NOT NULL,
                is_available BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public void insertDriver(DriverDetails driver) throws SQLException {
        String sql = "INSERT INTO drivers (driver_id, driver_name, license_id, is_available) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, driver.getDriverId());
            pst.setString(2, driver.getDriverName());
            pst.setString(3, driver.getLicenseId());
            pst.setBoolean(4, driver.isAvailable());

            pst.executeUpdate();

            ResultSet keys = pst.getGeneratedKeys();
            if (keys.next()) {
                driver.setId(keys.getInt(1));
            }
        }
    }

    public DriverDetails selectDriverById(String driverId) throws SQLException {
        String sql = "SELECT * FROM drivers WHERE driver_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, driverId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String driverName = rs.getString("driver_name");
                String licenseId = rs.getString("license_id");
                boolean isAvailable = rs.getBoolean("is_available");

                DriverDetails driver = new DriverDetails(driverId, driverName, licenseId);
                driver.setId(rs.getInt("id"));
                driver.setAvailable(isAvailable);
                return driver;
            }
        }
        return null;
    }

    public void updateDriver(DriverDetails driver) throws SQLException {
        String sql = "UPDATE drivers SET driver_name = ?, license_id = ?, is_available = ? WHERE driver_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, driver.getDriverName());
            pst.setString(2, driver.getLicenseId());
            pst.setBoolean(3, driver.isAvailable());
            pst.setString(4, driver.getDriverId());
            pst.executeUpdate();
        }
    }

    public void deleteDriver(String driverId) throws SQLException {
        String sql = "DELETE FROM drivers WHERE driver_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, driverId);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Driver not found with ID: " + driverId);
            }
        }
    }

    public void updateDriverAvailability(String driverId, boolean isAvailable) throws SQLException {
        String sql = "UPDATE drivers SET is_available = ? WHERE driver_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setBoolean(1, isAvailable);
            pst.setString(2, driverId);
            pst.executeUpdate();
        }
    }

    public boolean checkDriverExists(String driverId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM drivers WHERE driver_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, driverId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<DriverDetails> getAllDrivers() throws SQLException {
        List<DriverDetails> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers ORDER BY id";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String driverId = rs.getString("driver_id");
                String driverName = rs.getString("driver_name");
                String licenseId = rs.getString("license_id");
                boolean isAvailable = rs.getBoolean("is_available");

                DriverDetails driver = new DriverDetails(driverId, driverName, licenseId);
                driver.setId(rs.getInt("id"));
                driver.setAvailable(isAvailable);
                drivers.add(driver);
            }
        }
        return drivers;
    }
}
