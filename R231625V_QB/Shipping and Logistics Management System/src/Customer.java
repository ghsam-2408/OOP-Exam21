import java.util.ArrayList;
import java.util.List;

public abstract class Customer {
    private String customerId;
    private String fullName;
    private String contactInfo;
    private List<ShipmentDetails> shipmentList;

    public Customer(String customerId, String fullName, String contactInfo) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.contactInfo = contactInfo;
        this.shipmentList = new ArrayList<>();
    }

    public void addShipment(ShipmentDetails shipmentDetails) {
        shipmentList.add(shipmentDetails);
    }

    public List<ShipmentDetails> getShipments() {
        return shipmentList;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getContactInfo() {
        return contactInfo;
    }
}