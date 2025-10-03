import java.util.ArrayList;
import java.util.List;

public abstract class Customer {
    private int id; // auto-generated database ID
    private String customerId;
    private String fullName;
    private String contactInfo;
    private List<ShipmentDetails> shipmentList;

    public Customer(String customerId, String fullName, String contactInfo) {
        validateContactInfo(contactInfo);
        this.customerId = customerId;
        this.fullName = fullName;
        this.contactInfo = contactInfo;
        this.shipmentList = new ArrayList<>();
    }

    private void validateContactInfo(String contactInfo) {
        if (contactInfo == null || !contactInfo.matches("\\d{10}")) {
            throw new IllegalArgumentException("Contact info must be exactly 10 digits");
        }
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContactInfo(String contactInfo) {
        validateContactInfo(contactInfo);
        this.contactInfo = contactInfo;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}