public class DriverDetails implements IShipmentStatusUpdater {
    private String driverId;
    private String driverName;
    private String licenseId;
    private VehicleDetails assignedVehicle;

    public DriverDetails(String driverId, String driverName, String licenseId) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.licenseId = licenseId;
    }

    public void assignVehicle(VehicleDetails vehicleDetails) {
        this.assignedVehicle = vehicleDetails;
    }

    @Override
    public void modifyShipmentStatus(ShipmentDetails shipmentDetails, String status) {
        shipmentDetails.setCurrentStatus(status);
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