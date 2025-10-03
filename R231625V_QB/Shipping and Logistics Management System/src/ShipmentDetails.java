public class ShipmentDetails {
    private int id; // auto-generated database ID
    private String shipmentId;
    private String itemDescription;
    private int itemCount;
    private String origin;
    private String destination;
    private String currentStatus;
    private Customer customer;
    private VehicleDetails assignedVehicle;

    public ShipmentDetails(String shipmentId, String itemDescription, int itemCount, String origin, String destination, Customer customer) {
        this.shipmentId = shipmentId;
        this.itemDescription = itemDescription;
        this.itemCount = itemCount;
        this.origin = origin;
        this.destination = destination;
        this.customer = customer;
        this.currentStatus = "requested";
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public int getItemCount() {
        return itemCount;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String status) {
        this.currentStatus = status;
    }

    public void setAssignedVehicle(VehicleDetails vehicleDetails) {
        this.assignedVehicle = vehicleDetails;
    }

    public VehicleDetails getAssignedVehicle() {
        return assignedVehicle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}