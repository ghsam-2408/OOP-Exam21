public class IndividualCustomer extends Customer {
    private String nationalIdentity;

    public IndividualCustomer(String customerId, String fullName, String contactInfo, String nationalIdentity) {
        super(customerId, fullName, contactInfo);
        this.nationalIdentity = nationalIdentity;
    }

    public String getNationalIdentity() {
        return nationalIdentity;
    }
}