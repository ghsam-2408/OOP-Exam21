public class DriverDetails implements IShipmentStatusUpdater {
    private int id; // auto-generated database ID
    private String driverId;
    private String driverName;
    private String licenseId;
    private VehicleDetails assignedVehicle;
    private boolean available;

    public DriverDetails(String driverId, String driverName, String licenseId) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.licenseId = licenseId;
        this.available = true;
    }

    public void assignVehicle(VehicleDetails vehicleDetails) {
        this.assignedVehicle = vehicleDetails;
    }

    @Override
    public void modifyShipmentStatus(ShipmentDetails shipmentDetails, String status) {
        shipmentDetails.setCurrentStatus(status);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public VehicleDetails getAssignedVehicle() {
        return assignedVehicle;
    }
}