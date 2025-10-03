import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShipmentDAO {
    private Connection conn;

    public ShipmentDAO(Connection conn) throws SQLException {
        this.conn = conn;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS shipments (
                id INT AUTO_INCREMENT PRIMARY KEY,
                shipment_id VARCHAR(50) UNIQUE NOT NULL,
                item_description VARCHAR(200) NOT NULL,
                item_count INT NOT NULL,
                origin VARCHAR(100) NOT NULL,
                destination VARCHAR(100) NOT NULL,
                customer_id VARCHAR(50) NOT NULL,
                assigned_vehicle_id VARCHAR(50),
                assigned_driver_id VARCHAR(50),
                current_status VARCHAR(20) DEFAULT 'requested',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
                FOREIGN KEY (assigned_vehicle_id) REFERENCES vehicles(vehicle_id),
                FOREIGN KEY (assigned_driver_id) REFERENCES drivers(driver_id)
            )
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public void insertShipment(ShipmentDetails shipment) throws SQLException {
        String sql = "INSERT INTO shipments (shipment_id, item_description, item_count, origin, destination, customer_id, current_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, shipment.getShipmentId());
            pst.setString(2, shipment.getItemDescription());
            pst.setInt(3, shipment.getItemCount());
            pst.setString(4, shipment.getOrigin());
            pst.setString(5, shipment.getDestination());
            pst.setString(6, shipment.getCustomer().getCustomerId());
            pst.setString(7, shipment.getCurrentStatus());

            pst.executeUpdate();

            ResultSet keys = pst.getGeneratedKeys();
            if (keys.next()) {
                shipment.setId(keys.getInt(1));
            }
        }
    }

    public ShipmentDetails selectShipmentById(String shipmentId) throws SQLException {
        String sql = """
            SELECT s.*, c.full_name, c.contact_info, c.customer_type 
            FROM shipments s 
            JOIN customers c ON s.customer_id = c.customer_id 
            WHERE s.shipment_id = ?
            """;
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, shipmentId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Create customer object
                String customerId = rs.getString("customer_id");
                String fullName = rs.getString("full_name");
                String contactInfo = rs.getString("contact_info");
                String customerType = rs.getString("customer_type");

                Customer customer;
                if ("corporate".equals(customerType)) {
                    customer = new CorporateCustomer(customerId, fullName, contactInfo);
                } else {
                    customer = new IndividualCustomer(customerId, fullName, contactInfo, "");
                }

                // Create shipment object
                ShipmentDetails shipment = new ShipmentDetails(
                    rs.getString("shipment_id"),
                    rs.getString("item_description"),
                    rs.getInt("item_count"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    customer
                );
                shipment.setId(rs.getInt("id"));
                shipment.setCurrentStatus(rs.getString("current_status"));

                return shipment;
            }
        }
        return null;
    }

    public void assignVehicleAndDriver(String shipmentId, String vehicleId, String driverId) throws SQLException {
        String sql = "UPDATE shipments SET assigned_vehicle_id = ?, assigned_driver_id = ?, current_status = 'processed' WHERE shipment_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, vehicleId);
            pst.setString(2, driverId);
            pst.setString(3, shipmentId);
            pst.executeUpdate();
        }
    }

    public void updateShipmentStatus(String shipmentId, String status) throws SQLException {
        String sql = "UPDATE shipments SET current_status = ? WHERE shipment_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, status);
            pst.setString(2, shipmentId);
            pst.executeUpdate();
        }
    }

    public boolean isVehicleAvailable(String vehicleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM shipments WHERE assigned_vehicle_id = ? AND current_status IN ('processed', 'in transit')";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, vehicleId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return false;
    }

    public boolean isDriverAvailable(String driverId) throws SQLException {
        String sql = "SELECT is_available FROM drivers WHERE driver_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, driverId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_available");
            }
        }
        return false;
    }

    public double getVehicleCurrentLoad(String vehicleId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(item_count), 0) FROM shipments WHERE assigned_vehicle_id = ? AND current_status IN ('processed', 'in transit')";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, vehicleId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    public void deleteShipment(String shipmentId) throws SQLException {
        String sql = "DELETE FROM shipments WHERE shipment_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, shipmentId);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Shipment not found with ID: " + shipmentId);
            }
        }
    }

    public List<ShipmentDetails> getAllShipments() throws SQLException {
        List<ShipmentDetails> shipments = new ArrayList<>();
        String sql = """
            SELECT s.*, c.full_name, c.contact_info, c.customer_type, c.national_identity
            FROM shipments s 
            JOIN customers c ON s.customer_id = c.customer_id 
            ORDER BY s.id
            """;

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                // Create customer object
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

                // Create shipment object
                ShipmentDetails shipment = new ShipmentDetails(
                    rs.getString("shipment_id"),
                    rs.getString("item_description"),
                    rs.getInt("item_count"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    customer
                );
                shipment.setId(rs.getInt("id"));
                shipment.setCurrentStatus(rs.getString("current_status"));
                shipments.add(shipment);
            }
        }
        return shipments;
    }

    public String getAssignedDriverId(String shipmentId) throws SQLException {
        String sql = "SELECT assigned_driver_id FROM shipments WHERE shipment_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, shipmentId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("assigned_driver_id");
            }
        }
        return null;
    }
}
