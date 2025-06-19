import java.util.ArrayList;
import java.util.List;

public class CorporateCustomer extends Customer {
    private List<String> personnelContacts;

    public CorporateCustomer(String customerId, String fullName, String contactInfo) {
        super(customerId, fullName, contactInfo);
        this.personnelContacts = new ArrayList<>();
    }

    public void addPersonnelContact(String personnel) {
        personnelContacts.add(personnel);
    }

    public List<String> getPersonnelContacts() {
        return personnelContacts;
    }
}