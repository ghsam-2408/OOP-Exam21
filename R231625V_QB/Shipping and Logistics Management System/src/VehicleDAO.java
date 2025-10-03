import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {
    private Connection conn;

    public VehicleDAO(Connection conn) throws SQLException {
        this.conn = conn;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS vehicles (
                id INT AUTO_INCREMENT PRIMARY KEY,
                vehicle_id VARCHAR(50) UNIQUE NOT NULL,
                vehicle_model VARCHAR(100) NOT NULL,
                capacity DOUBLE NOT NULL,
                vehicle_type VARCHAR(20) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public void insertVehicle(VehicleDetails vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (vehicle_id, vehicle_model, capacity, vehicle_type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, vehicle.getVehicleId());
            pst.setString(2, vehicle.getVehicleModel());
            pst.setDouble(3, vehicle.getCapacity());
            pst.setString(4, vehicle instanceof HeavyTruck ? "heavy" : "light");

            pst.executeUpdate();

            ResultSet keys = pst.getGeneratedKeys();
            if (keys.next()) {
                vehicle.setId(keys.getInt(1));
            }
        }
    }

    public VehicleDetails selectVehicleById(String vehicleId) throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE vehicle_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, vehicleId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String vehicleModel = rs.getString("vehicle_model");
                double capacity = rs.getDouble("capacity");
                String vehicleType = rs.getString("vehicle_type");

                VehicleDetails vehicle;
                if ("heavy".equals(vehicleType)) {
                    vehicle = new HeavyTruck(vehicleId, vehicleModel, capacity);
                } else {
                    vehicle = new LightTruck(vehicleId, vehicleModel, capacity);
                }
                vehicle.setId(rs.getInt("id"));
                return vehicle;
            }
        }
        return null;
    }

    public void updateVehicle(VehicleDetails vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET vehicle_model = ?, capacity = ? WHERE vehicle_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, vehicle.getVehicleModel());
            pst.setDouble(2, vehicle.getCapacity());
            pst.setString(3, vehicle.getVehicleId());
            pst.executeUpdate();
        }
    }

    public void deleteVehicle(String vehicleId) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, vehicleId);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Vehicle not found with ID: " + vehicleId);
            }
        }
    }

    public boolean checkVehicleExists(String vehicleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE vehicle_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, vehicleId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public double getVehicleCapacity(String vehicleId) throws SQLException {
        String sql = "SELECT capacity FROM vehicles WHERE vehicle_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, vehicleId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getDouble("capacity");
            }
        }
        return 0;
    }

    public List<VehicleDetails> getAllVehicles() throws SQLException {
        List<VehicleDetails> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles ORDER BY id";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String vehicleId = rs.getString("vehicle_id");
                String vehicleModel = rs.getString("vehicle_model");
                double capacity = rs.getDouble("capacity");
                String vehicleType = rs.getString("vehicle_type");

                VehicleDetails vehicle;
                if ("heavy".equals(vehicleType)) {
                    vehicle = new HeavyTruck(vehicleId, vehicleModel, capacity);
                } else {
                    vehicle = new LightTruck(vehicleId, vehicleModel, capacity);
                }
                vehicle.setId(rs.getInt("id"));
                vehicles.add(vehicle);
            }
        }
        return vehicles;
    }
}
