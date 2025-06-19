import java.util.ArrayList;
import java.util.List;

public abstract class VehicleDetails {
    private String vehicleId;
    private String vehicleModel;
    private double capacity;
    private DriverDetails currentDriver;
    private List<ShipmentDetails> currentShipments;

    public VehicleDetails(String vehicleId, String vehicleModel, double capacity) {
        this.vehicleId = vehicleId;
        this.vehicleModel = vehicleModel;
        this.capacity = capacity;
        this.currentShipments = new ArrayList<>();
    }

    public void assignDriver(DriverDetails driverDetails) throws DriverOccupiedException {
        if (currentDriver != null) {
            throw new DriverOccupiedException("Driver is already assigned to this vehicle.");
        }
        this.currentDriver = driverDetails;
    }

    public void addShipment(ShipmentDetails shipmentDetails) throws LoadExceededException {
        if (getTotalCurrentLoad() + shipmentDetails.getItemCount() > capacity) {
            throw new LoadExceededException("Load exceeded: Cannot add this shipment.");
        }
        currentShipments.add(shipmentDetails);
    }

    private double getTotalCurrentLoad() {
        return currentShipments.stream().mapToDouble(ShipmentDetails::getItemCount).sum();
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public double getCapacity() {
        return capacity;
    }

    public DriverDetails getCurrentDriver() {
        return currentDriver;
    }

    public List<ShipmentDetails> getCurrentShipments() {
        return currentShipments;
    }
}